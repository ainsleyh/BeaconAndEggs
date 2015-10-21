package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.beaconhackathon.slalom.beaconandeggs.Models.Category;
import com.beaconhackathon.slalom.beaconandeggs.Models.GroceryCart;
import com.beaconhackathon.slalom.beaconandeggs.Models.Item;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;

public class BeaconAndEggs extends Activity {

    private GroceryCart groceryCart;

    private List<Category> availableCategories;

    private UserItemListDatabaseHelper userItemListDB;

    private ListViewAdapter mListViewAdapter;

    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_and_eggs);

        userItemListDB = new UserItemListDatabaseHelper(getApplicationContext());

        groceryCart= new GroceryCart(fillItemList());

        final ListView groceryListView = (ListView) findViewById(R.id.groceryListView);

        mListViewAdapter = new ListViewAdapter(this, groceryCart);

        groceryListView.setAdapter(mListViewAdapter);

        mListViewAdapter.setMode(Attributes.Mode.Single);

        groceryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SwipeLayout) (groceryListView.getChildAt(
                        position - groceryListView.getFirstVisiblePosition()))).open(true);
            }
        });
        groceryListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("ListView", "OnTouch");
                return false;
            }
        });
        groceryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, "OnItemLongClickListener", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        groceryListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("ListView", "onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        groceryListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("ListView", "onItemSelected:" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("ListView", "onNothingSelected:");
            }
        });

        // TODO populate categories with json data & remove this
        availableCategories = new ArrayList<>();

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

    private ArrayList<Item> fillItemList() {
        LinkedList<Item> items = new LinkedList<>();
        Cursor itemsCursor = userItemListDB.getAllItems(
                userItemListDB.getReadableDatabase()
        );
        itemsCursor.moveToFirst();
        while (!itemsCursor.isAfterLast()) {
            items.add(new Item(itemsCursor.getString(0), null, null));
            itemsCursor.moveToNext();
        }
        return new ArrayList<>(items);
    }

    /**
     * Called when the Done button is Click
     *
     * @param view the Button
     */
    public void onClickDone(View view) {
        /*UUID id = UUID.randomUUID();
        UUID categoryId = UUID.fromString("01a43abb-62ea-42f3-9daf-4d25c7940f5b");
        Item eggs = new Item("Eggs", id, categoryId);
        this.groceryCart.items.add(eggs);*/

        // change view to MapLocator
        Intent intent = new Intent(BeaconAndEggs.this, MapLocator.class);
        intent.putExtra("groceryCart", this.groceryCart);
        startActivity(intent);
    }

    /**
     * Returns the categories of the items in the listview
     *
     * @return A list of categories
     */
    private List<Category> determineCategories() {

        List<Category> selectedCategories = new ArrayList<Category>();

        for (Item item : groceryCart.items) {
            Category itemCategory = null;
            for (Category category : availableCategories) {
                if (category.id == item.categoryID) {
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
    public void getLocationOfItems() {

        // what is the closest beacon with a selected category?
        /*List<Beacon> list = new ArrayList<Beacon>();
        Beacon closestBeacon = null;
        Map<Beacon, List<Category>> beacons = new HashMap<Beacon, List<Category>>();
        if (!list.isEmpty()) {
            // grab the closest beacon with a category selected
            BEACONLOOP: for (Beacon beacon: list) {

                List<Category> beaconCategories = beacons.get(beacon);

                for (Category cat: beaconCategories) {
                    if (selectedCategories.contains(cat)) {
                        closestBeacon = beacon;
                        break BEACONLOOP;
                    }
                }
            }
        }*/

        // now we have the closest beacon, highlight on map
        // mark as visited and repeat
    }
}
