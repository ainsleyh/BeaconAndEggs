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
    private List<Item> items;

    private ItemListDatabaseHelper itemDatabase;

    public GroceryCart(ItemListDatabaseHelper theItemDatabase) {
        itemDatabase = theItemDatabase;
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
        itemDatabase.removeItem(
                itemDatabase.getWritableDatabase(),
                items.get(theIndex).name
        );
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
