package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;
import com.beaconhackathon.slalom.beaconandeggs.Models.Category;
import com.beaconhackathon.slalom.beaconandeggs.Models.GroceryCart;
import com.beaconhackathon.slalom.beaconandeggs.Models.Item;
import com.beaconhackathon.slalom.beaconandeggs.Models.Items;
import com.beaconhackathon.slalom.beaconandeggs.Models.State;
import com.beaconhackathon.slalom.beaconandeggs.Models.Store;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;

public class BeaconAndEggs extends Activity {

    public static final String itemDatabase = "UserItemList";

    public static final String recipeItemDatabase = "Ingredients";

    public static final String dbItemNameColumn = "ItemName";

    public static final String dbItemCatColumn = "ItemCategory";

    public static final String dbRecipeItemNameColumn = "IngredientName";

    public static final String dbRecipeItemCatColumn = "IngredientCategory";

    private GroceryCart groceryCart;

    private Store selectedStore;

    private TextView notificationDialogTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_beacon_and_eggs);

        notificationDialogTextView = (TextView) findViewById(R.id.notification);

        groceryCart = new GroceryCart(
                new ItemListDatabaseHelper(
                        getApplicationContext(),
                        itemDatabase,
                        dbItemNameColumn,
                        dbItemCatColumn
                ),
                new ItemListDatabaseHelper(
                        getApplicationContext(),
                        recipeItemDatabase,
                        dbRecipeItemNameColumn,
                        dbRecipeItemCatColumn
                )
        );
        // See if we need to add item(s) from extras.
        checkToAddItems();

        final ListView groceryListView = (ListView) findViewById(R.id.groceryListView);

        ListViewAdapter mListViewAdapter = new ListViewAdapter(this, groceryCart);

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

        groceryListView.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Item currItem = (Item) parent.getItemAtPosition(position);
                if (!groceryCart.addItemToRecipe(currItem)) {
                    Toast.makeText(
                            getApplicationContext(),
                            currItem.name +
                                    " already exists in your ingredient list!",
                            Toast.LENGTH_SHORT
                    ).show();
                }else{
                    Toast.makeText(
                            getApplicationContext(),
                            currItem.name +
                                    " has been added your ingredient list!",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                return true;
            }
        });

        // populate available items in the store, and categories
        populateAvailableCategories();
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
        /*if (id==R.id.action_showRecipeSearch){
            onClickShowRecipeSearch(item);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
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
                    item.categoryID = items.getJSONObject(x).getString("categoryId");
                    item.categoryName = cat.name;
                    item.id = UUID.fromString(items.getJSONObject(x).getString("id"));
                    item.nutritionFacts = items.getJSONObject(x).getString("nutritionFacts");
                    item.state = State.Available;

                    cat.items.add(item);
                }

                selectedStore.availableCategories.add(cat);
            }

        } catch (Exception ex) {
            Log.d("Error", ex.getMessage());
        }

    }

    /**
     * Loads the Json from the Category json file
     *
     * @return the json from the file
     */
    public String loadJSONFromAsset() {
        String json;
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
     * Called when the Notification button is Clicked
     *
     * @param view the Button
     */
    public void onClickNotifications(View view) {
        final Dialog dialog = new Dialog(this);

        LinearLayout layout = new LinearLayout(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        BeaconApplication app = (BeaconApplication) getApplication();
        String coupons = "";
        if (!app.notifications.isEmpty()) {
            for (String coupon : app.notifications) {
                coupons = coupons + coupon + "\n";
            }
        } else {
            coupons = "There are currently no coupons available.";
        }

        notificationDialogTextView.setText(Html.fromHtml(coupons));
        notificationDialogTextView.setVisibility(View.VISIBLE);

        ViewGroup parent = (ViewGroup)notificationDialogTextView.getParent();
        parent.removeView(notificationDialogTextView);

        layout.addView(notificationDialogTextView);

        dialog.setContentView(layout);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                notificationDialogTextView.setVisibility(View.INVISIBLE);
            }
        });

        dialog.show();
    }

    /**
     * Called when the Done button is Click
     *
     * @param view the Button
     */
    public void onClickDone(View view) {

        // change view to MapLocator
        Intent intent = new Intent(BeaconAndEggs.this, MapLocator.class);

        Items items = new Items();

        for (Item item : this.groceryCart.items) {
            OUTERLOOP: for (Category cat : this.selectedStore.availableCategories) {
                for (Item categoryItem : cat.items) {
                    if (categoryItem.name.toUpperCase().equals(item.name.toUpperCase()) && !items.items.contains(categoryItem)) {
                        items.items.add(categoryItem);
                        break OUTERLOOP;
                    }
                }
            }
        }
        intent.putExtra("groceryCart", items);
        intent.putExtra("store", this.selectedStore);
        startActivity(intent);
    }

    public void goToAdd(View view) {
        Intent intent = new Intent(BeaconAndEggs.this, ItemSearch.class);
        startActivity(intent);
    }

     /**
     * Called when the recipe icon is clicked
     *
     * @param item recipe search view
     */
    public void onClickShowRecipeSearch(View item){
        Intent intent = new Intent(BeaconAndEggs.this, RecipeSearch.class);
        startActivity(intent);

    }


    //enable adding multiple items
    private void checkToAddItems() {
        //See if the new items are being added to the list
        Bundle extras = getIntent().getExtras();
        checkToAddItem();
        if (extras != null) {
            Items itemsToAdd = (Items)extras.getSerializable("itemsToAdd");
            if (itemsToAdd == null || itemsToAdd.items == null)
                return;
            for(Item item : itemsToAdd.items) {
                if (item != null) {
                    if (!groceryCart.addItemToCart(item)) {
                        Toast.makeText(
                                getApplicationContext(),
                                item.name +
                                        " already exists in your list!",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            }
        }
    }

    private void checkToAddItem() {
        //See if the new items are being added to the list
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Item item = (Item) extras.get("item");
            if (item != null) {
                if (!groceryCart.addItemToCart(item)) {
                    /*Toast.makeText(
                            getApplicationContext(),
                            item.name +
                                    " already exists in your list!",
                            Toast.LENGTH_SHORT
                    ).show();*/
                }
            }
        }
    }
}
