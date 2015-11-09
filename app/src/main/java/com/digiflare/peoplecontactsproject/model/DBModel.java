package com.digiflare.peoplecontactsproject.model;

import com.google.gson.Gson;

import android.content.Context;
import android.util.Log;
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


    public DBModel(){
    }

    //pass in application context only, not activity context
    public static void setContext(Context context){
        applicationContext = context;
    }

    public static void init(){
        //TEST ONLY, REMOVE THIS ARRAYLIST.ADD WHEN DONE
        arrayList.add(new MasterRecord(new Person("kevin", "lam"), null));


    }

    //show all user profiles
    private static void testShowArrayList(){

        if(currentMasterRecord != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                Log.d("kevin", "user profile entry: " + arrayList.get(i).getPerson().firstName + " " + arrayList.get(i).getPerson().lastName);
            }

            Log.d("kevin", "current master record user: " + currentMasterRecord.getPerson().firstName + " " + currentMasterRecord.getPerson().lastName);
        }
    }

    //show particular profile's notes arraylist
    private static void testShowProfileNotes(Person profile){

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

        testShowArrayList();
    }

    //update current record
    public static void updateExistingPersonProfileName(Person newPerson, Person oldPerson){
        currentMasterRecord = new MasterRecord(newPerson, "");

        //cycle through arrayList to see what position oldPerson is located and replace with newPerson
        Log.d("kevin", "update existing person profile");

        for(int i=0;i<arrayList.size();i++){
            if(arrayList.get(i).getPerson().lastName.equals(oldPerson.lastName)
                    && arrayList.get(i).getPerson().firstName.equals(oldPerson.firstName)){
                arrayList.get(i).setPerson(newPerson);
            }
        }

        testShowArrayList();
    }

    //Check to see if profile already exists - so add/update button can show appropriate text
    public static boolean checkIfProfileExists(Person person){

        //cycle through ArrayList<MasterRecord>.People.firstName and ArrayList<MasterRecord>.People.lastName
        //if found, return true, else return false
        Log.d("kevin", "array list length " + arrayList.size());

        for(int i=0;i<arrayList.size();i++){
            String lastName = arrayList.get(i).getPerson().lastName;
            String firstName = arrayList.get(i).getPerson().firstName;

            if(person.lastName.equals(lastName) && person.firstName.equals(firstName)){
                Log.d("kevin", "profile already exists");
                currentMasterRecord = new MasterRecord(new Person(firstName, lastName), null);
                testShowArrayList();
                return true;
            }
        }

        Log.d("kevin", "profile does not exist");
        testShowArrayList();

        return false;
    }

    //add a new note to existing master record's notes arraylist
    public static void addNewNoteToExistingProfile(String note){

        Log.d("kevin", "size: " + arrayList.size());

        //currentMasterRecord.addNotes(note);

        for(int i=0;i<arrayList.size();i++){
            if(currentMasterRecord.getPerson().firstName.equals(arrayList.get(i).getPerson().firstName)
                    && currentMasterRecord.getPerson().lastName.equals(arrayList.get(i).getPerson().lastName)){
                arrayList.get(i).addNotes(note);
            }
        }

        testShowProfileNotes(currentMasterRecord.getPerson());

        getUsersArrayListNotes();

    }

    /**
     * Used to update recycler view upon adding new entry always based on currentMasterRecord
     */
    public static ArrayList<String> getUsersArrayListNotes(){

        for(int i=0;i<arrayList.size();i++){
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
     * convert the ArrayList<MasterRecord> into string for database storage
     */
    public static String convertToJSONArrayString(){

        String json = new Gson().toJson(arrayList);
        Log.d("kevin", "json array in string: " + json);

        return json;
    }
}

