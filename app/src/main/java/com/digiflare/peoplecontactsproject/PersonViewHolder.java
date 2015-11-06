package com.digiflare.peoplecontactsproject;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class PersonViewHolder extends RecyclerView.ViewHolder {

    private TextView noteText;

    public PersonViewHolder(final View itemView) {
        super(itemView);

        noteText = (TextView) itemView.findViewById(R.id.noteText);
    }

    public void setText(String note){
        noteText.setText(note);
    }
}