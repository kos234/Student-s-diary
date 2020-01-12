package com.example.kos;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;


import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    final Context context = this;
    private AlertDialog alertDialogConfirmation;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs = null;
    private int what;
    private final Handler handler = new Handler();
    private MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder;
    private  int numStolbWrite = 0;
    private Boolean ic_micro = true;
    private NotificationManager notificationManager;
    private static final int NOTIFY_ID = 1;
    private static final String CHANNEL_ID = "Novus_Pidor";
    private String url;
    Boolean cancelAsyncTask = false;
    private View viewConfirm;
    private TextView ConfirmationTextView;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
       //MyThread myThread = new MyThread();
      // myThread.start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences("Settings", MODE_PRIVATE);
        editor = settings.edit();
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},3);
        }
        prefs = getSharedPreferences("com.example.kos", MODE_PRIVATE);
        NavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                try {
               Fragment fragmentActiv = null;
               Class fragmentClass;
               switch (menuItem.getItemId()){
                   case R.id.Zvonki:
                       if(settings.getString("Fragment","Dnewnik").equals("Znonki"))
                           throw new Povtor("KRIA", 1);
                       else
                       fragmentClass = ZnonkiFragment.class;
                       editor.putString("Fragment","Znonki" );
                       break;
                   case R.id.Ychetel:
                       if(settings.getString("Fragment","Dnewnik").equals("Ychitelia"))
                           throw new Povtor("KRIA", 1);
                       else
                       fragmentClass = YchiteliaFragment.class;
                       editor.putString("Fragment","Ychitelia" );
                       break;
                       case R.id.Ocenki:
                           if(settings.getString("Fragment","Dnewnik").equals("Ocenki"))
                               throw new Povtor("KRIA", 1);
                           else
                       fragmentClass = OcenkiFragment.class;
                           editor.putString("Fragment","Ocenki" );
                       break;
                   case R.id.Nastroiki:
                       if(settings.getString("Fragment","Dnewnik").equals("Nastroiki"))
                           throw new Povtor("KRIA", 1);
                       else
                       fragmentClass = NastroikiFragment.class;
                       editor.putString("Fragment","Nastroiki" );
                       break;
                   case R.id.Spravka:
                       if(settings.getString("Fragment","Dnewnik").equals("Spravka"))
                           throw new Povtor("KRIA", 1);
                       else
                       fragmentClass = SpravkaFragment.class;
                       editor.putString("Fragment","Spravka" );
                       break;
                       default:
                           if(settings.getString("Fragment","Dnewnik").equals("Dnewnik"))
                               throw new Povtor("KRIA", 1);
                           else
                           fragmentClass = DnewnikFragment.class;
                           editor.putString("Fragment","Dnewnik" );
                           break;
               }

               try {
                   fragmentActiv = (Fragment) fragmentClass.newInstance();
               } catch (IllegalAccessException e) {
                   e.printStackTrace();
               } catch (InstantiationException e) {
                   e.printStackTrace();
               }
               FragmentManager fragmentManager = getSupportFragmentManager();
               fragmentManager.beginTransaction().replace(R.id.Smena,fragmentActiv).commit();
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

        Class fragmentClass;
        switch (settings.getString("Fragment", "Dnewnik")){
            case "Ychitelia":
                fragmentClass = YchiteliaFragment.class;
                break;
            case "Znonki":
                fragmentClass = ZnonkiFragment.class;
                break;
            case "Ocenki":
                fragmentClass = OcenkiFragment.class;
                break;
            case "Nastroiki":
                fragmentClass = NastroikiFragment.class;
                break;
            case "Spravka":
                fragmentClass = SpravkaFragment.class;
                break;
            default:
                fragmentClass = DnewnikFragment.class;
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.Smena,fragmentActiv).commit();

    }

    public void openDrawer() {
        DrawerLayout drawer = findViewById(R.id.Drawer);
        drawer.openDrawer(Gravity.LEFT);
    }

    public void ClickTab (View view){
        int textViewId =  R.id.ocenka_one, editTextId = R.id.ocenka_edit_one, numZapic = 1, numStolb = R.id.numStolb_1;

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


        String[] ConfirmationValue = settings.getString("ConfirmationValue",getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed)).split("=");

        if(ConfirmationValue[numZapic-1].equals(getString(R.string.Not_Confirmed))){

            final TextView textView = view.findViewById(textViewId);
            final EditText editText = view.findViewById(editTextId);
        final TextView numStolbik = view.findViewById(numStolb);
        final int numZapicFinal = numZapic;
        final String url = (settings.getInt("endUrl",2020) - 1) + " - " + settings.getInt("endUrl",2020);

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
                                    if(count == Integer.parseInt(numStolbik.getText().toString())){
                                       help = temp_read.split(delimeter);
                                       help[numZapicFinal] = editText.getText().toString();
                                       for(int i = 0;i <= 7;i++){
                                           stringBuffer.append(help[i] + "=");
                                       }
                                       stringBuffer.append("\n");
                                       count ++;
                                    } else{
                                        count ++;
                                    stringBuffer.append(temp_read).append("\n");
                                }}
                            } catch (FileNotFoundException q) {
                                q.printStackTrace();
                            } catch (IOException j) {
                                j.printStackTrace();
                            }


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
                        ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.RECORD_AUDIO},2);
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
                        ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.CAMERA},1);
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
               String[] ConfirmationValue = settings.getString("ConfirmationValue",getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed)).split("=");
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
                        String[] ConfirmationValue = settings.getString("ConfirmationValue",getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed)).split("=");
                        for (int i = 0; i < ConfirmationValue.length; i++){
                            if(i == numStolbWrite){
                                ConfirmationWrite = ConfirmationWrite + getString(R.string.Confirmed) + "~amr=";
                            }else
                                ConfirmationWrite = ConfirmationWrite + ConfirmationValue[i] + "=";
                        }
                        editor.putString("ConfirmationValue",ConfirmationWrite);
                        editor.apply();
                        ConfirmationTextView.setText(getString(R.string.Confirmed));
                    }
                });
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
        startActivityForResult(intent, 10);
       String ConfirmationWrite = "";
       String[] ConfirmationValue = settings.getString("ConfirmationValue",getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed) + "=" + getString(R.string.Not_Confirmed)).split("=");
        for (int i = 0; i < ConfirmationValue.length; i++){
            if(i == numStolbWrite){
                ConfirmationWrite = ConfirmationWrite + getString(R.string.Confirmed) + "~jpg=";
            }else
                ConfirmationWrite = ConfirmationWrite + ConfirmationValue[i] + "=";
        }
        editor.putString("ConfirmationValue",ConfirmationWrite);
        editor.apply();
        ConfirmationTextView.setText(getString(R.string.Confirmed));
        alertDialogConfirmation.hide();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // если пользователь закрыл запрос на разрешение, не дав ответа, массив grantResults будет пустым
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(context,getString(R.string.NotCameraPermission),Toast.LENGTH_LONG).show();
                }
                else
                    CameraConfirmarion ();

                return;
            }
            case 2: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(context,getString(R.string.NotMicrophonePermission),Toast.LENGTH_LONG).show();
                }

                return;
            }
            case 3: {
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
               // adress = Uri.parse("https://www.youtube.com/user/devcolibri");
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
             if(what == R.id.DevColibri) {
                 MediaPlayer aud = MediaPlayer.create(context, R.raw.ice);
                 aud.start();
                 SystemClock.sleep(1000);
             }else{
             Intent browser= new Intent(Intent.ACTION_VIEW, adress);
        startActivity(browser);
    }}

  class MyThread extends Thread {
        public void run(){
            String Type = null;
            String Name = null;
            String HourSay = null;
            String MinSay = null;
            String urlNot = null;
            String[] help, helpKab;
            String delimeter = "=";
            int tempOneOne = 666;
            int tempOneTwo = 666;
            int tempTwoOne = 666;
            int tempTwoTwo = 666;
            int tempOneTimesOne = 666;
            int tempOneTimesTwo = 666;
            int tempTwoOneTwo = 666;
            int tempTwoTwoTwo = 666;
            Boolean oneTime = true;
            Boolean clear = true;
            int min = 666;
            int hour = 666;
            int minTemp = 666;
            int hourTemp = 666;
            settings = getSharedPreferences("Settings", MODE_PRIVATE);
            while (true) {
               Date date = new Date();
                switch (date.toString().substring(0,3)) {
                    case "Mon":
                        if (settings.contains("Monday")) {
                            if (settings.getBoolean("Monday", true))
                                urlNot = "Monday.txt";
                            else
                                continue;
                        }
                        break;
                    case "Tue":
                        if (settings.contains("Tuesday")) {
                            if (settings.getBoolean("Tuesday", true))
                                urlNot = "Tuesday.txt";
                            else
                                continue;
                        }
                        break;
                    case "Wed":
                        if (settings.contains("Wednesday")) {
                            if (settings.getBoolean("Wednesday", true))
                                urlNot = "Wednesday.txt";
                            else
                                continue;
                        }
                        break;
                    case "Thu":
                        if (settings.contains("Thursday")) {
                            if (settings.getBoolean("Thursday", true))
                                urlNot = "Thursday.txt";
                            else
                                continue;
                        }
                        break;
                    case "Fri":
                        if (settings.contains("Thursday")) {
                            if (settings.getBoolean("Thursday", true))
                                urlNot = "Friday.txt";
                            else
                                continue;
                        }
                        break;
                    case "Sat":
                        if (settings.contains("Saturday")) {
                            if (settings.getBoolean("Saturday", true))
                                urlNot = "Saturday.txt";
                            else
                                continue;
                        }
                        break;
                }
                if (urlNot != null) {
                    StringBuffer stringBuffer1 = new StringBuffer();
                    try {
                        FileInputStream read = openFileInput(urlNot);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        String temp_read, temp;

                        while ((temp_read = bufferedReader.readLine()) != null) {
//                            temp = stringBuffer1.append(temp_read).toString();
//                            stringBuffer1.setLength(0);
                            help = temp_read.split(delimeter);
                            helpKab = help[1].split(",");
                            Name = helpKab[0];
                            tempOneOne = Integer.parseInt(help[0].substring(0, 2));
                            tempOneTwo = Integer.parseInt(help[0].substring(3, 5));
                            tempTwoOne = Integer.parseInt(help[0].substring(8, 10));
                            tempTwoTwo = Integer.parseInt(help[0].substring(11,13));
                            if (oneTime)  {
                                tempOneTimesOne = tempOneOne;
                                tempOneTimesTwo = tempOneTwo;
                                oneTime = false;
                                clear = true;
                            }
                            else if((Integer.parseInt(date.toString().substring(11, 13)) + 1) == tempOneTimesOne) {
                                Type = "До начала урока";
                                hour = tempOneTimesOne - Integer.parseInt(date.toString().substring(11, 13));
                                min = hour * 60 - Integer.parseInt(date.toString().substring(14, 16)) + tempOneTimesTwo;
                                hour = 0;
                                while (min > 60) {
                                    hour = hour + 1;
                                    min = min - 60;
                                }
                            }
                           else if (tempOneOne <= Integer.parseInt(date.toString().substring(11, 13)) && tempOneTwo <= Integer.parseInt(date.toString().substring(14, 16))) {
                                if (tempTwoOne == Integer.parseInt(date.toString().substring(11, 13)) && tempTwoTwo >= Integer.parseInt(date.toString().substring(14, 16))) {
                                    min = tempTwoTwo - Integer.parseInt(date.toString().substring(14, 16));
                                    hour = 0;
                                    tempTwoOneTwo = tempTwoOne;
                                    tempTwoTwoTwo = tempTwoTwo;
                                    Type = "До конца урока";
                                }
                                if (tempTwoOne > Integer.parseInt(date.toString().substring(11, 13))) {
                                    hour = tempTwoOne - Integer.parseInt(date.toString().substring(11, 13));
                                    min = hour * 60 - Integer.parseInt(date.toString().substring(14, 16)) + tempTwoTwo;
                                    hour = 0;
                                    while (min > 60) {
                                        hour = hour + 1;
                                        min = min - 60;
                                    }
                                    tempTwoOneTwo = tempTwoOne;
                                    tempTwoTwoTwo = tempTwoTwo;
                                    Type = "До конца урока";
                                }
                            } else if (tempTwoOneTwo <= Integer.parseInt(date.toString().substring(11, 13)) && tempTwoTwoTwo <= Integer.parseInt(date.toString().substring(14, 16)) && tempOneOne >= Integer.parseInt(date.toString().substring(11, 13)) && tempOneTwo >= Integer.parseInt(date.toString().substring(14, 16))) {

                                    if (tempOneOne == Integer.parseInt(date.toString().substring(11, 13)) && tempOneTwo >= Integer.parseInt(date.toString().substring(14, 16))) {
                                        min = tempOneTwo - Integer.parseInt(date.toString().substring(14, 16));
                                        hour = 0;
                                    }
                                    if (tempOneOne > Integer.parseInt(date.toString().substring(11, 13))) {
                                        hour = tempOneOne - Integer.parseInt(date.toString().substring(11, 13));
                                        min = hour * 60 - Integer.parseInt(date.toString().substring(14, 16)) + tempOneTwo;
                                        hour = 0;
                                        while (min > 60) {
                                            hour = hour + 1;
                                            min = min - 60;
                                        }
                                    }
                                    Type = "До конца перемены";
                                }
//                           else {
//                                oneTime = true;
//
//                            }





                        }
                        bufferedReader.close();
                        reader.close();
                        read.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (min != 666 || hour != 666) {
                        if (minTemp != min || hourTemp != hour) {
                            HourSay = Padej(hour, HourSay, true);
                            MinSay = Padej(min, MinSay, false);
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
                                                .setContentTitle(Type)
                                                .setContentText(HourSay + MinSay);

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

    public String Padej (int kool, String say,Boolean who) {
        if (kool == 0) {
            say = "";
        }
        else if (kool == 1) {
            if (who)
            say = "1 час ";
            else
            say = "1 минута " ;
        }
        else if (kool >= 2 && kool <= 4) {
            if (who)
            say = kool + " часа ";
            else
                say = kool + " минуты ";
        }
        else if (kool >= 5 && kool <= 20) {
            if (who)
            say = kool + " часов ";
            else
                say = kool + " минут ";
        }
        else if (Integer.parseInt(Integer.toString(kool).substring(0,1)) > 1 && Integer.parseInt(Integer.toString(kool).substring(Integer.toString(kool).length()-1 )) == 1) {
            if (who)
            say = kool + " час ";
            else
                say = kool + " минута ";
        }
        else if (Integer.parseInt(Integer.toString(kool).substring(0,1)) > 1 && Integer.parseInt(Integer.toString(kool).substring(Integer.toString(kool).length()-1 )) >= 2 && Integer.parseInt(Integer.toString(kool).substring(Integer.toString(kool).length()-1 )) <= 4) {
            if (who)
            say = kool + " часа ";
            else
                say = kool + " минуты ";
        }
        else if (Integer.parseInt(Integer.toString(kool).substring(0,1)) > 1 && Integer.parseInt(Integer.toString(kool).substring(Integer.toString(kool).length()-1 )) >= 5 && Integer.parseInt(Integer.toString(kool).substring(Integer.toString(kool).length()-1 )) <= 9) {
            if (who)
            say = kool + " часов ";
            else
                say = kool + " минут ";
        }
        else if (Integer.parseInt(Integer.toString(kool).substring(0,1)) > 1 && Integer.parseInt(Integer.toString(kool).substring(Integer.toString(kool).length()-1 )) == 0) {
            if (who)
                say = kool + " часов ";
            else
                say = kool + " минут ";
        }

        return say;
    }












  /* @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }*/
}
