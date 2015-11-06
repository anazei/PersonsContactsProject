package com.digiflare.peoplecontactsproject.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "kevin.db";

    private static SQLiteDatabase db;

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();

    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("create table profiles (ID INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT)");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS profiles");
        onCreate(db);
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
