package com.beaconhackathon.slalom.beaconandeggs.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * A Recipe
 *
 * Created by ainsleyherndon on 10/5/15.
 */
public class Recipe {


    public Recipe(JSONObject recipeJSON){
        try {
            this.name = recipeJSON.getString("recipeName");
//            this.id //this field may not be used

            ArrayList<String> ingredients = new ArrayList<String>();
            JSONArray ingredientsJSON = recipeJSON.getJSONArray("ingredients");
            for (int i = 0; i < ingredientsJSON.length(); i++) {
                ingredients.add(ingredientsJSON.getString(i));
            }
            this.items = ingredients;

            this.flavors = new HashMap<>();

            if (recipeJSON.isNull("flavors")){
                flavors.put("Spicy", 0.0);
                flavors.put("Savory", 0.0);
                flavors.put("Bitter", 0.0);
                flavors.put("Sweet", 0.0);
                flavors.put("Sour", 0.0);
                flavors.put("Salty", 0.0);
            }else{
                JSONObject flavorsJSON = recipeJSON.getJSONObject("flavors");
                flavors.put("Spicy", flavorsJSON.getDouble("piquant"));
                flavors.put("Savory", flavorsJSON.getDouble("meaty"));
                flavors.put("Bitter", flavorsJSON.getDouble("bitter"));
                flavors.put("Sweet", flavorsJSON.getDouble("sweet"));
                flavors.put("Sour", flavorsJSON.getDouble("sour"));
                flavors.put("Salty", flavorsJSON.getDouble("salty"));
            }

            this.imageURL = recipeJSON.getJSONArray("smallImageUrls").getString(0);

            if(recipeJSON.isNull("totalTimeInSeconds")) {
                this.totalMinutes = "--";
            } else {
                this.totalMinutes = recipeJSON.getInt("totalTimeInSeconds") / 60 + "min";
            }
            this.rating = recipeJSON.getDouble("rating");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setSourceDetails(JSONObject sourceJSON)
    {
        try {
            this.sourceDisplayName = sourceJSON.getString("sourceDisplayName");
            this.sourceURL = sourceJSON.getString("sourceRecipeUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
    public ArrayList<String> items;

    /**
     * The directions to complete the recipe
     */
    public String[] directions;

    public HashMap<String,Double> flavors;

    public String imageURL;

    public String totalMinutes;

    public String sourceURL;

    public String sourceDisplayName;

    public double rating;
}
