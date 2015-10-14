package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.beaconhackathon.slalom.beaconandeggs.Models.Category;
import com.beaconhackathon.slalom.beaconandeggs.Models.GroceryCart;
import com.beaconhackathon.slalom.beaconandeggs.Models.Item;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class BeaconAndEggs extends Activity {

    private GroceryCart groceryCart;

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

        // TODO populate categories with json data & remove this
        groceryCart = new GroceryCart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacon_and_eggs, menu);
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
        startActivity(intent);
    }
}
