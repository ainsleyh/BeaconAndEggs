package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

/**
 * Maintains global application state
 *
 * Created by ainsleyherndon on 9/28/15.
 */
public class BeaconApplication extends Application {

    private BeaconManager notificationBeaconManager;
    public static Region region;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationBeaconManager = new BeaconManager(getApplicationContext());
        region = new Region("monitored region",
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D", 45777, 8263);

        notificationBeaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                showNotification(
                        "Hello.",
                        "You've entered the zone!");
            }
            @Override
            public void onExitedRegion(Region region) {
                showNotification(
                        "Bye.",
                        "You're exiting the zone!");
            }
        });

        notificationBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    notificationBeaconManager.startMonitoring(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, BeaconAndEggs.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}

