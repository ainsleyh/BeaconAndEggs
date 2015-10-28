package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.os.AsyncTask;
import android.widget.Toast;

import com.beaconhackathon.slalom.beaconandeggs.Models.Item;
import com.beaconhackathon.slalom.beaconandeggs.Models.Items;
import com.beaconhackathon.slalom.beaconandeggs.Models.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RecipeSearch extends Activity {


    private ItemListDatabaseHelper mIngredientListDB;
    private ArrayList<String> mIngredientListItems;
    private IngredientListAdapter mIngredientListAdapter;
    private RecipeListAdapter mRecipeListAdapter;
    private Items mItemsToAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_recipe_search);
        super.onCreate(savedInstanceState);

        mIngredientListDB = new ItemListDatabaseHelper(getApplicationContext(),"Ingredients","IngredientName");

        mItemsToAdd = new Items();
        mItemsToAdd.items = new ArrayList<>();

        ListView ingredientListView = (ListView) findViewById(R.id.ingredientListView);
        mIngredientListItems = fillItemList();
        mIngredientListAdapter = new IngredientListAdapter(
                this,
                R.id.ingredientListRow,
                mIngredientListItems
        );
        ingredientListView.setAdapter(mIngredientListAdapter);

        ExpandableListView recipeListView = (ExpandableListView) findViewById(R.id.recipeSearchListView);
        mRecipeListAdapter = new RecipeListAdapter(
                this,
                R.id.recipeListRow,
                new ArrayList<Recipe>()
        );
        recipeListView.setAdapter(mRecipeListAdapter);

        final SearchView searchView = (SearchView) findViewById(R.id.recipeSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                submitRecipeSearchQuery();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                return true;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                submitRecipeSearchQuery();
                return true;
            }
        });

        submitRecipeSearchQuery();
    }

    private ArrayList<String> fillItemList() {
        LinkedList<String> items = new LinkedList<>();
        Cursor itemsCursor = mIngredientListDB.getAllItems(
                mIngredientListDB.getReadableDatabase()
        );
        itemsCursor.moveToFirst();
        while (!itemsCursor.isAfterLast()) {
            items.add(itemsCursor.getString(0));
            itemsCursor.moveToNext();
        }
        return new ArrayList<String>(items);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe_search, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    public void onClickRemoveIngredient(View v)
    {
        View ingredientRow = (View) v.getParent();
        String ingredientName = (String)((TextView)ingredientRow.findViewById(R.id.ingredient_list_row_text)).getText();
        mIngredientListDB.removeItem(mIngredientListDB.getWritableDatabase(), (String) ingredientName);
        mIngredientListAdapter.remove(ingredientName);
        submitRecipeSearchQuery();
        Toast.makeText(
                getApplicationContext(),
                "removed "+ingredientName+" from ingredient list",
                Toast.LENGTH_SHORT
        ).show();
    }


    public boolean onClickSubmitSearch(View v)
    {
        SearchView searchView = (SearchView) findViewById(R.id.recipeSearchView);
        submitRecipeSearchQuery();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        return true;
    }


    /**
     * Called when the header button is Click
     * Returns to main activity and adds requested ingredients to list
     *
     * @param view the Button
     */
    public void onClickHeader(View view) {

        // change view to MapLocator
        Intent intent = new Intent(RecipeSearch.this, BeaconAndEggs.class);
        intent.putExtra("itemsToAdd", mItemsToAdd);
        startActivity(intent);
    }

    /**
     * Called when the Add for recipe ingredients is clicked
     *
     * @param view the Button
     */
    public void onClickItemToAdd(View view) {
        // add item to itemsToAdd to main list
        Item itemToAdd = new Item();
        itemToAdd.name = (String) ((TextView)((View)view.getParent()).findViewById(R.id.recipe_ingredient_list_row_text)).getText();
        mItemsToAdd.items.add(itemToAdd);
        Toast.makeText(
                getApplicationContext(),
                itemToAdd.name +
                        " will be added your grocery list!",
                Toast.LENGTH_SHORT
        ).show();
    }


    public void submitRecipeSearchQuery()
    {
        //search call to api
        String recipeSearchURL = buildRecipeSearchURL();
        QueryRecipeAsyncTask queryRecipeAsyncTask = new QueryRecipeAsyncTask();
        queryRecipeAsyncTask.execute(new String[]{recipeSearchURL, recipeSearchURL, recipeSearchURL});
    }

    private String buildRecipeSearchURL() {
        String recipeSearchUrl = "http://api.yummly.com/v1/api/recipes?";

        SearchView searchView = (SearchView) findViewById(R.id.recipeSearchView);
        try {
            recipeSearchUrl+="q="+ URLEncoder.encode(searchView.getQuery().toString(), "UTF-8")+"&requirePictures=true";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        for(String ingredient : mIngredientListItems){
            recipeSearchUrl+= "&allowedIngredient[]="+ingredient.toLowerCase();
        }
        return recipeSearchUrl;
    }

    private void processRecipeJson(String recipeResults)
    {
        try {
            if(recipeResults!= null) {
                JSONObject recipeJSON = new JSONObject(recipeResults);
                JSONArray recipeMatches = (JSONArray) recipeJSON.get("matches");
                mRecipeListAdapter.clear();
                for (int i = 0; i < recipeMatches.length(); i++) {
                    JSONObject currentRecipe = (JSONObject) recipeMatches.get(i);
                    Recipe recipeResult = new Recipe(currentRecipe);
                    mRecipeListAdapter.add(recipeResult);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * Created by ianb on 10/18/2015.
     */
    private class QueryRecipeAsyncTask extends AsyncTask<String,Void,String>
    {
        @Override
        //accepts a url string and opens an http connection to the yummly api
        //returns a string of the result (json)
        protected String doInBackground(String... url) {
            String requestString = url[1]; //"http://api.yummly.com/v1/api/recipes?q=cheese&allowedIngredient[]=garlic&allowedIngredient[]=onion&allowedIngredient[]=bacon";
            String recipeResults = null;
            try {
                InputStream result = getRecipeSearchResults(requestString);
                recipeResults = readOutputStream(result);
            }catch(Exception e){
                e.printStackTrace();
            }
            return recipeResults;
        }

        //reads results of yummly api call and returns search results as a string
        private String readOutputStream(InputStream recipeStream) {
            StringBuffer output = new StringBuffer("");
            try {
                BufferedReader buffer = new BufferedReader(new InputStreamReader(recipeStream));
                String currentLine = buffer.readLine();
                while (currentLine != null) {
                    output.append(currentLine);
                    currentLine = buffer.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return output.toString();
        }

        // Makes HttpURLConnection to Yummly recipe search api
        private InputStream getRecipeSearchResults(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("X-Yummly-App-ID", "682da42e");
                httpConnection.setRequestProperty("X-Yummly-App-Key","87aa8341a461d05f6bb2361ebf678791");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stream;
        }

        @Override
        protected void onPostExecute(String resultString) {
            processRecipeJson(resultString);
        }
    }


}
