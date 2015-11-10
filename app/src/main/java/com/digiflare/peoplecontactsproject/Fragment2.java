package com.digiflare.peoplecontactsproject;

import com.digiflare.peoplecontactsproject.interfaces.FragmentListener;
import com.digiflare.peoplecontactsproject.model.DBModel;
import com.digiflare.peoplecontactsproject.model.PersonNoteModel;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class Fragment2 extends Fragment implements View.OnClickListener, FragmentListener {

    public final static String NAME = Fragment2.class.getSimpleName();

    private RecyclerView recyclerView;
    private ArrayList<PersonNoteModel> recyclerViewArrayList;
    private Button addNoteButton;
    private EditText noteInputText;

    public Fragment2(){
        recyclerViewArrayList = new ArrayList<PersonNoteModel>();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment2, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        addNoteButton = (Button) view.findViewById(R.id.add_note_button);
        addNoteButton.setOnClickListener(this);

        noteInputText = (EditText) view.findViewById(R.id.note_input);

        hideNoteField();

        //register itself to main activity's listener array as "ready"
        ((ApplicationActivity) getActivity()).registerNewListener(this);

        //self update recycler view when ready
        //updateRecyclerView();
    }

    public void hideNoteField(){

        noteInputText.setText("");

        addNoteButton.setVisibility(View.INVISIBLE);
        noteInputText.setVisibility(View.INVISIBLE);
    }

    public void showNoteField(){
        addNoteButton.setVisibility(View.VISIBLE);
        noteInputText.setVisibility(View.VISIBLE);

        //Log.d("kevin", "show note field: " + addNoteButton);
    }

    /**
     * update and display new list view once Person profile has been added
     */
    public void updateRecyclerView(){
        updatePersonNoteArrayList();
        recyclerView.setAdapter(new PersonAdapter(getActivity(), recyclerViewArrayList));
    }

    public void clearRecyclerViewAndHideAddNoteSection(){

        recyclerViewArrayList.clear();
        recyclerView.setAdapter(new PersonAdapter(getActivity(), recyclerViewArrayList));

        hideNoteField();
    }

    public void updatePersonNoteArrayList() {

        //clear any previous data first before re-adding
        recyclerViewArrayList.clear();

        ArrayList<String> notes = DBModel.getUsersArrayListNotes();

        int noteSize = notes.size();

        //if there's actually data inside the notes arraylist
        if(noteSize != 0) {

            for (int i = 0; i < noteSize; i++) {
                recyclerViewArrayList.add(new PersonNoteModel(R.layout.person_content_cell, notes.get(i)));
            }

        } else {

            //else, display a "No Notes" layout
            recyclerViewArrayList.add(new PersonNoteModel(R.layout.person_content_cell2, null));

        }
    }

    @Override
    public void onClick(View view) {
        DBModel.addNewNoteToExistingProfile(noteInputText.getText().toString());

        updateRecyclerView();
        noteInputText.setText("");
    }
}
