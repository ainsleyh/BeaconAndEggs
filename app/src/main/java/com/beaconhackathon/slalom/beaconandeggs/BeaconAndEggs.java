package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.*;

import com.beaconhackathon.slalom.beaconandeggs.Models.Category;
import com.beaconhackathon.slalom.beaconandeggs.Models.GroceryCart;
import com.beaconhackathon.slalom.beaconandeggs.Models.Item;
import com.beaconhackathon.slalom.beaconandeggs.Models.State;
import com.beaconhackathon.slalom.beaconandeggs.Models.Store;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;

public class BeaconAndEggs extends Activity {

    private GroceryCart groceryCart;

    private Store selectedStore;

    private UserItemListDatabaseHelper userItemListDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_and_eggs);

        userItemListDB = new UserItemListDatabaseHelper(getApplicationContext());

        ListView groceryListView = (ListView) findViewById(R.id.groceryListView);

        ArrayList<String> groceryListItems = fillItemList();

        ArrayAdapter<String> groceryListAdapter = new ArrayAdapter<>(
                this,
                R.layout.grocery_list_item,
                groceryListItems
        );

        groceryListView.setAdapter(groceryListAdapter);

        // populate available items in the store, and categories
        populateAvailableCategories();

        groceryCart = new GroceryCart();

        //See if the new items are being added to the list
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Item item = (Item) extras.get("item");
            groceryCart.items.add(item);
        }
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

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<String> fillItemList() {
        LinkedList<String> items = new LinkedList<>();
        Cursor itemsCursor = userItemListDB.getAllItems(
                userItemListDB.getReadableDatabase()
        );
        itemsCursor.moveToFirst();
        while (!itemsCursor.isAfterLast()) {
            items.add(itemsCursor.getString(0));
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

        /*UUID id = UUID.randomUUID();
        UUID categoryId = UUID.fromString("01a43abb-62ea-42f3-9daf-4d25c7940f5b");
        Item eggs = new Item("Eggs", id, categoryId);
        this.groceryCart.items.add(eggs);*/

        // change view to MapLocator
        Intent intent = new Intent(BeaconAndEggs.this, MapLocator.class);
        intent.putExtra("groceryCart", this.groceryCart);
        intent.putExtra("store", this.selectedStore);
        startActivity(intent);
    }

    public void goToAdd(View view) {
        Intent intent = new Intent(BeaconAndEggs.this, ItemSearch.class);
        startActivity(intent);
    }
}
