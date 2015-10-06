package com.beaconhackathon.slalom.beaconandeggs.Models;

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
public class Category {

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
     * The id of the corresponding beacon
     */
    public UUID beaconId;

    /**
     * The aisle number of the Category items
     */
    public int aisleNum;

    /**
     * The list of grocery store items for the category
     */
    public List<Item> items;

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
