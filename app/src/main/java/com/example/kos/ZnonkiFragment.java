package com.example.kos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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
        final View view =  inflater.inflate(R.layout.fragment_znonki, container,false);
        Date dateStart = new Date();
        settings = getActivity().getSharedPreferences("Settings", getActivity().MODE_PRIVATE);
        final androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_24px));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
        ViewPager viewPager = view.findViewById(R.id.rager);
        pagerAdapter = new PagerAdapterZvon(getActivity().getSupportFragmentManager(),context);
        viewPager.setAdapter(pagerAdapter);
       viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
           @Override
           public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

           }

           @Override
           public void onPageSelected(int position) {
               Boolean OnOff = true;
               SharedPreferences.Editor editor = settings.edit();
            switch (position){
                case 0:
                    editor.putString("Day","Monday.txt" );
                    if (settings.contains("Monday"))
                            OnOff = settings.getBoolean("Monday", true);
                    break;

                case 1:
                    editor.putString("Day","Tuesday.txt" );
                    if (settings.contains("Tuesday"))
                            OnOff = settings.getBoolean("Tuesday", true);
                    break;

                    case 2:
                    editor.putString("Day","Wednesday.txt" );
                    if (settings.contains("Wednesday"))
                            OnOff = settings.getBoolean("Wednesday", true);
                    break;

                    case 3:
                    editor.putString("Day","Thursday.txt" );
                    if (settings.contains("Thursday"))
                            OnOff = settings.getBoolean("Thursday", true);
                    break;

                    case 4:
                    editor.putString("Day","Friday.txt" );
                    if (settings.contains("Friday"))
                            OnOff = settings.getBoolean("Friday", true);
                    break;

                    case 5:
                    editor.putString("Day","Saturday.txt" );
                    if (settings.contains("Saturday"))
                            OnOff = settings.getBoolean("Saturday", true);
                    break;
            }
               editor.apply();
            ImageButton imageButton = view.findViewById(R.id.onOff);
            if(OnOff)
               imageButton.setImageResource(R.drawable.ic_power_settings_new_24px);
            else
               imageButton.setImageResource(R.drawable.ic_power_settings_new_red_24px);
           }

           @Override
           public void onPageScrollStateChanged(int state) {

           }
       });
        TabLayout tabLayout = view.findViewById(R.id.tabLayout4);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setScrollX(tabLayout.getWidth());
        tabLayout.getTabAt(5).select();
        Date start = new Date();
        switch (start.toString().substring(0,3)) {
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
        Date dateEnd = new Date();
        int Kek = Integer.parseInt(dateEnd.toString().substring(17,19)) - Integer.parseInt(dateStart.toString().substring(17,19));

       // Toast.makeText(getActivity(),Integer.toString(Kek),Toast.LENGTH_LONG).show();
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
                String[] temp = url.split(".txt");
                        if (settings.getBoolean(temp[0], true)) {
                            OnOff.setImageResource(R.drawable.ic_power_settings_new_red_24px);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(temp[0], false);
                            editor.apply();
                        }else {
                            OnOff.setImageResource(R.drawable.ic_power_settings_new_24px);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(temp[0],true);
                            editor.apply();
                        }
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                final AlertDialog.Builder Delete = new AlertDialog.Builder(context);
                Delete.setMessage(context.getString(R.string.deleteAllLesson))
                        .setCancelable(true)
                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
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
                                pagerAdapter = new PagerAdapterZvon(getActivity().getSupportFragmentManager(),context);
                                viewPager.setAdapter(pagerAdapter);

                            }
                        })
                        .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();

                            }
                        });

                AlertDialog Deleted = Delete.create();
                Deleted.setTitle(context.getString(R.string.deleting));
                Deleted.show();
                return false;
            }
        });
        button.setOnClickListener(new View.OnClickListener()  {
                                      @Override
                                      public void onClick(View view) {

                                          final LayoutInflater li = LayoutInflater.from(context);
                                          View promptsView = li.inflate(R.layout.prompt , null);
                                          final AlertDialog.Builder newzvonok = new AlertDialog.Builder(context);
                                          newzvonok.setView(promptsView);
                                          final EditText zvonokone = promptsView.findViewById(R.id.timeStart);
                                          final EditText zvonoktwo = promptsView.findViewById(R.id.timeEnd);
                                          final EditText Yrok = promptsView.findViewById(R.id.nameYrok);
                                          final EditText Kab = promptsView.findViewById(R.id.numKab);
                                          final Spinner spinner = (Spinner) promptsView.findViewById(R.id.spinner);
                                          List<String> choose = new ArrayList<String>();
                                          choose.add(context.getString(R.string.classroomSchool));
                                          choose.add(context.getString(R.string.classroomUniversity));
                                          ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>  (getActivity(),R.layout.spinner_list, choose);
                                          spinner.setAdapter(dataAdapter);
                                          newzvonok
                                                  .setCancelable(true)
                                                  .setPositiveButton(context.getString(R.string.save),
                                                          new DialogInterface.OnClickListener() {
                                                              @RequiresApi(api = Build.VERSION_CODES.N)
                                                              public void onClick(DialogInterface dialog, int id) {
                                                                  ZvonOne = zvonokone.getText().toString();
                                                                  ZvonTwo = zvonoktwo.getText().toString();
                                                                  NameYrok = Yrok.getText().toString();
                                                                  NumKab = Kab.getText().toString();
                                                                  if ((ZvonOne.length() == 5 || ZvonOne.equals("")) && (ZvonTwo.length() == 5 || ZvonTwo.equals(""))){
                                                                      if(ZvonOne.equals(""))
                                                                          ZvonOne = "08:00";
                                                                      if(ZvonTwo.equals(""))
                                                                          ZvonTwo = "08:40";
                                                                      if(NameYrok.equals(""))
                                                                          NameYrok = context.getString(R.string.lessonExample);
                                                                      if(NumKab.equals(""))
                                                                          NumKab = "5";
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


                                                                      if(ZvonOneOne < 25 && ZvonOneTwo < 60 && ZvonOne.charAt(2) == ':' && ZvonTwoOne < 25 && ZvonTwoTwo < 60 && ZvonTwo.charAt(2) == ':') {
                                                                          if ((ZvonOneOne < ZvonTwoOne) || (ZvonOneOne == ZvonTwoOne && ZvonOneTwo < ZvonTwoTwo)) {
                                                                              StringBuffer stringBuffer = new StringBuffer();
                                                                              url = settings.getString("Day","Monday.txt");
                                                                              try {
                                                                                  boolean Zapic = true;
                                                                              try {
                                                                                  FileInputStream read =  getActivity().openFileInput(url);
                                                                                  InputStreamReader reader = new InputStreamReader(read);
                                                                                  BufferedReader bufferedReader = new BufferedReader(reader);

                                                                                  String temp_read;
                                                                                  String[] help;
                                                                                  String delimeter = "=";
                                                                                  while ((temp_read = bufferedReader.readLine()) != null) {

                                                                                      help = temp_read.split(delimeter);
                                                                                      if((Integer.parseInt(help[0].substring(0,2)) == ZvonOneOne && Integer.parseInt(help[0].substring(3,5)) == ZvonOneTwo) || (Integer.parseInt(help[0].substring(8,10)) == ZvonTwoOne && Integer.parseInt(help[0].substring(11)) == ZvonTwoTwo)) {
                                                                                          throw new Povtor("KRIA", 1);
                                                                                      }
                                                                                      if(Integer.parseInt(help[0].substring(0,2)) > ZvonOneOne   && Zapic) {
                                                                                          stringBuffer.append(ZvonOne + " - " + ZvonTwo + "=" + NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab).append(("\n")).append(temp_read).append(("\n"));
                                                                                      Zapic = false;

                                                                                      } else
                                                                                      stringBuffer.append(temp_read).append(("\n"));
                                                                                  }
                                                                              } catch (FileNotFoundException e) {
                                                                                  e.printStackTrace();
                                                                              } catch (IOException e) {
                                                                                  e.printStackTrace();
                                                                              }
                                                                                if (Zapic)
                                                                                    stringBuffer.append(ZvonOne + " - " + ZvonTwo + "=" + NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab);
                                                                              try {
                                                                                  FileOutputStream write =  getActivity().openFileOutput(url, getActivity().MODE_PRIVATE);
                                                                                  String temp_write = stringBuffer.toString();

                                                                                  write.write(temp_write.getBytes());
                                                                                  write.close();
                                                                              } catch (FileNotFoundException e) {
                                                                                  e.printStackTrace();
                                                                              } catch (IOException e) {
                                                                                  e.printStackTrace();
                                                                              }
                                                                                      ItemFragment ItemFragment = new ItemFragment(url);
                                                                                      ItemFragment.Start();
                                                                                      viewPager =  viewOne.findViewById(R.id.rager);
                                                                                      pagerAdapter = new PagerAdapterZvon( getActivity().getSupportFragmentManager(),context);
                                                                                      viewPager.setAdapter(pagerAdapter);
                                                                                      switch (url) {
                                                                                          case "Tuesday.txt":
                                                                                              viewPager.setCurrentItem(1);
                                                                                              break;
                                                                                          case "Wednesday.txt":
                                                                                              viewPager.setCurrentItem(2);
                                                                                              break;
                                                                                          case "Thursday.txt":
                                                                                              viewPager.setCurrentItem(3);
                                                                                              break;
                                                                                          case "Friday.txt":
                                                                                              viewPager.setCurrentItem(4);
                                                                                              break;
                                                                                          case "Saturday.txt":
                                                                                              viewPager.setCurrentItem(5);
                                                                                              break;
                                                                                      }
                                                                          } catch (Povtor povtor) {
                                                                                Toast.makeText(context,context.getString(R.string.timeSpan),Toast.LENGTH_LONG).show();
                                                                              }
                                                                          }
                                                                          else
                                                                              Toast.makeText(context, context.getString(R.string.timeSpanStartEnd), Toast.LENGTH_SHORT).show();
                                                                      }else{
                                                                          Toast.makeText(
                                                                                  context, context.getString(R.string.FieldsNot), Toast.LENGTH_SHORT
                                                                          ).show();
                                                                      }
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