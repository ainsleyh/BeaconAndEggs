package com.beaconhackathon.slalom.beaconandeggs.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The user's grocery cart
 *
 * Created by ainsleyherndon on 10/5/15.
 */
public class GroceryCart implements Serializable {

    /**
     * A list of grocery items
     */
    public List<Item> items;

    public GroceryCart(List<Item> theList) {
        items = theList;
    }
}
