package com.example.kos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ZnonkiFragment extends Fragment {
    private SharedPreferences settings;
    private ImageButton OnOff;
    private ViewPager viewPager;
    private NewPagerAdapter pagerAdapter;
    private Context context;
    private String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_znonki, container,false);
        settings = getActivity().getSharedPreferences("Settings", getActivity().MODE_PRIVATE);
        final androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_24px));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
        viewPager = view.findViewById(R.id.rager);
        pagerAdapter = new NewPagerAdapter(GenerateData(), context, (FloatingActionButton) view.findViewById(R.id.floatingActionButton));
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
                            OnOff = settings.getBoolean("Monday", true);
                    break;

                case 1:
                    editor.putString("Day","Tuesday.txt" );
                            OnOff = settings.getBoolean("Tuesday", true);
                    break;

                    case 2:
                    editor.putString("Day","Wednesday.txt" );
                            OnOff = settings.getBoolean("Wednesday", true);
                    break;

                    case 3:
                    editor.putString("Day","Thursday.txt" );
                            OnOff = settings.getBoolean("Thursday", true);
                    break;

                    case 4:
                    editor.putString("Day","Friday.txt" );
                            OnOff = settings.getBoolean("Friday", true);
                    break;

                    case 5:
                    editor.putString("Day","Saturday.txt" );
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
        return view;
    }

    private List<ConstrFragmentViewPager> GenerateData() {
        List<ConstrFragmentViewPager> constrFragmentViewPagerList = new ArrayList<>();

        String[] day = getResources().getStringArray(R.array.DayTxt);
       for (int k = 0; k < day.length; k++) {
           String[] help;
           String delimeter = "=";
           ArrayList<ConstrRecyclerView> product = new ArrayList<>();
           try {
               FileInputStream read = context.openFileInput(day[k]);
               InputStreamReader reader = new InputStreamReader(read);
               BufferedReader bufferedReader = new BufferedReader(reader);
               String temp_read;
               while ((temp_read = bufferedReader.readLine()) != null) {
                   help = temp_read.split(delimeter);
                   product.add(new ConstrRecyclerView(help[0], help[1]));
               }
               bufferedReader.close();
               reader.close();
               read.close();
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           } catch (NullPointerException ignore) {
               ignore.printStackTrace();
           }

           constrFragmentViewPagerList.add(new ConstrFragmentViewPager(product, day[k], new RecyclerAdapter(product)));
       }
        return constrFragmentViewPagerList;
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
                                String[] day = context.getResources().getStringArray(R.array.DayTxt);
                                for (int k = 0; k < day.length; k++) {

                                    File outFile = new File(context.getFilesDir() + "/" + day[k]);
                                    if (outFile.exists()) {
                                        outFile.delete();
                                    }
                                }

                                pagerAdapter = new NewPagerAdapter(GenerateData(), context, (FloatingActionButton) view.findViewById(R.id.floatingActionButton));
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

    }


}