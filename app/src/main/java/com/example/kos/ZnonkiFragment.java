package com.example.kos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ZnonkiFragment extends Fragment {
    private SharedPreferences settings;
    private ImageButton OnOff;
    private ViewPager viewPager;
    private PagerAdapterZvon pagerAdapter;
    private Context context;
    private String ZvonOne, ZvonTwo, NameYrok, NumKab;
    private String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_znonki, container,false);

        settings = getActivity().getSharedPreferences("Settings", getActivity().MODE_PRIVATE);
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
       toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.menu));
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               ((MainActivity) getActivity()).openDrawer();
           }
       });
        viewPager = view.findViewById(R.id.rager);
        pagerAdapter = new PagerAdapterZvon(getActivity().getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout4);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setScrollX(tabLayout.getWidth());
        tabLayout.getTabAt(5).select();
        Date start = new Date();
        switch (start.toString().substring(0,3)) {
            case "Mon":
                viewPager.setCurrentItem(0);
                url = "Monday.txt";
                break;
            case "Tue":
                viewPager.setCurrentItem(1);
                url = "Tuesday.txt";
                break;
            case "Wed":
                viewPager.setCurrentItem(2);
                url = "Wednesday.txt";
                break;
            case "Thu":
                viewPager.setCurrentItem(3);
                url = "Thursday.txt";
                break;
            case "Fri":
                viewPager.setCurrentItem(4);
                url = "Friday.txt";
                break;
            case "Sat":
                viewPager.setCurrentItem(5);
                url = "Saturday.txt";
                break;
            default:
                viewPager.setCurrentItem(0);
                url = "Monday.txt";
                break;
        }


        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Day", url);
        editor.apply();

       addListenerOnButton(view);

        return view;
    }

    public boolean checkString(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }
    public void addListenerOnButton (final View viewOne){
        OnOff = viewOne.findViewById(R.id.onOff);
        FloatingActionButton button = viewOne.findViewById(R.id.floatingActionButton);
        OnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = settings.getString("Day","Monday.txt");
                switch (url) {
                    case "Monday.txt":
                        if (settings.getBoolean("Monday", true)) {
                            OnOff.setImageResource(R.drawable.off);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("Monday", false);
                            editor.apply();
                        }else {
                            OnOff.setImageResource(R.drawable.on);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("Monday",true);
                            editor.apply();
                        }

                        break;
                    case "Tuesday.txt":
                        if (settings.getBoolean("Tuesday", true)) {
                            OnOff.setImageResource(R.drawable.off);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("Tuesday", false);
                            editor.apply();
                        }else {
                            OnOff.setImageResource(R.drawable.on);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("Tuesday",true);
                            editor.apply();
                        }

                        break;
                    case "Wednesday.txt":
                        if (settings.getBoolean("Wednesday", true)) {
                            OnOff.setImageResource(R.drawable.off);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("Wednesday", false);
                            editor.apply();
                        }else {
                            OnOff.setImageResource(R.drawable.on);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("Wednesday",true);
                            editor.apply();
                        }

                        break;
                    case "Thursday.txt":
                        if (settings.getBoolean("Thursday", true)) {
                            OnOff.setImageResource(R.drawable.off);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("Thursday", false);
                            editor.apply();
                        }else {
                            OnOff.setImageResource(R.drawable.on);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("Thursday",true);
                            editor.apply();
                        }

                        break;
                    case "Friday.txt":
                        if (settings.getBoolean("Friday", true)) {
                            OnOff.setImageResource(R.drawable.off);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("Friday", false);
                            editor.apply();
                        }else {
                            OnOff.setImageResource(R.drawable.on);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("Friday",true);
                            editor.apply();
                        }

                        break;
                    case "Saturday.txt":
                        if (settings.getBoolean("Saturday", true)) {
                            OnOff.setImageResource(R.drawable.off);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("Saturday", false);
                            editor.apply();
                        }else {
                            OnOff.setImageResource(R.drawable.on);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("Saturday",true);
                            editor.apply();
                        }

                        break;
                }
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                final AlertDialog.Builder Delete = new AlertDialog.Builder(context);
                Delete.setMessage("Удалить все звонки?")
                        .setCancelable(true)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    FileOutputStream write =  getActivity().openFileOutput("Monday.txt", getActivity().MODE_PRIVATE);
                                    String temp_write ="";

                                    write.write(temp_write.getBytes());
                                    write.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    FileOutputStream write =  getActivity().openFileOutput("Tuesday.txt", getActivity().MODE_PRIVATE);
                                    String temp_write ="";

                                    write.write(temp_write.getBytes());
                                    write.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    FileOutputStream write =  getActivity().openFileOutput("Wednesday.txt", getActivity().MODE_PRIVATE);
                                    String temp_write ="";

                                    write.write(temp_write.getBytes());
                                    write.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    FileOutputStream write =  getActivity().openFileOutput("Thursday.txt", getActivity().MODE_PRIVATE);
                                    String temp_write ="";

                                    write.write(temp_write.getBytes());
                                    write.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    FileOutputStream write =  getActivity().openFileOutput("Friday.txt", getActivity().MODE_PRIVATE);
                                    String temp_write ="";

                                    write.write(temp_write.getBytes());
                                    write.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    FileOutputStream write =  getActivity().openFileOutput("Saturday.txt", getActivity().MODE_PRIVATE);
                                    String temp_write ="";

                                    write.write(temp_write.getBytes());
                                    write.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }



                                viewPager =  viewOne.findViewById(R.id.rager);
                                pagerAdapter = new PagerAdapterZvon( getActivity().getSupportFragmentManager());
                                viewPager.setAdapter(pagerAdapter);

                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();

                            }
                        });

                AlertDialog Deleted = Delete.create();
                Deleted.setTitle("Удаление");
                Deleted.show();
                return false;
            }
        });
        button.setOnClickListener(new View.OnClickListener()  {
                    @Override
                    public void onClick(View view) {
                        final LayoutInflater li = LayoutInflater.from(context);
                        View promptsView = li.inflate(R.layout.prompt , null);
                        AlertDialog.Builder newzvonok = new AlertDialog.Builder(context);
                        newzvonok.setView(promptsView);
                        final EditText zvonokone = (EditText) promptsView.findViewById(R.id.timeStart);
                        final EditText zvonoktwo = (EditText) promptsView.findViewById(R.id.timeEnd);
                        final EditText Yrok = (EditText) promptsView.findViewById(R.id.nameYrok);
                        final EditText Kab = (EditText) promptsView.findViewById(R.id.numKab);
                        newzvonok
                                .setCancelable(true)
                                .setPositiveButton("Добавить",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                ZvonOne = zvonokone.getText().toString();
                                                ZvonTwo = zvonoktwo.getText().toString();
                                                NameYrok = Yrok.getText().toString();
                                                NumKab = Kab.getText().toString();
                                                if (ZvonOne.length() == 5 && ZvonTwo.length() == 5 && NameYrok.length() > 0 && NumKab.length() > 0){
                                                    int ZvonOneOne = 666;
                                                    int ZvonOneTwo = 666;
                                                    int ZvonTwoOne = 666;
                                                    int ZvonTwoTwo = 666;
                                                    if (checkString(ZvonOne.substring(0,2)))
                                                        ZvonOneOne = Integer.parseInt(ZvonOne.substring(0,2));
                                                    if(checkString(ZvonOne.substring(3)))
                                                        ZvonOneTwo = Integer.parseInt(ZvonOne.substring(3));
                                                    if(checkString(ZvonTwo.substring(0,2)))
                                                        ZvonTwoOne = Integer.parseInt(ZvonTwo.substring(0,2));
                                                    if(checkString(ZvonTwo.substring(3)))
                                                        ZvonTwoTwo = Integer.parseInt(ZvonTwo.substring(3));





//                                                    if (ZvonOneOne != 666 && ZvonOneTwo != 666 && ZvonTwoOne != 666 && ZvonTwoTwo !=666) {
                                                        if(ZvonOneOne < 25 && ZvonOneTwo < 60 && ZvonOne.charAt(2) == ':' && ZvonTwoOne < 25 && ZvonTwoTwo < 60 && ZvonTwo.charAt(2) == ':') {
                                                            if ((ZvonOneOne < ZvonTwoOne) || (ZvonOneOne == ZvonTwoOne && ZvonOneTwo < ZvonTwoTwo)) {
                                                                StringBuffer stringBuffer = new StringBuffer();
                                                                url = settings.getString("Day","Monday.txt");

                                                                try {
                                                                    FileInputStream read =  getActivity().openFileInput(url);
                                                                    InputStreamReader reader = new InputStreamReader(read);
                                                                    BufferedReader bufferedReader = new BufferedReader(reader);

                                                                    String temp_read;
                                                                    while ((temp_read = bufferedReader.readLine()) != null) {
                                                                        stringBuffer.append(temp_read).append(("\n"));
                                                                    }
                                                                } catch (FileNotFoundException e) {
                                                                    e.printStackTrace();
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }

                                                                try {
                                                                    FileOutputStream write =  getActivity().openFileOutput(url, getActivity().MODE_PRIVATE);
                                                                    String temp_write = stringBuffer.toString()  + ZvonOne + " - " + ZvonTwo + "=" + NameYrok + ", Кабинет №" + NumKab;

                                                                    write.write(temp_write.getBytes());
                                                                    write.close();
                                                                } catch (FileNotFoundException e) {
                                                                    e.printStackTrace();
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                switch (url) {
                                                                    case "Monday.txt" :
                                                                        classMonday classMonday = new classMonday();
                                                                        classMonday.Start();
                                                                        viewPager =  viewOne.findViewById(R.id.rager);
                                                                        pagerAdapter = new PagerAdapterZvon( getActivity().getSupportFragmentManager());
                                                                        viewPager.setAdapter(pagerAdapter);
                                                                        break;
                                                                    case "Tuesday.txt" :
                                                                        classTuesday classTuesday = new classTuesday();
                                                                        classTuesday.Start();
                                                                        viewPager =  viewOne.findViewById(R.id.rager);
                                                                        pagerAdapter = new PagerAdapterZvon( getActivity().getSupportFragmentManager());
                                                                        viewPager.setAdapter(pagerAdapter);
                                                                        viewPager.setCurrentItem(1);
                                                                        break;
                                                                    case "Wednesday.txt" :
                                                                   classWednesday classWednesday = new classWednesday();
                                                                   classWednesday.Start();
                                                                        viewPager = viewOne.findViewById(R.id.rager);
                                                                        pagerAdapter = new PagerAdapterZvon( getActivity().getSupportFragmentManager());
                                                                        viewPager.setAdapter(pagerAdapter);
                                                                        viewPager.setCurrentItem(2);
                                                                        break;
                                                                    case "Thursday.txt" :
                                                                        classThursday classThursday = new classThursday();
                                                                        classThursday.Start();
                                                                        viewPager =  viewOne.findViewById(R.id.rager);
                                                                        pagerAdapter = new PagerAdapterZvon( getActivity().getSupportFragmentManager());
                                                                        viewPager.setAdapter(pagerAdapter);
                                                                        viewPager.setCurrentItem(3);
                                                                        break;
                                                                    case "Friday.txt" :
                                                                        classFriday classFriday = new classFriday();
                                                                        classFriday.Start();
                                                                        viewPager =  viewOne.findViewById(R.id.rager);
                                                                        pagerAdapter = new PagerAdapterZvon( getActivity().getSupportFragmentManager());
                                                                        viewPager.setAdapter(pagerAdapter);
                                                                        viewPager.setCurrentItem(4);
                                                                        break;
                                                                    case "Saturday.txt" :
                                                                        classSaturday classSaturday = new classSaturday();
                                                                        classSaturday.Start();
                                                                        viewPager =  viewOne.findViewById(R.id.rager);
                                                                        pagerAdapter = new PagerAdapterZvon( getActivity().getSupportFragmentManager());
                                                                        viewPager.setAdapter(pagerAdapter);
                                                                        viewPager.setCurrentItem(5);
                                                                        break;

                                                                }


                                                            }
                                                            else
                                                                Toast.makeText(context, "Не верный промежуток! Первое значение не может быть больше второго!", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(
                                                                    context, "Не верный формат!", Toast.LENGTH_SHORT
                                                            ).show();
                                                        }
//                                                        Toast.makeText(
//                                                                context, "ЫФВФЫФЫВФЫВФЫФЫВФЫ", Toast.LENGTH_SHORT
//                                                        ).show();}
                                                }
                                                else {
                                                    Toast.makeText(
                                                            context, "Не верный формат!", Toast.LENGTH_SHORT
                                                    ).show();
                                                }
                                            }
                                        });

                        //Создаем AlertDialog:
                        AlertDialog alertDialog = newzvonok.create();

                        //и отображаем его:
                        // alertDialog.setTitle("Новый урок");
                        alertDialog.show();

                    }
                }
        );
    }


}
