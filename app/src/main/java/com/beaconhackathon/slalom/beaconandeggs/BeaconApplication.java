package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.RemoteException;
import android.os.Vibrator;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains global application state
 *
 * Created by ainsleyherndon on 9/28/15.
 */
public class BeaconApplication extends Application {

    private BeaconManager notificationBeaconManager;
    public static Region region;

    public List<String> notifications;

    @Override
    public void onCreate() {
        super.onCreate();

        notifications = new ArrayList<>();

        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            // emulator is not running so we instantiate the beaconmanager
            notificationBeaconManager = new BeaconManager(getApplicationContext());
            region = new Region("monitored region",
                    "B9407F30-F5F8-466E-AFF9-25556B57FE6D", 45777, 8263);

            notificationBeaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
                @Override
                public void onEnteredRegion(Region region, List<Beacon> list) {
                    addNotification("Lettuce 20% off! Use Code: Healthly101 at counter");

                    Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(100);
                }

                @Override
                public void onExitedRegion(Region region) {
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
    }

    public void addNotification(String message) {
        // we can do an empty check since we only have one beacon
        if (notifications.isEmpty())
            notifications.add(message);

        // TODO notify user of sale
    }
}
