package kos.progs.diary.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import kos.progs.diary.constructors.ConstructorSetCheck;
import kos.progs.diary.constructors.ConstructorThemeRecycler;
import kos.progs.diary.MainActivity;
import kos.progs.diary.R;
import kos.progs.diary.adapters.AdapterRecyclerTheme;
import kos.progs.diary.adapters.AdapterSpinner;
import kos.progs.diary.onBackPressed;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class FragmentSettings extends Fragment implements onBackPressed {
    private Context context;
    private SharedPreferences settings,
            Current_Theme;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private FrameLayout linearLayoutBottom;
    private SharedPreferences.Editor editor;
    private ArrayList<ConstructorThemeRecycler> constrRecyclerViewArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AdapterRecyclerTheme adapter;
    private NestedScrollView nestedScrollView;
    private int scroll = 0;
    public String[] currentWindow = new String[]{"null"};
    public AlertDialog alertDialog = null;
    public RelativeLayout view;
    public View contentTheme;

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (alertDialog != null)
                alertDialog.dismiss();
            Bundle bundle = ((MainActivity) context).getBundles.get(4);
            try {
                bundle.putInt("scroll", nestedScrollView.getScrollY());
            } catch (Exception e) {
                bundle.putInt("scroll", scroll);
            }
            bundle.putParcelableArrayList("list", constrRecyclerViewArrayList);
            bundle.putStringArray("currentWindow", currentWindow);
            bottomSheetBehavior.removeBottomSheetCallback(((MainActivity) context).bottomSheetCallback);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        try {
            try {
                outState.putInt("scroll", nestedScrollView.getScrollY());
            } catch (Exception e) {
                outState.putInt("scroll", scroll);
            }
            outState.putParcelableArrayList("list", constrRecyclerViewArrayList);
            outState.putStringArray("currentWindow", currentWindow);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            Bundle b;
            if (savedInstanceState == null)
                b = getArguments();
            else
                b = savedInstanceState;
            view = (RelativeLayout) inflater.inflate(R.layout.fragment_nastroiki, container, false);


            settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
            Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
            editor = settings.edit();
            try {
                ((MainActivity) context).menuAdapter.onCheck(4, settings.getInt("Fragment", 0));
                editor.putInt("Fragment", 4).apply();
            } catch (NullPointerException ignored) {
            }
            if (Objects.requireNonNull(b).size() != 0) {
                scroll = b.getInt("scroll");
                currentWindow = b.getStringArray("currentWindow");
                constrRecyclerViewArrayList = b.getParcelableArrayList("list");
            }
            new onStart().execute();
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }

        return view;
    }

    @Override
    public boolean onBackPressed() {
        try {
            if (alertDialog != null) {
                alertDialog.hide();
                alertDialog = null;
                return true;
            } else if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                return true;
            } else
                return false;
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return true;
        }
    }

    class onStart extends AsyncTask<Void, ConstructorSetCheck, Void> {
        ProgressBar progressBar;
        View content;

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                ((MainActivity) context).toolbar.setTitle(getString(R.string.settings));
                progressBar = view.findViewById(R.id.progress);
                progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);
                content = LayoutInflater.from(context).inflate(R.layout.content_settings, null);
                contentTheme = LayoutInflater.from(context).inflate(R.layout.content_create_custom_theme, null);
                linearLayoutBottom = ((MainActivity) context).linearLayoutBottom;
                linearLayoutBottom.removeAllViews();
                bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBottom);
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                super.onPostExecute(aVoid);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                view.removeView(progressBar);
                view.addView(content, layoutParams);
                linearLayoutBottom.addView(contentTheme);
                contentTheme.findViewById(R.id.save_theme).post(() -> bottomSheetBehavior.setPeekHeight(contentTheme.findViewById(R.id.save_theme).getHeight() + 20 * MainActivity.dpSize));
                linearLayoutBottom.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                if (!currentWindow[0].equals("null")) {
                    switch (currentWindow[0]) {
                        case "edit":
                            onEdit((TextView) content.findViewById(Integer.parseInt(currentWindow[1])));
                            break;
                        case "editGrades":
                            onClickEdit(content.findViewById(R.id.quarters_edit));
                            break;
                        case "editTheme":
                            ((MainActivity) Objects.requireNonNull(context)).EditTheme(constrRecyclerViewArrayList.get(Integer.parseInt(currentWindow[1])).colors, Integer.parseInt(currentWindow[1]));
                            new EditTheme().execute(constrRecyclerViewArrayList.get(Integer.parseInt(currentWindow[1])).colors);
                            break;
                        case "clear":
                            ((MainActivity) context).ClickClear(content.findViewById(Integer.parseInt(currentWindow[1])));
                            break;
                        case "createTheme":
                            ((MainActivity) context).onCreateTheme(bottomSheetBehavior, linearLayoutBottom);
                            break;
                        case "fileChoose":
                            ((MainActivity) context).fileChooser();
                            break;
                    }
                }
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected void onProgressUpdate(ConstructorSetCheck... values) {
            try {
                super.onProgressUpdate(values);
                values[0].aSwitch.setChecked(values[0].icChecked);
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //Visual
                nestedScrollView = content.findViewById(R.id.rootScroll);
                nestedScrollView.post(() -> {
                    try {
                        nestedScrollView.setScrollY(scroll);
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });
                final int cardColor = Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)), textLightcolor = Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)), textDarkcolor = Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)), borderColor = Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)), borderThemeColor = Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme));
                CardView cardVisual = content.findViewById(R.id.card_students);
                cardVisual.setCardBackgroundColor(cardColor);
                cardVisual = content.findViewById(R.id.card_timetables);
                cardVisual.setCardBackgroundColor(cardColor);
                cardVisual = content.findViewById(R.id.card_grades);
                cardVisual.setCardBackgroundColor(cardColor);
                cardVisual = content.findViewById(R.id.card_settings);
                cardVisual.setCardBackgroundColor(cardColor);
                cardVisual = content.findViewById(R.id.card_general_setting);
                cardVisual.setCardBackgroundColor(cardColor);
                cardVisual = content.findViewById(R.id.card_themes);
                cardVisual.setCardBackgroundColor(cardColor);
                cardVisual = content.findViewById(R.id.card_create_theme_nastroiki);
                cardVisual.setCardBackgroundColor(cardColor);
                cardVisual = content.findViewById(R.id.card_clear);
                cardVisual.setCardBackgroundColor(cardColor);
                cardVisual = content.findViewById(R.id.card_data);
                cardVisual.setCardBackgroundColor(cardColor);
                cardVisual = contentTheme.findViewById(R.id.save_theme);
                cardVisual.setCardBackgroundColor(cardColor);

                TextView textVisual = content.findViewById(R.id.title_students);
                textVisual.setTextColor(textDarkcolor);
                textVisual = content.findViewById(R.id.bottom_title_students);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.title_timetables);
                textVisual.setTextColor(textDarkcolor);
                textVisual = content.findViewById(R.id.bottom_title_timetables_not);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_title_timetables_saturday);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.title_grades);
                textVisual.setTextColor(textDarkcolor);
                textVisual = content.findViewById(R.id.bottom_title_grades);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_grades_Examination);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_grades_Year);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_grades_End);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.title_settings);
                textVisual.setTextColor(textDarkcolor);
                textVisual = content.findViewById(R.id.bottom_title_settings);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.title_general_setting);
                textVisual.setTextColor(textDarkcolor);
                textVisual = content.findViewById(R.id.bottom_title_general_setting_what);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_title_general_setting_anim);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_title_general_setting_padding);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_title_general_setting_dialog_stroke);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_title_general_setting_width_stroke);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_title_white_theme);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.title_themes);
                textVisual.setTextColor(textDarkcolor);
                textVisual = content.findViewById(R.id.bottom_title_dark_theme);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.Card_create);
                textVisual.setTextColor(textDarkcolor);
                textVisual = contentTheme.findViewById(R.id.title_save_theme);
                textVisual.setTextColor(textDarkcolor);
                textVisual = content.findViewById(R.id.title_clear);
                textVisual.setTextColor(textDarkcolor);
                textVisual = content.findViewById(R.id.title_data);
                textVisual.setTextColor(textDarkcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_name);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_title_general_setting_fragment_default);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.text_day_ocenki);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_title_quarter);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.quarters_edit);
                textVisual.setTextColor(textLightcolor);


                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_icon);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_border);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_back);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_bar);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_text_bar);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_selected_section);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_text_light);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_text_dark);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_text_hint);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_cursor);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_card);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_bottom_border);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_add);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_add_plus);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_button_act);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_arrow);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_loading);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_not_conf);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_table);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_not_on);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_not_off);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_switch_on);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_switch_off);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_choose_back);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_choose_border);
                textVisual.setTextColor(textLightcolor);
                textVisual = contentTheme.findViewById(R.id.bottom_title_theme_audio);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_title_dnew);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_title_spravka);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_title_zvon);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_title_ychit);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_title_ocenki);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.bottom_title_nastroiki);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.import_data);
                textVisual.setTextColor(textLightcolor);
                textVisual = content.findViewById(R.id.export_data);
                textVisual.setTextColor(textLightcolor);

                content.findViewById(R.id.border_one).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_two).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_three).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_four).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_five).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_six).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_seven).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_eight).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_nine).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_ten).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_eleven).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_twelve).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_thirteen).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_fourteen).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_fifteen).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_sixteen).setBackgroundColor(borderColor);
                content.findViewById(R.id.border_seventeen).setBackgroundColor(borderColor);

                content.findViewById(R.id.border_white_theme).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_icon).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_border).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_back).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_bar).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_bar_text).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_selected_section).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_text_light).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_text_dark).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_text_hint).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_cursor).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_card).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_bottom_border).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_add).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_add_plus).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_button_act).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_arrow).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_progress).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_not_conf).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_table).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_not_on).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_not_off).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_switch_on).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_switch_off).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_choose_back).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_choose_border).setBackgroundColor(borderThemeColor);
                contentTheme.findViewById(R.id.border_theme_audio).setBackgroundColor(borderThemeColor);


                EditText editVisual = contentTheme.findViewById(R.id.custom_name);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MainActivity.setCursorColorsQ(editVisual, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                } else {
                    MainActivity.setCursorColor(editVisual, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                }
                editVisual.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
                editVisual.setTextColor(textLightcolor);

                final TextView textDpColor = content.findViewById(R.id.dpText);
                textDpColor.setTextColor(textLightcolor);
                textDpColor.setText(settings.getInt("dpSizeSettings", 10) + "dp");
                textDpColor.setOnClickListener(v -> {
                    try {
                        onEdit(textDpColor);
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                content.findViewById(R.id.bottom_title_students).setOnClickListener(v -> {
                    try {
                        onEdit(textDpColor);
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                SwitchCompat switchNotify = content.findViewById(R.id.switchNotify);
                setSwitchColor(switchNotify, context);
                publishProgress(new ConstructorSetCheck(switchNotify, settings.getBoolean("notifySettings", true)));
                switchNotify.setOnCheckedChangeListener((buttonView, b) -> {
                    try {
                        editor.putBoolean("notifySettings", b);
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        if (b) {

                            Calendar calendar = Calendar.getInstance(), cal = Calendar.getInstance();

                            calendar.set(Calendar.YEAR, cal.get(Calendar.YEAR));
                            calendar.set(Calendar.MONTH, cal.get(Calendar.MONTH));
                            calendar.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
                            if (DateFormat.is24HourFormat(context))
                                calendar.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
                            else
                                calendar.set(Calendar.HOUR, cal.get(Calendar.HOUR));
                            calendar.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
                            calendar.set(Calendar.SECOND, cal.get(Calendar.SECOND));
                            calendar.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));

                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, ((MainActivity) context).notifyIntent);
                        } else
                            alarmManager.cancel(((MainActivity) context).notifyIntent);

                        editor.apply();
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                content.findViewById(R.id.bottom_title_timetables_not).setOnClickListener(v -> {
                    try {
                        switchNotify.setChecked(!settings.getBoolean("notifySettings", true));
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                SwitchCompat switchWhat = content.findViewById(R.id.switchWhat);
                setSwitchColor(switchWhat, context);
                publishProgress(new ConstructorSetCheck(switchWhat, settings.getBoolean("whatSettings", false)));
                switchWhat.setOnCheckedChangeListener((compoundButton, b) -> {
                    try {
                        editor.putBoolean("whatSettings", b);
                        editor.apply();
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                content.findViewById(R.id.bottom_title_general_setting_what).setOnClickListener(v -> switchWhat.setChecked(!settings.getBoolean("whatSettings", false)));

                SwitchCompat switchPoved = content.findViewById(R.id.switchPoved);
                setSwitchColor(switchPoved, context);
                publishProgress(new ConstructorSetCheck(switchPoved, settings.getBoolean("PovedSettings", true)));
                switchPoved.setOnCheckedChangeListener((compoundButton, b) -> {
                    try {
                        editor.putBoolean("PovedSettings", b);
                        editor.apply();
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                content.findViewById(R.id.bottom_title_grades).setOnClickListener(v -> switchPoved.setChecked(!settings.getBoolean("PovedSettings", true)));

                SwitchCompat switchExamination = content.findViewById(R.id.icgradesExamination);
                setSwitchColor(switchExamination, context);
                publishProgress(new ConstructorSetCheck(switchExamination, settings.getBoolean("icgradesExamination", true)));
                switchExamination.setOnCheckedChangeListener((compoundButton, b) -> {
                    try {
                        editor.putBoolean("icgradesExamination", b);
                        editor.apply();
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                content.findViewById(R.id.bottom_grades_Examination).setOnClickListener(v -> switchExamination.setChecked(!settings.getBoolean("icgradesExamination", true)));

                SwitchCompat switchYear = content.findViewById(R.id.icgradesYear);
                setSwitchColor(switchYear, context);
                publishProgress(new ConstructorSetCheck(switchYear, settings.getBoolean("icgradesYear", true)));
                switchYear.setOnCheckedChangeListener((compoundButton, b) -> {
                    try {
                        editor.putBoolean("icgradesYear", b);
                        editor.apply();
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                content.findViewById(R.id.bottom_grades_Year).setOnClickListener(v -> switchYear.setChecked(!settings.getBoolean("icgradesYear", true)));

                SwitchCompat icgradesEnd = content.findViewById(R.id.icgradesEnd);
                setSwitchColor(icgradesEnd, context);
                publishProgress(new ConstructorSetCheck(icgradesEnd, settings.getBoolean("icgradesEnd", true)));
                icgradesEnd.setOnCheckedChangeListener((compoundButton, b) -> {
                    try {
                        editor.putBoolean("icgradesEnd", b);
                        editor.apply();
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                content.findViewById(R.id.bottom_grades_End).setOnClickListener(v -> icgradesEnd.setChecked(!settings.getBoolean("icgradesEnd", true)));

                SwitchCompat switchAnimations = content.findViewById(R.id.switchAnim);
                setSwitchColor(switchAnimations, context);
                publishProgress(new ConstructorSetCheck(switchAnimations, settings.getBoolean("AnimationSettings", true)));
                switchAnimations.setOnCheckedChangeListener((compoundButton, b) -> {
                    try {
                        editor.putBoolean("AnimationSettings", b);
                        editor.apply();
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                content.findViewById(R.id.bottom_title_general_setting_anim).setOnClickListener(v -> switchAnimations.setChecked(!settings.getBoolean("AnimationSettings", true)));

                SwitchCompat switchPadding = content.findViewById(R.id.switchPadding);
                setSwitchColor(switchPadding, context);
                publishProgress(new ConstructorSetCheck(switchPadding, settings.getBoolean("bottomPadding", false)));
                switchPadding.setOnCheckedChangeListener((compoundButton, b) -> {
                    try {
                        editor.putBoolean("bottomPadding", b);
                        editor.apply();
                        LinearLayout linearLayoutRoot = ((MainActivity) context).findViewById(R.id.rootViewMainContent);
                        if (b) {
                            linearLayoutRoot.setPadding(0, 0, 0, 0);
                        } else {
                            String type = "navigation_bar_width";
                            if ((MainActivity.getResources.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
                                type = "navigation_bar_height_landscape";
                            }
                            if (MainActivity.getResources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                                linearLayoutRoot.setPadding(0, 0, 0, MainActivity.getResources.getDimensionPixelSize(MainActivity.getResources.getIdentifier("navigation_bar_height", "dimen", "android")));
                            else
                                linearLayoutRoot.setPadding(0, 0, MainActivity.getResources.getDimensionPixelSize(MainActivity.getResources.getIdentifier(type, "dimen", "android")), 0);

                        }
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                content.findViewById(R.id.bottom_title_general_setting_padding).setOnClickListener(v -> switchPadding.setChecked(!settings.getBoolean("bottomPadding", false)));

                final SwitchCompat switchWhiteTheme = content.findViewById(R.id.switchWhite);
                setSwitchColor(switchWhiteTheme, context);
                if (settings.getInt("id_current_theme", 1) == 1)
                    publishProgress(new ConstructorSetCheck(switchWhiteTheme, true));

                switchWhiteTheme.setOnCheckedChangeListener((compoundButton, b) -> {
                    try {
                        if (settings.getInt("id_current_theme", 1) == 1)
                            publishProgress(new ConstructorSetCheck(switchWhiteTheme, true));
                        else
                            ((MainActivity) context).new ChangeTheme().execute(new String[]{Integer.toString(1), null,
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_icon)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_border_theme)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_background)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_toolbar)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_toolbar_text)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_text_light)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_text_dark)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_text_hint)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_cursor)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_card)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_bottomBorder)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_button_add)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_button_add_plus)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_button_act)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_button_arrow)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_selected_section)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_progress)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_not_confirmed)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_Table_column)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_notification_on)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_notification_off)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_switch_on)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_switch_off)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_color_block_choose_background)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_color_block_choose_border)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.custom_color_audio_player)),
                            });
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                cardVisual = content.findViewById(R.id.card_white_theme);
                cardVisual.setCardBackgroundColor(cardColor);
                cardVisual.setOnClickListener(v -> switchWhiteTheme.setChecked(settings.getInt("id_current_theme", 1) != 1));

                final SwitchCompat switchBlackTheme = content.findViewById(R.id.switchDark);
                setSwitchColor(switchBlackTheme, context);
                if (settings.getInt("id_current_theme", 1) == 2)
                    publishProgress(new ConstructorSetCheck(switchBlackTheme, true));


                switchBlackTheme.setOnCheckedChangeListener((compoundButton, b) -> {
                    try {
                        if (settings.getInt("id_current_theme", 1) == 2)
                            publishProgress(new ConstructorSetCheck(switchBlackTheme, true));
                        else
                            ((MainActivity) context).new ChangeTheme().execute(new String[]{Integer.toString(2), null,
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_icon)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_border_theme)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_background)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_toolbar)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_toolbar_text)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_text_light)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_text_dark)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_text_hint)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_cursor)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_card)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_bottomBorder)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_button_add)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_button_add_plus)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_button_act)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_button_arrow)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_selected_section)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_progress)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_not_confirmed)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_Table_column)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_notification_on)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_notification_off)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_switch_on)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_switch_off)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_color_block_choose_background)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_color_block_choose_border)),
                                    Integer.toString(ContextCompat.getColor(context, R.color.black_color_audio_player)),
                            });
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                cardVisual = content.findViewById(R.id.card_dark_theme);
                cardVisual.setCardBackgroundColor(cardColor);
                cardVisual.setOnClickListener(v -> switchBlackTheme.setChecked(settings.getInt("id_current_theme", 1) != 2));

                SwitchCompat switchBorderAlert = content.findViewById(R.id.switchBorder_alert);
                setSwitchColor(switchBorderAlert, context);
                publishProgress(new ConstructorSetCheck(switchBorderAlert, settings.getBoolean("BorderAlertSettings", false)));
                switchBorderAlert.setOnCheckedChangeListener((compoundButton, b) -> {
                    try {
                        editor.putBoolean("BorderAlertSettings", b);
                        editor.apply();
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                content.findViewById(R.id.bottom_title_general_setting_dialog_stroke).setOnClickListener(v -> switchBorderAlert.setChecked(!settings.getBoolean("BorderAlertSettings", false)));

                SwitchCompat switchSaturday = content.findViewById(R.id.switchSaturday);
                setSwitchColor(switchSaturday, context);
                publishProgress(new ConstructorSetCheck(switchSaturday, settings.getBoolean("SaturdaySettings", true)));
                switchSaturday.setOnCheckedChangeListener((compoundButton, b) -> {
                    try {
                        editor.putBoolean("SaturdaySettings", b);
                        editor.apply();
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                content.findViewById(R.id.bottom_title_timetables_saturday).setOnClickListener(v -> switchSaturday.setChecked(!settings.getBoolean("SaturdaySettings", true)));

                final TextView textViewEditQuarter = content.findViewById(R.id.quarters_edit);
                textViewEditQuarter.setText(String.valueOf(settings.getInt("countChet", 4)));
                textViewEditQuarter.setOnClickListener(v -> {
                    try {
                        onClickEdit(textViewEditQuarter);
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                content.findViewById(R.id.bottom_title_quarter).setOnClickListener(v -> {
                    try {
                        onClickEdit(textViewEditQuarter);
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                final TextView dpBorder = content.findViewById(R.id.dpBorder);
                dpBorder.setTextColor(textLightcolor);
                dpBorder.setText(settings.getInt("dpBorderSettings", 1) + "dp");
                dpBorder.setOnClickListener(v -> {
                    try {
                        onEdit(dpBorder);
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                content.findViewById(R.id.bottom_title_general_setting_width_stroke).setOnClickListener(v -> {
                    try {
                        onEdit(dpBorder);
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });

                final Spinner spinner = content.findViewById(R.id.spinner_default);
                List<String> choose = new ArrayList<>();
                choose.add(getString(R.string.not_chosen));
                choose.add(getString(R.string.HEX_code));
                choose.add(getString(R.string.Choose_color));
                String temp = choose.get(choose.indexOf(settings.getString("dafauilt_choose_color", getString(R.string.not_chosen))));
                choose.set(choose.indexOf(settings.getString("dafauilt_choose_color", getString(R.string.not_chosen))), choose.get(0));
                choose.set(0, temp);

                final AdapterSpinner adapterSpinner = new AdapterSpinner(context, choose, false);
                spinner.setAdapter(adapterSpinner);
                spinner.getBackground().setColorFilter(textLightcolor, PorterDuff.Mode.SRC_ATOP);
                spinner.setOnTouchListener((v, event) -> {
                    try {
                        if (event.getAction() == MotionEvent.ACTION_DOWN && currentWindow[0].equals("null")) {
                            currentWindow = new String[]{"spinner_default"};
                        }
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                    return false;
                });
                if (currentWindow[0].equals("spinner_default"))
                    spinner.post(() -> {
                        try {
                            spinner.performClick();
                        } catch (Exception error) {
                            ((MainActivity) context).errorStack(error);
                        }
                    });
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        try {
                            editor.putString("dafauilt_choose_color", adapterSpinner.getItem(i));
                            editor.apply();
                            String temp = choose.get(i);
                            choose.set(i, choose.get(0));
                            choose.set(0, temp);
                        } catch (Exception error) {
                            ((MainActivity) context).errorStack(error);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                content.findViewById(R.id.bottom_title_settings).setOnClickListener(v -> spinner.performClick());

                final Spinner spinnerFragment = content.findViewById(R.id.spinner_fragment_default);
                spinnerFragment.getBackground().setColorFilter(textLightcolor, PorterDuff.Mode.SRC_ATOP);
                List<String> chooseFragment = new ArrayList<>();
                chooseFragment.add(getString(R.string.app_name));
                chooseFragment.add(getString(R.string.timetables));
                chooseFragment.add(getString(R.string.teachers));
                chooseFragment.add(getString(R.string.grades));
                chooseFragment.add(getString(R.string.settings));
                chooseFragment.add(getString(R.string.help));
                chooseFragment.add(getString(R.string.fragment_default_off));

                temp = chooseFragment.get(chooseFragment.indexOf(settings.getString("dafauilt_fragment", getString(R.string.fragment_default_off))));
                chooseFragment.set(chooseFragment.indexOf(settings.getString("dafauilt_fragment", getString(R.string.fragment_default_off))), chooseFragment.get(0));
                chooseFragment.set(0, temp);

                final AdapterSpinner adapterSpinnerFragment = new AdapterSpinner(context, chooseFragment, false);
                spinnerFragment.setAdapter(adapterSpinnerFragment);
                spinnerFragment.setOnTouchListener((v, event) -> {
                    try {
                        if (event.getAction() == MotionEvent.ACTION_DOWN && currentWindow[0].equals("null")) {
                            currentWindow = new String[]{"spinner_fragment_default"};
                        }
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                    return false;
                });
                if (currentWindow[0].equals("spinner_fragment_default"))
                    spinnerFragment.post(() -> {
                        try {
                            spinnerFragment.performClick();
                        } catch (Exception error) {
                            ((MainActivity) context).errorStack(error);
                        }
                    });
                spinnerFragment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        try {
                            editor.putString("dafauilt_fragment", adapterSpinnerFragment.getItem(i));
                            editor.apply();
                            String temp = chooseFragment.get(i);
                            chooseFragment.set(i, chooseFragment.get(0));
                            chooseFragment.set(0, temp);
                        } catch (Exception error) {
                            ((MainActivity) context).errorStack(error);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                content.findViewById(R.id.bottom_title_general_setting_fragment_default).setOnClickListener(v -> spinnerFragment.performClick());

                final Spinner spinnerMount = content.findViewById(R.id.mount_choose);
                spinnerMount.getBackground().setColorFilter(textLightcolor, PorterDuff.Mode.SRC_ATOP);

                List<String> chooseMount = Arrays.asList(MainActivity.getResources.getStringArray(R.array.months));
                temp = chooseMount.get(settings.getInt("mesStartOcenki", 12));
                chooseMount.set(settings.getInt("mesStartOcenki", 12), chooseMount.get(0));
                chooseMount.set(0, temp);

                final AdapterSpinner adapterSpinnerMount = new AdapterSpinner(context, chooseMount, false);
                spinnerMount.setAdapter(adapterSpinnerMount);
                spinnerMount.getBackground().setColorFilter(textLightcolor, PorterDuff.Mode.SRC_ATOP);
                spinnerMount.setOnTouchListener((v, event) -> {
                    try {
                        if (event.getAction() == MotionEvent.ACTION_DOWN && currentWindow[0].equals("null")) {
                            currentWindow = new String[]{"spinner_mount_choose"};
                        }
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                    return false;
                });
                if (currentWindow[0].equals("spinner_mount_choose"))
                    spinnerMount.post(() -> {
                        try {
                            spinnerMount.performClick();
                        } catch (Exception error) {
                            ((MainActivity) context).errorStack(error);
                        }
                    });
                spinnerMount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        try {
                            if (i != 12)
                                editor.putInt("mesStartOcenki", Arrays.asList(MainActivity.getResources.getStringArray(R.array.months)).indexOf(adapterSpinnerMount.getItem(i)));
                            else {
                                editor.remove("mesStartOcenki");
                                ((MainActivity) context).getBundles.get(3).clear();
                            }
                            editor.apply();
                            currentWindow = new String[]{"null"};
                            String temp = chooseMount.get(i);
                            chooseMount.set(i, chooseMount.get(0));
                            chooseMount.set(0, temp);
                        } catch (Exception error) {
                            ((MainActivity) context).errorStack(error);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                content.findViewById(R.id.text_day_ocenki).setOnClickListener(v -> spinnerMount.performClick());

                if (constrRecyclerViewArrayList.size() == 0)
                    Start();
                recyclerView = content.findViewById(R.id.themes);
                setAdapter();

            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }
    }

    public void setAdapter() {
        try {
            adapter = new AdapterRecyclerTheme(constrRecyclerViewArrayList, context);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);

            adapter.setOnCheckedChangeListener(position -> {
                try {
                    if (constrRecyclerViewArrayList.get(position).IdSwitch == settings.getInt("id_current_theme", 1)) {
                        adapter.getSwitch().setChecked(true);
                    } else
                        ((MainActivity) context).new ChangeTheme().execute(constrRecyclerViewArrayList.get(position).colors);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            adapter.setOnItemClickListener(position -> {
                try {
                    currentWindow = new String[]{"editTheme", String.valueOf(position), String.valueOf(BottomSheetBehavior.STATE_EXPANDED)};
                    ((MainActivity) Objects.requireNonNull(context)).EditTheme(constrRecyclerViewArrayList.get(position).colors, position);
                    new EditTheme().execute(constrRecyclerViewArrayList.get(position).colors);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            }, position -> {
                try {
                    StringBuilder stringBuffer = new StringBuilder();

                    try {
                        FileInputStream read = context.openFileInput("Themes.txt");
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        String temp_read;
                        String[] help;
                        while ((temp_read = bufferedReader.readLine()) != null) {
                            help = temp_read.split("=");
                            if (!help[0].equals(Integer.toString(constrRecyclerViewArrayList.get(position).IdSwitch)))
                                stringBuffer.append(temp_read).append("\n");
                        }
                        bufferedReader.close();
                        reader.close();
                        read.close();
                    } catch (IOException | NullPointerException ignored) {
                    }

                    try {
                        FileOutputStream write = Objects.requireNonNull(context).openFileOutput("Themes.txt", MODE_PRIVATE);
                        String temp_write = stringBuffer.toString();

                        write.write(temp_write.getBytes());
                        write.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (constrRecyclerViewArrayList.get(position).IdSwitch == settings.getInt("id_current_theme", R.id.switchWhite))
                        switch (MainActivity.getResources.getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                            case Configuration.UI_MODE_NIGHT_NO:
                                ((MainActivity) context).new ChangeTheme().execute(new String[]{Integer.toString(1), null,
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_icon)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_border_theme)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_background)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_toolbar)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_toolbar_text)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_text_light)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_text_dark)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_text_hint)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_cursor)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_card)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_bottomBorder)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_button_add)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_button_add_plus)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_button_act)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_button_arrow)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_selected_section)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_progress)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_not_confirmed)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_Table_column)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_notification_on)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_notification_off)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_switch_on)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_switch_off)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_color_block_choose_background)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_color_block_choose_border)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.custom_color_audio_player)),
                                });
                                break;
                            case Configuration.UI_MODE_NIGHT_YES:
                                ((MainActivity) context).new ChangeTheme().execute(new String[]{Integer.toString(2), null,
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_icon)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_border_theme)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_background)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_toolbar)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_toolbar_text)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_text_light)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_text_dark)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_text_hint)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_cursor)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_card)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_bottomBorder)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_button_add)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_button_add_plus)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_button_act)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_button_arrow)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_selected_section)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_progress)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_not_confirmed)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_Table_column)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_notification_on)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_notification_off)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_switch_on)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_switch_off)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_color_block_choose_background)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_color_block_choose_border)),
                                        Integer.toString(ContextCompat.getColor(context, R.color.black_color_audio_player)),
                                });
                                break;
                        }

                    currentWindow = new String[]{"null"};
                    constrRecyclerViewArrayList.remove(position);
                    if (settings.getBoolean("AnimationSettings", true)) {
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, constrRecyclerViewArrayList.size());
                    } else
                        adapter.notifyDataSetChanged();
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public void NotifyAdapter(int position, String newName) {
        try {
            constrRecyclerViewArrayList.get(position).name = newName;
            adapter.notifyDataSetChanged();
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public void onEdit(final TextView textView) {
        try {
            currentWindow = new String[]{"edit", String.valueOf(textView.getId())};
            final View promptsView = LayoutInflater.from(context).inflate(R.layout.input_edittext, null);
            final AlertDialog.Builder input = new AlertDialog.Builder(context);
            input.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.input_edittext_linear).setBackground(alertbackground);

            alertDialog = input.create();

            final EditText editText = promptsView.findViewById(R.id.input_edittext);
            editText.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            editText.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
            if (textView.getId() == R.id.dpText)
                editText.setHint("10dp");
            else editText.setHint("1dp");
            editText.setText(textView.getText());
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        String string = s.toString();
                        if (!string.contains("dp") || string.indexOf("p") < string.length() - 1) {
                            string = s.toString().replace("d", "").replace("p", "") + "dp";
                            editText.setText(string);
                            editText.setSelection(string.length() - 2);
                        }
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MainActivity.setCursorColorsQ(editText, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            } else {
                MainActivity.setCursorColor(editText, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            }

            TextView button = promptsView.findViewById(R.id.input_edittext_button);
            button.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            button.setOnClickListener(v -> {
                try {
                    textView.setText(editText.getText());
                    alertDialog.hide();
                    alertDialog = null;
                    if (textView.getId() == R.id.dpText)
                        editor.putInt("dpSizeSettings", Integer.parseInt(editText.getText().toString().split("dp")[0]) * MainActivity.dpSize);
                    else
                        editor.putInt("dpBorderSettings", Integer.parseInt(editText.getText().toString().split("dp")[0]));
                    editor.apply();
                    currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });


            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
            wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
            wmlp.y = 15 * MainActivity.dpSize;

            alertDialog.show();
            alertDialog.setOnCancelListener(dialog -> {
                try {
                    currentWindow = new String[]{"null"};
                    alertDialog = null;
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public static void setSwitchColor(SwitchCompat switchColor, Context context) {
        try {
            SharedPreferences Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ColorStateList buttonStates = new ColorStateList(
                        new int[][]{
                                new int[]{android.R.attr.state_checked},
                                new int[]{}
                        },
                        new int[]{
                                Current_Theme.getInt("custom_switch_on", ContextCompat.getColor(context, R.color.custom_switch_on)),
                                Current_Theme.getInt("custom_switch_off", ContextCompat.getColor(context, R.color.custom_switch_off))
                        }
                );
                switchColor.getThumbDrawable().setTintList(buttonStates);
                switchColor.getTrackDrawable().setTintList(buttonStates);
            } else {
                StateListDrawable thumbStates = new StateListDrawable();
                thumbStates.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(Current_Theme.getInt("custom_switch_on", ContextCompat.getColor(context, R.color.custom_switch_on))));
                thumbStates.addState(new int[]{}, new ColorDrawable(Current_Theme.getInt("custom_switch_off", ContextCompat.getColor(context, R.color.custom_switch_off)))); // this one has to come last
                switchColor.setThumbDrawable(thumbStates);
            }
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    class EditTheme extends AsyncTask<String[], String[], Void> {
        TextView textView;

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                linearLayoutBottom.removeAllViews();
                linearLayoutBottom.addView(contentTheme);
                AlertDialog.Builder progressDialog = new AlertDialog.Builder(context);
                final View promptsView = LayoutInflater.from(context).inflate(R.layout.loading_color, null);
                progressDialog.setView(promptsView)
                        .setCancelable(false);
                GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.loading_drawable);
                Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                if (settings.getBoolean("BorderAlertSettings", false))
                    alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                promptsView.findViewById(R.id.linerLoading).setBackground(alertbackground);
                ProgressBar progressBar = promptsView.findViewById(R.id.progress_loading_color);
                progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);
                textView = promptsView.findViewById(R.id.textLoading);
                textView.setText(getString(R.string.loading));
                textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                alertDialog = progressDialog.create();
                Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                super.onPostExecute(aVoid);
                try {
                    alertDialog.hide();
                    alertDialog = null;
                } catch (Exception ignored) {
                }
                bottomSheetBehavior.addBottomSheetCallback(((MainActivity) context).bottomSheetCallback);
                try {
                    bottomSheetBehavior.setState(Integer.parseInt(currentWindow[2]));
                } catch (IllegalArgumentException e) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected void onProgressUpdate(String[]... strings) {
            try {
                TextView name = linearLayoutBottom.findViewById(R.id.custom_name);
                name.setText(strings[0][1]);
                linearLayoutBottom.findViewById(R.id.custom_icon).setBackgroundColor(Integer.parseInt(strings[0][2]));
                linearLayoutBottom.findViewById(R.id.custom_border_theme).setBackgroundColor(Integer.parseInt(strings[0][3]));
                linearLayoutBottom.findViewById(R.id.custom_background).setBackgroundColor(Integer.parseInt(strings[0][4]));
                linearLayoutBottom.findViewById(R.id.custom_toolbar).setBackgroundColor(Integer.parseInt(strings[0][5]));
                linearLayoutBottom.findViewById(R.id.custom_toolbar_text).setBackgroundColor(Integer.parseInt(strings[0][6]));
                linearLayoutBottom.findViewById(R.id.custom_text_light).setBackgroundColor(Integer.parseInt(strings[0][7]));
                linearLayoutBottom.findViewById(R.id.custom_text_dark).setBackgroundColor(Integer.parseInt(strings[0][8]));
                linearLayoutBottom.findViewById(R.id.custom_text_hint).setBackgroundColor(Integer.parseInt(strings[0][9]));
                linearLayoutBottom.findViewById(R.id.custom_cursor).setBackgroundColor(Integer.parseInt(strings[0][10]));
                linearLayoutBottom.findViewById(R.id.custom_card).setBackgroundColor(Integer.parseInt(strings[0][11]));
                linearLayoutBottom.findViewById(R.id.custom_bottomBorder).setBackgroundColor(Integer.parseInt(strings[0][12]));
                linearLayoutBottom.findViewById(R.id.custom_button_add).setBackgroundColor(Integer.parseInt(strings[0][13]));
                linearLayoutBottom.findViewById(R.id.custom_button_add_plus).setBackgroundColor(Integer.parseInt(strings[0][14]));
                linearLayoutBottom.findViewById(R.id.custom_button_act).setBackgroundColor(Integer.parseInt(strings[0][15]));
                linearLayoutBottom.findViewById(R.id.custom_button_arrow).setBackgroundColor(Integer.parseInt(strings[0][16]));
                linearLayoutBottom.findViewById(R.id.custom_selected_section).setBackgroundColor(Integer.parseInt(strings[0][17]));
                linearLayoutBottom.findViewById(R.id.custom_progress).setBackgroundColor(Integer.parseInt(strings[0][18]));
                linearLayoutBottom.findViewById(R.id.custom_not_confirmed).setBackgroundColor(Integer.parseInt(strings[0][19]));
                linearLayoutBottom.findViewById(R.id.custom_Table_column).setBackgroundColor(Integer.parseInt(strings[0][20]));
                linearLayoutBottom.findViewById(R.id.custom_notification_on).setBackgroundColor(Integer.parseInt(strings[0][21]));
                linearLayoutBottom.findViewById(R.id.custom_notification_off).setBackgroundColor(Integer.parseInt(strings[0][22]));
                linearLayoutBottom.findViewById(R.id.custom_switch_on).setBackgroundColor(Integer.parseInt(strings[0][23]));
                linearLayoutBottom.findViewById(R.id.custom_switch_off).setBackgroundColor(Integer.parseInt(strings[0][24]));
                linearLayoutBottom.findViewById(R.id.custom_color_block_choose_background).setBackgroundColor(Integer.parseInt(strings[0][25]));
                linearLayoutBottom.findViewById(R.id.custom_color_block_choose_border).setBackgroundColor(Integer.parseInt(strings[0][26]));
                linearLayoutBottom.findViewById(R.id.custom_color_audio_player).setBackgroundColor(Integer.parseInt(strings[0][27]));
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(String[]... strings) {
            try {
                publishProgress(strings[0]);
            } catch (Exception error) {
                ((MainActivity) context).errorStack(error);
            }
            return null;
        }
    }

    public void onClickEdit(final TextView view) {
        try {
            currentWindow = new String[]{"editGrades"};
            final View promptsView = LayoutInflater.from(context).inflate(R.layout.input_edittext, null);
            final AlertDialog.Builder input = new AlertDialog.Builder(context);
            input.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.input_edittext_linear).setBackground(alertbackground);

            alertDialog = input.create();
            final EditText editText = promptsView.findViewById(R.id.input_edittext);
            editText.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            editText.setText(view.getText());
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MainActivity.setCursorColorsQ(editText, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            } else {
                MainActivity.setCursorColor(editText, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            }
            TextView button = promptsView.findViewById(R.id.input_edittext_button);
            button.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            button.setOnClickListener(v -> {
                if (!editText.getText().toString().equals("0")) {

                    if (editText.getText().toString().equals("") || editText.getText().toString().equals(" "))
                        editor.putInt("countChet", 4);
                    else
                        editor.putInt("countChet", Integer.parseInt(editText.getText().toString()));
                    editor.apply();
                    alertDialog.hide();
                    alertDialog = null;
                    currentWindow = new String[]{"null"};
                    view.setText(editText.getText());
                } else
                    MainActivity.ToastMakeText(context, context.getString(R.string.number_quarters_warning));
            });


            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
            wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
            wmlp.y = 15 * MainActivity.dpSize;

            alertDialog.show();
            alertDialog.setOnCancelListener(dialog -> {
                currentWindow = new String[]{"null"};
                alertDialog = null;
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    private void Start() {
        try {
            String[] help;
            String delimeter = "=";
            constrRecyclerViewArrayList.clear();

            try {
                FileInputStream read = Objects.requireNonNull(context).openFileInput("Themes.txt");
                InputStreamReader reader = new InputStreamReader(read);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String temp_read;
                while ((temp_read = bufferedReader.readLine()) != null) {
                    help = temp_read.split(delimeter);
                    constrRecyclerViewArrayList.add(new ConstructorThemeRecycler(help));
                }
                bufferedReader.close();
                reader.close();
                read.close();
            } catch (IOException e) {
            } catch (NullPointerException ignore) {

            }
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
