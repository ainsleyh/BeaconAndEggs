package com.beaconhackathon.slalom.beaconandeggs.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The Category class holds alike Items in the grocery store.
 *
 * Example: Meat, Dairy, etc
 *
 * Created by ainsleyherndon on 10/5/15.
 */
public class Category implements Serializable {

    public Category() {
        items = new ArrayList<Item>();
    }

    /**
     * The name
     */
    public String name;

    /**
     * The id
     */
    public UUID id;

    /**
     * The minor of the corresponding beacon
     */
    public int beaconId;

    /**
     * The aisle number of the Category items
     */
    public int aisleNum;

    /**
     * The list of grocery store items for the category
     */
    public List<Item> items;

    /**
     * Returns whether the items in the category are Checked
     * off the list (not Available)
     *
     * True=items are checked
     * False=items not checked
     */
    public boolean ItemsChecked() {
        if (items == null)
            return true;

        for (Item item : items) {
            if (item.state == State.Available)
                return false;
        }

        return true;
    }

    /**
     * Marks all of the items for the category as "Checked"
     */
    public void MarkItemsAsChecked() {
        if (items == null)
            return;

        for (Item item : items) {
            item.state = State.Checked;
        }
    }

}
