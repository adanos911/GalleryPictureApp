package com.ayanot.discoveryourfantasy.helpUtil;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.ayanot.discoveryourfantasy.MainActivity;
import com.ayanot.discoveryourfantasy.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * <h3>Класс, создающий прогресс при загрузке(выгрузке) изображения на(с)
 * yandex disk</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
public class NotificationProgressBar {
    public static final String OPEN_NOTIF_MES = "OPEN_AFTER_NOTIFICATION";
    private static int maxProgress = 100;

    private Context context;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;

    private int id;

    public NotificationProgressBar(Context context, String title, int id) {
        this.context = context;
        this.id = id;
        createNotifChannel();
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra(OPEN_NOTIF_MES, title);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new NotificationCompat.Builder(context, "10")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText("Loading...")
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setProgress(maxProgress, 0, true);
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    public void show() {
        Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }

//    public void start() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int progress = 0;
//                while (progress < maxProgress) {
//                    SystemClock.sleep(300);
//                    progress += 10;
//                    builder.setProgress(maxProgress, progress, false)
//                            .setContentText(progress + " of " + maxProgress);
//                    notificationManager.notify(1, builder.build());
//                }
//            }
//        }).start();
//    }

    public void end(String title, String text) {
        builder.setProgress(0, 10, false)
                .setContentTitle(title)
                .setContentText(text);
        notificationManager.notify(id, builder.build());
    }


    private void createNotifChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "download";
            String description = "second";
            int importance = NotificationManager.IMPORTANCE_HIGH; //Important for heads-up notification
            NotificationChannel channel = new NotificationChannel("10", name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            channel.setVibrationPattern(new long[]{0L});
            channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
