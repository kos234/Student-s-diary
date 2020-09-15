package com.example.kos;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.customview.widget.ViewDragHelper;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kos.constructors.ConstructorItemMenu;
import com.example.kos.fragments.FragmentBells;
import com.example.kos.fragments.FragmentDnewnik;
import com.example.kos.fragments.FragmentGrades;
import com.example.kos.fragments.FragmentHelp;
import com.example.kos.fragments.FragmentSettings;
import com.example.kos.fragments.FragmentTeachers;
import com.example.kos.adapters.AdapterMenuRecyclerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private ValueAnimator valueAnimator;
    private final Context context = this;
    private SharedPreferences settings, Current_Theme;
    private SharedPreferences.Editor editor;
    private int what, positionTheme = 0;
    public FragmentManager fragmentManager;
    public final List<Bundle> getBundles = new ArrayList<>();
    private MediaRecorder mediaRecorder;
    public static final int REQUEST_CODE_CAMERA = 1, REQUEST_CODE_MICROPHONE_CONF = 3, REQUEST_CODE_CAMERA_CONF = 4, REQUEST_CODE_FOLDER_CONF = 5, REQUEST_CODE_CHOOSE_FILE = 6;
    public String urlTemp, TempNameTheme;
    public int[] tempSizes = null;
    private NavigationView navigationView;
    public boolean cancelAsyncTask = false, icMicro = true, ClickSaveThemeType = true, icHideStatusBar = false, icReload = false;
    private final HashMap<Integer, Integer> colors = new HashMap<>();
    private RecyclerView menuRecycler;
    private final ArrayList<ConstructorItemMenu> itemsMenu = new ArrayList<>();
    public GridLayout viewConfirmTemp;
    public AdapterMenuRecyclerView menuAdapter;
    public Toolbar toolbar;
    public FrameLayout linearLayoutBottom;
    public BottomSheetBehavior<View> bottomSheetBehavior;
    public PendingIntent notifyIntent;
    public static int dpSize;
    public static Resources getResources;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            if (savedInstanceState != null)
                icReload = savedInstanceState.getBoolean("reload");
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            setTheme(R.style.AppTheme);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            icHideStatusBar = true;
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            new onStart().execute();
        } catch (Exception error) {
            errorStack(error);
        }
    }

    public static void ToastMakeText(Context context, String text) {
        SharedPreferences Current_Theme = Objects.requireNonNull(context).getSharedPreferences("Current_Theme", MODE_PRIVATE);
        View layout = LayoutInflater.from(context).inflate(R.layout.toast, null);
        TextView textView = layout.findViewById(R.id.toastText);
        textView.setText(text);
        textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        CardView cardView = layout.findViewById(R.id.toastRoot);
        cardView.setCardBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void onTheme() {
        try {
            switch (MainActivity.getResources.getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_NO:
                    if (!settings.getBoolean("cancel_set_white", false) && settings.getInt("id_current_theme", 1) != 1) {
                        ifSetTheme(false);
                    }
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    if (settings.getBoolean("firstStart", true)) {
                        SharedPreferences.Editor editorTheme = Current_Theme.edit();
                        editor.putBoolean("firstStart", false);
                        editor.putInt("id_current_theme", 2);
                        editor.apply();

                        editorTheme.putInt("custom_icon", ContextCompat.getColor(context, R.color.black_icon));
                        editorTheme.putInt("custom_border_theme", ContextCompat.getColor(context, R.color.black_border_theme));
                        editorTheme.putInt("custom_background", ContextCompat.getColor(context, R.color.black_background));
                        editorTheme.putInt("custom_toolbar", ContextCompat.getColor(context, R.color.black_toolbar));
                        editorTheme.putInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.black_toolbar_text));
                        editorTheme.putInt("custom_text_light", ContextCompat.getColor(context, R.color.black_text_light));
                        editorTheme.putInt("custom_text_dark", ContextCompat.getColor(context, R.color.black_text_dark));
                        editorTheme.putInt("custom_text_hint", ContextCompat.getColor(context, R.color.black_text_hint));
                        editorTheme.putInt("custom_cursor", ContextCompat.getColor(context, R.color.black_cursor));
                        editorTheme.putInt("custom_card", ContextCompat.getColor(context, R.color.black_card));
                        editorTheme.putInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.black_bottomBorder));
                        editorTheme.putInt("custom_button_add", ContextCompat.getColor(context, R.color.black_button_add));
                        editorTheme.putInt("custom_button_add_plus", ContextCompat.getColor(context, R.color.black_button_add_plus));
                        editorTheme.putInt("custom_button_act", ContextCompat.getColor(context, R.color.black_button_act));
                        editorTheme.putInt("custom_button_arrow", ContextCompat.getColor(context, R.color.black_button_arrow));
                        editorTheme.putInt("custom_selected_section", ContextCompat.getColor(context, R.color.black_selected_section));
                        editorTheme.putInt("custom_progress", ContextCompat.getColor(context, R.color.black_progress));
                        editorTheme.putInt("custom_not_confirmed", ContextCompat.getColor(context, R.color.black_not_confirmed));
                        editorTheme.putInt("custom_Table_column", ContextCompat.getColor(context, R.color.black_Table_column));
                        editorTheme.putInt("custom_notification_on", ContextCompat.getColor(context, R.color.black_notification_on));
                        editorTheme.putInt("custom_notification_off", ContextCompat.getColor(context, R.color.black_notification_off));
                        editorTheme.putInt("custom_switch_on", ContextCompat.getColor(context, R.color.black_switch_on));
                        editorTheme.putInt("custom_switch_off", ContextCompat.getColor(context, R.color.black_switch_off));
                        editorTheme.putInt("custom_color_block_choose_background", ContextCompat.getColor(context, R.color.black_color_block_choose_background));
                        editorTheme.putInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.black_color_block_choose_border));
                        editorTheme.putInt("custom_color_audio_player", ContextCompat.getColor(context, R.color.black_color_audio_player));

                        editorTheme.apply();
                    } else if (!settings.getBoolean("cancel_set_dark", false) && settings.getInt("id_current_theme", 1) != 2) {
                        ifSetTheme(true);
                    }
                    break;
            }
        } catch (Exception error) {
            errorStack(error);
        }
    }

    public int getStatusBarSize() {
        int statusBarHeight = 0;
        try {
            int resourceId = MainActivity.getResources.getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0 && icHideStatusBar) {
                statusBarHeight = MainActivity.getResources.getDimensionPixelSize(resourceId);
            }
        } catch (Exception error) {
            errorStack(error);
        }
        return statusBarHeight;
    }

    public void ifSetTheme(final boolean icDark) {
        try {
            final View promptsView = LayoutInflater.from(context).inflate(R.layout.alert_delete_dnewnik, null);
            final AlertDialog.Builder setTheme = new AlertDialog.Builder(context);
            setTheme.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.alert_delete).setBackground(alertbackground);

            final AlertDialog setThemeS = setTheme.create();

            TextView textTitle = promptsView.findViewById(R.id.title_alert);
            textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            textTitle.setText(context.getString(R.string.title_change_theme));

            TextView textBottomTitle = promptsView.findViewById(R.id.title_bottom_alert);
            textBottomTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            if (icDark)
                textBottomTitle.setText(context.getString(R.string.setDarkThemeSettings));
            else
                textBottomTitle.setText(context.getString(R.string.setWhiteThemeSettings));

            final TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
            ButtonCancel.setText(getString(R.string.cancel_change_theme));
            ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonCancel.setOnClickListener(view -> {
                try {
                    setThemeS.hide();
                    if (icDark)
                        editor.putBoolean("cancel_set_dark", true);
                    else
                        editor.putBoolean("cancel_set_white", true);
                    editor.apply();
                } catch (Exception error) {
                    errorStack(error);
                }
            });
            promptsView.findViewById(R.id.button_three_alert).setVisibility(View.GONE);
            promptsView.findViewById(R.id.viewBorderOne).setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));
            promptsView.findViewById(R.id.viewBorderTwo).setVisibility(View.GONE);

            TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
            ButtonSave.setText(getString(R.string.yes));
            ButtonSave.setOnClickListener(view -> {
                try {
                    if (icDark) {
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
                    } else {
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
                    }
                } catch (Exception error) {
                    errorStack(error);
                }
            });
            ButtonSave.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));

            Objects.requireNonNull(setThemeS.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            setThemeS.show();

        } catch (Exception error) {
            errorStack(error);
        }
    }

    class onStart extends AsyncTask<Void, Void, Void> {
        LinearLayout start_image;
        FrameLayout frameLayout;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                getResources = getResources();
                dpSize = (int) MainActivity.getResources.getDisplayMetrics().density;
                settings = getSharedPreferences("Settings", MODE_PRIVATE);
                editor = settings.edit();
                Current_Theme = getSharedPreferences("Current_Theme", MODE_PRIVATE);
                onTheme();
                drawerLayout = findViewById(R.id.Drawer);
                start_image = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.start_display, null);
                start_image.setBackgroundColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));
                TextView textTemp = start_image.findViewById(R.id.title_start_display);
                textTemp.setTextColor(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)));
                ProgressBar progressBar = start_image.findViewById(R.id.progress_start_display);
                int colorTitle = Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress));
                if (colorTitle == ContextCompat.getColor(context, R.color.custom_progress))
                    colorTitle = Color.WHITE;
                progressBar.getIndeterminateDrawable().setColorFilter(colorTitle, PorterDuff.Mode.SRC_ATOP);
                drawerLayout.addView(start_image);
                navigationView = findViewById(R.id.navigation);
                menuRecycler = findViewById(R.id.menuRecyclerView);
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));
                toolbar = findViewById(R.id.toolbar);
                Drawable menuToolbar = MainActivity.getResources.getDrawable(R.drawable.ic_menu_24px);
                menuToolbar.setColorFilter(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)), PorterDuff.Mode.SRC_ATOP);
                toolbar.setNavigationIcon(menuToolbar);
                toolbar.setNavigationOnClickListener(view -> {
                    try {
                        drawerLayout.openDrawer(Gravity.LEFT);
                    } catch (Exception error) {
                        ((MainActivity) context).errorStack(error);
                    }
                });
                toolbar.setTitleTextColor(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)));
                frameLayout = findViewById(R.id.frame_zvonki);
                frameLayout.setPadding(0, ((MainActivity) context).getStatusBarSize(), 0, 0);
                frameLayout.setBackgroundColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));
                linearLayoutBottom = findViewById(R.id.field_create_fragment);
                bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBottom);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            } catch (Exception error) {
                errorStack(error);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (!settings.getBoolean("bottomPadding", false)) {
                    LinearLayout linearLayoutRoot = findViewById(R.id.rootViewMainContent);
                    String type = "navigation_bar_width";
                    if ((MainActivity.getResources.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
                        type = "navigation_bar_height_landscape";
                    }
                    if (MainActivity.getResources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                        linearLayoutRoot.setPadding(0, 0, 0, MainActivity.getResources.getDimensionPixelSize(MainActivity.getResources.getIdentifier("navigation_bar_height", "dimen", "android")));
                    else
                        linearLayoutRoot.setPadding(0, 0, MainActivity.getResources.getDimensionPixelSize(MainActivity.getResources.getIdentifier(type, "dimen", "android")), 0);
                }

                valueAnimator = ValueAnimator.ofInt(0, getStatusBarSize());
                valueAnimator.setEvaluator(new IntEvaluator());
                valueAnimator.addUpdateListener(valueAnimator -> linearLayoutBottom.setPadding(0, (int) valueAnimator.getAnimatedValue(), 0, 0));
                bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);

                Field mDragger = drawerLayout.getClass().getDeclaredField("mLeftDragger");
                mDragger.setAccessible(true);
                ViewDragHelper draggerObj = (ViewDragHelper) mDragger.get(drawerLayout);
                Field mEdgeSize = Objects.requireNonNull(draggerObj).getClass().getDeclaredField("mEdgeSize");
                mEdgeSize.setAccessible(true);
                int edge = mEdgeSize.getInt(draggerObj);
                mEdgeSize.setInt(draggerObj, edge * 5);//Изменение поля открытия Дровера
                findViewById(R.id.header).setPadding(15 * MainActivity.dpSize, ((MainActivity) context).getStatusBarSize(), 0, 0);
                drawerLayout.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                if (settings.getBoolean("AnimationSettings", true)) {
                    start_image.animate()
                            .translationY(0)
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    drawerLayout.removeView(start_image);
                                }
                            });
                } else drawerLayout.removeView(start_image);
                if (settings.getBoolean("notifySettings", true)) {
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
                    notifyIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationTime.class), PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, notifyIntent);
                }


            } catch (Exception error) {
                errorStack(error);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            try {
                navigationView.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                menuAdapter = new AdapterMenuRecyclerView(context, itemsMenu, fragmentManager);
                menuRecycler.setAdapter(menuAdapter);
                menuRecycler.setHasFixedSize(true);
                menuRecycler.setLayoutManager(new LinearLayoutManager(context));
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_FOLDER_CONF);
                }
            } catch (Exception error) {
                errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                fragmentManager = getSupportFragmentManager();
                Class<?> fragmentClass = null;
                Intent intent = getIntent();
                for (int s = 0; s < 6; s++) {
                    boolean Invisibly = true;
                    switch (s) {
                        case 1:
                            if (intent.getBooleanExtra("notification", false) || ((settings.getInt("Fragment", 0) == 1 && Objects.requireNonNull(settings.getString("dafauilt_fragment", getString(R.string.fragment_default_off))).equals(getString(R.string.fragment_default_off))) || Objects.requireNonNull(settings.getString("dafauilt_fragment", getString(R.string.fragment_default_off))).equals(getString(R.string.timetables)))) {
                                fragmentClass = FragmentBells.class;
                                Invisibly = false;
                            }
                            itemsMenu.add(new ConstructorItemMenu(R.drawable.ic_notifications_24px, getString(R.string.timetables), !Invisibly));
                            break;
                        case 2:
                            if (!intent.getBooleanExtra("notification", false) && (settings.getInt("Fragment", 0) == 2 && Objects.requireNonNull(settings.getString("dafauilt_fragment", getString(R.string.fragment_default_off))).equals(getString(R.string.fragment_default_off))) || Objects.requireNonNull(settings.getString("dafauilt_fragment", getString(R.string.fragment_default_off))).equals(getString(R.string.teachers))) {
                                fragmentClass = FragmentTeachers.class;
                                Invisibly = false;
                            }
                            itemsMenu.add(new ConstructorItemMenu(R.drawable.ic_group_24px, getString(R.string.teachers), !Invisibly));
                            break;
                        case 3:
                            if (!intent.getBooleanExtra("notification", false) && (settings.getInt("Fragment", 0) == 3 && Objects.requireNonNull(settings.getString("dafauilt_fragment", getString(R.string.fragment_default_off))).equals(getString(R.string.fragment_default_off))) || Objects.requireNonNull(settings.getString("dafauilt_fragment", getString(R.string.fragment_default_off))).equals(getString(R.string.grades))) {
                                fragmentClass = FragmentGrades.class;
                                Invisibly = false;
                            }
                            itemsMenu.add(new ConstructorItemMenu(R.drawable.ic_assessment_24px, getString(R.string.grades), !Invisibly));
                            break;
                        case 4:
                            if (!intent.getBooleanExtra("notification", false) && (settings.getInt("Fragment", 0) == 4 && Objects.requireNonNull(settings.getString("dafauilt_fragment", getString(R.string.fragment_default_off))).equals(getString(R.string.fragment_default_off))) || Objects.requireNonNull(settings.getString("dafauilt_fragment", getString(R.string.fragment_default_off))).equals(getString(R.string.settings))) {
                                fragmentClass = FragmentSettings.class;
                                Invisibly = false;
                            }
                            itemsMenu.add(new ConstructorItemMenu(R.drawable.ic_settings_applications_24px, getString(R.string.settings), !Invisibly));
                            break;
                        case 5:
                            if (!intent.getBooleanExtra("notification", false) && (settings.getInt("Fragment", 0) == 5 && Objects.requireNonNull(settings.getString("dafauilt_fragment", getString(R.string.fragment_default_off))).equals(getString(R.string.fragment_default_off))) || Objects.requireNonNull(settings.getString("dafauilt_fragment", getString(R.string.fragment_default_off))).equals(getString(R.string.help))) {
                                fragmentClass = FragmentHelp.class;
                                Invisibly = false;
                            }
                            itemsMenu.add(new ConstructorItemMenu(R.drawable.ic_help_24px, getString(R.string.help), !Invisibly));
                            break;
                        default:
                            if (!intent.getBooleanExtra("notification", false) && (settings.getInt("Fragment", 0) == 0 && Objects.requireNonNull(settings.getString("dafauilt_fragment", getString(R.string.fragment_default_off))).equals(getString(R.string.fragment_default_off))) || Objects.requireNonNull(settings.getString("dafauilt_fragment", getString(R.string.fragment_default_off))).equals(getString(R.string.app_name))) {
                                fragmentClass = FragmentDnewnik.class;
                                Invisibly = false;
                            }
                            itemsMenu.add(new ConstructorItemMenu(R.drawable.ic_menu_book_24px, getString(R.string.app_name), !Invisibly));
                            break;
                    }

                    getBundles.add(new Bundle());

                    if (!Invisibly && !icReload) {
                        Fragment fragmentActiv;
                        try {
                            fragmentActiv = (Fragment) fragmentClass.newInstance();
                            fragmentActiv.setArguments(getBundles.get(s));
                            fragmentManager.beginTransaction().addToBackStack("q").replace(R.id.Smena, fragmentActiv).commit();
                        } catch (IllegalAccessException | InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                }

                publishProgress();

                bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
                    int oldState = -1;

                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                                if (!settings.getBoolean("AnimationSettings", true))
                                    if (newState == BottomSheetBehavior.STATE_EXPANDED)
                                        valueAnimator.setCurrentFraction(1);
                                    else valueAnimator.setCurrentFraction(0);
                            }
                            if (oldState != newState) {
                                oldState = newState;
                                if (fragmentManager.getFragments().get(0) instanceof FragmentSettings) {
                                    FragmentSettings fragmentSettings = (FragmentSettings) fragmentManager.getFragments().get(0);
                                    if (newState == BottomSheetBehavior.STATE_HIDDEN)
                                        fragmentSettings.currentWindow = new String[]{"null"};
                                    else if (newState == BottomSheetBehavior.STATE_SETTLING)
                                        fragmentSettings.currentWindow = new String[]{fragmentSettings.currentWindow[0], fragmentSettings.currentWindow[1], String.valueOf(BottomSheetBehavior.STATE_COLLAPSED)};
                                    else
                                        fragmentSettings.currentWindow = new String[]{fragmentSettings.currentWindow[0], fragmentSettings.currentWindow[1], String.valueOf(newState)};
                                } else if (fragmentManager.getFragments().get(0) instanceof FragmentHelp) {
                                    FragmentHelp fragmentHelp = (FragmentHelp) fragmentManager.getFragments().get(0);
                                    if (newState == BottomSheetBehavior.STATE_HIDDEN)
                                        fragmentHelp.currentWindow = new String[]{"null"};
                                    else if (newState == BottomSheetBehavior.STATE_SETTLING)
                                        fragmentHelp.currentWindow = new String[]{fragmentHelp.currentWindow[0], String.valueOf(BottomSheetBehavior.STATE_COLLAPSED)};
                                    else
                                        fragmentHelp.currentWindow = new String[]{fragmentHelp.currentWindow[0], String.valueOf(newState)};
                                }
                            }
                        } catch (Exception error) {
                            errorStack(error);
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                        try {
                            if (settings.getBoolean("AnimationSettings", true))
                                valueAnimator.setCurrentFraction(slideOffset);
                        } catch (Exception error) {
                            errorStack(error);
                        }
                    }
                };
            } catch (Exception error) {
                errorStack(error);
            }
            return null;
        }
    }


    @Override
    public void onBackPressed() {
        try {
            if (!(fragmentManager.findFragmentById(R.id.Smena) instanceof onBackPressed) || !((onBackPressed) Objects.requireNonNull(fragmentManager.findFragmentById(R.id.Smena))).onBackPressed()) {
                if (fragmentManager.getBackStackEntryCount() == 1)
                    finish();
                else
                    super.onBackPressed();
            }
        } catch (Exception error) {
            errorStack(error);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        try {
            outState.putBoolean("reload", true);
            super.onSaveInstanceState(outState);
        } catch (Exception error) {
            errorStack(error);
        }
    }

    public ImageButton getButton() {
        try {
            return findViewById(R.id.onOff);
        } catch (Exception error) {
            errorStack(error);
            return new ImageButton(context);
        }
    }

    public void closeDrawer() {
        try {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } catch (Exception error) {
            errorStack(error);
        }
    }

    public void errorStack(Exception error) {
        try {
            final Writer writer = new StringWriter();
            error.printStackTrace(new PrintWriter(writer));
            final StringBuilder log = new StringBuilder("Android API level - " + Build.VERSION.SDK_INT + "\n" + getString(R.string.app_name) + " - " + BuildConfig.VERSION_NAME + Build.VERSION.SDK_INT + "\nDevice - " + Build.DEVICE + " | " + Build.MODEL + "\nCurrent window - ");

            if (fragmentManager.getFragments().get(0) instanceof FragmentDnewnik) {
                FragmentDnewnik fragmentDnewnik = (FragmentDnewnik) fragmentManager.getFragments().get(0);
                log.append(TextUtils.join("|", fragmentDnewnik.currentWindow));
            } else if (fragmentManager.getFragments().get(0) instanceof FragmentBells) {
                FragmentBells fragmentBells = (FragmentBells) fragmentManager.getFragments().get(0);
                log.append(TextUtils.join("|", fragmentBells.currentWindow));
            } else if (fragmentManager.getFragments().get(0) instanceof FragmentTeachers) {
                FragmentTeachers fragmentTeachers = (FragmentTeachers) fragmentManager.getFragments().get(0);
                log.append(TextUtils.join("|", fragmentTeachers.currentWindow));
            } else if (fragmentManager.getFragments().get(0) instanceof FragmentGrades) {
                FragmentGrades FragmentSettings = (FragmentGrades) fragmentManager.getFragments().get(0);
                log.append(TextUtils.join("|", FragmentSettings.currentWindow));
            } else if (fragmentManager.getFragments().get(0) instanceof FragmentSettings) {
                FragmentSettings fragmentSettings = (FragmentSettings) fragmentManager.getFragments().get(0);
                log.append(TextUtils.join("|", fragmentSettings.currentWindow));
            } else if (fragmentManager.getFragments().get(0) instanceof FragmentHelp) {
                FragmentHelp fragmentHelp = (FragmentHelp) fragmentManager.getFragments().get(0);
                log.append(TextUtils.join("|", fragmentHelp.currentWindow));
            }

            log.append("\n\nError:\n").append(writer.toString());

            new Thread(() -> {
                try {
                    HttpURLConnection con = (HttpURLConnection) new URL("https://students-diary.herokuapp.com/write").openConnection();

                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8));
                    wr.write(log.toString().replace("'", "!"));
                    wr.flush();
                    wr.close();

                    BufferedReader iny = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String output;
                    StringBuilder response = new StringBuilder();

                    while ((output = iny.readLine()) != null) {
                        response.append(output);
                    }
                    iny.close();

                    if (!response.toString().equals("ok"))
                        throw new Exception("no ok");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            File mFolder = new File(context.getExternalFilesDir(null) + "/errors");
            File file = new File(mFolder.getAbsolutePath() + "/" + new Date());
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream write = new FileOutputStream(file);

            write.write(log.toString().getBytes());
            write.close();

            error.printStackTrace();
            MainActivity.ToastMakeText(context, context.getString(R.string.error_not));
        } catch (Exception p) {
            p.printStackTrace();
        }
    }

    public void onPatch(String path) {
        try {
            FragmentSettings fragmentSettings = (FragmentSettings) fragmentManager.getFragments().get(0);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            fragmentSettings.currentWindow = new String[]{"null"};
            new ImporterAsyncTask(path, getApplicationInfo().dataDir, Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath()).execute();
        } catch (Exception e) {
            errorStack(e);
        }
    }

    public void fileChooser() {
        try {
            FIleChooser fIleChooser = new FIleChooser(context);
            linearLayoutBottom.removeAllViews();
            LinearLayout linearLayout = fIleChooser.getRootView();
            linearLayoutBottom.addView(linearLayout);
            linearLayout.findViewById(R.id.scrollFile).post(() -> bottomSheetBehavior.setPeekHeight(linearLayout.findViewById(R.id.scrollFile).getHeight() + 20 * MainActivity.dpSize));
            bottomSheetBehavior.addBottomSheetCallback(((MainActivity) context).bottomSheetCallback);
            try {
                FragmentSettings fragmentSettings = (FragmentSettings) fragmentManager.getFragments().get(0);
                bottomSheetBehavior.setState(Integer.parseInt(fragmentSettings.currentWindow[2]));
            } catch (IllegalArgumentException e) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        } catch (Exception e) {
            errorStack(e);
        }
    }

    public void ClickData(View view) {
        try {
            if (view.getId() == R.id.import_data) {
                fileChooser();
            } else {
                new ExporterAsyncTask(getApplicationInfo().dataDir, Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath(), Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/backups", new Date() + ".zip").execute();
            }
        } catch (Exception error) {
            errorStack(error);
        }
    }

    class ImporterAsyncTask extends AsyncTask<Void, String, Void> {
        private final String sourceFile;
        private final String destinationFolderOne;
        private final String destinationFolderTwo;
        private AlertDialog alertDialog;
        private TextView textView;
        private final static int BUFFER_SIZE = 8192;

        public ImporterAsyncTask(String sourceFile, String destinationFolderOne, String destinationFolderTwo) {
            this.sourceFile = sourceFile;
            this.destinationFolderOne = destinationFolderOne;
            this.destinationFolderTwo = destinationFolderTwo;
        }

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
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
                textView.setText(getString(R.string.applying));
                textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                alertDialog = progressDialog.create();
                Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();
            } catch (Exception error) {
                errorStack(error);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            try {
                super.onProgressUpdate(values);
                textView.setText(getString(R.string.applying) + " " + values[0]);
            } catch (Exception error) {
                errorStack(error);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                super.onPostExecute(aVoid);
                alertDialog.hide();
                Intent mStartActivity = new Intent(context, MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            } catch (Exception error) {
                errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                File oldFile = new File(destinationFolderOne);
                java.io.File[] oldFilesList = oldFile.listFiles();
                for (File file : Objects.requireNonNull(oldFilesList)) {
                    if (file.isDirectory()) {
                        FileUtils.deleteDirectory(file);
                    } else {
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
                oldFile = new File(destinationFolderTwo);
                oldFilesList = oldFile.listFiles();
                for (File file : Objects.requireNonNull(oldFilesList)) {
                    if (!file.getAbsolutePath().contains("backups"))
                        if (file.isDirectory()) {
                            FileUtils.deleteDirectory(file);
                        } else {
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                }
                ZipInputStream zis = null;

                try {
                    zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(sourceFile)));
                    ZipEntry ze;
                    int count;
                    String fileName;
                    String[] folder;
                    File file, dir;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((ze = zis.getNextEntry()) != null) {
                        fileName = ze.getName();

                        fileName = fileName.substring(fileName.indexOf("/") + 1);
                        if (fileName.startsWith("/"))
                            folder = fileName.substring(1).split("/");
                        else folder = fileName.split("/");
                        publishProgress(folder[folder.length - 1]);
                        if (!(folder[0].equals("errors") || folder[0].equals("confirmations")))
                            file = new File(destinationFolderOne, fileName);
                        else
                            file = new File(destinationFolderTwo, fileName);
                        dir = ze.isDirectory() ? file : file.getParentFile();

                        assert dir != null;
                        if (!dir.isDirectory() && !dir.mkdirs())
                            throw new FileNotFoundException("Invalid path: " + dir.getAbsolutePath());
                        if (ze.isDirectory()) continue;
                        FileOutputStream fout = new FileOutputStream(file);
                        try {
                            while ((count = zis.read(buffer)) != -1)
                                fout.write(buffer, 0, count);
                        } finally {
                            fout.close();
                        }

                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } finally {
                    if (zis != null)
                        try {
                            zis.close();
                        } catch (IOException ignored) {

                        }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception error) {
                errorStack(error);
            }
            return null;
        }
    }

    class ExporterAsyncTask extends AsyncTask<Void, String, Void> {
        private final static int BUFFER_SIZE = 8192;
        private final String sourcePathOne;
        private final String sourcePathTwo;
        private String destinationPath;
        private final String destinationFileName;
        private AlertDialog alertDialog;
        private TextView textView;

        public ExporterAsyncTask(String sourcePathOne, String sourcePathTwo, String destinationPath, String destinationFileName) {
            this.sourcePathOne = sourcePathOne;
            this.sourcePathTwo = sourcePathTwo;
            this.destinationPath = destinationPath;
            this.destinationFileName = destinationFileName;
        }

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
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
                textView.setText(getString(R.string.saving));
                textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                alertDialog = progressDialog.create();
                Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();
            } catch (Exception error) {
                errorStack(error);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                super.onPostExecute(aVoid);
                alertDialog.hide();
                ToastMakeText(context, getString(R.string.exportOk));
            } catch (Exception error) {
                errorStack(error);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            try {
                super.onProgressUpdate(values);
                textView.setText(getString(R.string.saving) + " " + values[0]);
            } catch (Exception error) {
                errorStack(error);
            }
        }

        private void zipFile(ZipOutputStream zipOutputStream, ArrayList<File> fileList) {
            try {
                String entryPath;
                BufferedInputStream input;
                for (File file : fileList) {
                    String[] folders = file.getAbsolutePath().split("/");
                    publishProgress(folders[folders.length - 1]);
                    if (file.isDirectory()) {
                        zipFile(zipOutputStream, new ArrayList<>(Arrays.asList(Objects.requireNonNull(file.listFiles()))));
                    } else {
                        byte[] data = new byte[BUFFER_SIZE];
                        FileInputStream fileInputStream = new FileInputStream(file.getPath());
                        input = new BufferedInputStream(fileInputStream, BUFFER_SIZE);
                        entryPath = file.getAbsolutePath().replace(sourcePathOne, "").replace(sourcePathTwo, "");

                        ZipEntry entry = new ZipEntry(entryPath);
                        zipOutputStream.putNextEntry(entry);

                        int count;
                        while ((count = input.read(data, 0, BUFFER_SIZE)) != -1) {
                            zipOutputStream.write(data, 0, count);
                        }
                        input.close();
                    }
                }
            } catch (Exception error) {
                errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                new File(destinationPath).mkdirs();
                FileOutputStream fileOutputStream;
                ZipOutputStream zipOutputStream = null;
                try {
                    if (!destinationPath.endsWith("/"))
                        destinationPath += "/";

                    String destination = destinationPath + destinationFileName;
                    File file = new File(destination);
                    if (!file.exists())
                        file.createNewFile();

                    fileOutputStream = new FileOutputStream(file);
                    zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));

                    File[] fileListTemp = new File(sourcePathTwo).listFiles();
                    ArrayList<File> fileList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(sourcePathOne).listFiles())));
                    for (File temp : Objects.requireNonNull(fileListTemp)) {
                        if (!temp.getAbsolutePath().contains("backups"))
                            fileList.add(temp);
                    }

                    zipFile(zipOutputStream, fileList);

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (zipOutputStream != null)
                        try {
                            zipOutputStream.close();
                        } catch (IOException ignored) {

                        }
                }
            } catch (Exception error) {
                errorStack(error);
            }
            return null;
        }
    }

    public void ClickClear(View view) {
        //Settings
        try {
            final FragmentSettings fragmentSettings = (FragmentSettings) fragmentManager.getFragments().get(0);
            fragmentSettings.currentWindow = new String[]{"clear", String.valueOf(view.getId())};

            final int id;
            final String[] names = new String[]{getString(R.string.app_name), getString(R.string.timetables), getString(R.string.teachers), getString(R.string.grades), getString(R.string.settings), getString(R.string.help)};
            switch (view.getId()) {
                case R.id.bottom_title_zvon:
                    id = 1;
                    break;
                case R.id.bottom_title_ychit:
                    id = 2;
                    break;
                case R.id.bottom_title_ocenki:
                    id = 3;
                    break;
                case R.id.bottom_title_nastroiki:
                    id = 4;
                    break;
                case R.id.bottom_title_spravka:
                    id = 5;
                    break;
                default:
                    id = 0;
                    break;
            }

            final View promptsView = LayoutInflater.from(context).inflate(R.layout.alert_delete_dnewnik, null);
            final AlertDialog.Builder Delete = new AlertDialog.Builder(context);
            Delete.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.alert_delete).setBackground(alertbackground);

            fragmentSettings.alertDialog = Delete.create();

            TextView textTitle = promptsView.findViewById(R.id.title_alert);
            textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            textTitle.setText(context.getString(R.string.deleting));

            TextView textBottomTitle = promptsView.findViewById(R.id.title_bottom_alert);
            textBottomTitle.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textBottomTitle.setText(context.getString(R.string.title_clear_alert) + " " + names[id]);

            final TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
            ButtonCancel.setText(getString(R.string.cancel));
            ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonCancel.setOnClickListener(view12 -> {
                try {
                    fragmentSettings.alertDialog.hide();
                    fragmentSettings.alertDialog = null;
                    fragmentSettings.currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    errorStack(error);
                }
            });
            promptsView.findViewById(R.id.viewBorderOne).setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));

            promptsView.findViewById(R.id.button_three_alert).setVisibility(View.GONE);
            promptsView.findViewById(R.id.viewBorderTwo).setVisibility(View.GONE);
            TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
            ButtonSave.setText(getString(R.string.yes));
            ButtonSave.setOnClickListener(view1 -> {
                try {
                    switch (id) {
                        case 0:
                            File fileDnew = new File(context.getFilesDir() + "/" + "dnewnik");
                            FileUtils.deleteDirectory(fileDnew);
                            break;
                        case 1:
                            String[] day = MainActivity.getResources.getStringArray(R.array.DayTxt);
                            File fileZvon;
                            for (String s : day) {
                                fileZvon = new File(context.getFilesDir() + "/" + s);
                                if (fileZvon.exists()) {
                                    fileZvon.delete();
                                }
                            }
                            editor.remove("SaturdaySettings");
                            editor.remove("notifySettings");
                            editor.remove("Monday");
                            editor.remove("Tuesday");
                            editor.remove("Wednesday");
                            editor.remove("Thursday");
                            editor.remove("Friday");
                            editor.remove("Saturday");
                            editor.apply();
                            break;
                        case 2:
                            File fileYchit = new File(context.getFilesDir() + "/" + "Ychitelia.txt");
                            if (fileYchit.exists()) {
                                fileYchit.delete();
                            }
                            break;
                        case 3:
                            File fileOcenki = new File(context.getFilesDir() + "/" + "ocenki");
                            FileUtils.deleteDirectory(fileOcenki);

                            fileOcenki = new File(context.getExternalFilesDir(null) + "/confirmations");
                            FileUtils.deleteDirectory(fileOcenki);

                            context.getSharedPreferences("Confirmations", MODE_PRIVATE).edit().clear().apply();

                            editor.remove("mesStartOcenki");
                            editor.remove("countChet");
                            editor.remove("PovedSettings");
                            editor.remove("icgradesExamination");
                            editor.remove("icgradesYear");
                            editor.remove("icgradesEnd");
                            editor.apply();

                            break;
                        case 4:
                            File outFile = new File(context.getFilesDir() + "/" + "Themes.txt");
                            if (outFile.exists()) {
                                outFile.delete();
                            }

                            editor.remove("dpSizeSettings");
                            editor.remove("notifySettings");
                            editor.remove("SaturdaySettings");
                            editor.remove("mesStartOcenki");
                            editor.remove("countChet");
                            editor.remove("PovedSettings");
                            editor.remove("icgradesExamination");
                            editor.remove("icgradesYear");
                            editor.remove("icgradesEnd");
                            editor.remove("dafauilt_choose_color");
                            editor.remove("whatSettings");
                            editor.remove("AnimationSettings");
                            editor.remove("BorderAlertSettings");
                            editor.remove("dafauilt_fragment");
                            editor.remove("dpBorderSettings");
                            editor.apply();

                            break;
                        default:
                            File fileSpravka = new File(context.getExternalFilesDir(null) + "/errors");
                            FileUtils.deleteDirectory(fileSpravka);
                            break;
                    }

                    fragmentSettings.alertDialog.hide();
                    fragmentSettings.alertDialog = null;
                    fragmentSettings.currentWindow = new String[]{"null"};
                    getBundles.get(id).clear();
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            ButtonSave.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));

            Objects.requireNonNull(fragmentSettings.alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            fragmentSettings.alertDialog.show();
            Delete.setOnCancelListener(dialog -> {
                try {
                    fragmentSettings.currentWindow = new String[]{"null"};
                    fragmentSettings.alertDialog = null;
                } catch (Exception error) {
                    errorStack(error);
                }
            });

        } catch (Exception error) {
            errorStack(error);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            FragmentGrades fragmentGrades = (FragmentGrades) fragmentManager.getFragments().get(0);
            if (fragmentGrades.adapterGrades.getMediaPlayer() != null) {
                try {
                    fragmentGrades.adapterGrades.releasePlayer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void MicroConfirmation(GridLayout viewConfirm, final String url, int[] sizes) {
        try {
            if (icMicro) {
                icMicro = false;
                viewConfirm.removeView(viewConfirm.findViewById(R.id.photo_linear));
                final FragmentGrades fragmentGrades = (FragmentGrades) fragmentManager.getFragments().get(0);
                ImageButton imageButton = viewConfirm.findViewById(R.id.ConfirmationMicrophone);
                LinearLayout linearLayout = viewConfirm.findViewById(R.id.LinerMicro);
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.addView(LayoutInflater.from(context).inflate(R.layout.micro_button, null));
                if (sizes.length != 2)
                    sizes = new int[]{imageButton.getWidth(), imageButton.getHeight()};

                imageButton.setLayoutParams(new LinearLayout.LayoutParams(sizes[0], sizes[1]));
                fragmentGrades.currentWindow = new String[]{"micro", fragmentGrades.currentWindow[1], String.valueOf(sizes[0]), String.valueOf(sizes[1])};
                TextView text = viewConfirm.findViewById(R.id.TextMicro);
                text.setText(getString(R.string.Voice_recording) + " ");
                final TextView time = viewConfirm.findViewById(R.id.TimeMicro);
                time.setVisibility(View.VISIBLE);
                time.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                time.setText("00:00");
                final TextView buttonStart = linearLayout.findViewById(R.id.button_one_alert);
                buttonStart.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
                buttonStart.setOnClickListener(view -> {
                    try {
                        if (buttonStart.getText().equals(getString(R.string.StartMicro))) {
                            mediaRecorder = new MediaRecorder();
                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                            try {
                                File mFolder = new File(context.getExternalFilesDir(null) + "/confirmations");
                                File file = new File(mFolder.getAbsolutePath() + "/" + url + ".amr");
                                if (!mFolder.exists()) {
                                    mFolder.mkdir();
                                }
                                mediaRecorder.setMaxDuration(3599000);
                                mediaRecorder.setOutputFile(file.toString());
                                mediaRecorder.prepare();
                                mediaRecorder.start();
                                new TimeRecording(time).execute();
                                buttonStart.setText(getString(R.string.PauseMicro));
                            } catch (IllegalStateException e) {
                                MainActivity.ToastMakeText(context, context.getString(R.string.errorMicro));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (buttonStart.getText().equals(getString(R.string.PauseMicro))) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                mediaRecorder.pause();
                                cancelAsyncTask = true;
                                buttonStart.setText(getString(R.string.ResumeMicro));
                            } else
                                MainActivity.ToastMakeText(context, context.getString(R.string.apiError));

                        } else if (buttonStart.getText().equals(getString(R.string.ResumeMicro))) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                mediaRecorder.resume();
                                cancelAsyncTask = false;
                                new TimeRecording(time).execute();
                                buttonStart.setText(getString(R.string.PauseMicro));
                            } else
                                MainActivity.ToastMakeText(context, context.getString(R.string.apiError));
                        }
                    } catch (Exception error) {
                        errorStack(error);
                    }
                });

                TextView buttonEnd = linearLayout.findViewById(R.id.button_two_alert);
                buttonEnd.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
                buttonEnd.setOnClickListener(view -> {
                    try {
                        if (mediaRecorder != null) {
                            cancelAsyncTask = true;
                            mediaRecorder.stop();
                            viewConfirmTemp = null;
                            urlTemp = null;
                            tempSizes = null;
                            fragmentGrades.adapterGrades.onConfirmationSet(url, url + ".amr");
                            icMicro = true;
                            fragmentGrades.currentWindow = new String[]{"null"};
                        }
                    } catch (Exception error) {
                        errorStack(error);
                    }
                });
            }
        } catch (Exception error) {
            errorStack(error);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            super.onActivityResult(requestCode, resultCode, intent);
            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    if (resultCode == RESULT_OK) {
                        FragmentGrades fragmentGrades = (FragmentGrades) fragmentManager.getFragments().get(0);
                        fragmentGrades.adapterGrades.onConfirmationSet(urlTemp, urlTemp + ".jpg");
                        urlTemp = null;
                        fragmentGrades.currentWindow = new String[]{"null"};
                    }
                    break;
                case REQUEST_CODE_CHOOSE_FILE:
                    if (resultCode == RESULT_OK) {
                        new ImporterAsyncTask(Objects.requireNonNull(intent.getData()).getPath(), getApplicationInfo().dataDir, Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath()).execute();
                        for (Bundle b : getBundles) {
                            b.clear();
                        }
                    }
                    break;
            }
        } catch (Exception error) {
            errorStack(error);
        }
    }

    class TimeRecording extends AsyncTask<View, String, Void> {
        TextView time;

        public TimeRecording(TextView time) {
            try {
                this.time = time;
            } catch (Exception error) {
                errorStack(error);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            try {
                super.onProgressUpdate(values);
                time.setText(values[0]);
            } catch (Exception error) {
                errorStack(error);
            }
        }

        @Override
        protected Void doInBackground(View... views) {
            try {
                String[] timeTemp = time.getText().toString().split(":");
                int[] timeValues = new int[]{Integer.parseInt(timeTemp[0]), Integer.parseInt(timeTemp[1])};
                for (int i = 0; i < 3599; i++) {
                    if ((timeValues[1] + 1) >= 60) {
                        timeValues[1] = 0;
                        if (timeValues[0] < 10) {
                            timeValues[0]++;
                            publishProgress("0" + timeValues[0] + ":00");
                        } else {
                            timeValues[0]++;
                            publishProgress(timeValues[0] + timeValues[0] + ":00");
                        }
                    } else if ((timeValues[1] + 1) < 10) {
                        timeValues[1]++;
                        if (timeValues[0] < 10)
                            publishProgress("0" + timeValues[0] + ":0" + timeValues[1]);
                        else
                            publishProgress(timeValues[0] + ":0" + timeValues[1]);
                    } else {
                        timeValues[1]++;
                        if (timeValues[0] < 10)
                            publishProgress("0" + timeValues[0] + ":" + timeValues[1]);
                        else
                            publishProgress(timeValues[0] + ":" + timeValues[1]);
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (Exception ignored) {
                    }
                    if (cancelAsyncTask)
                        break;
                }
            } catch (Exception error) {
                errorStack(error);
            }
            return null;
        }

    }

    public void CameraConfirmation(String url) {
        try {
            urlTemp = url;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File mFolder = new File(context.getExternalFilesDir(null) + "/confirmations");
            File file = new File(mFolder.getAbsolutePath() + "/" + url + ".jpg");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            Uri outputFileUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        } catch (Exception error) {
            errorStack(error);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            switch (requestCode) {
                case REQUEST_CODE_CAMERA_CONF:
                    if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        MainActivity.ToastMakeText(context, context.getString(R.string.NotCameraPermission));
                    } else
                        CameraConfirmation(urlTemp);
                    break;
                case REQUEST_CODE_MICROPHONE_CONF:
                    if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        MainActivity.ToastMakeText(context, context.getString(R.string.NotMicrophonePermission));
                    } else {
                        MicroConfirmation((GridLayout) viewConfirmTemp, urlTemp, tempSizes);
                    }
                    break;
                case REQUEST_CODE_FOLDER_CONF:
                    if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        MainActivity.ToastMakeText(context, context.getString(R.string.NotStoragePermission));
                    }
                    break;
            }
        } catch (Exception error) {
            errorStack(error);
        }
    }

    public void ClickMedia(View view) {
        try {
            Uri adress = null;
            switch (view.getId()) {
                case R.id.button_donate:
                    adress = Uri.parse("https://donate.qiwi.com/payin/kos");
                    break;
                case R.id.gitHub:
                    what = R.id.gitHub;
                    adress = Uri.parse("https://github.com/kos234/Student-s-diary");
                    break;
                case R.id.Gmail:
                    if (what == R.id.gitHub)
                        what = R.id.Gmail;
                    adress = Uri.parse("mailto:kos.progs@gmail.com");
                    break;
                case R.id.transifex:
                    if (what == R.id.Gmail)
                        what = R.id.transifex;
                    adress = Uri.parse("https://www.transifex.com/students-diary/students-diary");
                    break;
                case R.id.vk:
                    if (what == R.id.transifex)
                        what = R.id.vk;
                    adress = Uri.parse("https://vk.com/id388061716");
                    break;
                case R.id.obz_twelve:
                    adress = Uri.parse("https://github.com/kos234/Student-s-diary/blob/test/notifications.md");
                    break;
            }
            if (what == R.id.vk && settings.getBoolean("whatSettings", false)) {
                what = 0;
                View viewAlert = LayoutInflater.from(context).inflate(R.layout.what_layout, null);
                AlertDialog.Builder ConfirmationAlert = new AlertDialog.Builder(context);
                ConfirmationAlert.setView(viewAlert);
                ConfirmationAlert
                        .setCancelable(true);
                VideoView videoView = viewAlert.findViewById(R.id.videoView);
                videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.what));
                videoView.setZOrderOnTop(true);
                videoView.requestFocus();
                videoView.start();
                final AlertDialog alertDialog = ConfirmationAlert.create();
                alertDialog.show();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        try {
                            super.onPostExecute(aVoid);
                            alertDialog.hide();
                        } catch (Exception error) {
                            errorStack(error);
                        }
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Thread.sleep((3 * 60 + 32) * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();
            } else {
                Intent browser = new Intent(Intent.ACTION_VIEW, adress);
                startActivity(browser);
            }
        } catch (Exception error) {
            errorStack(error);
        }
    }

    public void EditTheme(final String[] colorsTheme, int position) {
        try {
            new Thread(() -> {
                colors.put(R.id.custom_icon, Integer.valueOf(colorsTheme[2]));
                colors.put(R.id.custom_border_theme, Integer.valueOf(colorsTheme[3]));
                colors.put(R.id.custom_background, Integer.valueOf(colorsTheme[4]));
                colors.put(R.id.custom_toolbar, Integer.valueOf(colorsTheme[5]));
                colors.put(R.id.custom_toolbar_text, Integer.valueOf(colorsTheme[6]));
                colors.put(R.id.custom_text_light, Integer.valueOf(colorsTheme[7]));
                colors.put(R.id.custom_text_dark, Integer.valueOf(colorsTheme[8]));
                colors.put(R.id.custom_text_hint, Integer.valueOf(colorsTheme[9]));
                colors.put(R.id.custom_cursor, Integer.valueOf(colorsTheme[10]));
                colors.put(R.id.custom_card, Integer.valueOf(colorsTheme[11]));
                colors.put(R.id.custom_bottomBorder, Integer.valueOf(colorsTheme[12]));
                colors.put(R.id.custom_button_add, Integer.valueOf(colorsTheme[13]));
                colors.put(R.id.custom_button_add_plus, Integer.valueOf(colorsTheme[14]));
                colors.put(R.id.custom_button_act, Integer.valueOf(colorsTheme[15]));
                colors.put(R.id.custom_button_arrow, Integer.valueOf(colorsTheme[16]));
                colors.put(R.id.custom_selected_section, Integer.valueOf(colorsTheme[17]));
                colors.put(R.id.custom_progress, Integer.valueOf(colorsTheme[18]));
                colors.put(R.id.custom_not_confirmed, Integer.valueOf(colorsTheme[19]));
                colors.put(R.id.custom_Table_column, Integer.valueOf(colorsTheme[20]));
                colors.put(R.id.custom_notification_on, Integer.valueOf(colorsTheme[21]));
                colors.put(R.id.custom_notification_off, Integer.valueOf(colorsTheme[22]));
                colors.put(R.id.custom_switch_on, Integer.valueOf(colorsTheme[23]));
                colors.put(R.id.custom_switch_off, Integer.valueOf(colorsTheme[24]));
                colors.put(R.id.custom_color_block_choose_background, Integer.valueOf(colorsTheme[25]));
                colors.put(R.id.custom_color_block_choose_border, Integer.valueOf(colorsTheme[26]));
                colors.put(R.id.custom_color_audio_player, Integer.valueOf(colorsTheme[27]));
            }).start();
            ClickSaveThemeType = false;
            positionTheme = position;
        } catch (Exception error) {
            errorStack(error);
        }
    }

    public void ClickCreateCustomTheme(View view) {
        try {
            if (view.getId() == R.id.Card_create) {
                linearLayoutBottom.removeAllViews();
                FragmentSettings fragmentSettings = (FragmentSettings) fragmentManager.getFragments().get(0);
                linearLayoutBottom.addView(fragmentSettings.contentTheme);
                onCreateTheme(bottomSheetBehavior, linearLayoutBottom);
            } else {
                new CreateTheme().execute(colors);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        } catch (Exception error) {
            errorStack(error);
        }
    }

    public void onCreateTheme(BottomSheetBehavior<View> bottomSheetBehavior, FrameLayout linearLayout) {
        try {
            linearLayout.setPadding(0, getStatusBarSize(), 0, 0);
            FragmentSettings fragmentSettings = (FragmentSettings) fragmentManager.getFragments().get(0);
            fragmentSettings.currentWindow = new String[]{"createTheme", String.valueOf(0), String.valueOf(BottomSheetBehavior.STATE_EXPANDED)};
            bottomSheetBehavior.addBottomSheetCallback(((MainActivity) context).bottomSheetCallback);
            try {
                bottomSheetBehavior.setState(Integer.parseInt(fragmentSettings.currentWindow[2]));
            } catch (IllegalArgumentException e) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            new Thread(() -> {
                colors.put(R.id.custom_icon, Current_Theme.getInt("custom_icon", ContextCompat.getColor(context, R.color.custom_icon)));
                colors.put(R.id.custom_border_theme, Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
                colors.put(R.id.custom_background, Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                colors.put(R.id.custom_toolbar, Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));
                colors.put(R.id.custom_toolbar_text, Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)));
                colors.put(R.id.custom_text_light, Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                colors.put(R.id.custom_text_dark, Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
                colors.put(R.id.custom_text_hint, Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
                colors.put(R.id.custom_cursor, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
                colors.put(R.id.custom_card, Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
                colors.put(R.id.custom_bottomBorder, Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));
                colors.put(R.id.custom_button_add, Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                colors.put(R.id.custom_button_add_plus, Current_Theme.getInt("custom_button_add_plus", ContextCompat.getColor(context, R.color.custom_button_add_plus)));
                colors.put(R.id.custom_button_act, Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
                colors.put(R.id.custom_button_arrow, Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)));
                colors.put(R.id.custom_selected_section, Current_Theme.getInt("custom_selected_section", ContextCompat.getColor(context, R.color.custom_selected_section)));
                colors.put(R.id.custom_progress, Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)));
                colors.put(R.id.custom_not_confirmed, Current_Theme.getInt("custom_not_confirmed", ContextCompat.getColor(context, R.color.custom_not_confirmed)));
                colors.put(R.id.custom_Table_column, Current_Theme.getInt("custom_Table_column", ContextCompat.getColor(context, R.color.custom_Table_column)));
                colors.put(R.id.custom_notification_on, Current_Theme.getInt("custom_notification_on", ContextCompat.getColor(context, R.color.custom_notification_on)));
                colors.put(R.id.custom_notification_off, Current_Theme.getInt("custom_notification_off", ContextCompat.getColor(context, R.color.custom_notification_off)));
                colors.put(R.id.custom_switch_on, Current_Theme.getInt("custom_switch_on", ContextCompat.getColor(context, R.color.custom_switch_on)));
                colors.put(R.id.custom_switch_off, Current_Theme.getInt("custom_switch_off", ContextCompat.getColor(context, R.color.custom_switch_off)));
                colors.put(R.id.custom_color_block_choose_background, Current_Theme.getInt("custom_color_block_choose_background", ContextCompat.getColor(context, R.color.custom_color_block_choose_background)));
                colors.put(R.id.custom_color_block_choose_border, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                colors.put(R.id.custom_color_audio_player, Current_Theme.getInt("custom_color_audio_player", ContextCompat.getColor(context, R.color.custom_color_audio_player)));
            }).start();
            TextView textView = linearLayout.findViewById(R.id.custom_name);
            textView.setText(getString(R.string.newTheme));
            linearLayout.findViewById(R.id.custom_icon).setBackgroundColor(Current_Theme.getInt("custom_icon", ContextCompat.getColor(context, R.color.custom_icon)));
            linearLayout.findViewById(R.id.custom_border_theme).setBackgroundColor(Current_Theme.getInt("custom_border_theme", ContextCompat.getColor(context, R.color.custom_border_theme)));
            linearLayout.findViewById(R.id.custom_background).setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            linearLayout.findViewById(R.id.custom_toolbar).setBackgroundColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));
            linearLayout.findViewById(R.id.custom_toolbar_text).setBackgroundColor(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)));
            linearLayout.findViewById(R.id.custom_text_light).setBackgroundColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            linearLayout.findViewById(R.id.custom_text_dark).setBackgroundColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            linearLayout.findViewById(R.id.custom_text_hint).setBackgroundColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
            linearLayout.findViewById(R.id.custom_cursor).setBackgroundColor(Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            linearLayout.findViewById(R.id.custom_card).setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            linearLayout.findViewById(R.id.custom_bottomBorder).setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));
            linearLayout.findViewById(R.id.custom_button_add).setBackgroundColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
            linearLayout.findViewById(R.id.custom_button_add_plus).setBackgroundColor(Current_Theme.getInt("custom_button_add_plus", ContextCompat.getColor(context, R.color.custom_button_add_plus)));
            linearLayout.findViewById(R.id.custom_button_act).setBackgroundColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            linearLayout.findViewById(R.id.custom_button_arrow).setBackgroundColor(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)));
            linearLayout.findViewById(R.id.custom_selected_section).setBackgroundColor(Current_Theme.getInt("custom_selected_section", ContextCompat.getColor(context, R.color.custom_selected_section)));
            linearLayout.findViewById(R.id.custom_progress).setBackgroundColor(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)));
            linearLayout.findViewById(R.id.custom_not_confirmed).setBackgroundColor(Current_Theme.getInt("custom_not_confirmed", ContextCompat.getColor(context, R.color.custom_not_confirmed)));
            linearLayout.findViewById(R.id.custom_Table_column).setBackgroundColor(Current_Theme.getInt("custom_Table_column", ContextCompat.getColor(context, R.color.custom_Table_column)));
            linearLayout.findViewById(R.id.custom_notification_on).setBackgroundColor(Current_Theme.getInt("custom_notification_on", ContextCompat.getColor(context, R.color.custom_notification_on)));
            linearLayout.findViewById(R.id.custom_notification_off).setBackgroundColor(Current_Theme.getInt("custom_notification_off", ContextCompat.getColor(context, R.color.custom_notification_off)));
            linearLayout.findViewById(R.id.custom_switch_on).setBackgroundColor(Current_Theme.getInt("custom_switch_on", ContextCompat.getColor(context, R.color.custom_switch_on)));
            linearLayout.findViewById(R.id.custom_switch_off).setBackgroundColor(Current_Theme.getInt("custom_switch_off", ContextCompat.getColor(context, R.color.custom_switch_off)));
            linearLayout.findViewById(R.id.custom_color_block_choose_background).setBackgroundColor(Current_Theme.getInt("custom_color_block_choose_background", ContextCompat.getColor(context, R.color.custom_color_block_choose_background)));
            linearLayout.findViewById(R.id.custom_color_block_choose_border).setBackgroundColor(Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            linearLayout.findViewById(R.id.custom_color_audio_player).setBackgroundColor(Current_Theme.getInt("custom_color_audio_player", ContextCompat.getColor(context, R.color.custom_color_audio_player)));

            ClickSaveThemeType = true;
        } catch (Exception error) {
            errorStack(error);
        }
    }

    public void HexColor(final View viewColor, final View promptsView, final AlertDialog alertDialog) {
        try {
            promptsView.findViewById(R.id.choose_type).setVisibility(View.GONE);
            promptsView.findViewById(R.id.hex_button).setVisibility(View.VISIBLE);

            TextView textView = promptsView.findViewById(R.id.textView_type_color);
            textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

            promptsView.findViewById(R.id.viewBorder).setBackgroundColor(Current_Theme.getInt("custom_bottomBorder", ContextCompat.getColor(context, R.color.custom_bottomBorder)));


            final EditText editText = promptsView.findViewById(R.id.edit_text_type_color);
            ColorDrawable colorDrawable = (ColorDrawable) viewColor.getBackground();
            editText.setText(Integer.toHexString(colorDrawable.getColor()).substring(1));
            editText.setHintTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            editText.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

            editText.setOnEditorActionListener((textView1, i, keyEvent) -> {
                try {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        if (getCurrentFocus() != null) {
                            View vw = getCurrentFocus();
                            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(vw.getWindowToken(), 0);
                        }
                    }
                } catch (Exception error) {
                    errorStack(error);
                }
                return false;
            });

            TextView ButtonCancel = promptsView.findViewById(R.id.cancel_type_color);
            ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonCancel.setOnClickListener(view -> {
                try {
                    promptsView.findViewById(R.id.choose_type).setVisibility(View.VISIBLE);
                    promptsView.findViewById(R.id.hex_button).setVisibility(View.GONE);
                } catch (Exception error) {
                    errorStack(error);
                }
            });

            TextView ButtonSave = promptsView.findViewById(R.id.save_type_color);
            ButtonSave.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            ButtonSave.setOnClickListener(view -> {
                try {
                    String hexColor;
                    if (editText.getText().toString().equals(""))
                        hexColor = "FFFFFF";
                    else
                        hexColor = editText.getText().toString();

                    viewColor.setBackgroundColor(Color.parseColor("#" + hexColor));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        colors.replace(viewColor.getId(), Color.parseColor("#" + hexColor));
                    else {
                        colors.remove(viewColor.getId());
                        colors.put(viewColor.getId(), Color.parseColor("#" + hexColor));
                    }

                    alertDialog.hide();
                } catch (Exception error) {
                    errorStack(error);
                }
            });
        } catch (Exception error) {
            errorStack(error);
        }
    }

    public void ClickColor(final View viewColor) {
        try {

            final View promptsView = LayoutInflater.from(context).inflate(R.layout.type_color, null);
            final AlertDialog.Builder deleted = new AlertDialog.Builder(context);
            deleted.setView(promptsView);
            deleted.setCancelable(true);

            final AlertDialog alertDialog = deleted.create();

            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.frame_type_color).setBackground(alertbackground);

            if (Objects.requireNonNull(settings.getString("dafauilt_choose_color", getString(R.string.not_chosen))).equals(getString(R.string.not_chosen)) || Objects.requireNonNull(settings.getString("dafauilt_choose_color", getString(R.string.not_chosen))).equals(getString(R.string.HEX_code))) {

                final TextView buttonHex = promptsView.findViewById(R.id.hex_code);

                if (Objects.requireNonNull(settings.getString("dafauilt_choose_color", getString(R.string.not_chosen))).equals(getString(R.string.HEX_code))) {
                    try {
                        HexColor(viewColor, promptsView, alertDialog);
                    } catch (Exception error) {
                        errorStack(error);
                    }
                } else {

                    buttonHex.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
                    buttonHex.setOnClickListener(view -> {
                        try {
                            HexColor(viewColor, promptsView, alertDialog);
                        } catch (Exception error) {
                            errorStack(error);
                        }
                    });

                    TextView buttonChoose = promptsView.findViewById(R.id.choose_color);
                    buttonChoose.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
                    buttonChoose.setOnClickListener(view -> {
                        try {
                            alertDialog.hide();

                            AmbilWarnaDialog colorEdit = new AmbilWarnaDialog(context, colors.get(viewColor.getId()), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                                @Override
                                public void onCancel(AmbilWarnaDialog dialog) {

                                }

                                @Override
                                public void onOk(AmbilWarnaDialog dialog, int color) {
                                    try {
                                        viewColor.setBackgroundColor(color);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                            colors.replace(viewColor.getId(), color);
                                        else {
                                            colors.remove(viewColor.getId());
                                            colors.put(viewColor.getId(), color);
                                        }
                                    } catch (Exception error) {
                                        errorStack(error);
                                    }
                                }
                            });
                            GradientDrawable alertbackground1 = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
                            Objects.requireNonNull(alertbackground1).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                            if (settings.getBoolean("BorderAlertSettings", false))
                                alertbackground1.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));


                            colorEdit.show();
                        } catch (Exception error) {
                            errorStack(error);
                        }
                    });
                }
                Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();


            } else {
                AmbilWarnaDialog colorEdit = new AmbilWarnaDialog(context, colors.get(viewColor.getId()), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {

                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        try {
                            viewColor.setBackgroundColor(color);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                colors.replace(viewColor.getId(), color);
                            else {
                                colors.remove(viewColor.getId());
                                colors.put(viewColor.getId(), color);
                            }
                        } catch (Exception error) {
                            errorStack(error);
                        }
                    }
                });

                colorEdit.show();
            }
        } catch (Exception error) {
            errorStack(error);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void setCursorColorsQ(EditText view, @ColorInt int color) {
        Objects.requireNonNull(view.getTextCursorDrawable()).mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(view.getTextSelectHandle()).mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(view.getTextSelectHandleLeft()).mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(view.getTextSelectHandleRight()).mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        view.setHighlightColor(color);
        view.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{android.R.attr.state_enabled}}, new int[]{color}));
    }

    public static void setCursorColor(EditText view, @ColorInt int color) {
        try {
            view.setHighlightColor(color);
            view.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{android.R.attr.state_enabled}}, new int[]{color}));
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(view);

            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(view);

            Drawable drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            Objects.requireNonNull(drawable).setColorFilter(color, PorterDuff.Mode.SRC_IN);
            field = Objects.requireNonNull(editor).getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, new Drawable[]{drawable, drawable});

            field = TextView.class.getDeclaredField("mTextSelectHandleRes");
            field.setAccessible(true);
            drawableResId = field.getInt(view);
            drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            Objects.requireNonNull(drawable).setColorFilter(color, PorterDuff.Mode.SRC_IN);
            field = Objects.requireNonNull(editor).getClass().getDeclaredField("mSelectHandleCenter");
            field.setAccessible(true);
            field.set(editor, drawable);

            field = TextView.class.getDeclaredField("mTextSelectHandleLeftRes");
            field.setAccessible(true);
            drawableResId = field.getInt(view);
            drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            Objects.requireNonNull(drawable).setColorFilter(color, PorterDuff.Mode.SRC_IN);
            field = Objects.requireNonNull(editor).getClass().getDeclaredField("mSelectHandleLeft");
            field.setAccessible(true);
            field.set(editor, drawable);

            field = TextView.class.getDeclaredField("mTextSelectHandleRightRes");
            field.setAccessible(true);
            drawableResId = field.getInt(view);
            drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            Objects.requireNonNull(drawable).setColorFilter(color, PorterDuff.Mode.SRC_IN);
            field = Objects.requireNonNull(editor).getClass().getDeclaredField("mSelectHandleRight");
            field.setAccessible(true);
            field.set(editor, drawable);
        } catch (Exception error) {
            ((MainActivity) view.getContext()).errorStack(error);
        }
    }

    class CreateTheme extends AsyncTask<HashMap<Integer, Integer>, String, Void> {
        AlertDialog alertDialog;
        TextView textView;
        int id_theme = -1;

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
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
                textView.setText(getString(R.string.Saving_theme));
                textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                alertDialog = progressDialog.create();
                Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();
            } catch (Exception error) {
                errorStack(error);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                alertDialog.hide();
                FragmentSettings fragmentSettings = (FragmentSettings) fragmentManager.getFragments().get(0);
                if (ClickSaveThemeType || id_theme == settings.getInt("id_current_theme", 1)) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    fragmentSettings.currentWindow = new String[]{"null"};
                    fragmentSettings.NotifyAdapter(positionTheme, TempNameTheme);
                }
            } catch (Exception error) {
                errorStack(error);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            try {
                super.onProgressUpdate(values);
                textView.setText(values[0]);
            } catch (Exception error) {
                errorStack(error);
            }
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(HashMap<Integer, Integer>... hashMaps) {
            try {
                StringBuilder stringBuffer = new StringBuilder();
                EditText editText = findViewById(R.id.custom_name);
                if (editText.getText().toString().equals(""))
                    TempNameTheme = getString(R.string.WhiteTheme);
                else TempNameTheme = editText.getText().toString();

                try {
                    FileInputStream read = context.openFileInput("Themes.txt");
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String temp_read;
                    int wtite_num = 0;
                    while ((temp_read = bufferedReader.readLine()) != null) {
                        if (!ClickSaveThemeType && (positionTheme == wtite_num)) {
                            stringBuffer.append(temp_read.split("=")[0]).append("=").append(TempNameTheme).append("=").append(hashMaps[0].get(R.id.custom_icon)).append("=").append(hashMaps[0].get(R.id.custom_border_theme)).append("=").append(hashMaps[0].get(R.id.custom_background)).append("=").append(hashMaps[0].get(R.id.custom_toolbar)).append("=").append(hashMaps[0].get(R.id.custom_toolbar_text)).append("=").append(hashMaps[0].get(R.id.custom_text_light)).append("=").append(hashMaps[0].get(R.id.custom_text_dark)).append("=").append(hashMaps[0].get(R.id.custom_text_hint)).append("=").append(hashMaps[0].get(R.id.custom_cursor)).append("=").append(hashMaps[0].get(R.id.custom_card)).append("=").append(hashMaps[0].get(R.id.custom_bottomBorder)).append("=").append(hashMaps[0].get(R.id.custom_button_add)).append("=").append(hashMaps[0].get(R.id.custom_button_add_plus)).append("=").append(hashMaps[0].get(R.id.custom_button_act)).append("=").append(hashMaps[0].get(R.id.custom_button_arrow)).append("=").append(hashMaps[0].get(R.id.custom_selected_section)).append("=").append(hashMaps[0].get(R.id.custom_progress)).append("=").append(hashMaps[0].get(R.id.custom_not_confirmed)).append("=").append(hashMaps[0].get(R.id.custom_Table_column)).append("=").append(hashMaps[0].get(R.id.custom_notification_on)).append("=").append(hashMaps[0].get(R.id.custom_notification_off)).append("=").append(hashMaps[0].get(R.id.custom_switch_on)).append("=").append(hashMaps[0].get(R.id.custom_switch_off)).append("=").append(hashMaps[0].get(R.id.custom_color_block_choose_background)).append("=").append(hashMaps[0].get(R.id.custom_color_block_choose_border)).append("=").append(hashMaps[0].get(R.id.custom_color_audio_player)).append("\n");

                            id_theme = Integer.parseInt(temp_read.split("=")[0]);
                        } else
                            stringBuffer.append(temp_read).append("\n");

                        wtite_num = wtite_num + 1;
                    }
                    bufferedReader.close();
                    reader.close();
                    read.close();
                } catch (IOException | NullPointerException ignored) {
                }

                int Generate_id = 0;
                if (ClickSaveThemeType) {
                    Generate_id = new Random().nextInt(1000) + 142132;
                    editor.putInt("id_current_theme", Generate_id);
                    editor.apply();
                }

                try {
                    FileOutputStream write = context.openFileOutput("Themes.txt", MODE_PRIVATE);
                    String temp_write = stringBuffer.toString();

                    if (ClickSaveThemeType) {
                        temp_write = temp_write + Generate_id + "=" + TempNameTheme + "=" +
                                hashMaps[0].get(R.id.custom_icon) + "=" +
                                hashMaps[0].get(R.id.custom_border_theme) + "=" +
                                hashMaps[0].get(R.id.custom_background) + "=" +
                                hashMaps[0].get(R.id.custom_toolbar) + "=" +
                                hashMaps[0].get(R.id.custom_toolbar_text) + "=" +
                                hashMaps[0].get(R.id.custom_text_light) + "=" +
                                hashMaps[0].get(R.id.custom_text_dark) + "=" +
                                hashMaps[0].get(R.id.custom_text_hint) + "=" +
                                hashMaps[0].get(R.id.custom_cursor) + "=" +
                                hashMaps[0].get(R.id.custom_card) + "=" +
                                hashMaps[0].get(R.id.custom_bottomBorder) + "=" +
                                hashMaps[0].get(R.id.custom_button_add) + "=" +
                                hashMaps[0].get(R.id.custom_button_add_plus) + "=" +
                                hashMaps[0].get(R.id.custom_button_act) + "=" +
                                hashMaps[0].get(R.id.custom_button_arrow) + "=" +
                                hashMaps[0].get(R.id.custom_selected_section) + "=" +
                                hashMaps[0].get(R.id.custom_progress) + "=" +
                                hashMaps[0].get(R.id.custom_not_confirmed) + "=" +
                                hashMaps[0].get(R.id.custom_Table_column) + "=" +
                                hashMaps[0].get(R.id.custom_notification_on) + "=" +
                                hashMaps[0].get(R.id.custom_notification_off) + "=" +
                                hashMaps[0].get(R.id.custom_switch_on) + "=" +
                                hashMaps[0].get(R.id.custom_switch_off) + "=" +
                                hashMaps[0].get(R.id.custom_color_block_choose_background) + "=" +
                                hashMaps[0].get(R.id.custom_color_block_choose_border) + "=" +
                                hashMaps[0].get(R.id.custom_color_audio_player);
                    }


                    write.write(temp_write.getBytes());
                    write.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (ClickSaveThemeType || id_theme == settings.getInt("id_current_theme", 1)) {
                    publishProgress(getString(R.string.Apply_Theme));

                    SharedPreferences.Editor editorColor = Current_Theme.edit();

                    editorColor.putInt("custom_icon", hashMaps[0].get(R.id.custom_icon));
                    editorColor.putInt("custom_border_theme", hashMaps[0].get(R.id.custom_border_theme));
                    editorColor.putInt("custom_background", hashMaps[0].get(R.id.custom_background));
                    editorColor.putInt("custom_toolbar", hashMaps[0].get(R.id.custom_toolbar));
                    editorColor.putInt("custom_toolbar_text", hashMaps[0].get(R.id.custom_toolbar_text));
                    editorColor.putInt("custom_text_light", hashMaps[0].get(R.id.custom_text_light));
                    editorColor.putInt("custom_text_dark", hashMaps[0].get(R.id.custom_text_dark));
                    editorColor.putInt("custom_text_hint", hashMaps[0].get(R.id.custom_text_hint));
                    editorColor.putInt("custom_cursor", hashMaps[0].get(R.id.custom_cursor));
                    editorColor.putInt("custom_card", hashMaps[0].get(R.id.custom_card));
                    editorColor.putInt("custom_bottomBorder", hashMaps[0].get(R.id.custom_bottomBorder));
                    editorColor.putInt("custom_button_add", hashMaps[0].get(R.id.custom_button_add));
                    editorColor.putInt("custom_button_add_plus", hashMaps[0].get(R.id.custom_button_add_plus));
                    editorColor.putInt("custom_button_act", hashMaps[0].get(R.id.custom_button_act));
                    editorColor.putInt("custom_button_arrow", hashMaps[0].get(R.id.custom_button_arrow));
                    editorColor.putInt("custom_selected_section", hashMaps[0].get(R.id.custom_selected_section));
                    editorColor.putInt("custom_progress", hashMaps[0].get(R.id.custom_progress));
                    editorColor.putInt("custom_not_confirmed", hashMaps[0].get(R.id.custom_not_confirmed));
                    editorColor.putInt("custom_Table_column", hashMaps[0].get(R.id.custom_Table_column));
                    editorColor.putInt("custom_notification_on", hashMaps[0].get(R.id.custom_notification_on));
                    editorColor.putInt("custom_notification_off", hashMaps[0].get(R.id.custom_notification_off));
                    editorColor.putInt("custom_switch_on", hashMaps[0].get(R.id.custom_switch_on));
                    editorColor.putInt("custom_switch_off", hashMaps[0].get(R.id.custom_switch_off));
                    editorColor.putInt("custom_color_block_choose_background", hashMaps[0].get(R.id.custom_color_block_choose_background));
                    editorColor.putInt("custom_color_block_choose_border", hashMaps[0].get(R.id.custom_color_block_choose_border));
                    editorColor.putInt("custom_color_audio_player", hashMaps[0].get(R.id.custom_color_audio_player));

                    editorColor.apply();

                }
            } catch (Exception error) {
                errorStack(error);
            }

            return null;
        }
    }

    public class ChangeTheme extends AsyncTask<String[], Void, Void> {
        AlertDialog alertDialog;

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
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
                TextView textView = promptsView.findViewById(R.id.textLoading);
                textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                textView.setText(getString(R.string.Apply_Theme));
                alertDialog = progressDialog.create();
                Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();
            } catch (Exception error) {
                errorStack(error);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                super.onPostExecute(aVoid);
                alertDialog.hide();
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } catch (Exception error) {
                errorStack(error);
            }
        }


        @Override
        protected Void doInBackground(String[]... strings) {
            try {
                editor.putInt("id_current_theme", Integer.parseInt(strings[0][0]));
                editor.apply();

                SharedPreferences.Editor editorColor = Current_Theme.edit();
                editorColor.putInt("custom_icon", Integer.parseInt(strings[0][2]));
                editorColor.putInt("custom_border_theme", Integer.parseInt(strings[0][3]));
                editorColor.putInt("custom_background", Integer.parseInt(strings[0][4]));
                editorColor.putInt("custom_toolbar", Integer.parseInt(strings[0][5]));
                editorColor.putInt("custom_toolbar_text", Integer.parseInt(strings[0][6]));
                editorColor.putInt("custom_text_light", Integer.parseInt(strings[0][7]));
                editorColor.putInt("custom_text_dark", Integer.parseInt(strings[0][8]));
                editorColor.putInt("custom_text_hint", Integer.parseInt(strings[0][9]));
                editorColor.putInt("custom_cursor", Integer.parseInt(strings[0][10]));
                editorColor.putInt("custom_card", Integer.parseInt(strings[0][11]));
                editorColor.putInt("custom_bottomBorder", Integer.parseInt(strings[0][12]));
                editorColor.putInt("custom_button_add", Integer.parseInt(strings[0][13]));
                editorColor.putInt("custom_button_add_plus", Integer.parseInt(strings[0][14]));
                editorColor.putInt("custom_button_act", Integer.parseInt(strings[0][15]));
                editorColor.putInt("custom_button_arrow", Integer.parseInt(strings[0][16]));
                editorColor.putInt("custom_selected_section", Integer.parseInt(strings[0][17]));
                editorColor.putInt("custom_progress", Integer.parseInt(strings[0][18]));
                editorColor.putInt("custom_not_confirmed", Integer.parseInt(strings[0][19]));
                editorColor.putInt("custom_Table_column", Integer.parseInt(strings[0][20]));
                editorColor.putInt("custom_notification_on", Integer.parseInt(strings[0][21]));
                editorColor.putInt("custom_notification_off", Integer.parseInt(strings[0][22]));
                editorColor.putInt("custom_switch_on", Integer.parseInt(strings[0][23]));
                editorColor.putInt("custom_switch_off", Integer.parseInt(strings[0][24]));
                editorColor.putInt("custom_color_block_choose_background", Integer.parseInt(strings[0][25]));
                editorColor.putInt("custom_color_block_choose_border", Integer.parseInt(strings[0][26]));
                editorColor.putInt("custom_color_audio_player", Integer.parseInt(strings[0][27]));

                editorColor.apply();
            } catch (Exception error) {
                errorStack(error);
            }
            return null;
        }
    }
}