package com.example.kos;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.IntegerRes;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.zip.Inflater;

import yuku.ambilwarna.AmbilWarnaDialog;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    final Context context = this;
    private AlertDialog alertDialogConfirmation;
    private SharedPreferences settings,
                                Current_Theme,
                                Confirmed,
                                prefs = null;
    private SharedPreferences.Editor editor,
                        editorConfirmed;
    private int what,
            numStolbWrite = 0,
            numZapic = 1;
    FragmentManager fragmentManager;
    private final Handler handler = new Handler();
    private MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder;
    private Boolean ic_micro = true;
    private NotificationManager notificationManager;
    private static final int NOTIFY_ID = 1,
                            REQUEST_CODE_CAMERA = 1,
                            REQUEST_CODE_MICROPHONE_CONF = 3,
                            REQUEST_CODE_CAMERA_CONF = 4,
                            REQUEST_CODE_FOLDER_CONF = 5;
    private static final String CHANNEL_ID = "Novus_Pidor";
    private String url;
    public String TempNameTheme;
    NavigationView navigationView;
    Boolean cancelAsyncTask = false;
    private View viewConfirm;
    public int color;
    private HashMap<Integer, Integer> colors = new HashMap();
    private TextView ConfirmationTextView;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        prefs = getSharedPreferences("com.example.kos", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new onStart().execute();



    }

    /**
     * Добавить возможность отключать субботу
     * Добавить выбор цвета по хексу
     * Добавить открытие цветового выбора по дефолту
     * Редактирование темы
    */


    class onStart extends AsyncTask<Void,ConstrOnStart,Void>{
        ImageView imageView;
        int menuSize;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            drawerLayout = findViewById(R.id.Drawer);
            DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView = new ImageView(context);
            imageView.setLayoutParams(params);
            imageView.setBackgroundColor(Color.RED);
            drawerLayout.addView(imageView);
            navigationView = findViewById(R.id.navigation);
            menuSize = navigationView.getMenu().size();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            drawerLayout.setBackgroundColor(Current_Theme.getInt("custom_background",ContextCompat.getColor(context, R.color.custom_background)));
            drawerLayout.removeView(imageView);
            new MyThread().start();
        }

        @Override
        protected void onProgressUpdate(ConstrOnStart... values) {
            super.onProgressUpdate(values);
            if(values[0].getId() == 1) {
                navigationView.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
                if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_FOLDER_CONF);
                }
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Current_Theme.getInt("custom_notification_bar", ContextCompat.getColor(context, R.color.custom_notification_bar)));
            }

            else if(values[0].getId() == 2)
                navigationView.getMenu().getItem(values[0].getItem()).setTitle(values[0].getS());

            else if(values[0].getId() == 3){
                navigationView.setItemIconTintList(new ColorStateList(new int[][]{ new int[]{android.R.attr.state_enabled} }, new int[] {Current_Theme.getInt("custom_button_arrow",ContextCompat.getColor(context, R.color.custom_button_arrow))}));
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        try {
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
                                    if(IdShowFragment == IdHideFragment)
                                        throw new Povtor("KRIA", 1);
                                    editor.putString("Fragment","Ychitelia" );
                                    break;
                                case R.id.Zvonki:
                                        IdShowFragment = 2;
                                    if(IdShowFragment == IdHideFragment)
                                        throw new Povtor("KRIA", 1);
                                    editor.putString("Fragment","Znonki" );
                                    break;
                                case R.id.Ocenki:
                                        IdShowFragment = 3;
                                    if(IdShowFragment == IdHideFragment)
                                        throw new Povtor("KRIA", 1);
                                    editor.putString("Fragment","Ocenki" );
                                    break;
                                case R.id.Nastroiki:
                                        IdShowFragment = 4;
                                    if(IdShowFragment == IdHideFragment)
                                        throw new Povtor("KRIA", 1);
                                    editor.putString("Fragment","Nastroiki" );
                                    break;
                                case R.id.Spravka:
                                        IdShowFragment = 5;
                                    if(IdShowFragment == IdHideFragment)
                                        throw new Povtor("KRIA", 1);
                                    editor.putString("Fragment","Spravka" );
                                    break;
                                default:
                                        IdShowFragment = 0;
                                    if(IdShowFragment == IdHideFragment)
                                        throw new Povtor("KRIA", 1);
                                    editor.putString("Fragment","Dnewnik" );
                                    break;
                            }


                            fragmentManager.beginTransaction().show(fragmentManager.getFragments().get(IdShowFragment)).commit();
                            menuItem.setChecked(true);
                            drawerLayout = findViewById(R.id.Drawer);
                            drawerLayout.closeDrawer(Gravity.LEFT);
                            editor.apply();

                        }catch (Povtor povtor){
                            DrawerLayout drawer = findViewById(R.id.Drawer);
                            drawer.closeDrawer(Gravity.LEFT);
                        }
                        return false;
                    }
                });
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            settings = getSharedPreferences("Settings", MODE_PRIVATE);
            Current_Theme = getSharedPreferences("Current_Theme", MODE_PRIVATE);
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
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }

                fragmentManager.beginTransaction().add(R.id.Smena, fragmentActiv).commit();
                if(Invisibly)
                    fragmentManager.beginTransaction().hide(fragmentActiv).commit();
            }

            return null;
        }
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

        editText.setBackgroundColor(Color.parseColor("#fafafa"));
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
                                q.printStackTrace();
                            } catch (IOException j) {
                                j.printStackTrace();
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
            ImageButton ConfirmationMicrophone = viewConfirm.findViewById(R.id.ConfirmationMicrophone);
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

           alertDialogConfirmation = ConfirmationAlert.create();

            //и отображаем его:
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
                        ImageView imageView = viewConfirm.findViewById(R.id.ConfirmedPhoto);
                        Bitmap bitmapOrg = BitmapFactory.decodeFile(file.toString());
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap rotate = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight(), matrix, true);
                        imageView.setImageBitmap(rotate);
       //                 imageView.setRotation(90);
                        alertDialogConfirmation = ConfirmationAlert.create();
                        alertDialogConfirmation.show();

                    }else {
                        viewConfirm = li.inflate(R.layout.confirmed_voice, null);
                        AlertDialog.Builder ConfirmationAlert = new AlertDialog.Builder(context);
                        ConfirmationAlert.setView(viewConfirm);
                        ConfirmationAlert
                                .setCancelable(true);
                        final ImageButton imageButton = viewConfirm.findViewById(R.id.playVoicePlayer);
                        Display display = getWindowManager().getDefaultDisplay();
                        DisplayMetrics metricsB = new DisplayMetrics();
                        display.getMetrics(metricsB);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) Math.floor(metricsB.widthPixels / 6) , (int) Math.floor(metricsB.widthPixels / 6));
                        layoutParams.setMargins(0,0,0,20);
                        imageButton.setLayoutParams(layoutParams);
                        imageButton.setBackgroundResource(R.drawable.ic_play_arrow_black);
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
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                imageButton.setBackgroundResource(R.drawable.ic_play_arrow_black);
                            }
                        });
                        TextView timeEnd = viewConfirm.findViewById(R.id.TimeEndVoice);
                        final TextView timeCurrent = viewConfirm.findViewById(R.id.TimeCurrentVoice);
                        final SeekBar seekBar = viewConfirm.findViewById(R.id.voiceRecordPlayer);
                         seekBar.setMax(mediaPlayer.getDuration());
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
                                    imageButton.setBackgroundResource(R.drawable.ic_play_arrow_black);
                                }else{
                                    mediaPlayer.start();
                                  startPlayProgressUpdater(seekBar,timeCurrent);
                                  imageButton.setBackgroundResource(R.drawable.ic_pause_black);
                                }


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
            time.setText("00:00");

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB );
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            final Button buttonStart = linearLayout.findViewById(R.id.ButtonStart);
                buttonStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(buttonStart.getText().equals(getString(R.string.StartMicro))){
                            try {

                                File mFolder = new File(context.getExternalFilesDir(null) + "/confirmation");
                                File file = new File(mFolder.getAbsolutePath() + "/" + (settings.getInt("endUrl",2020) - 1) + " - " + settings.getInt("endUrl",2020) + "_" + numStolbWrite +".amr");
                                if (!mFolder.exists()) {
                                    mFolder.mkdir();
                                }
                                mediaRecorder.setMaxDuration(3599000);
                                mediaRecorder.setOutputFile(file.toString());
                                mediaRecorder.prepare();
                                new TimeRecording().execute();
                                mediaRecorder.start();
                                buttonStart.setText(getString(R.string.PauseMicro));
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
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

            Button buttonEnd = linearLayout.findViewById(R.id.ButtonEnd);
                buttonEnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialogConfirmation.hide();
                        cancelAsyncTask = true;
                        mediaRecorder.stop();
                        String ConfirmationWrite = "";
                        String[] ConfirmationValue = Confirmed.getString((settings.getInt("endUrl",2020) - 1) + " - " + settings.getInt("endUrl",2020),getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed)).split("=");
                        for (int i = 0; i < ConfirmationValue.length; i++){
                            if(i == numStolbWrite){
                                ConfirmationWrite = ConfirmationWrite + getString(R.string.Confirmed) + "~amr=";
                            }else
                                ConfirmationWrite = ConfirmationWrite + ConfirmationValue[i] + "=";
                        }
                        editorConfirmed.putString((settings.getInt("endUrl",2020) - 1) + " - " + settings.getInt("endUrl",2020),ConfirmationWrite);
                        editorConfirmed.apply();
                        ConfirmationTextView.setText(getString(R.string.Confirmed));
                    }
                });

            color = Color.DKGRAY;
            new ReplaceColorStolb().execute();

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
            q.printStackTrace();
        } catch (IOException j) {
            j.printStackTrace();
        }

        final StringBuffer EndstringBuffer = new StringBuffer();
        final LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.edit_dz , null);
        final TextView textView = promptsView.findViewById(R.id.textViewNameYrok);
        final EditText editText = promptsView.findViewById(R.id.textEdit);
        if(2 <= finalHelp.length) {
            String[] temp3 = finalHelp[1].split("`");
            String tempik = "";
            if (temp3.length == 1)
                editText.setText(temp3[0]);   //ssssssssssssssssssssssssssssssssssssssssssssssss
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
            AlertDialog.Builder Zapic = new AlertDialog.Builder(context);
            final String[] finalHelp1 = finalHelp;
            final String[] tempbuffer = stringBuffer.toString().split("~");
            final int finalI = i;
            Zapic.setView(promptsView).setCancelable(true).setPositiveButton(context.getString(R.string.save), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int p) {
                    String tempTextEdit = "123";
                    if (!editText.getText().equals(""))
                        tempTextEdit = editText.getText().toString();
                        textViewDz.setText(tempTextEdit);
                    for (int j = 1; j <= tempbuffer.length; j++) {
                        if (j == finalI) {
                            String[] reject = editText.getText().toString().split("\n");
                            String TempJect = "";
                            for (int u = 0; u < reject.length; u++)
                                TempJect = TempJect + reject[u] + "`";
                            EndstringBuffer.append(finalHelp1[0] + "=" + TempJect).append("\n");
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
            AlertDialog alertDialog = Zapic.create();

            //и отображаем его:

            alertDialog.show();


        }}catch (Povtor povtor){}
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs.getBoolean("firstrun", true)) {
            settings = getSharedPreferences("Settings", MODE_PRIVATE);
            editor.putBoolean("Monday",true);
            editor.putBoolean("Tuesday",true);
            editor.putBoolean("Wednesday",true);
            editor.putBoolean("Thursday",true);
            editor.putBoolean("Friday",true);
            editor.putBoolean("Saturday",true);
            editor.apply();


            AlertDialog.Builder onStart = new AlertDialog.Builder(MainActivity.this);
            onStart.setMessage("Это приложение может отправить до 500 уведомлений в день, рекомендуем выключить все оповещение от этого приложения.")
                    .setCancelable(true)
                    .setPositiveButton("Открыть настройки", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("app_package", context.getPackageName());
                                intent.putExtra("app_uid", context.getApplicationInfo().uid);
                            } else {
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + context.getPackageName()));
                            }
                            context.startActivity(intent);
                        }
                    });
              AlertDialog onStartStarting = onStart.create();
              onStartStarting.setTitle("Предупреждение");
              onStartStarting.show();
            prefs.edit().putBoolean("firstrun", false).commit();
        }
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
        public void run(){
            String Type = null,
                    Name = null,
                    HourSay = null,
                    MinSay = null,
                    urlNot = null;
            String[] help, helpKab;
            String delimeter = "=";
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
                            help = temp_read.split(delimeter);
                            helpKab = help[1].split(",");
                            Name = helpKab[0];

                            TimeHoursStart = Integer.parseInt(help[0].substring(0, 2));
                            TimeMinsStart = Integer.parseInt(help[0].substring(3, 5));
                            TimeHoursEnd = Integer.parseInt(help[0].substring(8, 10));
                            TimeMinsEnd = Integer.parseInt(help[0].substring(11,13));

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
                                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
                                notificationManager.createNotificationChannel(notificationChannel);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                NotificationCompat.Builder notifycationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                        .setAutoCancel(false)
                                        .setSmallIcon(R.drawable.ic_stat_name)
                                        .setWhen(System.currentTimeMillis())
                                        .setContentIntent(pendingIntent)
                                        .setContentTitle(Name)
                                        .setContentText(Type + ": " + HourSay + MinSay)
                                        .setPriority(IMPORTANCE_HIGH);
                                notificationManager.notify(NOTIFY_ID, notifycationBuilder.build());
                            } else {
                                NotificationCompat.Builder builder =
                                        new NotificationCompat.Builder(context)
                                                .setSmallIcon(R.drawable.ic_stat_name)
                                                .setContentTitle(Name)
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

    public void ClickCreateCustomTheme(View view){
        LinearLayout linearLayout = findViewById(R.id.field_create_fragment);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        if(view.getId() == R.id.Card_create) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            new Thread(new Runnable() {
                public void run() {
                    colors.put(R.id.custom_icon, ContextCompat.getColor(context,R.color.custom_icon));
                    colors.put(R.id.custom_background, ContextCompat.getColor(context, R.color.custom_background));
                    colors.put(R.id.custom_toolbar, ContextCompat.getColor(context, R.color.custom_toolbar));
                    colors.put(R.id.custom_toolbar_text, ContextCompat.getColor(context, R.color.custom_toolbar_text));
                    colors.put(R.id.custom_notification_bar, ContextCompat.getColor(context, R.color.custom_notification_bar));
                    colors.put(R.id.custom_text_light, ContextCompat.getColor(context, R.color.custom_text_light));
                    colors.put(R.id.custom_text_dark, ContextCompat.getColor(context, R.color.custom_text_dark));
                    colors.put(R.id.custom_text_hint, ContextCompat.getColor(context, R.color.custom_hint_text));
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
                    colors.put(R.id.custom_switch_on_background, ContextCompat.getColor(context, R.color.custom_switch_on_background));
                    colors.put(R.id.custom_border_theme, ContextCompat.getColor(context, R.color.custom_border_theme));
                }
            }).start();



            final EditText editText = linearLayout.findViewById(R.id.custom_name);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        if (getCurrentFocus() != null) {
                            View vw = getCurrentFocus();
                            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(vw.getWindowToken(), 0);
                                TempNameTheme = editText.getText().toString();

                            editText.clearFocus();
                        }
                    }
                    return false;
                }
            });

        }else{
            new CreateTheme().execute(colors);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    public void ClickColor(final View view){
        AmbilWarnaDialog colorEdit = new AmbilWarnaDialog(context, colors.get(view.getId()), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                view.setBackgroundColor(color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    colors.replace(view.getId(),color);
                else {
                    colors.remove(view.getId());
                    colors.put(view.getId(),color);
                }

            }
        });

        colorEdit.show();
    }

    class CreateTheme extends AsyncTask<HashMap<Integer, Integer>, String, Void>{
        AlertDialog alertDialog;
        TextView textView;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlertDialog.Builder progressDialog = new AlertDialog.Builder(context);
            final LayoutInflater li = LayoutInflater.from(context);
            final View promptsView = li.inflate(R.layout.loading_color , null);
            progressDialog.setView(promptsView)
                    .setCancelable(false);
            textView = promptsView.findViewById(R.id.textLoading);
            textView.setText(getString(R.string.Saving_theme));
            alertDialog = progressDialog.create();
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            alertDialog.hide();
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            textView.setText(values[0]);
        }

        @Override
        protected Void doInBackground(HashMap<Integer, Integer>... hashMaps) {
            StringBuffer stringBuffer = new StringBuffer();
            try {
                FileInputStream read = context.openFileInput("Themes.txt");
                InputStreamReader reader = new InputStreamReader(read);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String temp_read;
                while ((temp_read = bufferedReader.readLine()) != null) {
                   stringBuffer.append(temp_read).append("\n");
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

            int Generate_id = new Random().nextInt(1000) + 142132;
            editor.putInt("id_current_theme", Generate_id);
            editor.apply();
            if(TempNameTheme == null)
                TempNameTheme = getString(R.string.WhiteTheme);

            try {
                  FileOutputStream write =  context.openFileOutput("Themes.txt", MODE_PRIVATE);
                  String temp_write = stringBuffer.toString() +
                    Generate_id + "=" + TempNameTheme + "=" +
                      hashMaps[0].get(R.id.custom_icon) + "=" +
                      hashMaps[0].get(R.id.custom_border_theme)  + "=" +
                      hashMaps[0].get(R.id.custom_background) + "=" +
                      hashMaps[0].get(R.id.custom_toolbar) + "=" +
                      hashMaps[0].get(R.id.custom_toolbar_text) + "=" +
                      hashMaps[0].get(R.id.custom_notification_bar) + "=" +
                      hashMaps[0].get(R.id.custom_text_light) + "=" +
                      hashMaps[0].get(R.id.custom_text_dark) + "=" +
                      hashMaps[0].get(R.id.custom_text_hint) + "=" +
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
                      hashMaps[0].get(R.id.custom_switch_on_background);

                  write.write(temp_write.getBytes());
                  write.close();
              } catch (FileNotFoundException e) {
                  e.printStackTrace();
              } catch (IOException e) {
                  e.printStackTrace();
              }

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
            editorColor.putInt("custom_switch_on_background", hashMaps[0].get(R.id.custom_switch_on_background));

            editorColor.apply();
            return null;
        }
    }

}
