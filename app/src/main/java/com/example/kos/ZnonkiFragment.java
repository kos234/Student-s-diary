package com.example.kos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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

import static android.content.Context.MODE_PRIVATE;


public class ZnonkiFragment extends Fragment {
    private SharedPreferences settings,Current_Theme;
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
        Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
        final androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        Drawable iconNavigation = ContextCompat.getDrawable(context, R.drawable.ic_menu_24px);
        iconNavigation.setColorFilter(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(iconNavigation);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
        toolbar.setTitleTextColor(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)));
        toolbar.setBackgroundColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));
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
                        if(settings.getBoolean("SaturdaySettings",true)) {
                            editor.putString("Day", "Saturday.txt");
                            OnOff = settings.getBoolean("Saturday", true);
                        }
                    break;
            }
               editor.apply();
            ImageButton imageButton = view.findViewById(R.id.onOff);
            if(OnOff) {
                Drawable drawable =  ContextCompat.getDrawable(context, R.drawable.ic_power_settings_new_24px);
                drawable.setColorFilter(Current_Theme.getInt("custom_notification_on", ContextCompat.getColor(context, R.color.custom_notification_on)), PorterDuff.Mode.SRC_ATOP);
                imageButton.setImageDrawable(drawable);
            }else{
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_power_settings_new_24px);
                drawable.setColorFilter(Current_Theme.getInt("custom_notification_off", ContextCompat.getColor(context, R.color.custom_notification_off)), PorterDuff.Mode.SRC_ATOP);
                imageButton.setImageDrawable(drawable);
           }}

           @Override
           public void onPageScrollStateChanged(int state) {

           }
       });
        TabLayout tabLayout = view.findViewById(R.id.tabLayout4);
        tabLayout.setTabTextColors(ColorStateList.valueOf(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text))));
        tabLayout.setBackgroundColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setScrollX(tabLayout.getWidth());
        tabLayout.setSelectedTabIndicatorColor(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)));
        tabLayout.getTabAt(0).select();
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
                if(settings.getBoolean("SaturdaySettings",true)) {
                    viewPager.setCurrentItem(5);
                    url = "Saturday.txt";
                }
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
           if(k + 1 == day.length && !settings.getBoolean("SaturdaySettings",true))
               continue;

           String[] help, helpSplitTimes, helpTimesOne, helpTimesTwo;
           String delimeter = "=";
           ArrayList<ConstrRecyclerView> product = new ArrayList<>();
           try {
               FileInputStream read = context.openFileInput(day[k]);
               InputStreamReader reader = new InputStreamReader(read);
               BufferedReader bufferedReader = new BufferedReader(reader);
               String temp_read;
               while ((temp_read = bufferedReader.readLine()) != null) {
                   help = temp_read.split(delimeter);
                   helpSplitTimes = help[0].split("-");
                   helpTimesOne = helpSplitTimes[0].split(":");
                   helpTimesTwo = helpSplitTimes[1].split(":");

                   String write;
                   if(helpTimesOne.length == 3 && helpTimesTwo.length == 3)
                       write = helpTimesOne[0] + ":" + helpTimesOne[1] + " " + helpTimesOne[2] + " - " +  helpTimesTwo[0] + ":" + helpTimesTwo[1] + " " + helpTimesTwo[2];
                   else
                       write = helpTimesOne[0] + ":" + helpTimesOne[1] + " - " +  helpTimesTwo[0] + ":" + helpTimesTwo[1];
                   product.add(new ConstrRecyclerView(write, help[1]));
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

           constrFragmentViewPagerList.add(new ConstrFragmentViewPager(product, day[k], new RecyclerAdapter(product, context)));
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
        url = settings.getString("Day","Monday.txt");
        String[] temp = url.split(".txt");
        if (settings.getBoolean(temp[0], true)) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_power_settings_new_24px);
            drawable.setColorFilter(Current_Theme.getInt("custom_notification_off", ContextCompat.getColor(context, R.color.custom_notification_off)), PorterDuff.Mode.SRC_ATOP);
            OnOff.setImageDrawable(drawable);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(temp[0], false);
            editor.apply();
        }else {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_power_settings_new_24px);
            drawable.setColorFilter(Current_Theme.getInt("custom_notification_on", ContextCompat.getColor(context, R.color.custom_notification_on)), PorterDuff.Mode.SRC_ATOP);
            OnOff.setImageDrawable(drawable);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(temp[0],true);
            editor.apply();
        }
        FloatingActionButton button = viewOne.findViewById(R.id.floatingActionButton);
        OnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = settings.getString("Day","Monday.txt");
                String[] temp = url.split(".txt");
                        if (settings.getBoolean(temp[0], true)) {
                            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_power_settings_new_24px);
                            drawable.setColorFilter(Current_Theme.getInt("custom_notification_off", ContextCompat.getColor(context, R.color.custom_notification_off)), PorterDuff.Mode.SRC_ATOP);
                            OnOff.setImageDrawable(drawable);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(temp[0], false);
                            editor.apply();
                        }else {
                            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_power_settings_new_24px);
                            drawable.setColorFilter(Current_Theme.getInt("custom_notification_on", ContextCompat.getColor(context, R.color.custom_notification_on)), PorterDuff.Mode.SRC_ATOP);
                            OnOff.setImageDrawable(drawable);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(temp[0],true);
                            editor.apply();
                        }
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

                final LayoutInflater li = LayoutInflater.from(context);
                final View promptsView = li.inflate(R.layout.alert_delete_dnewnik , null);
                final AlertDialog.Builder Delete = new AlertDialog.Builder(context);
                Delete.setView(promptsView);
                GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
                alertbackground.setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                if(settings.getBoolean("BorderAlertSettings",false))
                    alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                promptsView.findViewById(R.id.alert_delete).setBackground(alertbackground);

                final AlertDialog Deleted = Delete.create();

                TextView textTitle = promptsView.findViewById(R.id.title_alert);
                textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                textTitle.setText(context.getString(R.string.deleting));

                TextView textBottomTitle = promptsView.findViewById(R.id.title_bottom_alert);
                textBottomTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                textBottomTitle.setText(context.getString(R.string.deleteAllLesson));

                TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
                ButtonCancel.setText(getString(R.string.cancel));
                ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                ButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Deleted.hide();
                    }
                });

                TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
                ButtonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewClick) {
                        String[] day = context.getResources().getStringArray(R.array.DayTxt);
                        for (int k = 0; k < day.length; k++) {

                            File outFile = new File(context.getFilesDir() + "/" + day[k]);
                            if (outFile.exists()) {
                                outFile.delete();
                            }
                        }

                        pagerAdapter = new NewPagerAdapter(GenerateData(), context, (FloatingActionButton) view.findViewById(R.id.floatingActionButton));
                        viewPager.setAdapter(pagerAdapter);

                       switch (settings.getString("Day", "Monday.txt")) {
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
                                if(settings.getBoolean("SaturdaySettings",true))
                                    viewPager.setCurrentItem(5);
                                break;
                            default:
                                viewPager.setCurrentItem(0);
                                break;
                        }

                        Deleted.hide();

                    }
                });
                ButtonSave.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));

                Deleted.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                Deleted.show();
                return false;
            }
        });
    }
}