package com.example.musicplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_ID = "channel";


    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

    }

    public void createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "channel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("This is musicPlayer");

            NotificationManager manager  = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);




        }
    }
}
