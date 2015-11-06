package com.digiflare.peoplecontactsproject;

import com.digiflare.peoplecontactsproject.utils.SQLiteHelper;
import com.google.gson.Gson;

import com.digiflare.peoplecontactsproject.interfaces.FragmentListener;
import com.digiflare.peoplecontactsproject.model.DBModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

public class ApplicationActivity extends AppCompatActivity implements FragmentListener {

    private String SHARED_PREFERENCE_KEY = "sharedKey";

    private Fragment1_port fragment1;
    private Fragment2_port fragment2;
    private FragmentListener fragment2Listener;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SQLiteHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        db = new SQLiteHelper(this);

        fragment2 = new Fragment2_port();
        fragment1 = new Fragment1_port();

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        //DBModel check database to try to repopulate the DBModel's ArrayList<MasterRecord>

        //check sharedPreferences to see if there was previously anything in the first and last name fields to repopulate currentMasterRecord
        String jsonString = sharedPreferences.getString(SHARED_PREFERENCE_KEY, "");

        if(!TextUtils.isEmpty(jsonString)) {

            Log.d("kevin", "recreated activity, json string to be filled in is: " + jsonString);

            Bundle bundle = new Bundle();
            bundle.putString("hello", jsonString);
            fragment1.setArguments(bundle);

        }

        //place both fragments into the correct ID linear layout
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.bottomPanelUi, fragment2, Fragment2_port.NAME)
                .commit();

        fragmentManager.beginTransaction()
                .replace(R.id.topPanelUi, fragment1, Fragment1_port.NAME)
                .commit();

        //provide an application context for DBModel
        DBModel.setContext(getApplicationContext());
    }

    public void showNoteField(){
        fragment2Listener.showNoteField();
    }

    public void updateRecyclerView(){
        fragment2Listener.updateRecyclerView();
    }

    public void clearRecyclerViewAndHideAddNoteSection(){
        fragment2Listener.clearRecyclerViewAndHideAddNoteSection();
    }

    /**
     * Register a new listener for fragment2
     */
    public void registerNewListener(FragmentListener fragment){
        fragment2Listener = fragment;
    }

    /**
     * onStop during screen orientation, retrieve all the user profiles from DBModel and convert into JSON string
     *
     * if there is no master record, then it will save NO_DATA,
     * else JSON string of master record will be saved into shared preferences
     */
    @Override
    protected void onStop() {
        super.onStop();

        //Log.d("kevin", "current master record: " + DBModel.getCurrentMasterRecord());

        //String jsonString = DBModel.getCurrentMasterRecord();
        String jsonString = fragment1.getFirstAndLastNameFields();

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();

        if(jsonString != null){

            editor.putString(SHARED_PREFERENCE_KEY, jsonString);
            //Log.d("kevin", "master record exists");

        } else {
            editor.putString("NO_DATA", "");
            //Log.d("kevin", "master record does not exist");
        }

        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        fragment2Listener = null;

        //take all of DBModel's ArrayList<MasterRecord> and serialize into json string
        String jsonString = DBModel.convertToJSONArrayString();

        //save the string into database when leaving app
        //DBModel.saveTable(jsonString);

    }


}