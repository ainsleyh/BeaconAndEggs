package com.beaconhackathon.slalom.beaconandeggs.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides information for an individual store
 *
 * Created by ainsleyherndon on 10/17/15.
 */
public class Store implements Serializable {

    public Store() {
        availableCategories = new ArrayList<>();
    }

    /**
     * The name
     */
    public String name;

    /**
     * Available Categories for the store
     */
    public List<Category> availableCategories;
}
