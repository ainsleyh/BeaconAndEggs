package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Window;
import android.view.WindowManager;

import com.beaconhackathon.slalom.beaconandeggs.Models.Category;
import com.beaconhackathon.slalom.beaconandeggs.Models.GroceryCart;
import com.beaconhackathon.slalom.beaconandeggs.Models.Item;
import com.beaconhackathon.slalom.beaconandeggs.Models.Store;

import java.util.ArrayList;
import java.util.List;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

/**
 * Created by ainsleyherndon on 10/9/15.
 */
public class MapLocator extends Activity {

    private GroceryCart groceryCart;

    private BeaconManager rangingBeaconManager;
    private Region region;

    private Store store;

    private List<Category> selectedCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.map_view);

        // retrieve the grocery list from the previous view
        Intent intent = this.getIntent();
        groceryCart = (GroceryCart) intent.getSerializableExtra("groceryCart");

        store = (Store) intent.getSerializableExtra("store");

        // locate the categories of the items in the list
        selectedCategories = determineCategories();

        region = new Region("monitored region",
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D", 45777, 8263);

        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // emulator is running so do not instantiate the beaconmanager
            return;
        }

        // set up ranging Beacon Manager
        rangingBeaconManager = new BeaconManager(this);

        rangingBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
                @Override
                public void onBeaconsDiscovered(Region region, List<Beacon> list) {

                    Beacon nearestBeacon = getClosestBeacon(list);

                    if (nearestBeacon == null) {
                        // notify user, and return
                        return;
                    }

                    //we should take an average proximity across the last N calls for
                    //better accuracy

                    // immediate, near, far
                    Utils.Proximity proximity = Utils.computeProximity(nearestBeacon);
                    // distance in meters
                    double distance = Utils.computeAccuracy(nearestBeacon);

                    //In general, the greater the distance between the device and the beacon,
                    // the lesser the strength of the received signal.

                    // now we have the closest beacon, highlight on map
                    // mark as visited and repeat
                }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            rangingBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    try {
                        rangingBeaconManager.startRanging(region);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        try {
            rangingBeaconManager.stopRanging(region);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    /**
     * Returns the categories of the items in the listview
     *
     * @return A list of categories
     */
    private List<Category> determineCategories() {

        List<Category> selectedCategories = new ArrayList<>();

        for (Item item : groceryCart.items) {
            Category itemCategory = null;
            for (Category category : store.availableCategories) {
                if (category.id.equals(item.categoryID)) {
                    itemCategory = category;
                    break;
                }
            }

            if (!selectedCategories.contains(itemCategory))
                selectedCategories.add(itemCategory);
        }

        return selectedCategories;
    }

    /**
     * Determines the closest beacon with an item & category on the listview
     */
    public Beacon getClosestBeacon(List<Beacon> list) {

        // what is the closest beacon with a selected category?
        Beacon nearestBeacon = null;

        if (!list.isEmpty()) {
            // grab the closest beacon with a category selected
            BEACONLOOP: for (Beacon beacon: list) {

                for (Category cat: selectedCategories) {
                    if (cat.beaconId == beacon.getMinor()
                            && !cat.ItemsChecked()) {
                        nearestBeacon = beacon;
                        break BEACONLOOP;
                    }
                }
            }
        }
        return nearestBeacon;
    }

}
