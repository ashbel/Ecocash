package com.fis.ecocash.ecocash;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       // Toast.makeText(context, "ALARM!! ALARM!!", Toast.LENGTH_SHORT).show();

        //Stop sound service to play sound for alarm
        context.startService(new Intent(context, BackupService.class));

        //This will send a notification message and show notification in notification tray
        ComponentName comp = new ComponentName(context.getPackageName(),
                BackupNotificationService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));

    }
}
