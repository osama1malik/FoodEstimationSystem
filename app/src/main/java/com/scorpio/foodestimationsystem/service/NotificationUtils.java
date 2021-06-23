package com.scorpio.foodestimationsystem.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.scorpio.foodestimationsystem.MainActivity;
import com.scorpio.foodestimationsystem.R;

import java.util.concurrent.atomic.AtomicInteger;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager notificationManager;
    private Context context;

    private String CHANNEL_ID = "notification channel";

    private String TIMELINE_CHANNEL_NAME = "Timeline notification";

    public NotificationUtils(Context base) {
        super(base);
        context = base;
        createChannel();
    }

    public NotificationCompat.Builder setNotification(String title, String body) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, TIMELINE_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(channel);
        }
    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }

    public void setReminder(long timeInMillis, String title, String body) {
        Intent intent = new Intent(context, RemainderBroadcast.class);

        intent.putExtra("title", title);
        intent.putExtra("body", body);

        int nmb = MainActivity.seed.incrementAndGet();
        Log.i("TAG", "setReminder: " + nmb);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, nmb, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
    }

}