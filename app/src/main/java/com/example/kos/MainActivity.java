package com.example.kos;


import android.Manifest;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.BoringLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
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
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    final Context context = this;
    private List<helperDnewnik> helperDnewniks = new ArrayList<>();
    private SharedPreferences settings;
    private SharedPreferences prefs = null;
    private  int startNedeli;
    private int startMes;
    private NotificationManager notificationManager;
    private static final int NOTIFY_ID = 1;
    private static final String CHANNEL_ID = "Novus_Pidor";
    private String url;
    private LinearLayout linearLayout;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       MyThread myThread = new MyThread();
       myThread.start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("com.example.kos", MODE_PRIVATE);
        NavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
               Fragment fragmentActiv = null;
               Class fragmentClass;
                SharedPreferences.Editor editor = settings.edit();
               switch (menuItem.getItemId()){
                   case R.id.Zvonki:
                       fragmentClass = ZnonkiFragment.class;
                       editor.putString("Fragment","Znonki" );
                       break;
                   case R.id.Ychetel:
                       fragmentClass = YchiteliaFragment.class;
                       editor.putString("Fragment","Ychitelia" );
                       break;
                       case R.id.Ocenki:
                       fragmentClass = OcenkiFragment.class;
                           editor.putString("Fragment","Ocenki" );
                       break;
                   case R.id.Nastroiki:
                       fragmentClass = NastroikiFragment.class;
                       editor.putString("Fragment","Nastroiki" );
                       break;
                   case R.id.Spravka:
                       fragmentClass = SpravkaFragment.class;
                       editor.putString("Fragment","Spravka" );
                       break;
                       default:
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
                return false;
            }
        });

        Class fragmentClass;
        settings = getSharedPreferences("Settings", MODE_PRIVATE);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.Drawer);
        drawer.openDrawer(Gravity.LEFT);
    }

    public void ClicksRow(final View view){
        TextView textViewName = view.findViewById(R.id.textView1_1_dnev);
        TextView textViewKab = view.findViewById(R.id.textView1_2_dnev);
       url = (settings.getInt("StartNedeli",1) + settings.getInt("Card",1)) + "." + settings.getInt("IntMes",1) + "." + settings.getInt("Year",1);
       final StringBuffer stringBuffer = new StringBuffer();
       String[] helpKab, finalHelp = new String[1];
       int i = 0;
        try {
            FileInputStream read =  openFileInput(url);
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
            String tempik = " ";
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
        helpKab = finalHelp[0].split(",");
        textView.setText(helpKab[0] + ":");
        AlertDialog.Builder Zapic = new AlertDialog.Builder(context);
       final String[] finalHelp1 = finalHelp;
        final String[] tempbuffer = stringBuffer.toString().split("~");
        final int finalI = i;
        Zapic.setView(promptsView).setCancelable(true).setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int p) {
                for (int j=1; j <= tempbuffer.length; j++){
                    if(j == finalI) {
                        String[] reject = editText.getText().toString().split("\n");
                        String TempJect = "";
                        for (int u = 0; u < reject.length; u++)
                            TempJect = TempJect + reject[u] + "`";
                         EndstringBuffer.append(finalHelp1[0] + "=" + TempJect).append("\n");
                    }
                    else
                        EndstringBuffer.append(tempbuffer[j - 1]).append("\n");

                }


                try {
                    FileOutputStream write =  openFileOutput(url, MODE_PRIVATE);
                    write.write(EndstringBuffer.toString().getBytes());
                    write.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                linearLayout = findViewById(R.id.LinerTask);
                new ReloadAsyncTask().execute();
            }
        });
        AlertDialog alertDialog = Zapic.create();

        //и отображаем его:

        alertDialog.show();

        /**
         * Запись в файл какой сейчас день(число позиции) в фрагменте дневника
         * Тут это надо будет прочитать
         * Старт недели, месяц и год взять от туда же
         * прибавить к старту недели позицию фрагмента
         * открыть Gui редактирования с уже введенными данными!
         * Как-то обновить )
         * И добавить кучу багов
         */

    }
    class ReloadAsyncTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            ProgressBar progressBar = new ProgressBar(context);
            linearLayout.addView(progressBar, layoutParams);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            settings = getSharedPreferences("Settings", MODE_PRIVATE);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putInt("Card",0);
            editor.apply();
            final PagerAdapterInCard pagerAdapterInCard = new PagerAdapterInCard(helperDnewniks, context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.removeAllViews();
            final ViewPager viewPager = new ViewPager(context);
            viewPager.setAdapter(pagerAdapterInCard);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(120, 0, 120, 0);
            viewPager.setPageMargin(60);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    editor.putInt("Card",position);
                    editor.apply();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            linearLayout.addView(viewPager,layoutParams);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            helperDnewniks.clear();
            startNedeli = settings.getInt("StartNedeli",1);
            startMes = settings.getInt("IntMes",1);
            for (int i = 0; i < 6; i++){
                String url = (startNedeli + i) + "." + startMes + "." + settings.getInt("Year",119);
                String nameDay;

                switch (i){
                    case 1:
                        nameDay = "Вторник";
                        break;
                    case 2:
                        nameDay = "Среда";
                        break;
                    case 3:
                        nameDay = "Четверг";
                        break;
                    case 4:
                        nameDay = "Пятница";
                        break;
                    case 5:
                        nameDay = "Суббота";
                        break;

                    default:
                        nameDay = "Понедельник";
                        break;
                }
                try {
                    FileInputStream read = openFileInput(url);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String temp_read,helpZapis = "", helpZapis2 = "",helpZapis3 = "";
                    String[] help, helpKab;
                    String delimeter = "=";
                    if((temp_read = bufferedReader.readLine()) == null){
                        throw new FileNotFoundException();
                    }else{
                        help = temp_read.split(delimeter);
                        helpKab = help[0].split(",");
                        helpZapis = helpKab[0]+ "=";
                        helpZapis2 = helpKab[1].substring(1)+ "=";
                        helpZapis3 = helpZapis3 + " =";
                    }
                    while ((temp_read = bufferedReader.readLine()) != null) {
                        help = temp_read.split(delimeter);

                        helpKab = help[0].split(",");


                        helpZapis = helpZapis  + helpKab[0]+ "=";
                        helpZapis2 = helpZapis2  + helpKab[1].substring(1)+ "=";
                        if (2 <= help.length)
                            helpZapis3 = helpZapis3 + help[1]+ "=";
                        else
                            helpZapis3 = helpZapis3 + " =";
                    }
                    helperDnewniks.add(new helperDnewnik(nameDay,helpZapis,helpZapis2,helpZapis3));

                } catch (FileNotFoundException e) {}
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
//        try {
//            FileInputStream read = openFileInput("11.10-12.10.txt");
//            InputStreamReader reader = new InputStreamReader(read);
//            BufferedReader bufferedReader = new BufferedReader(reader);
//            String temp_read, temp;
//            while ((temp_read = bufferedReader.readLine()) != null) {}
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        if (prefs.getBoolean("firstrun", true)) {
            settings = getSharedPreferences("Settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
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
