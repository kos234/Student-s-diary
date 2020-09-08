package com.example.kos.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.kos.constructors.ConstructorFragmentViewPager;
import com.example.kos.constructors.ConstructorRecyclerView;
import com.example.kos.MainActivity;
import com.example.kos.R;
import com.example.kos.adapters.AdapterNewPager;
import com.example.kos.onBackPressed;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class FragmentBells extends Fragment implements onBackPressed {
    private SharedPreferences settings, Current_Theme;
    SharedPreferences.Editor editor;
    public ViewPager viewPager;
    private AdapterNewPager pagerAdapter;
    private Context context;
    private String url;
    private View view;
    public String[] currentWindow = new String[]{"null"}, action = new String[]{"null"};
    private int[] scrolls = new int[]{0, 0, 0, 0, 0, 0};
    private ArrayList<ConstructorFragmentViewPager> list = new ArrayList<>();
    private FloatingActionButton floatingActionButton;
    private ImageButton imageButton;
    public final ArrayList<AlertDialog> alertDialogs = new ArrayList<>();
    private String[] day;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            Bundle b;
            if (savedInstanceState == null)
                b = getArguments();
            else
                b = savedInstanceState;


            view = inflater.inflate(R.layout.fragment_znonki, container, false);
            settings = Objects.requireNonNull(context).getSharedPreferences("Settings", MODE_PRIVATE);
            editor = settings.edit();
            try {
                ((MainActivity) context).menuAdapter.onCheck(1, settings.getInt("Fragment", 0));
                editor.putInt("Fragment", 1).apply();
            } catch (NullPointerException ignored) {
            }
            Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
            floatingActionButton = view.findViewById(R.id.floatingActionButton);
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add))));
            day = MainActivity.getResources.getStringArray(R.array.DayTxt);
            if (Objects.requireNonNull(b).size() != 0)
                if (b.getStringArray("action") != null) {
                    action = b.getStringArray("action");
                    currentWindow = b.getStringArray("currentWindow");
                }


            if (b.size() > 2) {
                list = b.getParcelableArrayList("list");
                scrolls = b.getIntArray("scrolls");
                if (list.size() == 6 && !settings.getBoolean("SaturdaySettings", true))
                    list.remove(5);
                else if (list.size() == 5 && settings.getBoolean("SaturdaySettings", true))
                    onGenerate(day[5]);
                pagerAdapter = new AdapterNewPager(list, context, floatingActionButton, scrolls);
                viewPager = view.findViewById(R.id.rager);
                viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
                viewPager.setAdapter(pagerAdapter);
                TabLayout tabLayout = view.findViewById(R.id.tabLayout4);
                tabLayout.setBackgroundColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));
                tabLayout.setTabTextColors(ColorStateList.valueOf(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text))));
                tabLayout.setScrollX(tabLayout.getWidth());
                tabLayout.setupWithViewPager(viewPager);
                tabLayout.setSelectedTabIndicatorColor(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)));
                Objects.requireNonNull(tabLayout.getTabAt(0)).select();
                if (currentWindow[0].equals("null"))
                    currentWindow = b.getStringArray("currentWindow");
                if (action[0].equals("add"))
                    viewPager.setCurrentItem(Integer.parseInt(Objects.requireNonNull(currentWindow)[1]));
                else
                    viewPager.setCurrentItem(b.getInt("currentItem"));
            } else new StartAsyncTask().execute();
            addListenerOnButton();
            ((MainActivity) context).toolbar.setTitle(getString(R.string.timetables));

        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }

        return view;
    }

    public String[] getCurrentWindow() {
        return currentWindow;
    }

    public void setCurrentWindow(String[] currentWindow) {
        this.currentWindow = currentWindow;
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (alertDialogs.size() == 1) {
                if (alertDialogs.get(0) != null)
                    alertDialogs.get(0).dismiss();
            } else if (alertDialogs.size() == 2) {
                if (alertDialogs.get(0) != null)
                    alertDialogs.get(0).dismiss();
                if (alertDialogs.get(1) != null)
                    alertDialogs.get(1).dismiss();
            }
            Bundle bundle = ((MainActivity) context).getBundles.get(1);
            try {
                bundle.putIntArray("scrolls", pagerAdapter.getScrolls());
            } catch (NullPointerException e) {
                bundle.putIntArray("scrolls", scrolls);
            }
            try {
                bundle.putInt("currentItem", viewPager.getCurrentItem());
            } catch (NullPointerException e) {
                bundle.putInt("currentItem", 0);
            }
            bundle.putParcelableArrayList("list", list);
            bundle.putStringArray("currentWindow", currentWindow);
            bundle.putString("action", "null");
            imageButton.setVisibility(View.GONE);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        try {
            try {
                outState.putIntArray("scrolls", pagerAdapter.getScrolls());
            } catch (NullPointerException e) {
                outState.putIntArray("scrolls", scrolls);
            }
            try {
                outState.putInt("currentItem", viewPager.getCurrentItem());
            } catch (NullPointerException e) {
                outState.putInt("currentItem", 0);
            }
            outState.putParcelableArrayList("list", list);
            outState.putStringArray("currentWindow", currentWindow);
            outState.putString("action", "null");
            super.onSaveInstanceState(outState);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onBackPressed() {
        try {
            if (alertDialogs.size() != 0) {
                alertDialogs.remove(alertDialogs.size() - 1);
                return true;
            } else
                return false;
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return true;
        }
    }

    class StartAsyncTask extends AsyncTask<Void, Void, Void> {
        TabLayout tabLayout;
        RelativeLayout relativeLayout;

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                relativeLayout = view.findViewById(R.id.add_frag);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                layoutParams.addRule(RelativeLayout.BELOW, R.id.tabLayout4);
                ProgressBar progressBar = new ProgressBar(context);
                progressBar.setId(R.id.ProgressBar);
                progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);
                relativeLayout.addView(progressBar, layoutParams);
                tabLayout = view.findViewById(R.id.tabLayout4);
                tabLayout.setBackgroundColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));
                viewPager = view.findViewById(R.id.rager);
                viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        try {
                            boolean OnOff = true;
                            switch (position) {
                                case 0:
                                    editor.putString("Day", "Monday.txt");
                                    OnOff = settings.getBoolean("Monday", true);
                                    break;

                                case 1:
                                    editor.putString("Day", "Tuesday.txt");
                                    OnOff = settings.getBoolean("Tuesday", true);
                                    break;

                                case 2:
                                    editor.putString("Day", "Wednesday.txt");
                                    OnOff = settings.getBoolean("Wednesday", true);
                                    break;

                                case 3:
                                    editor.putString("Day", "Thursday.txt");
                                    OnOff = settings.getBoolean("Thursday", true);
                                    break;

                                case 4:
                                    editor.putString("Day", "Friday.txt");
                                    OnOff = settings.getBoolean("Friday", true);
                                    break;

                                case 5:
                                    if (settings.getBoolean("SaturdaySettings", true)) {
                                        editor.putString("Day", "Saturday.txt");
                                        OnOff = settings.getBoolean("Saturday", true);
                                    }
                                    break;
                            }
                            editor.apply();
                            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_power_settings_new_24px);
                            if (OnOff) {
                                Objects.requireNonNull(drawable).setColorFilter(Current_Theme.getInt("custom_notification_on", ContextCompat.getColor(context, R.color.custom_notification_on)), PorterDuff.Mode.SRC_ATOP);
                            } else {
                                Objects.requireNonNull(drawable).setColorFilter(Current_Theme.getInt("custom_notification_off", ContextCompat.getColor(context, R.color.custom_notification_off)), PorterDuff.Mode.SRC_ATOP);
                            }
                            imageButton.setImageDrawable(drawable);
                        } catch (Exception error) {
                            ((MainActivity) context).errorStack(error);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                super.onPostExecute(aVoid);
                relativeLayout.removeView(relativeLayout.findViewById(R.id.ProgressBar));
                viewPager.setAdapter(pagerAdapter);
                tabLayout.setTabTextColors(ColorStateList.valueOf(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text))));
                tabLayout.setScrollX(tabLayout.getWidth());
                tabLayout.setupWithViewPager(viewPager);
                tabLayout.setSelectedTabIndicatorColor(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)));
                Objects.requireNonNull(tabLayout.getTabAt(0)).select();
                if (action[0].equals("add"))
                    viewPager.setCurrentItem(Integer.parseInt(currentWindow[1]));
                else {
                    Date start = new Date();
                    switch (start.toString().substring(0, 3)) {
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
                            if (settings.getBoolean("SaturdaySettings", true)) {
                                viewPager.setCurrentItem(5);
                                url = "Saturday.txt";
                            }
                            break;
                        default:
                            viewPager.setCurrentItem(0);
                            url = "Monday.txt";
                            break;
                    }
                }
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                GenerateData();
                pagerAdapter = new AdapterNewPager(list, context, floatingActionButton, scrolls);

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("Day", url);
                editor.apply();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }
    }

    public class DeleteAll extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                viewPager.setAdapter(pagerAdapter);

                switch (Objects.requireNonNull(settings.getString("Day", "Monday.txt"))) {
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
                        if (settings.getBoolean("SaturdaySettings", true))
                            viewPager.setCurrentItem(5);
                        break;
                    default:
                        viewPager.setCurrentItem(0);
                        break;
                }
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                GenerateData();
                pagerAdapter = new AdapterNewPager(list, context, (FloatingActionButton) view.findViewById(R.id.floatingActionButton), scrolls);
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }
    }

    private void GenerateData() {
        try {
            list.clear();
            for (int k = 0; k < day.length; k++) {
                if (k + 1 == day.length && !settings.getBoolean("SaturdaySettings", true))
                    continue;

                onGenerate(day[k]);
            }
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public void onGenerate(String dayTXT) {
        try {
            String[] help, helpSplitTimes, helpTimesOne, helpTimesTwo;
            String delimeter = "=";
            ArrayList<ConstructorRecyclerView> product = new ArrayList<>();
            try {
                FileInputStream read = context.openFileInput(dayTXT);
                InputStreamReader reader = new InputStreamReader(read);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String temp_read, write;
                while ((temp_read = bufferedReader.readLine()) != null) {
                    help = temp_read.split(delimeter);
                    helpSplitTimes = help[0].split("-");
                    helpTimesOne = helpSplitTimes[0].split(":");
                    helpTimesTwo = helpSplitTimes[1].split(":");

                    if (helpTimesOne.length == 3 && helpTimesTwo.length == 3)
                        write = helpTimesOne[0] + ":" + helpTimesOne[1] + " " + helpTimesOne[2] + " - " + helpTimesTwo[0] + ":" + helpTimesTwo[1] + " " + helpTimesTwo[2];
                    else
                        write = helpTimesOne[0] + ":" + helpTimesOne[1] + " - " + helpTimesTwo[0] + ":" + helpTimesTwo[1];
                    product.add(new ConstructorRecyclerView(write, help[1] + ", " + help[2]));
                }
                bufferedReader.close();
                reader.close();
                read.close();
            } catch (NullPointerException | IOException ignored) {
            }

            list.add(new ConstructorFragmentViewPager(product, dayTXT));
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    private void addListenerOnButton() {
        try {
            imageButton = ((MainActivity) context).getButton();
            imageButton.setVisibility(View.VISIBLE);
            url = settings.getString("Day", "Monday.txt");
            String[] temp = Objects.requireNonNull(url).split(".txt");
            final Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_power_settings_new_24px);
            if (settings.getBoolean(temp[0], true)) {
                Objects.requireNonNull(drawable).setColorFilter(Current_Theme.getInt("custom_notification_on", ContextCompat.getColor(context, R.color.custom_notification_on)), PorterDuff.Mode.SRC_ATOP);
                imageButton.setImageDrawable(drawable);
            } else {
                Objects.requireNonNull(drawable).setColorFilter(Current_Theme.getInt("custom_notification_off", ContextCompat.getColor(context, R.color.custom_notification_off)), PorterDuff.Mode.SRC_ATOP);
                imageButton.setImageDrawable(drawable);
            }
            imageButton.setOnClickListener(view -> {
                try {
                    url = settings.getString("Day", "Monday.txt");
                    String[] temp1 = Objects.requireNonNull(url).split(".txt");
                    Drawable drawable1 = ContextCompat.getDrawable(context, R.drawable.ic_power_settings_new_24px);
                    SharedPreferences.Editor editor = settings.edit();
                    if (settings.getBoolean(temp1[0], true)) {
                        Objects.requireNonNull(drawable1).setColorFilter(Current_Theme.getInt("custom_notification_off", ContextCompat.getColor(context, R.color.custom_notification_off)), PorterDuff.Mode.SRC_ATOP);
                        editor.putBoolean(temp1[0], false);
                    } else {
                        Objects.requireNonNull(drawable1).setColorFilter(Current_Theme.getInt("custom_notification_on", ContextCompat.getColor(context, R.color.custom_notification_on)), PorterDuff.Mode.SRC_ATOP);
                        editor.putBoolean(temp1[0], true);
                    }
                    imageButton.setImageDrawable(drawable1);
                    editor.apply();
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }
}