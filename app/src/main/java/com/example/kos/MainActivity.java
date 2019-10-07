package com.example.kos;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    final Context context = this;
    private TextView TextNew;
    private SharedPreferences settings;
    private TabLayout tabLayout;
    private String ZvonOne, ZvonTwo;
    private SharedPreferences prefs = null;
    private List<Fragment> list = new ArrayList<>();
    private NotificationManager notificationManager;
    private static final int NOTIFY_ID = 1;
    private static final String CHANNEL_ID = "Novus_Pidor";
    private String url;


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

               switch (menuItem.getItemId()){
                   case R.id.Dnevnik:
                       fragmentClass = DnewnikFragment.class;
                       break;
                   case R.id.Zvonki:
                       fragmentClass = ZnonkiFragment.class;
                       break;
                   case R.id.Ychetel:
                       fragmentClass = YchiteliaFragment.class;
                       break;
                   case R.id.Nastroiki:
                       fragmentClass = NastroikiFragment.class;
                       break;
                   case R.id.Spravka:
                       fragmentClass = SpravkaFragment.class;
                       break;
                       default:
                           fragmentClass = DnewnikFragment.class;
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

                return false;
            }
        });

        Fragment fragmentActiv = null;
        Class fragmentClass;
        fragmentClass = DnewnikFragment.class;
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

    public static class PagerAdapter extends FragmentPagerAdapter {
        private String[] name;
        PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            name = new String[] {
                    "Понедельник",
                    "Вторник",
                    "Среда",
                    "Четверг",
                    "Пятница",
                    "Суббота"

            };
        }
        @Override
        public CharSequence getPageTitle(int position){
            return name[position];
        }
        @Override
        public int getCount() {
            return 6;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new classMonday();
                case 1:
                    return new classTuesday();
                case 2:
                    return new classWednesday();
                case 3:
                    return new classThursday();
                case 4:
                    return new classFriday();
                case 5:
                    return new classSaturday();

                default:
                    return new classMonday();
            }
        }
    }


    public void openDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.Drawer);
        drawer.openDrawer(Gravity.LEFT);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
            String HourSay = null;
            String MinSay = null;
            String urlNot = null;
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
                            temp = stringBuffer1.append(temp_read).toString();
                            stringBuffer1.setLength(0);

                            tempOneOne = Integer.parseInt(temp.substring(0, 2));
                            tempOneTwo = Integer.parseInt(temp.substring(3, 5));
                            tempTwoOne = Integer.parseInt(temp.substring(8, 10));
                            tempTwoTwo = Integer.parseInt(temp.substring(11,13));
                            if (oneTime)  {
                                tempOneTimesOne = tempOneOne;
                                tempOneTimesTwo = tempOneTwo;
                                oneTime = false;
                                clear = true;
                            }
                            else if((Integer.parseInt(date.toString().substring(11, 13)) + 1) == tempOneTimesOne) {
                                Type = "До начала уроков";
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
                                    Type = "До конца перемены:";
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
                                        .setContentTitle(Type)
                                        .setContentText(HourSay + MinSay)
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
