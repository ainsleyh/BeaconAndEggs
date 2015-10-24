package com.beaconhackathon.slalom.beaconandeggs;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

/**
 * Created by httpnick on 10/8/15.
 * DB helper to be used within the application
 * to store the user's persistent grocery list.
 */
public class ItemListDatabaseHelper extends SQLiteOpenHelper {

    private String DB_NAME;
    private String ITEM_NAME_COLUMN;

    public ItemListDatabaseHelper(Context context, String dbName, String itemNameColumn) {
        // arbitrary versioning!      V
        super(context, dbName, null, 1);
        ITEM_NAME_COLUMN = itemNameColumn;
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
                        "primary key)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * insert an item.
     * @param db db reference where table resides.
     * @param itemName item to insert.
     * @return whether insert successfully happened.
     */
    public long insertItem(SQLiteDatabase db, String itemName) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(ITEM_NAME_COLUMN, itemName);

        return db.insert(DB_NAME,
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
        resultCount.close();
        return resultCount.getCount() > 0;
    }



}
