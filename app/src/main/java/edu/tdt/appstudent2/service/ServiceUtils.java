package edu.tdt.appstudent2.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by bichan on 9/7/17.
 */

public class ServiceUtils {
    //public static final long[] TIME_REPLAY = {1800000, 3600000, 21600000, 43200000};
    public static final long[] TIME_REPLAY = {60000, 3600000, 21600000, 43200000};

    public static void startService(Context context, Class service){
        Intent intent = new Intent(context, service);
        context.startService(intent);
    }

    public static void startService(Context context, Class service, long timeReplay){
        Intent intent = new Intent(context, service);
        PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), timeReplay, pintent);
    }

    public static void stopService(Context context, Class service){
        Intent intent = new Intent(context, service);
        PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pintent);
    }

}
