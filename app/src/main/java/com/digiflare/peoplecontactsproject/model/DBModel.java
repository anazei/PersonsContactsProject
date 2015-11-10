package com.digiflare.peoplecontactsproject.model;

import com.google.gson.Gson;

import com.digiflare.peoplecontactsproject.utils.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * This class is the model that will pull the data from the database
 *
 * While the app is active, it will modify the model data stored in this class
 *
 * At certain points, the model will be saved to the database (i.e. onDestroy)
 *
 * The MasterRecord is the profile that is currently being edited and updated
 */
public final class DBModel {

    private static ArrayList<MasterRecord> arrayList = new ArrayList<MasterRecord>();
    private static MasterRecord currentMasterRecord = null;
    private static Context applicationContext;
    private static DatabaseHandler database;

    public DBModel(){
    }

    public static void init(Context context){

        applicationContext = context;

        Log.d("kevin", "db init");
        database = new DatabaseHandler(applicationContext);

        //check database to try to repopulate the DBModel's ArrayList<MasterRecord>
        readDataBase();
    }

    //show all user profiles
    private static void showArrayListLog(){

        if(currentMasterRecord != null) {

            int arrayListSize = arrayList.size();

            for (int i = 0; i < arrayListSize; i++) {
                Log.d("kevin", "user profile entry: " + arrayList.get(i).getPerson().firstName + " " + arrayList.get(i).getPerson().lastName);
            }

            Log.d("kevin", "current master record user: " + currentMasterRecord.getPerson().firstName + " " + currentMasterRecord.getPerson().lastName);
        }
    }

    //show particular profile's notes arraylist
    private static void showProfileNotesLog(Person profile){

        for(int i=0;i<arrayList.size();i++){

            if(profile.firstName.equals(arrayList.get(i).getPerson().firstName)
                    && profile.lastName.equals(arrayList.get(i).getPerson().lastName) ){

                Log.d("kevin", "profile " + profile.firstName + " " + profile.lastName + ": " + arrayList.get(i).getNotes() );
                break;
            }
        }
    }

    //add a new masterRecord to the arrayList
    public static void addNewPersonProfile(Person newPerson){

        MasterRecord record = new MasterRecord(newPerson, null);

        arrayList.add(record);
        currentMasterRecord = record;

        showArrayListLog();
    }

    //update current record
    public static void updateExistingPersonProfileName(Person newPerson, Person oldPerson){
        currentMasterRecord = new MasterRecord(newPerson, "");

        //cycle through arrayList to see what position oldPerson is located and replace with newPerson
        Log.d("kevin", "update existing person profile");

        int arrayListSize = arrayList.size();

        for(int i=0;i<arrayListSize;i++){
            if(arrayList.get(i).getPerson().lastName.equals(oldPerson.lastName)
                    && arrayList.get(i).getPerson().firstName.equals(oldPerson.firstName)){
                arrayList.get(i).setPerson(newPerson);
            }
        }

        showArrayListLog();
    }

    //Check to see if profile already exists - so add/update button can show appropriate text
    public static boolean checkIfProfileExists(Person person){

        //cycle through ArrayList<MasterRecord>.People.firstName and ArrayList<MasterRecord>.People.lastName
        //if found, return true, else return false
        Log.d("kevin", "array list length " + arrayList.size());

        int arrayListSize = arrayList.size();

        for(int i=0;i<arrayListSize;i++){
            String lastName = arrayList.get(i).getPerson().lastName;
            String firstName = arrayList.get(i).getPerson().firstName;

            if(person.lastName.equals(lastName) && person.firstName.equals(firstName)){
                Log.d("kevin", "profile already exists");
                currentMasterRecord = new MasterRecord(new Person(firstName, lastName), null);
                showArrayListLog();
                return true;
            }
        }

        Log.d("kevin", "profile does not exist");
        showArrayListLog();

        return false;
    }

    //add a new note to existing master record's notes arraylist
    public static void addNewNoteToExistingProfile(String note){

        Log.d("kevin", "size: " + arrayList.size());

        int arrayListSize = arrayList.size();

        for(int i=0;i<arrayListSize;i++){
            if(currentMasterRecord.getPerson().firstName.equals(arrayList.get(i).getPerson().firstName)
                    && currentMasterRecord.getPerson().lastName.equals(arrayList.get(i).getPerson().lastName)){
                arrayList.get(i).addNotes(note);
            }
        }

        showProfileNotesLog(currentMasterRecord.getPerson());

        getUsersArrayListNotes();

    }

