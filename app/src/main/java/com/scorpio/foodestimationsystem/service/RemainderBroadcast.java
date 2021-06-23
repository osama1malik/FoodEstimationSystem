package com.scorpio.foodestimationsystem.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import java.util.concurrent.atomic.AtomicInteger;

public class RemainderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");

        NotificationUtils _notificationUtils = new NotificationUtils(context);
        NotificationCompat.Builder _builder = _notificationUtils.setNotification(title, body);
        AtomicInteger seed = new AtomicInteger();
        _notificationUtils.getManager().notify(seed.incrementAndGet(), _builder.build());
    }
}