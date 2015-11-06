package com.digiflare.peoplecontactsproject.model;


public class PersonNoteModel {

    public final String note;
    public final int layoutId;

    public PersonNoteModel(int layoutId, String note){
        this.layoutId = layoutId;

        if(note != null) {
            this.note = note;
        } else {
            this.note = "";

        }
    }

}
