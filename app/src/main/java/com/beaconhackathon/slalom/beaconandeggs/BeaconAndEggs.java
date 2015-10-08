package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class BeaconAndEggs extends Activity {

    private ListView groceryListView;

    private ArrayAdapter<String> groceryListAdapter;

    private ArrayList<String> groceryListItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_and_eggs);

        groceryListView = (ListView) findViewById(R.id.groceryListView);

        groceryListItems = fillItemList();
        groceryListAdapter = new ArrayAdapter<>(this, R.layout.grocery_list_item, groceryListItems);
        groceryListView.setAdapter(groceryListAdapter);
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
        return new ArrayList<>(Arrays.asList(new String[]{
                "Milk",
                "Eggs",
                "Chicken",
                "Fuji Apples",
                "Rice",
                "Cheese",
                "Yogurt"
        }));
    }
}
