package com.digiflare.peoplecontactsproject.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

    private String TABLE_PROFILES = "profiles";
    private String KEY_ID = "id";
    private String KEY_JSON = "json";

    public DatabaseHandler(Context context){
        super(context, "profiles", null, 1);
        Log.d("kevin", "db run");
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {

        String createProfilesTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PROFILES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_JSON + " TEXT)";
        db.execSQL(createProfilesTable);

        Log.d("kevin", "onCreate db and add profile");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        onCreate(db);

        Log.d("kevin", "onUpgrade db");
    }

    /**
     * Retrieves all data from database and reads first row
     */
    public String getAllProfiles(){

        //check if table "profiles" exists first
        if(checkIfTableExists() == true) {

            String selectQuery = "SELECT * FROM " + TABLE_PROFILES;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                Log.d("kevin", "getAllProfiles " + cursor.getString(1));
                return cursor.getString(1);
            }
        }

        Log.d("kevin", "profiles do not exist yet");

        return null;
    }

    /**
     * Update first row in database if data already exists, else insert data
     */
    public void updateProfile(String data){

        //if table exists, update data, else add data
        if(checkIfTableExists() == true) {

            //update data
            SQLiteDatabase db = this.getWritableDatabase();

            String update = "UPDATE " + TABLE_PROFILES + " SET json = \"" + data + "\" WHERE id = \"1\"";
            db.execSQL(update);
            Log.d("kevin", "table exists, update profile");

        } else {

            Log.d("kevin", "tried updating profile but table doesn't exist so insert new profile");

            //insert data
            addContact(data);

        }
    }

    /**
     * Insert data into database
     */
    private void addContact(String data){

        SQLiteDatabase db = this.getWritableDatabase();

        String update = "INSERT INTO " + TABLE_PROFILES + " (json) VALUES(\""+ data + "\")";
        db.execSQL(update);

        Log.d("kevin", "add contact db");
    }

    /**
     * Checks to see if table profile exists
     */
    public boolean checkIfTableExists(){

        String selectQuery = "SELECT * FROM " + TABLE_PROFILES;

        Cursor cursor = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);
        } catch(Exception exception){
            exception.printStackTrace();
        }

        if(cursor != null) {
            if (cursor.getCount() > 0) {
                Log.d("kevin", "table does exist");
                return true;
            }
        }

        Log.d("kevin", "table does not exist");
        return false;
    }
}