package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.os.AsyncTask;

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

public class RecipeSearch extends Activity {


    private ItemListDatabaseHelper mIngredientListDB;
    private ArrayList<String> mIngredientListItems;
    private IngredientListAdapter mIngredientListAdapter;
    private RecipeListAdapter mRecipeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_recipe_search);
        super.onCreate(savedInstanceState);

        mIngredientListDB = new ItemListDatabaseHelper(getApplicationContext(),"Ingredients","IngredientName");

        ListView ingredientListView = (ListView) findViewById(R.id.ingredientListView);
        mIngredientListItems = fillItemList();
        mIngredientListAdapter = new IngredientListAdapter(
                this,
                R.id.ingredientListRow,
                mIngredientListItems
        );
        mIngredientListAdapter.setNotifyOnChange(true);
        ingredientListView.setAdapter(mIngredientListAdapter);

        ExpandableListView recipeListView = (ExpandableListView) findViewById(R.id.recipeSearchListView);
        mRecipeListAdapter = new RecipeListAdapter(
                this,
                R.id.recipeListRow,
                new ArrayList<Recipe>()
        );
        recipeListView.setAdapter(mRecipeListAdapter);

        SearchView searchView = (SearchView) findViewById(R.id.recipeSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                submitRecipeSearchQuery();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        //call recipe search activity
        //this functionality may be refactored from the menu
        if (id==R.id.action_showGroceryCart){
            onClickShowGroceryCart(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickShowGroceryCart (MenuItem item){
        Intent intent = new Intent(this,BeaconAndEggs.class);
        startActivity(intent);

    }
    public void onClickRemoveIngredient(View v)
    {
        View ingredientRow = (View) v.getParent();
        String ingredientName = (String)((TextView)ingredientRow.findViewById(R.id.ingredient_list_row_text)).getText();
        mIngredientListDB.removeItem(mIngredientListDB.getWritableDatabase(), (String) ingredientName);
        mIngredientListAdapter = (IngredientListAdapter)((ListView) findViewById(R.id.ingredientListView)).getAdapter();
        mIngredientListAdapter.remove(ingredientName);
        mIngredientListAdapter.notifyDataSetChanged();
        submitRecipeSearchQuery();
    }

    private void submitRecipeSearchQuery()
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
            recipeSearchUrl+="q="+ URLEncoder.encode(searchView.getQuery().toString(), "UTF-8");
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
//                mRecipeListAdapter.notifyDataSetChanged();
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
