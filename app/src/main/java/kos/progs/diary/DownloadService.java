package kos.progs.diary;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Objects;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static android.app.NotificationManager.IMPORTANCE_LOW;

public class DownloadService extends Service {
    Context context;
    String CHANNEL_ID = "Загрузка";
    private final static int NOTIFY_ID = 2;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        new downloadUpdates().execute(intent.getStringExtra("src"), intent.getStringExtra("size"));

        return START_STICKY;
    }

    class downloadUpdates extends AsyncTask<String, String, Void> {
        double donwloadButes = 0;
        double size = 0;
        String points = ".";

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("setupPermission", true);
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
                        .setContentTitle(getString(R.string.installation) + "...")
                        .setContentText(getString(R.string.install_updates));
                startForeground(2, notificationBuilder.build());
            } else {
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_stat_name)
                                .setAutoCancel(true)
                                .setVibrate(null)
                                .setSound(null)
                                .setContentTitle(getString(R.string.installation) + "...")
                                .setWhen(System.currentTimeMillis())
                                .setContentIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setContentText(getString(R.string.install_updates));

                startForeground(2, builder.build());
            }

            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("setupPermission", true);
            startActivity(intent1);
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            donwloadButes += Integer.parseInt(values[0]);
            double procent = donwloadButes / size * 100 * 100;
            int tempI = (int) Math.round(procent);
            procent = (double)tempI/100;
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
                        .setContentTitle(getString(R.string.loading) + "...")
                        .setContentText(getString(R.string.downloaded) + " - " + procent + "%");
                startForeground(2, notificationBuilder.build());
            } else {
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_stat_name)
                                .setAutoCancel(true)
                                .setVibrate(null)
                                .setSound(null)
                                .setContentTitle(getString(R.string.loading) + "...")
                                .setWhen(System.currentTimeMillis())
                                .setContentIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setContentText(getString(R.string.downloaded) + " - " + procent + "%");

                startForeground(2, builder.build());
            }

        }

        @Override
        protected Void doInBackground(String... values) {
            try {
                size = Integer.parseInt(values[1]);
                try (BufferedInputStream in = new BufferedInputStream(new URL(values[0]).openStream()); FileOutputStream fout = new FileOutputStream(Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath() + "/update.apk")) {

                    final byte[] data = new byte[8192];
                    int count;
                    while ((count = in.read(data, 0, 8192)) != -1) {
                        fout.write(data, 0, count);
                        publishProgress(String.valueOf(count));
                    }
                }

            } catch (Exception error) {
                error.printStackTrace();
            }
            return null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
