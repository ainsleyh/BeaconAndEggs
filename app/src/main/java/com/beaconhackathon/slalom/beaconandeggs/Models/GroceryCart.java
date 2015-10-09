package com.beaconhackathon.slalom.beaconandeggs.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * The user's grocery cart
 *
 * Created by ainsleyherndon on 10/5/15.
 */
public class GroceryCart {

    public GroceryCart() {
        items = new ArrayList<Item>();
    }

    /**
     * A list of grocery items
     */
    public List<Item> items;

}
