package kos.progs.diary.adapters;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import kos.progs.diary.constructors.ConstructorFragmentViewPager;
import kos.progs.diary.constructors.ConstructorRecyclerView;
import kos.progs.diary.fragments.FragmentBells;
import kos.progs.diary.MainActivity;

import kos.progs.diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class AdapterNewPager extends PagerAdapter {
    private final Context context;
    private final String[] name;
    private final SharedPreferences settings;
    private final SharedPreferences Current_Theme;
    public final ArrayList<ConstructorFragmentViewPager> constructorFragmentViewPagerArrayList;
    public final HashMap<Integer, AdapterRecycler> recyclerAdapters = new HashMap<>();
    private final FloatingActionButton floatingActionButton;
    private final ArrayList<RecyclerView> scrollViewArrayList = new ArrayList<>();
    private final int[] scrolls;
    private final FragmentBells fragmentBells;

    public AdapterNewPager(ArrayList<ConstructorFragmentViewPager> constructorFragmentViewPagerArrayList, Context context, FloatingActionButton floatingActionButton, int[] scrolls) {
        this.constructorFragmentViewPagerArrayList = constructorFragmentViewPagerArrayList;
        this.context = context;
        this.scrolls = scrolls;
        settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
        Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
        if (settings.getBoolean("SaturdaySettings", true))
            name = new String[]{
                    context.getString(R.string.monday),
                    context.getString(R.string.tuesday),
                    context.getString(R.string.wednesday),
                    context.getString(R.string.thursday),
                    context.getString(R.string.friday),
                    context.getString(R.string.saturday)
            };
        else
            name = new String[]{
                    context.getString(R.string.monday),
                    context.getString(R.string.tuesday),
                    context.getString(R.string.wednesday),
                    context.getString(R.string.thursday),
                    context.getString(R.string.friday),
            };

        this.floatingActionButton = floatingActionButton;
        fragmentBells = (FragmentBells) ((MainActivity) context).fragmentManager.getFragments().get(0);
    }

    @Override
    public int getCount() {
        try {
            return constructorFragmentViewPagerArrayList.size();
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        try {
            return name[position];
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return "";
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        try {
            return view.equals(object);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return true;
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        final View view = LayoutInflater.from(context).inflate(R.layout.fragment_item_pager, container, false);
        try {
            ConstructorFragmentViewPager constructorFragmentViewPager = constructorFragmentViewPagerArrayList.get(position);
            constructorFragmentViewPager.view = view;
            final ArrayList<ConstructorRecyclerView> product = constructorFragmentViewPager.products;
            final RecyclerView recyclerView = view.findViewById(R.id.Zvonki_Recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            if (scrolls.length > position)
                recyclerView.post(() -> {
                    try {
                        recyclerView.setScrollY(scrolls[position]);
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });
            scrollViewArrayList.add(recyclerView);
            final AdapterRecycler adapter = new AdapterRecycler(product, context, position);
            recyclerView.setAdapter(adapter);
            recyclerAdapters.put(position, adapter);

            TextView textView = view.findViewById(R.id.nullZvon);
            if (product.size() != 0)
                textView.setVisibility(View.INVISIBLE);
            else
                textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            final SharedPreferences settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
            final String url = constructorFragmentViewPager.url;
            String[] ids = fragmentBells.currentWindow;
            if (!ids[0].equals("null")) {
                switch (ids[0]) {
                    case "edit":
                        if (ids[2].equals(String.valueOf(position)))
                            onEdit(Integer.parseInt(ids[1]), product, position, fragmentBells, adapter);
                        break;
                    case "deleteAll":
                        if (ids[1].equals(String.valueOf(position)))
                            onDeleteAll(fragmentBells);
                        break;
                    case "create":
                        if (ids[1].equals(String.valueOf(position)))
                            onCreateWrite(fragmentBells);
                        break;
                }
            }
            adapter.setOnItemClickListener(pos -> {
                try {
                    onEdit(pos, product, position, fragmentBells, adapter);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            }, pos -> {
                try {
                    StringBuilder stringBuffer = new StringBuilder();
                    final String textEquals;
                    final String[] helptime, helpkab;

                    helptime = product.get(pos).TextName.split(" - ");
                    helpkab = product.get(pos).TextBottom.split(", ");

                    textEquals = helptime[0] + "-" + helptime[1] + "=" + helpkab[0] + "=" + helpkab[1];

                    try {
                        FileInputStream read = context.openFileInput(url);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        String temp_read;
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            if (!temp_read.equals(textEquals))
                                stringBuffer.append(temp_read).append("\n");
                        }

                        bufferedReader.close();
                        reader.close();
                        read.close();
                    } catch (IOException ignored) {

                    }


                    try {
                        FileOutputStream write = context.openFileOutput(url, MODE_PRIVATE);

                        write.write(stringBuffer.toString().getBytes());
                        write.close();
                    } catch (IOException ignored) {

                    }

                    product.remove(pos);
                    if (settings.getBoolean("AnimationSettings", true))
                        adapter.notifyItemRemoved(pos);
                    else
                        adapter.notifyDataSetChanged();
                    if (product.size() == 0)
                        TextViewVisible(view);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            Drawable drawableFAB = MainActivity.getResources.getDrawable(R.drawable.ic_add_24px);
            Objects.requireNonNull(drawableFAB).setColorFilter(Current_Theme.getInt("custom_button_add_plus", ContextCompat.getColor(context, R.color.custom_button_add_plus)), PorterDuff.Mode.SRC_ATOP);
            floatingActionButton.setImageDrawable(drawableFAB);
            floatingActionButton.setOnLongClickListener(view12 -> {
                try {
                    onDeleteAll(fragmentBells);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
                return false;
            });
            floatingActionButton.setOnClickListener(view1 -> {
                try {
                    onCreateWrite(fragmentBells);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            container.addView(view, 0);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
        return view;

    }

    private void onCreateWrite(final FragmentBells fragmentBells) {
        try {
            fragmentBells.currentWindow = new String[]{"create", String.valueOf(fragmentBells.viewPager.getCurrentItem())};
            View promptsView = LayoutInflater.from(context).inflate(R.layout.prompt, null);
            final AlertDialog.Builder newzvonok = new AlertDialog.Builder(context);
            newzvonok.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));

            promptsView.findViewById(R.id.LinerZvon_item).setBackground(alertbackground);

            final TextView zvonokone = promptsView.findViewById(R.id.timeStart);
            final TextView zvonoktwo = promptsView.findViewById(R.id.timeEnd);
            final EditText Yrok = promptsView.findViewById(R.id.nameYrok);
            final EditText Kab = promptsView.findViewById(R.id.numKab);
            final Spinner spinner = promptsView.findViewById(R.id.spinner);

            List<String> choose = new ArrayList<>();
            choose.add(context.getString(R.string.classroomSchool));
            choose.add(context.getString(R.string.classroomUniversity));
            AdapterSpinner adapterSpinner = new AdapterSpinner(context, choose, true);
            spinner.setAdapter(adapterSpinner);
            spinner.getBackground().setColorFilter(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)), PorterDuff.Mode.SRC_ATOP);
            newzvonok.setCancelable(true);
            fragmentBells.alertDialogs.add(newzvonok.create());
            if (!DateFormat.is24HourFormat(context)) {
                zvonokone.setText("8:00 AM");
                zvonoktwo.setText("8:40 AM");
            }

            zvonokone.setOnClickListener(view -> {
                try {
                    timePickerAlert(zvonokone);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            zvonoktwo.setOnClickListener(view -> {
                try {
                    timePickerAlert(zvonoktwo);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
            ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonCancel.setOnClickListener(view -> {
                try {
                    fragmentBells.alertDialogs.get(0).hide();
                    fragmentBells.alertDialogs.remove(0);
                    fragmentBells.currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            promptsView.findViewById(R.id.viewBorder).setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));

            TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
            ButtonSave.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonSave.setOnClickListener(view -> {
                try {
                    String ZvonOne = zvonokone.getText().toString();
                    String ZvonTwo = zvonoktwo.getText().toString();
                    String NameYrok = Yrok.getText().toString();
                    String NumKab = Kab.getText().toString();

                    if (NameYrok.equals(""))
                        NameYrok = context.getString(R.string.lessonExample);
                    if (NumKab.equals("")) NumKab = "5";

                    boolean ic12h = !DateFormat.is24HourFormat(context);

                    String[] cheakAmPmOne = ZvonOne.split(" "), cheakAmPmTwo = ZvonTwo.split(" ");

                    String[] TimesOne = cheakAmPmOne[0].split(":"), TimesTwo = cheakAmPmTwo[0].split(":");
                    int TimeStartHour = Integer.parseInt(TimesOne[0]);
                    int TimeStartMin = Integer.parseInt(TimesOne[1]);
                    int TimeEndHour = Integer.parseInt(TimesTwo[0]);
                    int TimeEndMin = Integer.parseInt(TimesTwo[1]);

                    if (((TimeStartHour < TimeEndHour) || (TimeStartHour == TimeEndHour && TimeStartMin < TimeEndMin)) && !ic12h || ic12h && !(cheakAmPmOne[1].equals("PM") && cheakAmPmTwo[1].equals("AM")) && ((TimeStartHour < TimeEndHour || (cheakAmPmOne[1].equals("AM") && cheakAmPmTwo[1].equals("PM"))) || (TimeStartHour == TimeEndHour && TimeStartMin < TimeEndMin))) {

                        StringBuilder stringBuffer = new StringBuilder();

                        try {
                            boolean Zapic = true;
                            int NumString = 0;

                            int positionTek = fragmentBells.viewPager.getCurrentItem();


                            String writeTimes = ZvonOne + "-" + ZvonTwo + "=" + NameYrok + "=" + spinner.getSelectedItem() + " №" + NumKab;
                            String[] viewTimes = new String[]{ZvonOne + " - " + ZvonTwo, NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab};


                            try {
                                FileInputStream read = context.openFileInput(settings.getString("Day", "Monday.txt"));
                                InputStreamReader reader = new InputStreamReader(read);
                                BufferedReader bufferedReader = new BufferedReader(reader);

                                String temp_read;
                                String[] help, helpTimes, helpAmPMOne, helpAmPMTwo, helptimeOne, helptimeTwo;
                                int i = 0;

                                while ((temp_read = bufferedReader.readLine()) != null) {

                                    help = temp_read.split("=");
                                    helpTimes = help[0].split("-");
                                    helpAmPMOne = helpTimes[0].split(" ");
                                    helpAmPMTwo = helpTimes[1].split(" ");
                                    helptimeOne = helpAmPMOne[0].split(":");
                                    helptimeTwo = helpAmPMTwo[0].split(":");
                                    if ((Integer.parseInt(helptimeOne[0]) == TimeStartHour && Integer.parseInt(helptimeOne[1]) == TimeStartMin) && (Integer.parseInt(helptimeTwo[0]) == TimeEndHour && Integer.parseInt(helptimeTwo[1]) == TimeEndMin))
                                        if (!DateFormat.is24HourFormat(context)) {
                                            if (helpAmPMOne[1].equals(cheakAmPmOne[1]) && helpAmPMTwo[1].equals(cheakAmPmTwo[1]))
                                                throw new SQLException("Povtor");
                                        } else
                                            throw new SQLException("Povtor");

                                    if (!ic12h && Zapic && ((Integer.parseInt(helptimeTwo[0]) == TimeStartHour && Integer.parseInt(helptimeTwo[1]) > TimeStartMin) || (Integer.parseInt(helptimeTwo[0]) > TimeStartHour))) {
                                        stringBuffer.append(writeTimes).append(("\n")).append(temp_read).append(("\n"));
                                        Zapic = false;
                                        NumString = i;
                                    } else if (ic12h && Zapic) {
                                        if ((helpAmPMOne[1].equals(cheakAmPmOne[1]) && (Integer.parseInt(helptimeOne[0]) > TimeStartHour || (Integer.parseInt(helptimeTwo[1]) > TimeStartMin && Integer.parseInt(helptimeTwo[0]) == TimeStartHour))) || (helpAmPMOne[1].equals("PM") && cheakAmPmOne[1].equals("AM"))) {
                                            stringBuffer.append(writeTimes).append(("\n")).append(temp_read).append(("\n"));
                                            Zapic = false;
                                            NumString = i;
                                        }
                                    } else {
                                        stringBuffer.append(temp_read).append(("\n"));
                                        i = i + 1;
                                    }
                                }
                            } catch (IOException ignored) {

                            }

                            ConstructorFragmentViewPager constructorFragmentViewPager = constructorFragmentViewPagerArrayList.get(positionTek);

                            if (Zapic) {
                                stringBuffer.append(writeTimes);
                                NumString = constructorFragmentViewPager.products.size();
                            }
                            try {
                                FileOutputStream write = context.openFileOutput(settings.getString("Day", "Monday.txt"), MODE_PRIVATE);
                                String temp_write = stringBuffer.toString();

                                write.write(temp_write.getBytes());
                                write.close();
                            } catch (IOException ignored) {

                            }
                            if (constructorFragmentViewPager.products.size() == 0)
                                TextViewInisible(constructorFragmentViewPager.view);
                            constructorFragmentViewPager.products.add(NumString, new ConstructorRecyclerView(viewTimes[0], viewTimes[1]));
                            if (settings.getBoolean("AnimationSettings", true))
                                Objects.requireNonNull(recyclerAdapters.get(positionTek)).notifyItemInserted(NumString);
                            else
                                Objects.requireNonNull(recyclerAdapters.get(positionTek)).notifyDataSetChanged();

                            fragmentBells.alertDialogs.get(0).hide();
                            fragmentBells.alertDialogs.remove(0);
                            fragmentBells.currentWindow = new String[]{"null"};

                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                            Calendar calendar = Calendar.getInstance(), cal = Calendar.getInstance();

                            calendar.set(Calendar.YEAR, cal.get(Calendar.YEAR));
                            calendar.set(Calendar.MONTH, cal.get(Calendar.MONTH));
                            calendar.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
                            if (DateFormat.is24HourFormat(context))
                                calendar.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
                            else
                                calendar.set(Calendar.HOUR, cal.get(Calendar.HOUR));
                            calendar.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            alarmManager.cancel(((MainActivity) context).notifyIntent);
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, ((MainActivity) context).notifyIntent);

                        } catch (SQLException povtor) {
                            MainActivity.ToastMakeText(context, context.getString(R.string.timeSpan));
                        }
                    } else
                        MainActivity.ToastMakeText(context, context.getString(R.string.timeSpanStartEnd));
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            TextView Title = promptsView.findViewById(R.id.textView2);
            Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            Title = promptsView.findViewById(R.id.text_time_start);
            Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            Title = promptsView.findViewById(R.id.text_time_end);
            Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            Title = promptsView.findViewById(R.id.text_mesto);
            Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            Title = promptsView.findViewById(R.id.text_num_kab);
            Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            Title = promptsView.findViewById(R.id.text_pred_name);
            Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            zvonokone.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            zvonoktwo.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            Yrok.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            Kab.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MainActivity.setCursorColorsQ(Yrok, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorColorsQ(Kab, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            } else {
                MainActivity.setCursorColor(Yrok, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorColor(Kab, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            }
            Yrok.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
            Kab.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
            Objects.requireNonNull(fragmentBells.alertDialogs.get(0).getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            fragmentBells.alertDialogs.get(0).show();
            fragmentBells.alertDialogs.get(0).setOnCancelListener(dialog -> {
                try {
                    fragmentBells.alertDialogs.remove(0);
                    fragmentBells.currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    private void onDeleteAll(final FragmentBells fragmentBells) {
        try {
            fragmentBells.currentWindow = new String[]{"deleteAll", String.valueOf(fragmentBells.viewPager.getCurrentItem())};
            final View promptsView = LayoutInflater.from(context).inflate(R.layout.alert_delete_dnewnik, null);
            final AlertDialog.Builder Delete = new AlertDialog.Builder(context);
            Delete.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.alert_delete).setBackground(alertbackground);

            fragmentBells.alertDialogs.add(Delete.create());

            TextView textTitle = promptsView.findViewById(R.id.title_alert);
            textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            textTitle.setText(context.getString(R.string.deleting));

            TextView textBottomTitle = promptsView.findViewById(R.id.title_bottom_alert);
            textBottomTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textBottomTitle.setText(context.getString(R.string.deleteAllLesson));

            final TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
            ButtonCancel.setText(context.getString(R.string.cancel));
            ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonCancel.setOnClickListener(view -> {
                try {
                    fragmentBells.alertDialogs.get(0).hide();
                    fragmentBells.alertDialogs.remove(0);
                    fragmentBells.currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            final TextView ButtonALl = promptsView.findViewById(R.id.button_three_alert);
            ButtonALl.setOnClickListener(viewClick -> {
                try {
                    String[] day = MainActivity.getResources.getStringArray(R.array.DayTxt);
                    File outFile;
                    for (String s : day) {
                        outFile = new File(context.getFilesDir() + "/" + s);
                        if (outFile.exists()) {
                            outFile.delete();
                        }
                    }
                    fragmentBells.new DeleteAll().execute();

                    fragmentBells.alertDialogs.get(0).hide();
                    fragmentBells.alertDialogs.remove(0);
                    fragmentBells.currentWindow = new String[]{"null"};
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    Calendar calendar = Calendar.getInstance(), cal = Calendar.getInstance();

                    calendar.set(Calendar.YEAR, cal.get(Calendar.YEAR));
                    calendar.set(Calendar.MONTH, cal.get(Calendar.MONTH));
                    calendar.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
                    if (DateFormat.is24HourFormat(context))
                        calendar.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
                    else
                        calendar.set(Calendar.HOUR, cal.get(Calendar.HOUR));
                    calendar.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    alarmManager.cancel(((MainActivity) context).notifyIntent);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, ((MainActivity) context).notifyIntent);

                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            ButtonALl.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonALl.setText(context.getString(R.string.deleteAllDayLesson));
            promptsView.findViewById(R.id.viewBorderOne).setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));
            promptsView.findViewById(R.id.viewBorderTwo).setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));

            final TextView ButtonOne = promptsView.findViewById(R.id.button_two_alert);
            ButtonOne.setOnClickListener(viewClick -> {
                try {
                    int positionTek = fragmentBells.viewPager.getCurrentItem();

                    File outFile = new File(context.getFilesDir() + "/" + settings.getString("Day", "Monday.txt"));
                    if (outFile.exists()) {
                        outFile.delete();
                    }
                    if (settings.getBoolean("AnimationSettings", true))
                        new AnimationDel().execute(positionTek);
                    else {
                        constructorFragmentViewPagerArrayList.get(positionTek).products.clear();
                        Objects.requireNonNull(recyclerAdapters.get(positionTek)).notifyDataSetChanged();
                    }

                    fragmentBells.alertDialogs.get(0).hide();
                    fragmentBells.alertDialogs.remove(0);
                    fragmentBells.currentWindow = new String[]{"null"};
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    Calendar calendar = Calendar.getInstance(), cal = Calendar.getInstance();

                    calendar.set(Calendar.YEAR, cal.get(Calendar.YEAR));
                    calendar.set(Calendar.MONTH, cal.get(Calendar.MONTH));
                    calendar.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
                    if (DateFormat.is24HourFormat(context))
                        calendar.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
                    else
                        calendar.set(Calendar.HOUR, cal.get(Calendar.HOUR));
                    calendar.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    alarmManager.cancel(((MainActivity) context).notifyIntent);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, ((MainActivity) context).notifyIntent);

                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            ButtonOne.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonOne.setText(context.getString(R.string.deleteOneDayLesson));

            Objects.requireNonNull(fragmentBells.alertDialogs.get(0).getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            fragmentBells.alertDialogs.get(0).show();
            Delete.setOnCancelListener(dialog -> {
                try {
                    fragmentBells.currentWindow = new String[]{"null"};
                    fragmentBells.alertDialogs.remove(0);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    private void onEdit(final int pos, final ArrayList<ConstructorRecyclerView> product, int position, final FragmentBells fragmentBells, final AdapterRecycler adapter) {
        try {
            fragmentBells.currentWindow = new String[]{"edit", String.valueOf(pos), String.valueOf(position)};
            final String textTime = product.get(pos).TextName;
            String textBottom = product.get(pos).TextBottom;

            View promptsView = LayoutInflater.from(context).inflate(R.layout.prompt, null);
            final AlertDialog.Builder newzvonok = new AlertDialog.Builder(context);
            newzvonok.setView(promptsView);

            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));

            promptsView.findViewById(R.id.LinerZvon_item).setBackground(alertbackground);

            final TextView zvonokone = promptsView.findViewById(R.id.timeStart);
            final TextView zvonoktwo = promptsView.findViewById(R.id.timeEnd);
            final TextView textView = promptsView.findViewById(R.id.textView2);
            final EditText Yrok = promptsView.findViewById(R.id.nameYrok);
            final EditText Kab = promptsView.findViewById(R.id.numKab);
            final String[] help, helpop, helpyrok, timeOneAM, timeTwoAM;

            help = textTime.split(" - ");
            timeOneAM = help[0].split(" ");
            timeTwoAM = help[1].split(" ");

            zvonokone.setText(help[0]);
            zvonoktwo.setText(help[1]);

            final boolean ic12h = timeOneAM.length == 2 && timeTwoAM.length == 2;

            zvonokone.setOnClickListener(view -> {
                try {
                    timePickerAlert(zvonokone);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            zvonoktwo.setOnClickListener(view -> {
                try {
                    timePickerAlert(zvonoktwo);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });


            helpop = textBottom.split(", ");
            Yrok.setText(helpop[0]);
            helpyrok = helpop[1].split(" №");
            Kab.setText(helpyrok[1]);
            final Spinner spinner = promptsView.findViewById(R.id.spinner);
            List<String> choose = new ArrayList<>();
            if (helpyrok[0].equals(context.getString(R.string.classroomSchool))) {
                textView.setText(context.getString(R.string.editLesson));
                choose.add(context.getString(R.string.classroomSchool));
                choose.add(context.getString(R.string.classroomUniversity));
            } else {
                textView.setText(context.getString(R.string.editCouple));
                choose.add(context.getString(R.string.classroomUniversity));
                choose.add(context.getString(R.string.classroomSchool));
            }
            textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

            AdapterSpinner adapterSpinner = new AdapterSpinner(context, choose, true);
            spinner.setAdapter(adapterSpinner);
            spinner.getBackground().setColorFilter(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)), PorterDuff.Mode.SRC_ATOP);
            newzvonok.setCancelable(true);
            fragmentBells.alertDialogs.add(newzvonok.create());

            promptsView.findViewById(R.id.viewBorder).setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));

            TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
            ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonCancel.setOnClickListener(view -> {
                fragmentBells.alertDialogs.get(0).hide();
                fragmentBells.alertDialogs.remove(0);
                fragmentBells.currentWindow = new String[]{"null"};
            });

            TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
            ButtonSave.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonSave.setOnClickListener(view -> {
                try {
                    String ZvonOne = zvonokone.getText().toString();
                    String ZvonTwo = zvonoktwo.getText().toString();
                    String NameYrok = Yrok.getText().toString();
                    String NumKab = Kab.getText().toString();

                    if (NameYrok.equals(""))
                        NameYrok = context.getString(R.string.lessonExample);
                    if (NumKab.equals("")) NumKab = "5";

                    String[] cheakAmPmOne = ZvonOne.split(" "), cheakAmPmTwo = ZvonTwo.split(" ");

                    String[] TimesOne = cheakAmPmOne[0].split(":"), TimesTwo = cheakAmPmTwo[0].split(":");
                    int TimeStartHour = Integer.parseInt(TimesOne[0]);
                    int TimeStartMin = Integer.parseInt(TimesOne[1]);
                    int TimeEndHour = Integer.parseInt(TimesTwo[0]);
                    int TimeEndMin = Integer.parseInt(TimesTwo[1]);


                    if (((TimeStartHour < TimeEndHour) || (TimeStartHour == TimeEndHour && TimeStartMin < TimeEndMin)) && !ic12h || ic12h && !(cheakAmPmOne[1].equals("PM") && cheakAmPmTwo[1].equals("AM")) && ((TimeStartHour < TimeEndHour || (cheakAmPmOne[1].equals("AM") && cheakAmPmTwo[1].equals("PM"))) || (TimeStartHour == TimeEndHour && TimeStartMin < TimeEndMin))) {

                        StringBuilder stringBuffer = new StringBuilder();

                        try {
                            boolean Zapic = true;
                            int NumString = 0;

                            int positionTek = fragmentBells.viewPager.getCurrentItem();

                            String writeTimes = ZvonOne + "-" + ZvonTwo + "=" + NameYrok + "=" + spinner.getSelectedItem() + " №" + NumKab;
                            String[] viewTimes = new String[]{ZvonOne + " - " + ZvonTwo, NameYrok + ", " + spinner.getSelectedItem() + " №" + NumKab};


                            try {
                                FileInputStream read = context.openFileInput(settings.getString("Day", "Monday.txt"));
                                InputStreamReader reader = new InputStreamReader(read);
                                BufferedReader bufferedReader = new BufferedReader(reader);

                                String temp_read;
                                String[] helpLocal, helpTimes, helpAmPMOne, helpAmPMTwo, helptimeOne, helptimeTwo;
                                int i = 0;

                                while ((temp_read = bufferedReader.readLine()) != null) {
                                    helpLocal = temp_read.split("=");
                                    helpTimes = helpLocal[0].split("-");
                                    helpAmPMOne = helpTimes[0].split(" ");
                                    helpAmPMTwo = helpTimes[1].split(" ");
                                    helptimeOne = helpAmPMOne[0].split(":");
                                    helptimeTwo = helpAmPMTwo[0].split(":");
                                    if (temp_read.equals(help[0] + "-" + help[1] + "=" + helpop[0] + "=" + helpyrok[0] + " №" + helpyrok[1]))
                                        continue;
                                    if ((Integer.parseInt(helptimeOne[0]) == TimeStartHour && Integer.parseInt(helptimeOne[1]) == TimeStartMin) && (Integer.parseInt(helptimeTwo[0]) == TimeEndHour && Integer.parseInt(helptimeTwo[1]) == TimeEndMin))
                                        if (!DateFormat.is24HourFormat(context)) {
                                            if (helpAmPMOne[1].equals(cheakAmPmOne[1]) && helpAmPMTwo[1].equals(cheakAmPmTwo[1]))
                                                throw new SQLException("Povtor");
                                        } else
                                            throw new SQLException("Povtor");

                                    if (!ic12h && Zapic && (Integer.parseInt(helptimeTwo[0]) == TimeStartHour && Integer.parseInt(helptimeTwo[1]) > TimeStartMin)) {
                                        stringBuffer.append(writeTimes).append(("\n")).append(temp_read).append(("\n"));
                                        Zapic = false;
                                        NumString = i;
                                    } else if (ic12h && Zapic) {
                                        if ((helpAmPMOne[1].equals(cheakAmPmOne[1]) && (Integer.parseInt(helptimeOne[0]) > TimeStartHour || (Integer.parseInt(helptimeTwo[1]) > TimeStartMin && Integer.parseInt(helptimeTwo[0]) == TimeStartHour))) || (helpAmPMOne[1].equals("PM") && cheakAmPmOne[1].equals("AM"))) {
                                            stringBuffer.append(writeTimes).append(("\n")).append(temp_read).append(("\n"));
                                            Zapic = false;
                                            NumString = i;
                                        }
                                    } else {
                                        stringBuffer.append(temp_read).append(("\n"));
                                        i = i + 1;
                                    }
                                }
                            } catch (IOException ignored) {

                            }
                            if (Zapic) {
                                stringBuffer.append(writeTimes);
                                NumString = constructorFragmentViewPagerArrayList.get(positionTek).products.size();
                            }
                            try {
                                FileOutputStream write = context.openFileOutput(settings.getString("Day", "Monday.txt"), MODE_PRIVATE);
                                String temp_write = stringBuffer.toString();

                                write.write(temp_write.getBytes());
                                write.close();
                            } catch (IOException ignored) {

                            }
                            product.get(pos).changeText(viewTimes[0], viewTimes[1]);
                            adapter.onMove(pos, NumString);
                            fragmentBells.currentWindow = new String[]{"null"};
                            fragmentBells.alertDialogs.get(0).hide();
                            fragmentBells.alertDialogs.remove(0);

                        } catch (SQLException povtor) {
                            MainActivity.ToastMakeText(context, context.getString(R.string.timeSpan));
                        }
                    } else
                        MainActivity.ToastMakeText(context, context.getString(R.string.timeSpanStartEnd));
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            //VISUAL
            TextView Title = promptsView.findViewById(R.id.text_time_start);
            Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            Title = promptsView.findViewById(R.id.text_time_end);
            Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            Title = promptsView.findViewById(R.id.text_mesto);
            Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            Title = promptsView.findViewById(R.id.text_num_kab);
            Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            Title = promptsView.findViewById(R.id.text_pred_name);
            Title.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            zvonokone.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            zvonoktwo.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            Yrok.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            Kab.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MainActivity.setCursorColorsQ(Yrok, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorColorsQ(Kab, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            } else {
                MainActivity.setCursorColor(Yrok, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                MainActivity.setCursorColor(Kab, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            }
            Yrok.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
            Kab.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
            Objects.requireNonNull(fragmentBells.alertDialogs.get(0).getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            fragmentBells.alertDialogs.get(0).show();
            fragmentBells.alertDialogs.get(0).setOnCancelListener(dialog -> {
                try {
                    fragmentBells.currentWindow = new String[]{"null"};
                    fragmentBells.alertDialogs.remove(0);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public int[] getScrolls() {
        try {
            int[] scrollInt = new int[scrollViewArrayList.size()];
            for (int i = 0; i < scrollViewArrayList.size(); i++) {
                scrollInt[i] = scrollViewArrayList.get(i).getScrollY();
            }
            return scrollInt;
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return new int[]{0};
        }
    }

    private void timePickerAlert(final TextView textView) {
        try {
            final View promptsView = LayoutInflater.from(context).inflate(R.layout.timepicker_layout, null);
            AlertDialog.Builder timepicker = new AlertDialog.Builder(context);
            timepicker.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.timePicker_liner).setBackground(alertbackground);

            fragmentBells.alertDialogs.add(1, timepicker.create());
            final TimePicker timePicker = promptsView.findViewById(R.id.timePicker);
            String[] cheakAmPm = textView.getText().toString().split(" "), times = cheakAmPm[0].split(":");
            final boolean is24 = !(cheakAmPm.length == 2);
            timePicker.setIs24HourView(is24);
            int hourCurrent = Integer.parseInt(times[0]);
            if (!is24 && cheakAmPm[1].equals("PM")) {
                hourCurrent = hourCurrent + 12;
            }
            timePicker.setCurrentHour(hourCurrent);
            timePicker.setCurrentMinute(Integer.parseInt(times[1]));

            TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
            ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonCancel.setOnClickListener(view -> {
                try {
                    fragmentBells.alertDialogs.get(1).hide();
                    fragmentBells.currentWindow = new String[]{"null"};
                    fragmentBells.alertDialogs.remove(1);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            TextView ButtonOk = promptsView.findViewById(R.id.button_two_alert);
            ButtonOk.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonOk.setOnClickListener(view -> {
                try {
                    String am_pm;
                    int hour = timePicker.getCurrentHour();
                    String minute = String.valueOf(timePicker.getCurrentMinute());
                    if (Integer.parseInt(minute) < 10)
                        minute = "0" + minute;
                    if (!is24) {
                        if (hour <= 12)
                            am_pm = "AM";
                        else {
                            am_pm = "PM";
                            hour = hour - 12;
                        }

                        textView.setText(hour + ":" + minute + " " + am_pm);
                    } else textView.setText(hour + ":" + minute);
                    fragmentBells.alertDialogs.get(1).hide();
                    fragmentBells.currentWindow = new String[]{"null"};
                    fragmentBells.alertDialogs.remove(1);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            Objects.requireNonNull(fragmentBells.alertDialogs.get(1).getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            fragmentBells.alertDialogs.get(1).show();
            fragmentBells.alertDialogs.get(1).setOnCancelListener(dialog -> {
                try {
                    fragmentBells.currentWindow = new String[]{"null"};
                    fragmentBells.alertDialogs.remove(1);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    private void TextViewVisible(View view) {
        try {
            TextView textView = view.findViewById(R.id.nullZvon);
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    private void TextViewInisible(View view) {
        try {
            TextView textView = view.findViewById(R.id.nullZvon);
            textView.setVisibility(View.INVISIBLE);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

     class AnimationDel extends AsyncTask<Integer, Integer, Void> {
        int i;

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            try {
                Objects.requireNonNull(recyclerAdapters.get(i)).notifyItemRemoved(values[0]);
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                TextViewVisible(constructorFragmentViewPagerArrayList.get(i).view);
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(Integer... voids) {
            try {
                i = voids[0];
                ArrayList<?> arrayList = constructorFragmentViewPagerArrayList.get(i).products;
                for (int l = arrayList.size() - 1; l >= 0; l--) {
                    arrayList.remove(l);
                    publishProgress(l);
                    Thread.sleep(100);
                }
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        try {
            container.removeView((View) object);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }
}