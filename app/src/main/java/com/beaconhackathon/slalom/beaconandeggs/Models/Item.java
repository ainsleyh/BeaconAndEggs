package com.beaconhackathon.slalom.beaconandeggs.Models;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * The Item class mimics an Item in a grocery store
 *
 * Created by ainsleyherndon on 10/5/15.
 */
public class Item implements Serializable {

    public Item() {
        this.state = State.Available;
    }

    public Item(String name, UUID id, UUID categoryID) {
        this.state = State.Available;
        this.name = name;
        this.id = id;
        this.categoryID = categoryID;
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
     * The Items parent category
     */
    public UUID categoryID;

    /*
     * Quantity of the item in cart
     * todo not sure if this should be here
     * todo but it's useful for adding an item
     */
    public int quantity;

    /**
     * The Item's nutrition facts
     * //  TODO Do we want this to be a string?
     * // We might prefer this to be its own entity but can probably be a string for now.
     */
    public String nutritionFacts;

    /**
     * The State of the item
     */
    public State state;

}
