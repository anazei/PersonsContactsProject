package com.digiflare.peoplecontactsproject;

import com.digiflare.peoplecontactsproject.interfaces.FragmentListener;
import com.digiflare.peoplecontactsproject.model.DBModel;
import com.digiflare.peoplecontactsproject.model.Person;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This is the top main panel
 */

public class Fragment1_port extends Fragment implements View.OnClickListener, TextWatcher {

    public static final String NAME = Fragment1_port.class.getSimpleName();
    private View view;
    private Button addUpdateButton;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private FragmentListener listener;
    private boolean updateMode = false;
    private Person person;
    private Person oldPerson; //store upon add or update, the most current person's first and last name because text fields will change and we need a way to record the last known entry
    private ApplicationActivity activityReference;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment1_port, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;

        firstNameInput = (EditText) view.findViewById(R.id.first_name_input);
        lastNameInput = (EditText) view.findViewById(R.id.last_name_input);

        addUpdateButton = (Button) view.findViewById(R.id.add_update_button);
        addUpdateButton.setOnClickListener(this);

        person = new Person("", "");
        firstNameInput.addTextChangedListener(this);
        lastNameInput.addTextChangedListener(this);

        //testing only
        DBModel.init();

        //Log.d("kevin", "get activity: " + getActivity());
        activityReference = (ApplicationActivity) getActivity();

        if(getArguments() != null) {
            //Log.d("kevin", "bundle " + getArguments().getString("hello", "DEFAULT"));

            String jsonString = getArguments().getString("hello", "DEFAULT");

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(jsonString);
            } catch(JSONException exception){

            }

            String firstName = jsonObject.optString("firstName");
            String lastName = jsonObject.optString("lastName");
            Person person = new Person(firstName, lastName);

            firstNameInput.setText(firstName);
            lastNameInput.setText(lastName);

            //if first name and last name exists as a record in DBModel, then run onClick to show recycler view, else, do nothing
            if(DBModel.checkIfProfileExists(person) == true) {

                //Log.d("kevin", "DBModel current master record on first start: " + DBModel.getCurrentMasterRecord());
                DBModel.setCurrentMasterRecord(person);

                //call onClick upon first start with shared preferences data so recycler view can be displayed
                onClick(null);
            }
        }
    }

    @Override
    public void onClick(final View view) {

        //assign a Person() to check DBModel to find match
        person.setFirstName(firstNameInput.getText().toString().toLowerCase());
        person.setLastName(lastNameInput.getText().toString().toLowerCase());

        //to determine updateMode, check to see if first name and last name Person exists already
        if(DBModel.checkIfProfileExists(person) == true){
            Toast.makeText(getActivity(), "Existing user profile retrieved", Toast.LENGTH_SHORT).show();
            updateMode = true;
            oldPerson = person;

            activityReference.showNoteField();
            activityReference.updateRecyclerView();

        }

        //if user already exists, set button to update, update mode = true
        //also retrieve any associated notes and display

        //then display add or update appropriately
        if (updateMode == false) {
            addUpdateButton.setText("Add");

        } else {
            addUpdateButton.setText("Update");
        }

        String firstName = firstNameInput.getText().toString().toLowerCase();
        String lastName = lastNameInput.getText().toString().toLowerCase();

        //determine if in update mode, if user is new
        if(updateMode == true){

            if(oldPerson != null) {
                //master user profile already exists, update master user profile's first and last name
                DBModel.updateExistingPersonProfileName(new Person(firstName, lastName), oldPerson);

                //if view parameter was passed in as null, then it means onClick was called during onCreate
                if(view != null) {
                    Toast.makeText(getActivity(), "User profile name was updated!", Toast.LENGTH_SHORT).show();
                }

                //update oldPerson to new first and last name
                oldPerson = new Person(firstName, lastName);
            }

        } else {

            //add new user
            if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)) {

                oldPerson = new Person(firstName, lastName);

                //model adds new entry
                DBModel.addNewPersonProfile(oldPerson);

                //change add button text to update
                addUpdateButton.setText("Update");

                updateMode = true;

                //show the add note field
                activityReference.showNoteField();
                activityReference.updateRecyclerView();

                Toast.makeText(getActivity(), "New Contact successfully added!", Toast.LENGTH_SHORT).show();
            } else {
                //display toast saying must not be empty
                Toast.makeText(getActivity(), "Textfields must not be empty!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {

        //if any of the text inputs are empty, always display add
        if (TextUtils.isEmpty(firstNameInput.getText().toString()) || TextUtils.isEmpty(lastNameInput.getText().toString())) {

            updateMode = false;
            addUpdateButton.setText("Add");

            //clear recyclerview and add note section
            activityReference.clearRecyclerViewAndHideAddNoteSection();

        } else {

        }
    }

    @Override
    public void afterTextChanged(final Editable s) {
    }

    /**
     * Returns the json string from the current EditText fields
     */
    public String getFirstAndLastNameFields(){
        String record = "{\"firstName\": \"" + firstNameInput.getText().toString() + "\",\"lastName\": \"" + lastNameInput.getText().toString() + "\"}";
        return record;
    }

}
