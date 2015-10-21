package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import org.json.*;

import com.beaconhackathon.slalom.beaconandeggs.Models.Category;
import com.beaconhackathon.slalom.beaconandeggs.Models.GroceryCart;
import com.beaconhackathon.slalom.beaconandeggs.Models.Item;
import com.beaconhackathon.slalom.beaconandeggs.Models.Notifications;
import com.beaconhackathon.slalom.beaconandeggs.Models.State;
import com.beaconhackathon.slalom.beaconandeggs.Models.Store;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;

public class BeaconAndEggs extends Activity {

    private GroceryCart groceryCart;

    private ItemListDatabaseHelper userItemListDB;

    private Store selectedStore;

    private Notifications notifications;

    private ListViewAdapter mListViewAdapter;

    private Context mContext = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_and_eggs);

        userItemListDB = new ItemListDatabaseHelper(getApplicationContext(), "UserItemList", "ItemName");

        groceryCart= new GroceryCart(fillItemList());

        final ListView groceryListView = (ListView) findViewById(R.id.groceryListView);
        notifications = new Notifications();

        mListViewAdapter = new ListViewAdapter(this, groceryCart);

        groceryListView.setAdapter(mListViewAdapter);

        mListViewAdapter.setMode(Attributes.Mode.Single);

        groceryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SwipeLayout) (groceryListView.getChildAt(
                        position - groceryListView.getFirstVisiblePosition()))).open(true);
            }
        });
        groceryListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("ListView", "OnTouch");
                return false;
            }
        });
        groceryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, "OnItemLongClickListener", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        groceryListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("ListView", "onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        groceryListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("ListView", "onItemSelected:" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("ListView", "onNothingSelected:");
            }
        });

        // populate available items in the store, and categories
        populateAvailableCategories();

        //See if the new items are being added to the list
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Item item = (Item) extras.get("item");
            groceryCart.items.add(item);
        }

        ListView groceryListItemView = (ListView)findViewById(R.id.groceryListView);
        groceryListItemView.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String itemName = (String) ((TextView) view).getText();
                ItemListDatabaseHelper ingredientDBHelper = new ItemListDatabaseHelper(getApplicationContext(), "Ingredients", "IngredientName");
                if (!ingredientDBHelper.dbContainsItem(ingredientDBHelper.getReadableDatabase(), itemName)) {
                    ingredientDBHelper.insertItem(ingredientDBHelper.getWritableDatabase(), itemName);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_beacon_and_eggs, menu);
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
        if (id==R.id.action_showRecipeSearch){
            onClickShowRecipeSearch(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Item> fillItemList() {
        LinkedList<Item> items = new LinkedList<>();
        Cursor itemsCursor = userItemListDB.getAllItems(
                userItemListDB.getReadableDatabase()
        );
        itemsCursor.moveToFirst();
        while (!itemsCursor.isAfterLast()) {
            items.add(new Item(itemsCursor.getString(0), null, null));
            itemsCursor.moveToNext();
        }
        return new ArrayList<>(items);
    }

    /**
     * Populates the list of available Categories and Items from the
     * json in an Assets file
     */
    private void populateAvailableCategories() {

        selectedStore = new Store();
        selectedStore.name = "Safeway";
        selectedStore.address = new ArrayList<>();
        selectedStore.address.add("1234 Strawberry Lane");
        selectedStore.address.add("Seattle, WA");

        try {
            String json = loadJSONFromAsset();
            JSONObject obj = new JSONObject(json);

            JSONArray arr = obj.getJSONArray("categories");
            for (int i = 0; i < arr.length(); i++)
            {
                Category cat = new Category();
                cat.name = arr.getJSONObject(i).getString("name");
                cat.id = UUID.fromString(arr.getJSONObject(i).getString("id"));
                cat.beaconId = arr.getJSONObject(i).getInt("beaconId");
                cat.aisleNum = arr.getJSONObject(i).getInt("aisleNum");

                JSONArray items = arr.getJSONObject(i).getJSONArray("items");
                for (int x = 0; x < items.length(); x++)
                {
                    Item item = new Item();
                    item.name = items.getJSONObject(x).getString("name");
                    item.id = UUID.fromString(items.getJSONObject(x).getString("id"));
                    item.nutritionFacts = items.getJSONObject(x).getString("nutritionFacts");
                    item.state = State.Available;

                    cat.items.add(item);
                }

                selectedStore.availableCategories.add(cat);
            }

        } catch (Exception ex) {

        }

    }

    /**
     * Loads the Json from the Category json file
     *
     * @return the json from the file
     */
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    /**
     * Called when the Done button is Click
     *
     * @param view the Button
     */
    public void onClickDone(View view) {

        // change view to MapLocator
        Intent intent = new Intent(BeaconAndEggs.this, MapLocator.class);
        intent.putExtra("groceryCart", this.groceryCart);
        intent.putExtra("store", this.selectedStore);
        intent.putExtra("notifications", this.notifications);
        startActivity(intent);
    }

    public void goToAdd(View view) {
        Intent intent = new Intent(BeaconAndEggs.this, ItemSearch.class);
        startActivity(intent);
    }

     /**
     * Called when the Menu item for recipe search is clicked
     *
     * @param item recipe search menu item
     */
    public void onClickShowRecipeSearch(MenuItem item){
        Intent intent = new Intent(BeaconAndEggs.this, RecipeSearch.class);
        startActivity(intent);

    }
}
