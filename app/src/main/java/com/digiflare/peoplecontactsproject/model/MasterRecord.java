package com.digiflare.peoplecontactsproject.model;

/**
 * This represents an individual master record user
 */

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

public class MasterRecord {

    private Person person;
    private ArrayList<String> notes = new ArrayList<String>();

    public MasterRecord(Person person, String notes){
        setPerson(person);
        addNotes(notes);
    }

    public Person getPerson(){
        return person;
    }

    public ArrayList<String> getNotes(){
        return notes;
    }

    public void setPerson(Person person){
        this.person = person;
    }

    public void addNotes(String note){
        if(!TextUtils.isEmpty(note) && note != null) {
            Log.d("kevin", "notes is not empty, add note");
            this.notes.add(note);
        }
    }
}
