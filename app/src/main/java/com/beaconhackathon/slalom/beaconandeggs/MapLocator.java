package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.beaconhackathon.slalom.beaconandeggs.Models.Category;
import com.beaconhackathon.slalom.beaconandeggs.Models.Item;
import com.beaconhackathon.slalom.beaconandeggs.Models.Items;
import com.beaconhackathon.slalom.beaconandeggs.Models.Store;

import java.util.ArrayList;
import java.util.List;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

/**
 * Created by ainsleyherndon on 10/9/15.
 */
public class MapLocator extends Activity {

    private Items groceryCart;

    private BeaconManager rangingBeaconManager;
    private Region region;

    private Store store;

    private List<Category> selectedCategories;

    private MapListViewAdapter mListViewAdapter;
    private ListView groceryListView;

    private Category selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.map_locator);

        // retrieve the grocery list from the previous view
        Intent intent = this.getIntent();
        groceryCart = (Items) intent.getSerializableExtra("groceryCart");

        store = (Store) intent.getSerializableExtra("store");

        // locate the categories of the items in the list
        selectedCategories = determineCategories();

        region = new Region("monitored region",
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D", 45777, 8263);

        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {

            // clear text view
            TextView text = (TextView) findViewById(R.id.textView2);
            text.setText("calculating...");

            // emulator is not running so we instantiate the beaconmanager
            // set up ranging Beacon Manager
            rangingBeaconManager = new BeaconManager(this);

            rangingBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
                @Override
                public void onBeaconsDiscovered(Region region, List<Beacon> list) {

                    if (list.size() == 0)
                        return;

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

                    if (selectedCategory != null && selectedCategory.beaconId == nearestBeacon.getMinor()) {
                        // just update the meters and return
                        updateText((int)distance);
                        return;
                    }

                    if (selectedCategory != null && selectedCategory.beaconId != nearestBeacon.getMinor()) {
                        // return, or update map & change the selected category
                        return;
                    }

                }
            });
        }

        // register the message handler
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("ListCompleted"));

        // set up List view
        groceryListView = (ListView) findViewById(R.id.listView2);
        mListViewAdapter = new MapListViewAdapter(this, groceryCart.items);
        groceryListView.setAdapter(mListViewAdapter);
        mListViewAdapter.setMode(Attributes.Mode.Single);

        groceryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position < mListViewAdapter._filteredItems.size()) {
                        ((SwipeLayout) (groceryListView.getChildAt(
                                position - groceryListView.getFirstVisiblePosition()))).open(true);
                    }
                } catch(Exception ex) {

                }
            }
        });

        // setting the scroll view to the top of the map view
        final ScrollView sv = (ScrollView) findViewById(R.id.scrollview);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                sv.scrollTo(0, 0);
            }
        }, 250); // 250 ms delay
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

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private void updateText(int meters) {
        TextView text = (TextView) findViewById(R.id.textView2);
        text.setText(Html.fromHtml(selectedCategory.name + " <b>" + meters + "m</b> away"));
    }

    /**
     * Listen for a the list view items to be marked as completed
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // either move to next category or move to done screen
            if (selectedCategory == null)
                return;

            selectedCategories.remove(selectedCategory);
            selectedCategory = null;

            if (selectedCategories.isEmpty()) {
                // TODO go to done screen
            } else {
                // change color
                TextView text = (TextView) findViewById(R.id.textView2);
                text.setText("calculating...");
            }

        }
    };

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

            if (itemCategory == null)
                continue;

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

                        if (selectedCategory == null) {
                            selectedCategory = cat;
                            mListViewAdapter.updateItemList(selectedCategory.items);
                        }
                        break BEACONLOOP;
                    }
                }
            }
        }
        return nearestBeacon;
    }

}
