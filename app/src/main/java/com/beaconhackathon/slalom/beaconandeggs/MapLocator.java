package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.beaconhackathon.slalom.beaconandeggs.Models.Category;
import com.beaconhackathon.slalom.beaconandeggs.Models.GroceryCart;
import com.beaconhackathon.slalom.beaconandeggs.Models.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ainsleyherndon on 10/9/15.
 */
public class MapLocator extends Activity {

    private GroceryCart groceryCart;

    private List<Category> availableCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_view);

        // retrieve the grocery list from the previous view
        Intent intent = this.getIntent();
        groceryCart = (GroceryCart) intent.getSerializableExtra("groceryCart");

        // TODO populate with json data
        availableCategories = new ArrayList<>();

        // locate the categories of the items in the list
        List<Category> selectedCategories = determineCategories();

        //getLocationOfItems();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
