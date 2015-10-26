package com.beaconhackathon.slalom.beaconandeggs.Models;
import android.database.Cursor;

import com.beaconhackathon.slalom.beaconandeggs.ItemListDatabaseHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The user's grocery cart
 *
 * Created by ainsleyherndon on 10/5/15.
 */
public class GroceryCart implements Serializable, Iterable<Item> {

    /**
     * A list of grocery items
     */
    public List<Item> items;

    private ItemListDatabaseHelper itemDatabase;

    private ItemListDatabaseHelper recipeDatabase;

    public GroceryCart(ItemListDatabaseHelper theItemDatabase,
                       ItemListDatabaseHelper theRecipeDatabase) {
        itemDatabase = theItemDatabase;
        recipeDatabase = theRecipeDatabase;
        items = fillItemList();
    }

    /**
     *
     * @param theItem item to be added.
     * @return true if item was added -
     * false if db/list already contains this item.
     */
    public boolean addItemToCart(Item theItem) {
        if (!items.contains(theItem)) {
            items.add(theItem);
            itemDatabase.insertItem(
                    itemDatabase.getWritableDatabase(),
                    theItem.name
            );
            return true;
        } else {
            return false;
        }
    }

    public boolean addItemToRecipe(Item theItem) {
        if (!recipeDatabase.dbContainsItem(
                recipeDatabase.getReadableDatabase(),
                theItem.name)
                ) {
            recipeDatabase.insertItem(
                    recipeDatabase.getWritableDatabase(),
                    theItem.name
                    );
            return true;
        } else {
            return false;
        }
    }

    private void removeItemFromRecipe(Item theItem) {
        recipeDatabase.removeItem(
                recipeDatabase.getWritableDatabase(),
                theItem.name
        );
    }

    private void removeItemFromMainList(Item theItem) {
        itemDatabase.removeItem(
                itemDatabase.getWritableDatabase(),
                theItem.name
        );
    }

    public Item getItemByIndex(int theIndex) {
        return items.get(theIndex);
    }

    public int getItemListLength() {
        return items.size();
    }

    public Iterator<Item> iterator() {
        return new ReadOnlyGroceryCartIterator(items);
    }

    public void removeAtIndex(int theIndex) {
        Item itemToRemove = items.get(theIndex);
        removeItemFromMainList(itemToRemove);
        removeItemFromRecipe(itemToRemove);
        items.remove(theIndex);
    }


    private ArrayList<Item> fillItemList() {
        LinkedList<Item> items = new LinkedList<>();
        Cursor itemsCursor = itemDatabase.getAllItems(
                itemDatabase.getReadableDatabase()
        );
        itemsCursor.moveToFirst();
        while (!itemsCursor.isAfterLast()) {
            items.add(
                    new Item(
                            itemsCursor.getString(0),
                            null,
                            null
                    )
            );
            itemsCursor.moveToNext();
        }
        return new ArrayList<>(items);
    }
}

class ReadOnlyGroceryCartIterator implements Iterator<Item> {
    private Iterator<Item> iter;

    public ReadOnlyGroceryCartIterator(List<Item> list) {
        iter = list.iterator();
    }

    @Override
    public boolean hasNext() {
        return iter.hasNext();
    }
    @Override
    public Item next() {
        return iter.next();
    }
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
