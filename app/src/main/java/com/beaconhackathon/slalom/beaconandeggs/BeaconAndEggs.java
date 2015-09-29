package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import java.util.List;

public class BeaconAndEggs extends Activity {

    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_and_eggs);

        beaconManager = new BeaconManager(this);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                // returns the list of beacons from closest to furthest
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);

                    //we should take an average proximity across the last N calls for
                    //better accuracy

                    // immediate, near, far
                    Utils.Proximity proximity = Utils.computeProximity(nearestBeacon);
                    // distance in meters
                    double distance = Utils.computeAccuracy(nearestBeacon);

                    String i = "";
                    //In general, the greater the distance between the device and the beacon,
                    // the lesser the strength of the received signal. T
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacon_and_eggs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //BEACON MANAGER METHODS BELOW

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(BeaconApplication.region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        try {
            beaconManager.stopRanging(BeaconApplication.region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onPause();
    }
}
