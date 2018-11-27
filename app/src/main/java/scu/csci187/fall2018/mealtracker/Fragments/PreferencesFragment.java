package scu.csci187.fall2018.mealtracker.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import scu.csci187.fall2018.mealtracker.Classes.SQLiteUserManager;
import scu.csci187.fall2018.mealtracker.R;
import scu.csci187.fall2018.mealtracker.Classes.UserPreferences;

public class PreferencesFragment extends Fragment {
    private UserPreferences userPrefs;
    private EditText calorieLow, calorieHigh, maxTimeInMinutes;
    private RadioGroup radioDietLabels;
    private int selectedRadioId = -1;
    private CheckBox vegetarian, vegan, pescatarian, kosher, gluten, paleo, shellfish,
                        dairy, treenut, peanut, egg;
    int lowCalorie, highCalorie, maxTime;

    Button buttonSavePrefs;

    public PreferencesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.preferences_layout, container, false);

        bindViews(view);
        addUIListeners();
        populatePreferencesFromDB();

        return view;
    }

    private void bindViews(View view) {
        calorieLow = view.findViewById(R.id.calorieLow);
        calorieHigh = view.findViewById(R.id.calorieHigh);
        maxTimeInMinutes = view.findViewById(R.id.timeHigh);
        radioDietLabels = view.findViewById(R.id.radioDietLabels);
        vegetarian = view.findViewById(R.id.checkboxVegetarian);
        vegan = view.findViewById(R.id.checkboxVegan);
        pescatarian = view.findViewById(R.id.checkboxPescatarian);
        kosher = view.findViewById(R.id.checkboxKosher);
        gluten = view.findViewById(R.id.checkboxGluten);
        paleo = view.findViewById(R.id.checkboxPaleo);
        shellfish = view.findViewById(R.id.checkboxShellfish);
        dairy = view.findViewById(R.id.checkboxDairy);
        treenut = view.findViewById(R.id.checkboxTreenut);
        peanut = view.findViewById(R.id.checkboxPeanut);
        egg = view.findViewById(R.id.checkboxEgg);
        buttonSavePrefs = view.findViewById(R.id.buttonSavePrefs);
    }

    public void addUIListeners() {
        addRadioListener();
        addSavePreferencesButtonListener();
    }

    public void populatePreferencesFromDB(){
        SQLiteUserManager myDB = new SQLiteUserManager(getContext());
        UserPreferences prefFromDb = myDB.getPreferences();
    }

    public void savePreferencesToDB() {
        String tempMinCal, tempMaxCal, tempMaxTime;

        tempMinCal = calorieLow.getText().toString();
        tempMaxCal = calorieHigh.getText().toString();
        tempMaxTime = maxTimeInMinutes.getText().toString();

        lowCalorie = (tempMinCal.isEmpty()) ? 0 : Integer.parseInt(tempMinCal);
        highCalorie = (tempMaxCal.isEmpty()) ? 0 : Integer.parseInt(tempMaxCal);
        maxTime = (tempMaxTime.isEmpty()) ? 0 : Integer.parseInt(tempMaxTime);
        UserPreferences newPreferences = new UserPreferences(lowCalorie, highCalorie, maxTime,
                selectedRadioId, vegetarian.isChecked(), vegan.isChecked(), pescatarian.isChecked(),
                kosher.isChecked(), gluten.isChecked(), paleo.isChecked(), shellfish.isChecked(),
                dairy.isChecked(), treenut.isChecked(), peanut.isChecked(), egg.isChecked());

        // Set member var with val that can be accessed.
        this.userPrefs = newPreferences;
        /*
            TODO: ERROR CHECKING INPUT FOR CALORIES, TIME
                 SAVE TO DATABASE
         */
    }

    public void addRadioListener() {
        final RadioGroup radio = radioDietLabels;
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = radio.findViewById(checkedId);
                selectedRadioId = radio.indexOfChild(radioButton);
            }
        });
    }

    public void addSavePreferencesButtonListener() {
        buttonSavePrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferencesToDB();
            }
        });
    }
}
