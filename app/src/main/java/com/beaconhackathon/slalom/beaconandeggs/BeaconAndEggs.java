package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.beaconhackathon.slalom.beaconandeggs.Models.Category;
import com.beaconhackathon.slalom.beaconandeggs.Models.GroceryCart;
import com.beaconhackathon.slalom.beaconandeggs.Models.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeaconAndEggs extends Activity {

    private ListView groceryListView;

    private ArrayAdapter<String> groceryListAdapter;

    private ArrayList<String> groceryListItems;
    private GroceryCart groceryCart;

    private List<Category> availableCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_and_eggs);

        groceryListView = (ListView) findViewById(R.id.groceryListView);

        groceryListItems = fillItemList();
        groceryListAdapter = new ArrayAdapter<>(this, R.layout.grocery_list_item, groceryListItems);
        groceryListView.setAdapter(groceryListAdapter);

        // TODO populate categories with json data & remove this
        availableCategories = new ArrayList<Category>();
        groceryCart = new GroceryCart();
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

    private ArrayList<String> fillItemList() {
        return new ArrayList<>(Arrays.asList(new String[]{
                "Milk",
                "Eggs",
                "Chicken",
                "Fuji Apples",
                "Rice",
                "Cheese",
                "Yogurt"
        }));
    }

    /**
     * Called when the Done button is Click
     *
     * @param view the Buttone
     */
    public void onClickDone(View view) {

        // locate the categories of the items in the list
        List<Category> selectedCategories = determineCategories();

        // TODO change view? and then begin Dijkstra's

        getLocationOfItems();
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
