package com.digiflare.peoplecontactsproject;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class PersonViewHolder2 extends RecyclerView.ViewHolder {

    private TextView noteText;

    public PersonViewHolder2(final View itemView) {
        super(itemView);

        noteText = (TextView) itemView.findViewById(R.id.noteText2);
    }

    public void setText(String note){
        noteText.setText(note);
    }
}