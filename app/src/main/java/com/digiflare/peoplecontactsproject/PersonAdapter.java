package com.digiflare.peoplecontactsproject;

import com.digiflare.peoplecontactsproject.model.PersonNoteModel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public class PersonAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<PersonNoteModel> arrayList;

    View view;

    /**
     * Constructor needs to pass in an arraylist of the individual cells
     */
    public PersonAdapter(Context context, ArrayList<PersonNoteModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    /**
     * This physically "binds" the arraylist's XML layouts (R.layout.person_content_cell)
     * as the viewType to be inflated in onCreateViewHolder
     */
    @Override
    public int getItemViewType(final int position) {
        return arrayList.get(position).layoutId; //.layoutId is really R.layout.person_content_cell
    }

    /**
     * The XML layout then gets passed in as a viewType R.layout.person_content_cell
     * This layout gets inflated as a view and gets inserted into a ViewHolder
     *
     * This ViewHolder then gets passed to onBindViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        view = LayoutInflater.from(context).inflate(viewType, parent, false);
        ViewHolder viewHolder = new ViewHolder(view){};

        switch(viewType){
            case R.layout.person_content_cell:
                viewHolder = new PersonViewHolder(view);
                break;

            case R.layout.person_content_cell2:
                viewHolder = new PersonViewHolder2(view);
                break;
        }

        return viewHolder;
    }

    /**
     * The ViewHolder is then passed into onBindViewHolder so that
     * the inflated layout can be referenced
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        switch(holder.getItemViewType())
        {
            case R.layout.person_content_cell:
                ((PersonViewHolder) holder).setText(arrayList.get(position).note);
                break;

            case R.layout.person_content_cell2:
                ((PersonViewHolder2) holder).setText("No Notes");
                break;
        }

    }

    /**
     * defines how many recycler view "entries" to display
     */
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
