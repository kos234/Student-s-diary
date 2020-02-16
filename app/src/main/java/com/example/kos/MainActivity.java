package com.example.kos;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import android.text.format.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import yuku.ambilwarna.AmbilWarnaDialog;

import static android.app.NotificationManager.IMPORTANCE_NONE;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    final Context context = this;
    private AlertDialog alertDialogConfirmation;
    private SharedPreferences settings, Current_Theme, Confirmed;
    private SharedPreferences.Editor editor,editorConfirmed;
    private int what, numStolbWrite = 0, numZapic = 1, positionTheme = 0;
    private FragmentManager fragmentManager;
    public List<Fragment> getFragment;
    private final Handler handler = new Handler();
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private NotificationManager notificationManager;
    private static final int NOTIFY_ID = 1, REQUEST_CODE_CAMERA = 1, REQUEST_CODE_MICROPHONE_CONF = 3, REQUEST_CODE_CAMERA_CONF = 4, REQUEST_CODE_FOLDER_CONF = 5;
    private static final String CHANNEL_ID = "NotificationTime";
    private String url;
    public String TempNameTheme;
    private NavigationView navigationView;
    private boolean cancelAsyncTask = false, ic_micro = true, ClickSaveThemeType = true;
    private View viewConfirm;
    public int color;
    private HashMap<Integer, Integer> colors = new HashMap();
    private TextView ConfirmationTextView;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new onStart().execute();

        /**
         * Перевести в кастомизацию цвет столбцов при подтверждении и загрузки при смене тем
         * Документация
         * Фрагмент по умолчанию
         * попробовать изменить цвет текста в календаре
         * Сделать шаблоны тем, шо типа если включена темная тема был выбран шаблон темной темы
         */

    }

    class onStart extends AsyncTask<Void,ConstrOnStart,Void>{
        LinearLayout start_image;
        int menuSize;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            settings = getSharedPreferences("Settings", MODE_PRIVATE);
            Current_Theme = getSharedPreferences("Current_Theme", MODE_PRIVATE);
            drawerLayout = findViewById(R.id.Drawer);
            LayoutInflater li = LayoutInflater.from(context);
            start_image = (LinearLayout) li.inflate(R.layout.start_display, null);
            start_image.setBackgroundColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));
            TextView textTemp = start_image.findViewById(R.id.title_start_display);
            textTemp.setTextColor(Current_Theme.getInt("custom_toolbar_text", ContextCompat.getColor(context, R.color.custom_toolbar_text)));
            ProgressBar progressBar = start_image.findViewById(R.id.progress_start_display);
            int color = Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress));
            if(color == ContextCompat.getColor(context, R.color.custom_progress))
                color = Color.WHITE;
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            drawerLayout.addView(start_image);
            navigationView = findViewById(R.id.navigation);
            menuSize = navigationView.getMenu().size();
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Current_Theme.getInt("custom_toolbar", ContextCompat.getColor(context, R.color.custom_toolbar)));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            drawerLayout.setBackgroundColor(Current_Theme.getInt("custom_background",ContextCompat.getColor(context, R.color.custom_background)));
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
            new MyThread().start();
            getFragment = fragmentManager.getFragments();
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Current_Theme.getInt("custom_notification_bar", ContextCompat.getColor(context, R.color.custom_notification_bar)));
        }

        @Override
        protected void onProgressUpdate(ConstrOnStart... values) {
            super.onProgressUpdate(values);
            if(values[0].getId() == 1) {
                navigationView.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_FOLDER_CONF);
                }
            }

            else if(values[0].getId() == 2)
                navigationView.getMenu().getItem(values[0].getItem()).setTitle(values[0].getS());

            else if(values[0].getId() == 3){
                navigationView.setItemIconTintList(new ColorStateList(new int[][]{ new int[]{android.R.attr.state_enabled} }, new int[] {Current_Theme.getInt("custom_button_arrow",ContextCompat.getColor(context, R.color.custom_button_arrow))}));
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                            int IdHideFragment;
                            switch (settings.getString("Fragment", "Dnewnik")) {
                                case "Ychitelia":
                                    IdHideFragment = 1;
                                    break;
                                case "Znonki":
                                    IdHideFragment = 2;
                                    break;
                                case "Ocenki":
                                    IdHideFragment = 3;
                                    break;
                                case "Nastroiki":
                                    IdHideFragment = 4;
                                    break;
                                case "Spravka":
                                    IdHideFragment = 5;
                                    break;
                                default:
                                    IdHideFragment = 0;
                                    break;
                            }


                            fragmentManager.beginTransaction().hide(fragmentManager.getFragments().get(IdHideFragment)).commit();

                            int IdShowFragment;
                            switch (menuItem.getItemId()){
                                case R.id.Ychetel:
                                    IdShowFragment = 1;
                                    editor.putString("Fragment","Ychitelia" );
                                    break;
                                case R.id.Zvonki:
                                        IdShowFragment = 2;
                                    editor.putString("Fragment","Znonki" );
                                    break;
                                case R.id.Ocenki:
                                    OcenkiFragment ocenkiFragment = (OcenkiFragment) getFragment.get(3);
                                    ocenkiFragment.cheakStart();
                                        IdShowFragment = 3;
                                    editor.putString("Fragment","Ocenki" );
                                    break;
                                case R.id.Nastroiki:
                                        IdShowFragment = 4;
                                    editor.putString("Fragment","Nastroiki" );
                                    break;
                                case R.id.Spravka:
                                        IdShowFragment = 5;
                                    editor.putString("Fragment","Spravka" );
                                    break;
                                default:
                                        IdShowFragment = 0;
                                    editor.putString("Fragment","Dnewnik" );
                                    break;
                            }


                            fragmentManager.beginTransaction().show(fragmentManager.getFragments().get(IdShowFragment)).commit();
                            menuItem.setChecked(true);
                            drawerLayout = findViewById(R.id.Drawer);
                            drawerLayout.closeDrawer(Gravity.LEFT);
                            editor.apply();

                        return false;
                    }
                });
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Confirmed = getSharedPreferences("Confirmed", MODE_PRIVATE);
            editor = settings.edit();
            editorConfirmed = Confirmed.edit();

            publishProgress(new ConstrOnStart(1));

            for(int i = 0; i < menuSize; i++){
                String name;
                switch (i){
                    case 1:
                        name = getString(R.string.timetables);
                        break;
                    case 2:
                        name = getString(R.string.teachers);
                        break;
                    case 3:
                        name = getString(R.string.grades);
                        break;
                    case 4:
                        name = getString(R.string.settings);
                        break;
                    case 5:
                        name = getString(R.string.help);
                        break;
                    default:
                        name = getString(R.string.app_name);
                        break;
                }


                    SpannableString s = new SpannableString(name);
                    s.setSpan(new ForegroundColorSpan(Current_Theme.getInt("custom_button_arrow",ContextCompat.getColor(context, R.color.custom_button_arrow))), 0, s.length(), 0);
                    publishProgress(new ConstrOnStart(2,i,s));
                }


            publishProgress(new ConstrOnStart(3));

            Class fragmentClass;
            fragmentManager = getSupportFragmentManager();
            for(int s = 0; s < 6; s++) {
                boolean Invisibly = true;
                switch (s) {
                    case 1:
                        fragmentClass = YchiteliaFragment.class;
                        if(settings.getString("Fragment", "Dnewnik").equals("Ychitelia"))
                            Invisibly = false;
                        break;
                    case 2:
                        fragmentClass = ZnonkiFragment.class;
                        if(settings.getString("Fragment", "Dnewnik").equals("Znonki"))
                            Invisibly = false;
                        break;
                    case 3:
                        fragmentClass = OcenkiFragment.class;
                        if(settings.getString("Fragment", "Dnewnik").equals("Ocenki"))
                            Invisibly = false;
                        break;
                    case 4:
                        fragmentClass = NastroikiFragment.class;
                        if(settings.getString("Fragment", "Dnewnik").equals("Nastroiki"))
                            Invisibly = false;
                        break;
                    case 5:
                        fragmentClass = SpravkaFragment.class;
                        if(settings.getString("Fragment", "Dnewnik").equals("Spravka"))
                            Invisibly = false;
                        break;
                    default:
                        fragmentClass = DnewnikFragment.class;
                        if(settings.getString("Fragment", "Dnewnik").equals("Dnewnik"))
                            Invisibly = false;
                        break;
                }
                Fragment fragmentActiv = null;

                try {
                    fragmentActiv = (Fragment) fragmentClass.newInstance();
                    fragmentManager.beginTransaction().add(R.id.Smena, fragmentActiv).commit();
                    if(Invisibly)
                        fragmentManager.beginTransaction().hide(fragmentActiv).commit();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }
    }

    public List<Fragment> getDnewnikFragment(){
        return getFragment;
    }


    public void openDrawer() {
        DrawerLayout drawer = findViewById(R.id.Drawer);
        drawer.openDrawer(Gravity.LEFT);
    }

    public void ClickTab (View view){
        int textViewId =  R.id.ocenka_one, editTextId = R.id.ocenka_edit_one, numStolb = R.id.numStolb_1;

        switch (view.getId()){
            case R.id.frame_ocenki_two:
                textViewId = R.id.ocenka_two;
                editTextId = R.id.ocenka_edit_two;
                numZapic = 2;
                numStolb = R.id.numStolb_2;
                break;

            case R.id.frame_ocenki_three:
                textViewId = R.id.ocenka_three;
                editTextId = R.id.ocenka_edit_three;
                numZapic = 3;
                numStolb = R.id.numStolb_3;
                break;

            case R.id.frame_ocenki_four:
                textViewId = R.id.ocenka_four;
                editTextId = R.id.ocenka_edit_four;
                numZapic = 4;
                numStolb = R.id.numStolb_4;
                break;

            case R.id.frame_ocenki_year:
                textViewId = R.id.ocenka_year;
                editTextId = R.id.ocenka_edit_year;
                numZapic = 5;
                numStolb = R.id.numStolb_5;
                break;

            case R.id.frame_ocenki_examination:
                textViewId = R.id.ocenka_examination;
                editTextId = R.id.ocenka_edit_examination;
                numZapic = 6;
                numStolb = R.id.numStolb_6;
                break;

            case R.id.frame_ocenki_end:
                textViewId = R.id.ocenka_end;
                editTextId = R.id.ocenka_edit_end;
                numZapic = 7;
                numStolb = R.id.numStolb_7;
                break;
        }


        String[] ConfirmationValue = Confirmed.getString((settings.getInt("endUrl",2020) - 1) + " - " + settings.getInt("endUrl",2020),getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed)).split("=");

        if(ConfirmationValue[numZapic-1].equals(getString(R.string.Not_Confirmed))){

            final TextView textView = view.findViewById(textViewId);
            final EditText editText = view.findViewById(editTextId);
        final TextView numStolbik = view.findViewById(numStolb);
        final int numZapicFinal = numZapic;
        final String url = (settings.getInt("endUrl",2020) - 1) + " - " + settings.getInt("endUrl",2020);

        editText.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
        editText.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
        setCursorColor(editText,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
        setCursorPointerColor(editText,Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
        editText.setVisibility(View.VISIBLE);
        final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        editText.requestFocus();

            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        //Hide keyboard
                        if (getCurrentFocus() != null) {
                            View vw = getCurrentFocus();
                            inputMethodManager.hideSoftInputFromWindow(vw.getWindowToken(), 0);
                            textView.setText(editText.getText());
                            editText.setVisibility(View.INVISIBLE);

                            StringBuffer stringBuffer = new StringBuffer();
                            try {
                                File mFolder = new File(context.getFilesDir() + "/ocenki");
                                File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                                if (!mFolder.exists()) {
                                    mFolder.mkdir();
                                }
                                if (!FileTxt.exists()) {
                                    FileTxt.createNewFile();
                                }

                                FileInputStream read =  new FileInputStream(FileTxt);
                                InputStreamReader reader = new InputStreamReader(read);
                                BufferedReader bufferedReader = new BufferedReader(reader);

                                String temp_read;
                                String[] help;
                                String delimeter = "=";
                                int count = 1;



                                while ((temp_read = bufferedReader.readLine()) != null) {
                                    help = temp_read.split(delimeter);
                                    if(count == Integer.parseInt(numStolbik.getText().toString())){
                                        if(editText.getText().toString().equals(""))
                                            help[numZapicFinal] = " ";
                                            else
                                       help[numZapicFinal] = editText.getText().toString();
                                       for(int i = 0;i <= 7;i++){
                                           stringBuffer.append(help[i] + "=");
                                       }
                                       stringBuffer.append("\n");
                                    } else
                                    stringBuffer.append(temp_read).append("\n");
                                    count ++;


                                }
                            } catch (FileNotFoundException q) {
                               
                            } catch (IOException j) {
                                
                            }
                            color = Color.RED;
                            new ReplaceColorStolb().execute();

                            try {
                                File mFolder = new File(context.getFilesDir() + "/ocenki");
                                File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                                if (!mFolder.exists()) {
                                    mFolder.mkdir();
                                }
                                if (!FileTxt.exists()) {
                                    FileTxt.createNewFile();
                                }

                                FileOutputStream write =  new FileOutputStream(FileTxt);
                                String temp_write = stringBuffer.toString();

                                write.write(temp_write.getBytes());
                                write.close();
                            } catch (FileNotFoundException p) {
                                p.printStackTrace();
                            } catch (IOException a) {
                                a.printStackTrace();
                            }
                        }
                    }
                    return false;
                }
            });

    }}


    class ReplaceColorStolb extends AsyncTask<Void,TableRow,Void>{
        @Override
        protected void onProgressUpdate(TableRow... values) {
            super.onProgressUpdate(values);
            int FrameId = R.id.frame_ocenki_one;

            switch (numZapic){
                case 2:
                    FrameId = R.id.frame_ocenki_two;
                    break;

                case 3:
                    FrameId = R.id.frame_ocenki_three;
                    break;

                case 4:
                    FrameId = R.id.frame_ocenki_four;
                    break;

                case 5:
                    FrameId = R.id.frame_ocenki_year;
                    break;

                case 6:
                    FrameId = R.id.frame_ocenki_examination;
                    break;

                case 7:
                    FrameId = R.id.frame_ocenki_end;
                    break;
            }

            FrameLayout frameLayout;
            frameLayout = values[0].findViewById(FrameId);
            frameLayout.setBackgroundColor(color);
        }

        @Override
        protected Void doInBackground(Void... strings) {
                TableRow tableRow;
                tableRow = findViewById(R.id.barOcenki);
                publishProgress(tableRow);
                for(int i = 1; i <= settings.getInt("PredmetiSize", 2); i++){
                    tableRow = findViewById(10203040 + i);
                    publishProgress(tableRow);
                }
                tableRow = findViewById(R.id.confirmationBar);
                publishProgress(tableRow);
            return null;
        }
    }

    public void Confirmation (View view){
        ConfirmationTextView = (TextView) view;

        switch (ConfirmationTextView.getId()){
            case R.id.two:
                numStolbWrite = 1;
                break;
            case R.id.three:
                numStolbWrite = 2;
                break;
            case R.id.four:
                numStolbWrite = 3;
                break;
            case R.id.year:
                numStolbWrite = 4;
                break;
            case R.id.examination:
                numStolbWrite = 5;
                break;
            case R.id.end:
                numStolbWrite = 6;
                break;
        }

        if(ConfirmationTextView.getText().equals(getString(R.string.Not_Confirmed))){

            final LayoutInflater li = LayoutInflater.from(context);
            viewConfirm = li.inflate(R.layout.confirmation_add , null);
            AlertDialog.Builder ConfirmationAlert = new AlertDialog.Builder(context);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
            alertbackground.setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if(settings.getBoolean("BorderAlertSettings",false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            viewConfirm.findViewById(R.id.liner_conf_add).setBackground(alertbackground);
            ImageButton ConfirmationMicrophone = viewConfirm.findViewById(R.id.ConfirmationMicrophone);
            Drawable drawableMic =  ContextCompat.getDrawable(context, R.drawable.ic_microphone);
            drawableMic.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_ATOP);
            ConfirmationMicrophone.setImageDrawable(drawableMic);
            GradientDrawable drawableOne = (GradientDrawable) getResources().getDrawable(R.drawable.shape);
            drawableOne.setColor(Current_Theme.getInt("custom_color_block_choose_background", ContextCompat.getColor(context, R.color.custom_color_block_choose_background)));
            drawableOne.setStroke(4, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            ConfirmationMicrophone.setBackground(drawableOne);
            ConfirmationMicrophone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.RECORD_AUDIO},REQUEST_CODE_MICROPHONE_CONF);
                    }else{
                        MicroConfirmation();
                    }
                }
            });

            ImageButton ConfirmationCamera = viewConfirm.findViewById(R.id.ConfirmationCamera);
            Drawable drawableCamera =  ContextCompat.getDrawable(context, R.drawable.ic_camera);
            drawableCamera.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_ATOP);
            ConfirmationCamera.setImageDrawable(drawableCamera);
            ConfirmationCamera.setBackground(drawableOne);
            ConfirmationCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.CAMERA},REQUEST_CODE_CAMERA_CONF);
                    }else{
                        CameraConfirmarion();
                    }
                }
            });

            ConfirmationAlert.setView(viewConfirm);
            ConfirmationAlert
                    .setCancelable(true);

            TextView textView = viewConfirm.findViewById(R.id.TextMicro);
            textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textView = viewConfirm.findViewById(R.id.text_camera_conf);
            textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

           alertDialogConfirmation = ConfirmationAlert.create();

            //и отображаем его:
            alertDialogConfirmation.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    ic_micro = true;
                }
            });
            alertDialogConfirmation.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialogConfirmation.show();
        }else{
            String[] ConfirmationValue = Confirmed.getString((settings.getInt("endUrl",2020) - 1) + " - " + settings.getInt("endUrl",2020),getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed)).split("=");
                String[] Format = ConfirmationValue[numStolbWrite].split("~");
                if(Format.length >= 2){
                    final LayoutInflater li = LayoutInflater.from(context);
                    File mFolder = new File(context.getExternalFilesDir(null) + "/confirmation");
                    final File file = new File(mFolder.getAbsolutePath() + "/" + (settings.getInt("endUrl",2020) - 1) + " - " + settings.getInt("endUrl",2020) + "_" + numStolbWrite +"." + Format[1]);
                    if (!mFolder.exists()) {
                        mFolder.mkdir();
                    }
                    if(Format[1].equals("jpg")) {
                        viewConfirm = li.inflate(R.layout.confirmed_photo, null);
                        AlertDialog.Builder ConfirmationAlert = new AlertDialog.Builder(context);
                        ConfirmationAlert.setView(viewConfirm);
                        ConfirmationAlert
                                .setCancelable(true)
                                .setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
                        alertbackground.setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                        if(settings.getBoolean("BorderAlertSettings",false))
                            alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                        viewConfirm.findViewById(R.id.liner_conf_photo).setBackground(alertbackground);

                        TextView textView = viewConfirm.findViewById(R.id.text_conf_photo);
                        textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

                        ImageView imageView = viewConfirm.findViewById(R.id.ConfirmedPhoto);
                        Bitmap bitmapOrg = BitmapFactory.decodeFile(file.toString());
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap rotate = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight(), matrix, true);
                        imageView.setImageBitmap(rotate);
                        alertDialogConfirmation = ConfirmationAlert.create();
                        alertDialogConfirmation.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        alertDialogConfirmation.show();

                    }else {
                        viewConfirm = li.inflate(R.layout.confirmed_voice, null);
                        AlertDialog.Builder ConfirmationAlert = new AlertDialog.Builder(context);
                        ConfirmationAlert.setView(viewConfirm);
                        GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
                        alertbackground.setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                        if(settings.getBoolean("BorderAlertSettings",false))
                            alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
                        viewConfirm.findViewById(R.id.liner_conf_voice).setBackground(alertbackground);

                        ConfirmationAlert
                                .setCancelable(true);

                        TextView textView = viewConfirm.findViewById(R.id.textView_conf_voice);
                        textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

                        final ImageButton imageButton = viewConfirm.findViewById(R.id.playVoicePlayer);
                        Display display = getWindowManager().getDefaultDisplay();
                        DisplayMetrics metricsB = new DisplayMetrics();
                        display.getMetrics(metricsB);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) Math.floor(metricsB.widthPixels / 6) , (int) Math.floor(metricsB.widthPixels / 6));
                        layoutParams.setMargins(0,0,0,20);
                        imageButton.setLayoutParams(layoutParams);
                        final Drawable drawableImagePlay = ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_black);
                        drawableImagePlay.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_IN);
                        imageButton.setImageDrawable(drawableImagePlay);
                        try {
                            if (mediaPlayer != null) {
                                mediaPlayer.release();
                                mediaPlayer = null;
                            }

                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(context, Uri.fromFile(file));
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        TextView timeEnd = viewConfirm.findViewById(R.id.TimeEndVoice);
                        timeEnd.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                        final TextView timeCurrent = viewConfirm.findViewById(R.id.TimeCurrentVoice);
                        timeCurrent.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

                        final SeekBar seekBar = viewConfirm.findViewById(R.id.voiceRecordPlayer);
                         seekBar.setMax(mediaPlayer.getDuration());
                        seekBar.getProgressDrawable().setColorFilter(Current_Theme.getInt("custom_color_audio_player", ContextCompat.getColor(context, R.color.custom_color_audio_player)), PorterDuff.Mode.MULTIPLY);
                        seekBar.getThumb().setColorFilter(Current_Theme.getInt("custom_color_audio_player", ContextCompat.getColor(context, R.color.custom_color_audio_player)), PorterDuff.Mode.SRC_IN);

                        seekBar.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                    mediaPlayer.seekTo(seekBar.getProgress());

                                if((int) Math.floor(seekBar.getProgress()/1000) < 60)
                                    if((int) Math.floor(seekBar.getProgress()/1000) < 10)
                                        timeCurrent.setText("00:0" + (int) Math.floor(seekBar.getProgress()/1000));
                                    else
                                        timeCurrent.setText("00:" + (int) Math.floor(seekBar.getProgress()/1000));
                                else
                                if((int) Math.floor(seekBar.getProgress()/ 1000/60) < 10)
                                    if ((int) Math.floor(seekBar.getProgress()/ 1000 % 60) < 10)
                                        timeCurrent.setText("0" + (int) Math.floor(seekBar.getProgress()/ 1000/60) + ":0" + (int) Math.floor(seekBar.getProgress()/1000));
                                    else
                                        timeCurrent.setText("0" + (int) Math.floor(seekBar.getProgress()/ 1000/60) + ":" + (int) Math.floor(seekBar.getProgress()/1000));
                                else
                                if ((int) Math.floor(seekBar.getProgress()/ 1000 % 60) < 10)
                                    timeCurrent.setText((int) Math.floor(seekBar.getProgress()/ 1000/60) + ":0" + (int) Math.floor(seekBar.getProgress()/1000));
                                else
                                    timeCurrent.setText((int) Math.floor(seekBar.getProgress()/ 1000/60) + ":" + (int) Math.floor(seekBar.getProgress()/1000));
                                return false;
                            }
                        });


                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mediaPlayer.isPlaying()){
                                    mediaPlayer.pause();
                                    imageButton.setImageDrawable(drawableImagePlay);
                                }else{
                                    mediaPlayer.start();
                                  startPlayProgressUpdater(seekBar,timeCurrent);
                                    Drawable drawableImagePause = ContextCompat.getDrawable(context, R.drawable.ic_pause_black);
                                    drawableImagePause.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_IN);
                                    imageButton.setImageDrawable(drawableImagePause);
                                }
                            }
                        });

                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                Drawable drawableImagePlay = ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_black);
                                drawableImagePlay.setColorFilter(Current_Theme.getInt("custom_button_arrow", ContextCompat.getColor(context, R.color.custom_button_arrow)), PorterDuff.Mode.SRC_IN);
                                imageButton.setImageDrawable(drawableImagePlay);
                            }
                        });

                        if((int) Math.floor(mediaPlayer.getDuration()/1000) < 60)
                            if((int) Math.floor(mediaPlayer.getDuration()/1000) < 10)
                                timeEnd.setText("00:0" + (int) Math.floor(mediaPlayer.getDuration()/1000));
                            else
                                timeEnd.setText("00:" + (int) Math.floor(mediaPlayer.getDuration()/1000));
                        else
                        if((int) Math.floor(mediaPlayer.getDuration()/ 1000/60) < 10)
                            if ((int) Math.floor(mediaPlayer.getDuration()/ 1000 % 60) < 10)
                                timeEnd.setText("0" + (int) Math.floor(mediaPlayer.getDuration()/ 1000/60) + ":0" + (int) Math.floor(mediaPlayer.getDuration()/1000));
                            else
                                timeEnd.setText("0" + (int) Math.floor(mediaPlayer.getDuration()/ 1000/60) + ":" + (int) Math.floor(mediaPlayer.getDuration()/1000));
                        else
                        if ((int) Math.floor(mediaPlayer.getDuration()/ 1000 % 60) < 10)
                            timeEnd.setText((int) Math.floor(mediaPlayer.getDuration()/ 1000/60) + ":0" + (int) Math.floor(mediaPlayer.getDuration()/1000));
                        else
                            timeEnd.setText((int) Math.floor(mediaPlayer.getDuration()/ 1000/60) + ":" + (int) Math.floor(mediaPlayer.getDuration()/1000));

                        alertDialogConfirmation = ConfirmationAlert.create();
                        alertDialogConfirmation.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                if (mediaPlayer != null) {
                                    try {
                                        mediaPlayer.release();
                                        mediaPlayer = null;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        alertDialogConfirmation.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        alertDialogConfirmation.show();
                    }

                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void startPlayProgressUpdater(final SeekBar seekBar, final TextView textView) {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if((int) Math.floor(mediaPlayer.getCurrentPosition()/1000) < 60)
            if((int) Math.floor(mediaPlayer.getCurrentPosition()/1000) < 10)
                textView.setText("00:0" + (int) Math.floor(mediaPlayer.getCurrentPosition()/1000));
            else
                textView.setText("00:" + (int) Math.floor(mediaPlayer.getCurrentPosition()/1000));
        else
            if((int) Math.floor(mediaPlayer.getCurrentPosition()/ 1000/60) < 10)
                if ((int) Math.floor(mediaPlayer.getCurrentPosition()/ 1000 % 60) < 10)
                    textView.setText("0" + (int) Math.floor(mediaPlayer.getCurrentPosition()/ 1000/60) + ":0" + (int) Math.floor(mediaPlayer.getCurrentPosition()/1000));
                else
                    textView.setText("0" + (int) Math.floor(mediaPlayer.getCurrentPosition()/ 1000/60) + ":" + (int) Math.floor(mediaPlayer.getCurrentPosition()/1000));
              else
            if ((int) Math.floor(mediaPlayer.getCurrentPosition()/ 1000 % 60) < 10)
                textView.setText((int) Math.floor(mediaPlayer.getCurrentPosition()/ 1000/60) + ":0" + (int) Math.floor(mediaPlayer.getCurrentPosition()/1000));
            else
                textView.setText((int) Math.floor(mediaPlayer.getCurrentPosition()/ 1000/60) + ":" + (int) Math.floor(mediaPlayer.getCurrentPosition()/1000));

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater(seekBar, textView);
                }
            };
            handler.postDelayed(notification,100);
        }
    }


    public void MicroConfirmation(){
        if(ic_micro){
            ic_micro = false;
            LayoutInflater li = LayoutInflater.from(context);
            ImageButton imageButton = viewConfirm.findViewById(R.id.ConfirmationMicrophone);
            LinearLayout linearLayout = viewConfirm.findViewById(R.id.LinerMicro);
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(linearParams);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.addView(li.inflate(R.layout.micro_button,null));
            LinearLayout.LayoutParams ImageParams = new LinearLayout.LayoutParams(imageButton.getWidth(),imageButton.getHeight());
            imageButton.setLayoutParams(ImageParams);
            TextView text = viewConfirm.findViewById(R.id.TextMicro);
            text.setText(getString(R.string.Voice_recording) + " ");
            TextView time = viewConfirm.findViewById(R.id.TimeMicro);
            time.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            time.setText("00:00");
            final TextView buttonStart = linearLayout.findViewById(R.id.button_one_alert);
            buttonStart.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                buttonStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(buttonStart.getText().equals(getString(R.string.StartMicro))){
                            mediaRecorder = new MediaRecorder();
                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB );
                            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                            try {
                                File mFolder = new File(context.getExternalFilesDir(null) + "/confirmation");
                                File file = new File(mFolder.getAbsolutePath() + "/" + (settings.getInt("endUrl",2020) - 1) + " - " + settings.getInt("endUrl",2020) + "_" + numStolbWrite +".amr");
                                if (!mFolder.exists()) {
                                    mFolder.mkdir();
                                }
                                mediaRecorder.setMaxDuration(3599000);
                                mediaRecorder.setOutputFile(file.toString());
                                mediaRecorder.prepare();
                                mediaRecorder.start();
                                new TimeRecording().execute();
                                buttonStart.setText(getString(R.string.PauseMicro));
                            } catch (IllegalStateException e) {
                                Toast.makeText(context,getString(R.string.errorMicro),Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else if(buttonStart.getText().equals(getString(R.string.PauseMicro))){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                mediaRecorder.pause();
                            cancelAsyncTask = true;
                            buttonStart.setText(getString(R.string.ResumeMicro));
                            }else
                                Toast.makeText(context,getString(R.string.apiError),Toast.LENGTH_LONG).show();
                        }else if(buttonStart.getText().equals(getString(R.string.ResumeMicro))){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                mediaRecorder.resume();
                            cancelAsyncTask = false;
                            new TimeRecording().execute();
                            buttonStart.setText(getString(R.string.PauseMicro));
                        }else
                            Toast.makeText(context,getString(R.string.apiError),Toast.LENGTH_LONG).show();
                        }
                    }
                });

            TextView buttonEnd = linearLayout.findViewById(R.id.button_two_alert);
            buttonEnd.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                buttonEnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mediaRecorder != null) {
                            alertDialogConfirmation.hide();
                            cancelAsyncTask = true;
                            mediaRecorder.stop();
                            String ConfirmationWrite = "";
                            String[] ConfirmationValue = Confirmed.getString((settings.getInt("endUrl", 2020) - 1) + " - " + settings.getInt("endUrl", 2020), getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed)).split("=");
                            for (int i = 0; i < ConfirmationValue.length; i++) {
                                if (i == numStolbWrite) {
                                    ConfirmationWrite = ConfirmationWrite + getString(R.string.Confirmed) + "~amr=";
                                } else
                                    ConfirmationWrite = ConfirmationWrite + ConfirmationValue[i] + "=";
                            }
                            editorConfirmed.putString((settings.getInt("endUrl", 2020) - 1) + " - " + settings.getInt("endUrl", 2020), ConfirmationWrite);
                            editorConfirmed.apply();
                            ConfirmationTextView.setText(getString(R.string.Confirmed));
                            color = Color.DKGRAY;
                            new ReplaceColorStolb().execute();
                            alertDialogConfirmation.hide();
                        }}
                });




        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
                if(requestCode == REQUEST_CODE_CAMERA && resultCode != RESULT_CANCELED){
                    String ConfirmationWrite = "";
                    String[] ConfirmationValue = Confirmed.getString((settings.getInt("endUrl",2020) - 1) + " - " + settings.getInt("endUrl",2020),getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed)).split("=");
                    for (int i = 0; i < ConfirmationValue.length; i++){
                        if(i == numStolbWrite){
                            ConfirmationWrite = ConfirmationWrite + getString(R.string.Confirmed) + "~jpg=";
                        }else
                            ConfirmationWrite = ConfirmationWrite + ConfirmationValue[i] + "=";
                    }
                    editorConfirmed.putString((settings.getInt("endUrl",2020) - 1) + " - " + settings.getInt("endUrl",2020),ConfirmationWrite);
                    editorConfirmed.apply();
                    ConfirmationTextView.setText(getString(R.string.Confirmed));
                    alertDialogConfirmation.hide();
                    color = Color.DKGRAY;
                    new ReplaceColorStolb().execute();
                }

    }

    class TimeRecording extends AsyncTask<Void,String,Void> {

        TextView time;
        String timeViewValue;

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            time.setText(values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            time = viewConfirm.findViewById(R.id.TimeMicro);
            timeViewValue = time.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String[] timeTemp = timeViewValue.split(":");
            int[] timeValues = new int[]{Integer.parseInt(timeTemp[0]), Integer.parseInt(timeTemp[1])};
            for (int i = 0; i < 3599; i++){
                if((timeValues[1] + 1) >= 60){
                    timeValues[1] = 00;
                    if(timeValues[0] < 10){
                        timeValues[0] ++;
                        publishProgress("0" + timeValues[0] + ":00");
                    }
                    else {
                        timeValues[0]++;
                        publishProgress(timeValues[0] + timeValues[0] + ":00");
                    }
                }else if((timeValues[1] + 1) < 10){
                    timeValues[1] ++;
                    if(timeValues[0] < 10)
                        publishProgress("0" + timeValues[0] + ":0" + timeValues[1]);
                    else
                        publishProgress(timeValues[0] + ":0" + timeValues[1]);
                }else {
                    timeValues[1] ++;
                    if(timeValues[0] < 10)
                        publishProgress("0" + timeValues[0] + ":" + timeValues[1]);
                    else
                        publishProgress(timeValues[0] + ":" + timeValues[1]);
                }

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                if (cancelAsyncTask)
                    break;
            }

            return null;
        }

    }

    public void CameraConfirmarion (){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File mFolder = new File(context.getExternalFilesDir(null) + "/confirmation");
        File file = new File(mFolder.getAbsolutePath() + "/" + (settings.getInt("endUrl",2020) - 1) + " - " + settings.getInt("endUrl",2020) + "_" + numStolbWrite +".jpg");
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }
        Uri outputFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA_CONF: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(context,getString(R.string.NotCameraPermission),Toast.LENGTH_LONG).show();
                }
                else
                    CameraConfirmarion ();

                return;
            }
            case REQUEST_CODE_MICROPHONE_CONF: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(context,getString(R.string.NotMicrophonePermission),Toast.LENGTH_LONG).show();
                }

                return;
            }
            case REQUEST_CODE_FOLDER_CONF: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(context,getString(R.string.NotStoragePermission),Toast.LENGTH_LONG).show();
                }

                return;
            }
        }
    }

    public void ClicksRow(final View view){
        try {
        TextView textViewName = view.findViewById(R.id.textView1_1_dnev);
        TextView textViewKab = view.findViewById(R.id.textView1_2_dnev);
        final TextView textViewDz = view.findViewById(R.id.textView1_3_dnev);
        final TextView textViewOcenka = view.findViewById(R.id.textView1_4_dnev);
       url = (settings.getInt("StartNedeli",1) + settings.getInt("Card",1)) + "." + settings.getInt("IntMes",1) + "." + settings.getInt("Year",1);
       final StringBuffer stringBuffer = new StringBuffer();
       String[] helpKab, finalHelp = new String[1];
       int i = 0;
        try {
           File mFolder = new File(context.getFilesDir() + "/dnewnik");
                File FileTxt = new File(mFolder.getAbsolutePath() + "/"+ url + ".txt");
                if (!mFolder.exists()) {
                    mFolder.mkdir();
                }
                if (!FileTxt.exists()) {
                    FileTxt.createNewFile();
                }

                FileInputStream read =  new FileInputStream(FileTxt);
            InputStreamReader reader = new InputStreamReader(read);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String temp_read;
          String[] help;
            boolean Fix = true;
            String delimeter = "=";
            while ((temp_read = bufferedReader.readLine()) != null) {
                if(Fix)
                    i++;
                help = temp_read.split(delimeter);
                String cheak = textViewName.getText() + ", " + textViewKab.getText();
                if (help[0].equals(cheak)) {
                   finalHelp = help;
                   Fix=false;
                    stringBuffer.append(temp_read).append("~");
                }else 
                stringBuffer.append(temp_read).append("~");
            }
        } catch (FileNotFoundException q) {
           
        } catch (IOException j) {
            
        }

        final StringBuffer EndstringBuffer = new StringBuffer();
        final LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.edit_dz , null);
        final TextView textView = promptsView.findViewById(R.id.textViewNameYrok);
        final EditText editText = promptsView.findViewById(R.id.textEdit);
        final EditText editOcenka = promptsView.findViewById(R.id.textOcenka);
        if(2 <= finalHelp.length) {
            String[] temp3 = finalHelp[1].split("`");
            String tempik = "";
            if (temp3.length == 1)
                editText.setText(temp3[0]);
            else {
                for (int n = 0; n < temp3.length; n++) {
                    if(n+1 == temp3.length)
                        tempik = tempik + temp3[n];
                    else
                        tempik = tempik + temp3[n] + "\n";
                }
                editText.setText(tempik);
            }
        }
        if(finalHelp[0] == null)
            throw new Povtor("KRIA", 1);
        else {
            helpKab = finalHelp[0].split(",");
            textView.setText(helpKab[0]);
            textView.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            editText.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
            editText.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            setCursorColor(editText, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            setCursorPointerColor(editText, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            editOcenka.setHintTextColor(Current_Theme.getInt("custom_text_hint", ContextCompat.getColor(context, R.color.custom_text_hint)));
            editOcenka.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            setCursorColor(editOcenka, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            setCursorPointerColor(editOcenka, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));

            AlertDialog.Builder Zapic = new AlertDialog.Builder(context);
            final String[] finalHelp1 = finalHelp;
            final String[] tempbuffer = stringBuffer.toString().split("~");
            final int finalI = i;
            Zapic.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
            alertbackground.setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if(settings.getBoolean("BorderAlertSettings",false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.linerEditDz).setBackground(alertbackground);
            final AlertDialog alertDialog = Zapic.create();

            TextView textTitle = promptsView.findViewById(R.id.edit_dz_title);
            textTitle.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));

            TextView textBottom = promptsView.findViewById(R.id.edit_dz_name);
            textBottom.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textBottom = promptsView.findViewById(R.id.edit_dz_dz);
            textBottom.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            textBottom = promptsView.findViewById(R.id.edit_dz_ocenka);
            textBottom.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

            TextView ButtonCancel = promptsView.findViewById(R.id.button_one_alert);
            ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
            ButtonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.hide();
                }
            });

            TextView ButtonSave = promptsView.findViewById(R.id.button_two_alert);
            ButtonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.hide();

                    String tempTextEdit , tempOcenka;
                    if (!editText.getText().toString().equals(""))
                        tempTextEdit = editText.getText().toString();
                    else
                        tempTextEdit = "№123";
                    if (!editOcenka.getText().toString().equals(""))
                        tempOcenka = editOcenka.getText().toString();
                    else
                        tempOcenka = "5";

                    textViewDz.setText(tempTextEdit);
                    textViewOcenka.setText(tempOcenka);

                    for (int j = 1; j <= tempbuffer.length; j++) {
                        if (j == finalI) {
                            String[] reject = tempTextEdit.split("\n");
                            String DzWrite = "";
                            for (int u = 0; u < reject.length; u++)
                                DzWrite = DzWrite + reject[u] + "`";
                            EndstringBuffer.append(finalHelp1[0] + "=" + DzWrite + "=" + tempOcenka).append("\n");
                        } else
                            EndstringBuffer.append(tempbuffer[j - 1]).append("\n");

                    }

                    try {
                        File mFolder = new File(context.getFilesDir() + "/dnewnik");
                        File FileTxt = new File(mFolder.getAbsolutePath() + "/" + url + ".txt");
                        if (!mFolder.exists()) {
                            mFolder.mkdir();
                        }
                        if (!FileTxt.exists()) {
                            FileTxt.createNewFile();
                        }

                        FileOutputStream write = new FileOutputStream(FileTxt);
                        write.write(EndstringBuffer.toString().getBytes());
                        write.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            ButtonSave.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));

            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();


        }}catch (Povtor povtor){}
    }

    public void ClickMedia(View view){
        Uri adress = null;
        switch (view.getId()){
            case R.id.Gosha:
                if(what == R.id.vk)
                    what = R.id.Gosha;
                adress = Uri.parse("https://www.youtube.com/user/PlurrimiTube");
                break;
            case R.id.StartAndroid:
                if(what == R.id.gitHub)
                    what = R.id.StartAndroid;
                adress = Uri.parse("https://www.youtube.com/user/vitaxafication");
                break;
                case R.id.DevColibri:
                    if(what == R.id.Gmail)
                        what = R.id.DevColibri;
                adress = Uri.parse("https://www.youtube.com/user/devcolibri");
                break;
            case R.id.gitHub:
                what = R.id.gitHub;
                adress = Uri.parse("https://github.com/kos234/Student-s-diary");
                break;
            case R.id.Gmail:
                if(what == R.id.Gosha)
                    what = R.id.Gmail;
                adress = Uri.parse("mailto:kostyaperfiliev94@gmail.com");
                break;
            case R.id.vk:
                if(what == R.id.StartAndroid)
                    what = R.id.vk;
                adress = Uri.parse("https://vk.com/codename_kos");
                break;
             }
             if(what == R.id.DevColibri && settings.getBoolean("whatSettings",false)) {
                 what = 0;
                 LayoutInflater li = LayoutInflater.from(context);
                 View viewAlert = li.inflate(R.layout.what_layout, null);
                 AlertDialog.Builder ConfirmationAlert = new AlertDialog.Builder(context);
                 ConfirmationAlert.setView(viewAlert);
                 ConfirmationAlert
                         .setCancelable(true);
                 VideoView videoView = viewAlert.findViewById(R.id.videoView);
                 videoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/" + R.raw.what));
                 videoView.setZOrderOnTop(true);
                 videoView.requestFocus();
                 videoView.start();
                 alertDialogConfirmation = ConfirmationAlert.create();
                 alertDialogConfirmation.show();
             }else{
             Intent browser= new Intent(Intent.ACTION_VIEW, adress);
        startActivity(browser);
    }}

  class MyThread extends Thread {

        public String[] generateDate(String readString){
            String[] returnStrings = new String[5],
                    help = readString.split("="),
                    helpTimes = help[0].split("-"),
                    helpTimeOne = helpTimes[0].split(":"),
                    helpTimeTwo = helpTimes[1].split(":");

            returnStrings[0] = help[1].split(",")[0];

            if(helpTimeOne.length == 3 && helpTimeTwo.length == 3){
                if(helpTimeOne[2].equals("PM"))
                    returnStrings[1] = String.valueOf(Integer.valueOf(helpTimeOne[0]) + 12);
                else
                    returnStrings[1] = helpTimeOne[0];

                if(helpTimeTwo[2].equals("PM"))
                    returnStrings[3] = String.valueOf(Integer.valueOf(helpTimeTwo[0]) + 12);
                else
                    returnStrings[3] = helpTimeTwo[0];
            }
            else{
                returnStrings[1] = helpTimeOne[0];
                returnStrings[3] = helpTimeTwo[0];
            }
            returnStrings[2] = helpTimeOne[1];
            returnStrings[4] = helpTimeTwo[1];

            return returnStrings;
        }

        public void run(){
            String Type = null,
                    Name = null,
                    HourSay = null,
                    MinSay = null,
                    urlNot = null;
            int TimeHoursStart , TimeMinsStart , TimeHoursEnd, TimeMinsEnd,
                    OneYrokHours = 666,
                    OneYrokMins = 666,
                    PeremenaHoursStart = 666,
                    PeremenaMinsStart = 666,
                    min = 666,
                    hour = 666,
                    minTemp = 666,
                    hourTemp = 666;

            Boolean OneYrok = true;
            Boolean clear = true;

            while (true) {
                if(!settings.getBoolean("notifySettings",true))
                    continue;

               Date date = new Date();
                switch (date.toString().substring(0,3)) {
                    case "Mon":
                            if (settings.getBoolean("Monday", true))
                                urlNot = "Monday.txt";
                            else continue;
                        break;
                    case "Tue":
                            if (settings.getBoolean("Tuesday", true))
                                urlNot = "Tuesday.txt";
                            else continue;
                        break;
                    case "Wed":
                            if (settings.getBoolean("Wednesday", true))
                                urlNot = "Wednesday.txt";
                            else continue;
                        break;
                    case "Thu":
                            if (settings.getBoolean("Thursday", true))
                                urlNot = "Thursday.txt";
                            else continue;
                        break;
                    case "Fri":
                            if (settings.getBoolean("Thursday", true))
                                urlNot = "Friday.txt";
                            else continue;

                        break;
                    case "Sat":
                        if(settings.getBoolean("SaturdaySettings",true))
                            if (settings.getBoolean("Saturday", true))
                                urlNot = "Saturday.txt";
                            else continue;
                        break;
                }

                if (urlNot != null) {

                    try {
                        FileInputStream read = openFileInput(urlNot);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        String temp_read;

                        while ((temp_read = bufferedReader.readLine()) != null) {
                            String[] dateTimes = generateDate(temp_read);
                            Name = dateTimes[0];

                            TimeHoursStart = Integer.parseInt(dateTimes[1]);
                            TimeMinsStart = Integer.parseInt(dateTimes[2]);
                            TimeHoursEnd = Integer.parseInt(dateTimes[3]);
                            TimeMinsEnd = Integer.parseInt(dateTimes[4]);

                            //Sat Jan 18 07:48:09 UTC 2020

                            if (OneYrok)  {
                                OneYrokHours = TimeHoursStart;
                                OneYrokMins = TimeMinsStart;
                                OneYrok = false;
                                clear = true;
                            }

                            else if((Integer.parseInt(date.toString().substring(11, 13)) + 1) == OneYrokHours) {
                                Type = getString(R.string.StartYrok);
                                hour = OneYrokHours - Integer.parseInt(date.toString().substring(11, 13));
                                min = hour * 60 - Integer.parseInt(date.toString().substring(14, 16)) + OneYrokMins;
                                hour = 0;
                                while (min > 60) {
                                    hour = hour + 1;
                                    min = min - 60;
                                }
                            }

                           else if (TimeHoursStart <= Integer.parseInt(date.toString().substring(11, 13)) && TimeMinsStart <= Integer.parseInt(date.toString().substring(14, 16)) && Integer.parseInt(date.toString().substring(11, 13)) <= TimeHoursEnd && Integer.parseInt(date.toString().substring(14, 16)) <= TimeMinsEnd ) {
                                    hour = TimeHoursEnd - Integer.parseInt(date.toString().substring(11, 13));
                                    min = hour * 60 - Integer.parseInt(date.toString().substring(14, 16)) + TimeMinsEnd;
                                    hour = 0;
                                    while (min > 60) {
                                        hour = hour + 1;
                                        min = min - 60;
                                    }
                                    PeremenaHoursStart = TimeHoursEnd;
                                    PeremenaMinsStart = TimeMinsEnd;
                                    Type = getString(R.string.EndYrok);

                            } else if (PeremenaHoursStart <= Integer.parseInt(date.toString().substring(11, 13)) && PeremenaMinsStart <= Integer.parseInt(date.toString().substring(14, 16)) && (TimeHoursStart > Integer.parseInt(date.toString().substring(11, 13)) || TimeMinsStart >= Integer.parseInt(date.toString().substring(14, 16)))) {
                                        hour = TimeHoursStart - Integer.parseInt(date.toString().substring(11, 13));
                                        min = hour * 60 - Integer.parseInt(date.toString().substring(14, 16)) + TimeMinsStart;
                                        hour = 0;
                                        while (min > 60) {
                                            hour = hour + 1;
                                            min = min - 60;
                                        }
                                    Type = getString(R.string.EndPeremen);
                                }


                        }
                        bufferedReader.close();
                        reader.close();
                        read.close();
                    } catch (FileNotFoundException e) {
                    } catch (IOException e) { }

                    if (min != 666 || hour != 666) {
                        if (minTemp != min || hourTemp != hour) {

                            HourSay = Padej(hour, true);
                            MinSay = Padej(min, false);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_NONE);
                                notificationManager.createNotificationChannel(notificationChannel);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                NotificationCompat.Builder notifycationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                        .setAutoCancel(false)
                                        .setVibrate(null)
                                        .setSound(null)
                                        .setSmallIcon(R.drawable.ic_stat_name)
                                        .setWhen(System.currentTimeMillis())
                                        .setContentIntent(pendingIntent)
                                        .setContentTitle(Name)
                                        .setContentText(Type + ": " + HourSay + MinSay);
                                notificationManager.notify(NOTIFY_ID, notifycationBuilder.build());
                            } else {
                                NotificationCompat.Builder builder =
                                        new NotificationCompat.Builder(context)
                                                .setSmallIcon(R.drawable.ic_stat_name)
                                                .setAutoCancel(false)
                                                .setVibrate(null)
                                                .setSound(null)
                                                .setContentTitle(Name)
                                                .setPriority(IMPORTANCE_NONE)
                                                .setContentText(Type + ": " + HourSay + MinSay);

                                Notification notification = builder.build();

                                NotificationManager notificationManager =
                                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(1, notification);
                            }

                        } else {
                            if (clear && min == 0 && hour == 0)
                                notificationManager.cancelAll();
                            clear = false;
                        }
                        minTemp = min;
                        hourTemp = hour;
                    }


                }
            }
        }
    }

    public String Padej (int kool, Boolean Type) {
        String say = " ";
        if (kool == 0)  {
            if(Type)
                say = " ";
            else
             say = "0 " + getString(R.string._0_Min);
        }
        else if (kool == 1) {
            if (Type) say = "1 " + getString(R.string._1_Hour);
            else say = "1 " + getString(R.string._1_Min);
        }
        else if (kool >= 2 && kool <= 4) {
            if (Type) say = kool + " " + getString(R.string._2_4_Hour);
            else say = kool + " "+ getString(R.string._2_4_Min);
        }
        else if (kool >= 5 && kool <= 20) {
            if (Type) say = kool + " " + getString(R.string._5_20_Hour);
            else say = kool + " "+ getString(R.string._5_20_Min);
        }
        else if (Integer.parseInt(Integer.toString(kool).substring(0,1)) > 1 && Integer.parseInt(Integer.toString(kool).substring(Integer.toString(kool).length()-1 )) == 1) {
            if (Type) say = kool + " " + getString(R.string._end_1_Hour);
            else say = kool + " "+ getString(R.string._end_1_Min);
        }
        else if (Integer.parseInt(Integer.toString(kool).substring(0,1)) > 1 && Integer.parseInt(Integer.toString(kool).substring(Integer.toString(kool).length()-1 )) >= 2 && Integer.parseInt(Integer.toString(kool).substring(Integer.toString(kool).length()-1 )) <= 4) {
            if (Type) say = kool + " " + getString(R.string._end_2_4_Hour);
            else say = kool + " "+ getString(R.string._end_2_4_Min);
        }
        else if (Integer.parseInt(Integer.toString(kool).substring(0,1)) > 1 && Integer.parseInt(Integer.toString(kool).substring(Integer.toString(kool).length()-1 )) >= 5 && Integer.parseInt(Integer.toString(kool).substring(Integer.toString(kool).length()-1 )) <= 9) {
            if(!Type) say = kool + " "+ getString(R.string._end_5_9_Min);
        }
        else if (Integer.parseInt(Integer.toString(kool).substring(0,1)) > 1 && Integer.parseInt(Integer.toString(kool).substring(Integer.toString(kool).length()-1 )) == 0) {
            if(!Type) say = kool + " "+ getString(R.string._end_0_Min);
        }

        return say;
    }

    public void EditTheme(final String[] colorsTheme, int position){
        new Thread(new Runnable() {
            public void run() {
        colors.put(R.id.custom_icon, Integer.valueOf(colorsTheme[2]));
        colors.put(R.id.custom_border_theme, Integer.valueOf(colorsTheme[3]));
        colors.put(R.id.custom_background, Integer.valueOf(colorsTheme[4]));
        colors.put(R.id.custom_toolbar, Integer.valueOf(colorsTheme[5]));
        colors.put(R.id.custom_toolbar_text, Integer.valueOf(colorsTheme[6]));
        colors.put(R.id.custom_notification_bar, Integer.valueOf(colorsTheme[7]));
        colors.put(R.id.custom_text_light, Integer.valueOf(colorsTheme[8]));
        colors.put(R.id.custom_text_dark, Integer.valueOf(colorsTheme[9]));
        colors.put(R.id.custom_text_hint, Integer.valueOf(colorsTheme[10]));
        colors.put(R.id.custom_cursor, Integer.valueOf(colorsTheme[11]));
        colors.put(R.id.custom_card, Integer.valueOf(colorsTheme[12]));
        colors.put(R.id.custom_bottomBorder, Integer.valueOf(colorsTheme[13]));
        colors.put(R.id.custom_button_add, Integer.valueOf(colorsTheme[14]));
        colors.put(R.id.custom_button_add_plus, Integer.valueOf(colorsTheme[15]));
        colors.put(R.id.custom_button_arrow, Integer.valueOf(colorsTheme[16]));
        colors.put(R.id.custom_progress, Integer.valueOf(colorsTheme[17]));
        colors.put(R.id.custom_not_confirmed, Integer.valueOf(colorsTheme[18]));
        colors.put(R.id.custom_Table_column, Integer.valueOf(colorsTheme[19]));
        colors.put(R.id.custom_notification_on, Integer.valueOf(colorsTheme[20]));
        colors.put(R.id.custom_notification_off, Integer.valueOf(colorsTheme[21]));
        colors.put(R.id.custom_switch_on, Integer.valueOf(colorsTheme[22]));
        colors.put(R.id.custom_switch_off, Integer.valueOf(colorsTheme[23]));
        colors.put(R.id.custom_color_block_choose_background, Integer.valueOf(colorsTheme[24]));
        colors.put(R.id.custom_color_block_choose_border, Integer.valueOf(colorsTheme[25]));
        colors.put(R.id.custom_color_audio_player, Integer.valueOf(colorsTheme[26]));
            }
        }).start();
        ClickSaveThemeType = false;
        positionTheme = position;
    }

    public void ClickCreateCustomTheme(View view){
        LinearLayout linearLayout = findViewById(R.id.field_create_fragment);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        if(view.getId() == R.id.Card_create) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            new Thread(new Runnable() {
                public void run() {
                    colors.put(R.id.custom_icon, ContextCompat.getColor(context,R.color.custom_icon));
                    colors.put(R.id.custom_border_theme, ContextCompat.getColor(context, R.color.custom_border_theme));
                    colors.put(R.id.custom_background, ContextCompat.getColor(context, R.color.custom_background));
                    colors.put(R.id.custom_toolbar, ContextCompat.getColor(context, R.color.custom_toolbar));
                    colors.put(R.id.custom_toolbar_text, ContextCompat.getColor(context, R.color.custom_toolbar_text));
                    colors.put(R.id.custom_notification_bar, ContextCompat.getColor(context, R.color.custom_notification_bar));
                    colors.put(R.id.custom_text_light, ContextCompat.getColor(context, R.color.custom_text_light));
                    colors.put(R.id.custom_text_dark, ContextCompat.getColor(context, R.color.custom_text_dark));
                    colors.put(R.id.custom_text_hint, ContextCompat.getColor(context, R.color.custom_text_hint));
                    colors.put(R.id.custom_cursor, ContextCompat.getColor(context, R.color.custom_cursor));
                    colors.put(R.id.custom_card, ContextCompat.getColor(context, R.color.custom_card));
                    colors.put(R.id.custom_bottomBorder, ContextCompat.getColor(context, R.color.custom_bottomBorder));
                    colors.put(R.id.custom_button_add, ContextCompat.getColor(context, R.color.custom_button_add));
                    colors.put(R.id.custom_button_add_plus, ContextCompat.getColor(context, R.color.custom_button_add_plus));
                    colors.put(R.id.custom_button_arrow, ContextCompat.getColor(context, R.color.custom_button_arrow));
                    colors.put(R.id.custom_progress, ContextCompat.getColor(context, R.color.custom_progress));
                    colors.put(R.id.custom_not_confirmed, ContextCompat.getColor(context, R.color.custom_not_confirmed));
                    colors.put(R.id.custom_Table_column, ContextCompat.getColor(context, R.color.custom_Table_column));
                    colors.put(R.id.custom_notification_on, ContextCompat.getColor(context, R.color.custom_notification_on));
                    colors.put(R.id.custom_notification_off, ContextCompat.getColor(context, R.color.custom_notification_off));
                    colors.put(R.id.custom_switch_on, ContextCompat.getColor(context, R.color.custom_switch_on));
                    colors.put(R.id.custom_switch_off, ContextCompat.getColor(context, R.color.custom_switch_off));
                    colors.put(R.id.custom_color_block_choose_background, ContextCompat.getColor(context, R.color.custom_color_block_choose_background));
                    colors.put(R.id.custom_color_block_choose_border, ContextCompat.getColor(context, R.color.custom_color_block_choose_border));
                    colors.put(R.id.custom_color_audio_player, ContextCompat.getColor(context, R.color.custom_color_audio_player));
                }
            }).start();

            ClickSaveThemeType = true;
        }else {
            new CreateTheme().execute(colors);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }


    public void ClickColor(final View viewColor){

        if(settings.getString("dafauilt_choose_color",getString(R.string.not_chosen)).equals(getString(R.string.not_chosen)) || settings.getString("dafauilt_choose_color",getString(R.string.not_chosen)).equals(getString(R.string.HEX_code))){

        final LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.type_color , null);
        final AlertDialog.Builder deleted = new AlertDialog.Builder(context);
        deleted.setView(promptsView);
        deleted.setCancelable(true);

        final AlertDialog alertDialog = deleted.create();

            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
            alertbackground.setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if(settings.getBoolean("BorderAlertSettings",false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.Liner_type_color).setBackground(alertbackground);


            GradientDrawable drawableOne = (GradientDrawable) getResources().getDrawable(R.drawable.shape);
            drawableOne.setColor(Current_Theme.getInt("custom_color_block_choose_background", ContextCompat.getColor(context, R.color.custom_color_block_choose_background)));
            drawableOne.setStroke(4, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));

            final Button buttonHex = promptsView.findViewById(R.id.hex_code);

            if(settings.getString("dafauilt_choose_color",getString(R.string.not_chosen)).equals(getString(R.string.HEX_code))){
                LinearLayout linearLayoutRoot = promptsView.findViewById(R.id.Liner_type_color);
                LinearLayout linearLayoutDelete = linearLayoutRoot.findViewById(R.id.Liner_Choose_Color);
                linearLayoutRoot.removeView(linearLayoutDelete);

                LinearLayout linearLayout = promptsView.findViewById(R.id.LinerHex);
                linearLayout.removeView(buttonHex);
                linearLayout.addView(li.inflate(R.layout.hex_button, null));

                TextView textView = linearLayout.findViewById(R.id.textView_type_color);
                textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

                final EditText editText = linearLayout.findViewById(R.id.edit_text_type_color);
                editText.setHintTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                editText.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

                editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if (i == EditorInfo.IME_ACTION_DONE) {
                            if (getCurrentFocus() != null) {
                                View vw = getCurrentFocus();
                                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(vw.getWindowToken(), 0);
                            }
                        }
                        return false;
                    }
                });

                TextView ButtonCancel = linearLayout.findViewById(R.id.cancel_type_color);
                ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                ButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.hide();
                    }
                });

                TextView ButtonSave = linearLayout.findViewById(R.id.save_type_color);
                ButtonSave.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                ButtonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String hexColor;
                        if(editText.getText().equals(""))
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
                    }
                });
            }else {

            buttonHex.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            buttonHex.setBackgroundDrawable(drawableOne);
            buttonHex.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout linearLayoutRoot = promptsView.findViewById(R.id.Liner_type_color);
                    LinearLayout linearLayoutDelete = linearLayoutRoot.findViewById(R.id.Liner_Choose_Color);
                    linearLayoutRoot.removeView(linearLayoutDelete);

                    LinearLayout linearLayout = promptsView.findViewById(R.id.LinerHex);
                    linearLayout.removeView(buttonHex);
                    linearLayout.addView(li.inflate(R.layout.hex_button, null));

                    TextView textView = linearLayout.findViewById(R.id.textView_type_color);
                    textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

                    final EditText editText = linearLayout.findViewById(R.id.edit_text_type_color);
                    editText.setHintTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                    editText.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

                    editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            if (i == EditorInfo.IME_ACTION_DONE) {
                                if (getCurrentFocus() != null) {
                                    View vw = getCurrentFocus();
                                    InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(vw.getWindowToken(), 0);
                                }
                            }
                            return false;
                        }
                    });

                    TextView ButtonCancel = linearLayout.findViewById(R.id.cancel_type_color);
                    ButtonCancel.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                    ButtonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.hide();
                        }
                    });

                    TextView ButtonSave = linearLayout.findViewById(R.id.save_type_color);
                    ButtonSave.setTextColor(Current_Theme.getInt("custom_button_add", ContextCompat.getColor(context, R.color.custom_button_add)));
                    ButtonSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewColor.setBackgroundColor(Color.parseColor("#" + editText.getText()));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                colors.replace(viewColor.getId(), Color.parseColor("#" + editText.getText()));
                            else {
                                colors.remove(viewColor.getId());
                                colors.put(viewColor.getId(), Color.parseColor("#" + editText.getText()));
                            }

                            alertDialog.hide();
                        }
                    });


                }
            });

            GradientDrawable drawableTwo = (GradientDrawable) getResources().getDrawable(R.drawable.shape);
            drawableTwo.setColor(Current_Theme.getInt("custom_color_block_choose_background", ContextCompat.getColor(context, R.color.custom_color_block_choose_background)));
            drawableTwo.setStroke(4, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));

            Button buttonChoose = promptsView.findViewById(R.id.choose_color);
            buttonChoose.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));
            buttonChoose.setBackgroundDrawable(drawableTwo);
            buttonChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    alertDialog.hide();

                    AmbilWarnaDialog colorEdit = new AmbilWarnaDialog(context, colors.get(viewColor.getId()), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        @Override
                        public void onCancel(AmbilWarnaDialog dialog) {

                        }

                        @Override
                        public void onOk(AmbilWarnaDialog dialog, int color) {
                            viewColor.setBackgroundColor(color);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                colors.replace(viewColor.getId(), color);
                            else {
                                colors.remove(viewColor.getId());
                                colors.put(viewColor.getId(), color);
                            }

                        }
                    });
                    GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.corners_alert);
                    alertbackground.setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                    if(settings.getBoolean("BorderAlertSettings",false))
                        alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));


                    colorEdit.show();
                }
            });
            }
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();


    }else{
            AmbilWarnaDialog colorEdit = new AmbilWarnaDialog(context, colors.get(viewColor.getId()), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {

                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    viewColor.setBackgroundColor(color);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        colors.replace(viewColor.getId(), color);
                    else {
                        colors.remove(viewColor.getId());
                        colors.put(viewColor.getId(), color);
                    }

                }
            });

            colorEdit.show();
        }
    }

    public static void setCursorColor(EditText view, @ColorInt int color) {
        try {
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(view);

            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(view);

            Drawable drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception ignored) {
        }
    }

    public static void setCursorPointerColor(EditText view, @ColorInt int color) {
        try {
            Field field = TextView.class.getDeclaredField("mTextSelectHandleRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(view);

            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(view);

            Drawable drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);

            field = editor.getClass().getDeclaredField("mSelectHandleCenter");
            field.setAccessible(true);
            field.set(editor, drawable);
        } catch (Exception ignored) {
        }
    }

    class CreateTheme extends AsyncTask<HashMap<Integer, Integer>, String, Void>{
        AlertDialog alertDialog;
        TextView textView;
        int id_theme = -1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlertDialog.Builder progressDialog = new AlertDialog.Builder(context);
            final LayoutInflater li = LayoutInflater.from(context);
            final View promptsView = li.inflate(R.layout.loading_color , null);
            progressDialog.setView(promptsView)
                    .setCancelable(false);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context,R.drawable.loading_drawable);
            alertbackground.setColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            if(settings.getBoolean("BorderAlertSettings",false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings",4), Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.linerLoading).setBackground(alertbackground);
            ProgressBar progressBar = promptsView.findViewById(R.id.progress_loading_color);
            progressBar.getIndeterminateDrawable().setColorFilter(Current_Theme.getInt("custom_progress", ContextCompat.getColor(context, R.color.custom_progress)), PorterDuff.Mode.SRC_ATOP);
            textView = promptsView.findViewById(R.id.textLoading);
            textView.setText(getString(R.string.Saving_theme));
            textView.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            alertDialog = progressDialog.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            alertDialog.hide();
            if(ClickSaveThemeType || id_theme == settings.getInt("id_current_theme", R.id.switchWhite)) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else {
                NastroikiFragment nastroikiFragment = (NastroikiFragment) getFragment.get(4);
                nastroikiFragment.NotifyAdapter(positionTheme, TempNameTheme);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            textView.setText(values[0]);
        }

        @Override
        protected Void doInBackground(HashMap<Integer, Integer>... hashMaps) {
            StringBuffer stringBuffer = new StringBuffer();
            EditText editText = findViewById(R.id.custom_name);
            if(editText.getText().toString().equals(""))
                TempNameTheme = getString(R.string.WhiteTheme);
            else  TempNameTheme = editText.getText().toString();

            try {
                FileInputStream read = context.openFileInput("Themes.txt");
                InputStreamReader reader = new InputStreamReader(read);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String temp_read;
                int wtite_num = 0;
                while ((temp_read = bufferedReader.readLine()) != null) {
                    if(!ClickSaveThemeType && (positionTheme == wtite_num)) {
                        stringBuffer.append(temp_read.split("=")[0] + "=" + TempNameTheme + "=" +
                                hashMaps[0].get(R.id.custom_icon) + "=" +
                                hashMaps[0].get(R.id.custom_border_theme) + "=" +
                                hashMaps[0].get(R.id.custom_background) + "=" +
                                hashMaps[0].get(R.id.custom_toolbar) + "=" +
                                hashMaps[0].get(R.id.custom_toolbar_text) + "=" +
                                hashMaps[0].get(R.id.custom_notification_bar) + "=" +
                                hashMaps[0].get(R.id.custom_text_light) + "=" +
                                hashMaps[0].get(R.id.custom_text_dark) + "=" +
                                hashMaps[0].get(R.id.custom_text_hint) + "=" +
                                hashMaps[0].get(R.id.custom_cursor) + "=" +
                                hashMaps[0].get(R.id.custom_card) + "=" +
                                hashMaps[0].get(R.id.custom_bottomBorder) + "=" +
                                hashMaps[0].get(R.id.custom_button_add) + "=" +
                                hashMaps[0].get(R.id.custom_button_add_plus) + "=" +
                                hashMaps[0].get(R.id.custom_button_arrow) + "=" +
                                hashMaps[0].get(R.id.custom_progress) + "=" +
                                hashMaps[0].get(R.id.custom_not_confirmed) + "=" +
                                hashMaps[0].get(R.id.custom_Table_column) + "=" +
                                hashMaps[0].get(R.id.custom_notification_on) + "=" +
                                hashMaps[0].get(R.id.custom_notification_off) + "=" +
                                hashMaps[0].get(R.id.custom_switch_on) + "=" +
                                hashMaps[0].get(R.id.custom_switch_off) + "=" +
                                hashMaps[0].get(R.id.custom_color_block_choose_background) + "=" +
                                hashMaps[0].get(R.id.custom_color_block_choose_border) + "=" +
                                hashMaps[0].get(R.id.custom_color_audio_player)).append("\n");

                        id_theme = Integer.parseInt(temp_read.split("=")[0]);
                    }else
                   stringBuffer.append(temp_read).append("\n");

                    wtite_num = wtite_num + 1;
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

            int Generate_id = 0;
            if(ClickSaveThemeType) {
                Generate_id = new Random().nextInt(1000) + 142132;
                editor.putInt("id_current_theme", Generate_id);
                editor.apply();
            }


            try {
                  FileOutputStream write =  context.openFileOutput("Themes.txt", MODE_PRIVATE);
                  String temp_write = stringBuffer.toString();

                if(ClickSaveThemeType){
                    temp_write = temp_write + Generate_id + "=" + TempNameTheme + "=" +
                            hashMaps[0].get(R.id.custom_icon) + "=" +
                            hashMaps[0].get(R.id.custom_border_theme)  + "=" +
                            hashMaps[0].get(R.id.custom_background) + "=" +
                            hashMaps[0].get(R.id.custom_toolbar) + "=" +
                            hashMaps[0].get(R.id.custom_toolbar_text) + "=" +
                            hashMaps[0].get(R.id.custom_notification_bar) + "=" +
                            hashMaps[0].get(R.id.custom_text_light) + "=" +
                            hashMaps[0].get(R.id.custom_text_dark) + "=" +
                            hashMaps[0].get(R.id.custom_text_hint) + "=" +
                            hashMaps[0].get(R.id.custom_cursor) + "=" +
                            hashMaps[0].get(R.id.custom_card) + "=" +
                            hashMaps[0].get(R.id.custom_bottomBorder) + "=" +
                            hashMaps[0].get(R.id.custom_button_add) + "=" +
                            hashMaps[0].get(R.id.custom_button_add_plus) + "=" +
                            hashMaps[0].get(R.id.custom_button_arrow) + "=" +
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
              } catch (FileNotFoundException e) {
                  e.printStackTrace();
              } catch (IOException e) {
                  e.printStackTrace();
              }

            if(ClickSaveThemeType || id_theme == settings.getInt("id_current_theme", R.id.switchWhite)) {
                publishProgress(getString(R.string.Apply_Theme));

                SharedPreferences.Editor editorColor = Current_Theme.edit();

                editorColor.putInt("custom_icon", hashMaps[0].get(R.id.custom_icon));
                editorColor.putInt("custom_border_theme", hashMaps[0].get(R.id.custom_border_theme));
                editorColor.putInt("custom_background", hashMaps[0].get(R.id.custom_background));
                editorColor.putInt("custom_toolbar", hashMaps[0].get(R.id.custom_toolbar));
                editorColor.putInt("custom_toolbar_text", hashMaps[0].get(R.id.custom_toolbar_text));
                editorColor.putInt("custom_notification_bar", hashMaps[0].get(R.id.custom_notification_bar));
                editorColor.putInt("custom_text_light", hashMaps[0].get(R.id.custom_text_light));
                editorColor.putInt("custom_text_dark", hashMaps[0].get(R.id.custom_text_dark));
                editorColor.putInt("custom_text_hint", hashMaps[0].get(R.id.custom_text_hint));
                editorColor.putInt("custom_cursor", hashMaps[0].get(R.id.custom_cursor));
                editorColor.putInt("custom_card", hashMaps[0].get(R.id.custom_card));
                editorColor.putInt("custom_bottomBorder", hashMaps[0].get(R.id.custom_bottomBorder));
                editorColor.putInt("custom_button_add", hashMaps[0].get(R.id.custom_button_add));
                editorColor.putInt("custom_button_add_plus", hashMaps[0].get(R.id.custom_button_add_plus));
                editorColor.putInt("custom_button_arrow", hashMaps[0].get(R.id.custom_button_arrow));
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
            return null;
        }
    }

}
