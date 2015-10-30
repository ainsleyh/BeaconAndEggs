package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.beaconhackathon.slalom.beaconandeggs.Models.Items;
import com.beaconhackathon.slalom.beaconandeggs.Models.PuchasedListViewAdapter;
import com.daimajia.swipe.util.Attributes;

/**
 * Created by ainsleyh on 10/29/2015.
 */
public class PurchaseItems extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.purchased_view);


        // retrieve the item list from the previous view
        Intent intent = this.getIntent();
        Items items = (Items) intent.getSerializableExtra("items");

        final ListView groceryListView = (ListView) findViewById(R.id.groceryListView);

        PuchasedListViewAdapter mListViewAdapter = new PuchasedListViewAdapter(this, items.items);
        groceryListView.setAdapter(mListViewAdapter);
        mListViewAdapter.setMode(Attributes.Mode.Single);
    }
}
