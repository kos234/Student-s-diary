package kos.progs.diary;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import kos.progs.diary.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static android.app.NotificationManager.IMPORTANCE_LOW;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationTime extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new notifer(context, intent).start();
    }

    class notifer extends Thread {
        final Context context;
        final String CHANNEL_ID;
        private final static int NOTIFY_ID = 1;
        private final Intent intent;

        public notifer(Context context, Intent intent) {
            this.context = context;
            CHANNEL_ID = context.getString(R.string.NotifSettingsName);
            this.intent = intent;
        }

        public boolean onOneHour(int TimeHoursStart, int TimeMinsStart, int hourDate, int minDate) {
            int i = (TimeHoursStart * 60 + TimeMinsStart) - (hourDate * 60 + minDate);
            return i <= 60 && i >= 0;
        }

        @Override
        public void run() {
            String Type = null,
                    Name = null,
                    HourSay,
                    MinSay,
                    urlNot = null;
            int TimeHoursStart, TimeMinsStart, TimeHoursEnd, TimeMinsEnd,
                    LastHoursEnd = 666,
                    LastMinEnd = 666,
                    min = 666,
                    hour = 666;

            SharedPreferences settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
            Date date = new Date();
            switch (date.toString().substring(0, 3)) {
                case "Mon":
                    if (settings.getBoolean("Monday", true))
                        urlNot = "Monday.txt";
                    break;
                case "Tue":
                    if (settings.getBoolean("Tuesday", true))
                        urlNot = "Tuesday.txt";
                    break;
                case "Wed":
                    if (settings.getBoolean("Wednesday", true))
                        urlNot = "Wednesday.txt";
                    break;
                case "Thu":
                    if (settings.getBoolean("Thursday", true))
                        urlNot = "Thursday.txt";
                    break;
                case "Fri":
                    if (settings.getBoolean("Thursday", true))
                        urlNot = "Friday.txt";
                    break;
                case "Sat":
                    if (settings.getBoolean("SaturdaySettings", true))
                        if (settings.getBoolean("Saturday", true))
                            urlNot = "Saturday.txt";
                    break;
            }
            if (urlNot != null) {
                ArrayList<String> stringBuilder = new ArrayList<>();
                try {
                    FileInputStream read = context.openFileInput(urlNot);
                    InputStreamReader reader = new InputStreamReader(read);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String temp_read;

                    while ((temp_read = bufferedReader.readLine()) != null) {
                        stringBuilder.add(temp_read);
                    }
                    String[] dateTimes, dateTimesLast;
                    for (int i = 0; i < stringBuilder.size(); i++) {
                        dateTimes = generateDate(stringBuilder.get(i));

                        TimeHoursStart = Integer.parseInt(dateTimes[1]);
                        TimeMinsStart = Integer.parseInt(dateTimes[2]);
                        TimeHoursEnd = Integer.parseInt(dateTimes[3]);
                        TimeMinsEnd = Integer.parseInt(dateTimes[4]);
                        if (i != 0) {
                            dateTimesLast = generateDate(stringBuilder.get(i - 1));
                            LastHoursEnd = Integer.parseInt(dateTimesLast[3]);
                            LastMinEnd = Integer.parseInt(dateTimesLast[4]);
                        }

                        int hourDate = Integer.parseInt(date.toString().substring(11, 13));
                        int minDate = Integer.parseInt(date.toString().substring(14, 16));

                        if (i == 0 && onOneHour(TimeHoursStart, TimeMinsStart, hourDate, minDate)) {
                            Type = context.getString(R.string.StartYrok);
                            hour = TimeHoursStart - hourDate;
                            if (hour != 0)
                                min = hour * 60 + TimeMinsStart - minDate;
                            else
                                min = TimeMinsStart - minDate;
                            hour = 0;
                            while (min >= 60) {
                                hour = hour + 1;
                                min = min - 60;
                            }
                            Name = dateTimes[0];
                        } else if (icEndYrok(TimeHoursStart, TimeMinsStart, hourDate * 60 + minDate, TimeHoursEnd, TimeMinsEnd)) {
                            hour = TimeHoursEnd - hourDate;
                            if (hour != 0)
                                min = hour * 60 + TimeMinsEnd - minDate;
                            else
                                min = TimeMinsEnd - minDate;
                            hour = 0;
                            while (min >= 60) {
                                hour = hour + 1;
                                min = min - 60;
                            }
                            Type = context.getString(R.string.EndYrok);
                            Name = dateTimes[0];
                        } else if (icPeremena(LastHoursEnd, LastMinEnd, hourDate * 60 + minDate, TimeHoursStart, TimeMinsStart)) {
                            hour = TimeHoursStart - hourDate;
                            if (hour != 0)
                                min = hour * 60 + TimeMinsStart - minDate;
                            else
                                min = TimeMinsStart - minDate;
                            hour = 0;
                            while (min >= 60) {
                                hour = hour + 1;
                                min = min - 60;
                            }
                            Type = context.getString(R.string.EndPeremen);
                            Name = dateTimes[0];
                        }
                    }


                    bufferedReader.close();
                    reader.close();
                    read.close();
                } catch (IOException ignored) {
                }

                if (min != 666) {
                        HourSay = Padej(hour, true);
                        MinSay = Padej(min, false);

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("notification", true);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, IMPORTANCE_LOW);
                            notificationManager.createNotificationChannel(notificationChannel);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setAutoCancel(true)
                                    .setPriority(IMPORTANCE_HIGH)
                                    .setVibrate(null)
                                    .setSound(null)
                                    .setSmallIcon(R.drawable.ic_stat_name)
                                    .setWhen(System.currentTimeMillis())
                                    .setContentIntent(pendingIntent)
                                    .setContentTitle(Name)
                                    .setContentText(Type + ":" + HourSay + MinSay);
                            notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
                        } else {
                            NotificationCompat.Builder builder =
                                    new NotificationCompat.Builder(context)
                                            .setSmallIcon(R.drawable.ic_stat_name)
                                            .setAutoCancel(true)
                                            .setVibrate(null)
                                            .setSound(null)
                                            .setContentTitle(Name)
                                            .setWhen(System.currentTimeMillis())
                                            .setContentIntent(pendingIntent)
                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                            .setContentText(Type + ":" + HourSay + MinSay);

                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                            notificationManager.notify(NOTIFY_ID, builder.build());
                        }

                    } else if(intent.getBooleanExtra("notification", false)) {
                        NotificationManager notificationManager;
                        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();
                }
            }
        }

        public String[] generateDate(String readString) {
            try {
                String[] returnStrings = new String[5],
                        help = readString.split("="),
                        helpTimes = help[0].split("-"),
                        helpAmPmOne = helpTimes[0].split(" "),
                        helpAmPmTwo = helpTimes[1].split(" "),
                        helpTimeOne = helpAmPmOne[0].split(":"),
                        helpTimeTwo = helpAmPmTwo[0].split(":");

                returnStrings[0] = help[1] + ", " + help[2];

                if (helpAmPmOne.length == 2 && helpAmPmTwo.length == 2) {
                    if (helpAmPmOne[1].equals("PM"))
                        returnStrings[1] = String.valueOf(Integer.parseInt(helpTimeOne[0]) + 12);
                    else
                        returnStrings[1] = helpTimeOne[0];

                    if (helpAmPmTwo[1].equals("PM"))
                        returnStrings[3] = String.valueOf(Integer.parseInt(helpTimeTwo[0]) + 12);
                    else
                        returnStrings[3] = helpTimeTwo[0];
                } else {
                    returnStrings[1] = helpTimeOne[0];
                    returnStrings[3] = helpTimeTwo[0];
                }
                returnStrings[2] = helpTimeOne[1];
                returnStrings[4] = helpTimeTwo[1];
                return returnStrings;
            } catch (Exception error) {
                errorStack(error, context);
                return new String[]{""};
            }
        }

        private boolean icPeremena(int LastHoursStart, int LastMinsStart, int date, int TimeHoursStart, int TimeMinsStart){
            return LastHoursStart * 60 + LastMinsStart <= date && date <= TimeHoursStart * 60 + TimeMinsStart;
        }

        private boolean icEndYrok(int TimeHoursStart, int TimeMinsStart, int date, int TimeHoursEnd, int TimeMinsEnd){
            return TimeHoursStart * 60 + TimeMinsStart <= date && date <= TimeHoursEnd * 60 + TimeMinsEnd;
        }

        private String Padej(int kool, Boolean Type) {
            String say = "";
            try {
                if (kool == 0) {
                    if (!Type)
                        say = " 0 " + context.getString(R.string._0_Min);
                } else if (kool == 1) {
                    if (Type) say = " 1 " + context.getString(R.string._1_Hour);
                    else say = " 1 " + context.getString(R.string._1_Min);
                } else if (kool >= 2 && kool <= 4) {
                    if (Type) say = " " + kool + " " + context.getString(R.string._2_4_Hour);
                    else say = " " + kool + " " + context.getString(R.string._2_4_Min);
                } else if (kool >= 5 && kool <= 20) {
                    if (Type) say = " " + kool + " " + context.getString(R.string._5_20_Hour);
                    else say = " " + kool + " " + context.getString(R.string._5_20_Min);
                } else {
                    int koolobok = Integer.parseInt(Integer.toString(kool).substring(0, 1));
                    int lalala = Integer.parseInt(Integer.toString(kool).substring(Integer.toString(kool).length() - 1));
                    if (koolobok > 1 && lalala == 1) {
                        if (Type) say = " " + kool + " " + context.getString(R.string._end_1_Hour);
                        else say = " " + kool + " " + context.getString(R.string._end_1_Min);
                    } else if (koolobok > 1 && lalala >= 2 && lalala <= 4) {
                        if (Type)
                            say = " " + kool + " " + context.getString(R.string._end_2_4_Hour);
                        else say = " " + kool + " " + context.getString(R.string._end_2_4_Min);
                    } else if (koolobok > 1 && lalala >= 5 && lalala <= 9) {
                        if (!Type)
                            say = " " + kool + " " + context.getString(R.string._end_5_9_Min);
                    } else if (koolobok > 1 && lalala == 0) {
                        if (!Type) say = " " + kool + " " + context.getString(R.string._end_0_Min);
                    }
                }
            } catch (Exception error) {
                errorStack(error, context);
            }
            return say;
        }

    }

    public void errorStack(Exception error, Context context) {
        try {
            final Writer writer = new StringWriter();
            error.printStackTrace(new PrintWriter(writer));
            final StringBuilder log = new StringBuilder("Android API level - " + Build.VERSION.SDK_INT + "\n" + context.getString(R.string.app_name) + " - " + BuildConfig.VERSION_NAME + Build.VERSION.SDK_INT + "\nDevice - " + Build.DEVICE + " | " + Build.MODEL + "\nCurrent window - ");
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
}
