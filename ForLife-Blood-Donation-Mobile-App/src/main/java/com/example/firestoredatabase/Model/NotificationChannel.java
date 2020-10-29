package com.example.firestoredatabase.Model;

import android.app.Application;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationChannel extends Application {
    public static final String CHANNEL_1_ID = "sendEmergencyNotification";
    public static final String CHANNEL_2_ID = "sendAwareNotification";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannesl();
        createNotificationChannes2();
    }

    private void createNotificationChannes2() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            android.app.NotificationChannel channel2 = new android.app.NotificationChannel(
                    CHANNEL_2_ID, "ForLife - Este timpul să donezi",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription("Încă trei zile și poți dona din nou!");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel2);
        }
    }

    private void createNotificationChannesl() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    CHANNEL_1_ID, "ForLife Urgență Sânge",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Apasă pentru mai multe detalii.");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
