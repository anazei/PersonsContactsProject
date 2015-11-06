package com.digiflare.peoplecontactsproject.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "kevinsDB";
    private static SQLiteDatabase db;

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
    }

    /**
     * SQL command to execute
     */
    public void command(String command){
        db.execSQL(command);
    }

    /**
     * Read from table
     */
    public String read(String command){
        Cursor cursor = db.rawQuery(command, null);
        //cursor.moveToFirst();

        return null;
    }

}
