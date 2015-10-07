package com.beaconhackathon.slalom.beaconandeggs.Models;

import java.util.List;
import java.util.UUID;

/**
 * A Recipe
 *
 * Created by ainsleyherndon on 10/5/15.
 */
public class Recipe {

    /**
     * The Recipe name
     */
    public String name;

    /**
     * The id
     */
    public UUID id;

    /**
     * The items needed in the recipe
     */
    public List<Item> items;

    /**
     * The directions to complete the recipe
     */
    public String directions;

}
