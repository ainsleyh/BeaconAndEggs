package com.beaconhackathon.slalom.beaconandeggs;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import com.beaconhackathon.slalom.beaconandeggs.Models.Item;

import java.io.Serializable;

/**
 * Created by httpnick on 10/8/15.
 * DB helper to be used within the application
 * to store the user's persistent grocery list.
 */
public class ItemListDatabaseHelper extends SQLiteOpenHelper implements Serializable {

    private String DB_NAME;
    private String ITEM_NAME_COLUMN;
    private String ITEM_CAT_COLUMN;

    public ItemListDatabaseHelper(Context context, String dbName,
                                  String itemNameColumn, String itemCategoryColumn) {
        // arbitrary versioning!     V
        super(context, dbName, null, 1);
        ITEM_NAME_COLUMN = itemNameColumn;
        ITEM_CAT_COLUMN = itemCategoryColumn;
        DB_NAME = dbName;

        onCreate(this.getWritableDatabase());
    }


    /**
     * Init the Database and fill with some make-shift data.
     * @param sqLiteDatabase the database to initialize.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table if not exists" +
                        " " + DB_NAME + "(" +
                        ITEM_NAME_COLUMN +
                        " varchar(100) " +
                        "primary key, " +
                        ITEM_CAT_COLUMN +
                        " varchar(100))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * insert an item.
     * @param db db reference where table resides.
     * @param item item to insert.
     * @return whether insert successfully happened.
     */
    public long insertItem(SQLiteDatabase db, Item item) {
        ContentValues insertValues = new ContentValues();

        insertValues.put(ITEM_NAME_COLUMN,
                item.name
        );
        insertValues.put(ITEM_CAT_COLUMN,
                item.categoryName
        );

        return db.insert(
                DB_NAME,
                null,
                insertValues
        );
    }

    /**
     * clear out db.
     * @param db reference to which db to clear.
     */
    public void clearDB(SQLiteDatabase db) {
        db.delete(DB_NAME, null, null);
    }

    /**
     * query all items in the DB.
     * @param db reference to which db to query.
     * @return all items in db.
     */
    public Cursor getAllItems(SQLiteDatabase db) {

        return db.rawQuery(
                "select * from " + DB_NAME + ";",
                null
        );
    }

    /**
     * Remove a single item from the db.
     * @param db reference to which db to query.
     * @param itemName name of item to remove.
     * @return whether item successfully was deleted.
     */
    public int removeItem(SQLiteDatabase db, String itemName) {

        return db.delete(
                DB_NAME,
                ITEM_NAME_COLUMN + "= '" + itemName + "'",
                null
        );
    }

    /**
     * returns true if an item exists in the db.
     * @param db reference to which db to query.
     * @param itemName name of item to query existence of.
     * @return whether item exists in the database.
     */
    public boolean dbContainsItem(SQLiteDatabase db, String itemName) {

        Cursor resultCount = db.query(
                DB_NAME,
                new String[]{ITEM_NAME_COLUMN},
                ITEM_NAME_COLUMN + " = ?",
                new String[]{itemName},
                "","",""
        );
        int count = resultCount.getCount();
        resultCount.close();
        return count > 0;
    }



}