    /**
     * Used to update recycler view upon adding new entry always based on currentMasterRecord
     */
    public static ArrayList<String> getUsersArrayListNotes(){

        int arrayListSize = arrayList.size();

        for(int i=0;i<arrayListSize;i++){
            if(arrayList.get(i).getPerson().firstName.equals(currentMasterRecord.getPerson().firstName)
                    && arrayList.get(i).getPerson().lastName.equals(currentMasterRecord.getPerson().lastName)){
                Log.d("kevin", "notes entry for " + currentMasterRecord.getPerson().firstName + " " + currentMasterRecord.getPerson().lastName + ": " +
                        arrayList.get(i).getNotes());
               return arrayList.get(i).getNotes();
            }
        }

        //return empty array list
        return new ArrayList<String>();
    }

    /**
     * Upon first time start and retrieving data from dbase and/or upon shared preferences on recreate activity,
     * set the current master record
     */
    public static void setCurrentMasterRecord(Person profile){
        currentMasterRecord = new MasterRecord(profile, null);
    }

    /**
     * Get most current master record for shared preferences in json string format
     *
     */
    public static String getCurrentMasterRecord(){

        if(currentMasterRecord != null) {
            String record = "{\"firstName\": \"" + currentMasterRecord.getPerson().firstName.toString() + "\",\"lastName\": \"" +
                    currentMasterRecord.getPerson().lastName.toString() + "\"}";
            return record;
        }

        return null;
    }

    /**
     * serialize the ArrayList<MasterRecord> into string format for database storage
     */
    public static String convertToJSONArrayString(){

        String json = new Gson().toJson(arrayList);
        Log.d("kevin", "json array in string: " + json);

        return json;
    }

    /**
     * Read database upon app start and populate fields if DB table exists
     */
    public static void readDataBase(){

        if(database.checkIfTableExists() == true) {

            //URL decode the string before returning it
            String decoded = null;

            try {
                decoded = URLDecoder.decode(database.getAllProfiles(), "UTF-8");
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            Log.d("kevin", "decoded JSON looks like this: " + decoded);

            //convert from JSON to ArrayList<MasterRecord>
            JSONArray jsonArray = null;

            try {
                jsonArray = new JSONArray(decoded);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            //populate ArrayList<MasterRecords>
            for (int i = 0; i < jsonArray.length(); i++) {

                MasterRecord tempMasterRecord = null;

                try {
                    //Log.d("kevin", "length of json array: " + jsonArray.optString(i).toString() );
                    JSONObject jsonObject = new JSONObject(jsonArray.optString(i).toString());

                    //Log.d("kevin", "json Object " + i + " person firstName: " + new JSONObject(jsonObject.optString("person")).optString("firstName") );
                    //Log.d("kevin", "json Object " + i + " person lastName: " + new JSONObject(jsonObject.optString("person")).optString("lastName") );

                    //construct a Person object first
                    String firstName = new JSONObject(jsonObject.optString("person")).optString("firstName").toString();
                    String lastName = new JSONObject(jsonObject.optString("person")).optString("lastName").toString();
                    Person person = new Person(firstName, lastName);

                    //put the Person in a temporary MasterRecord
                    tempMasterRecord = new MasterRecord(person, "");

                    //Log.d("kevin", "json Object " + i + " notes: " + jsonObject.optString("notes") );
                    JSONArray tempArray = new JSONArray(jsonObject.optString("notes"));

                    //begin to add the associated notes to that temporary MasterRecord
                    for (int j = 0; j < tempArray.length(); j++) {
                        //Log.d("kevin", "current notes array value: " + tempArray.get(j).toString());
                        tempMasterRecord.addNotes(tempArray.get(j).toString());
                    }

                    //add the MasterRecord to the ArrayList<MasterRecord>
                    arrayList.add(tempMasterRecord);
                } catch (Exception exception) {
                }
            }
        }
    }

    /**
     * Send in a JSON array to database onDestroy
     */
    public static void updateDatabase(String jsonString){

        //escape strings to send into SQL so it won't break the SQL request with too many quotations
        String encoded = null;

        try {
             encoded = URLEncoder.encode(jsonString, "UTF-8");
        } catch(Exception exception){
            exception.printStackTrace();
        }

        Log.d("kevin", "encoded string looks like this: " + encoded);

        database.updateProfile( encoded );
    }

}