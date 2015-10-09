package com.beaconhackathon.slalom.beaconandeggs.Models;

import java.util.List;
import java.util.UUID;

/**
 * The Item class mimics an Item in a grocery store
 *
 * Created by ainsleyherndon on 10/5/15.
 */
public class Item {

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

    /**
     * The Item's nutrition facts
     * //  TODO Do we want this to be a string?
     */
    public String nutritionFacts;

    /**
     * The State of the item
     */
    public State state;

    /**
     * The Recipes the item can be found in
     */
    public List<Recipe> recipes;

}
