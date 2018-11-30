package scu.csci187.fall2018.mealtracker.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import scu.csci187.fall2018.mealtracker.Classes.SQLiteUserManager;
import scu.csci187.fall2018.mealtracker.R;
import scu.csci187.fall2018.mealtracker.Classes.UserPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesFragment extends Fragment {
    private UserPreferences userPrefs;
    private EditText calorieLow, calorieHigh, maxTimeInMinutes;
    private RadioGroup radioDietLabels;
    private RadioButton rbNone, rbLowCarb, rbLowFat, rbHighProtein, rbHighFiber, rbLowSodium;
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
        rbNone = view.findViewById(R.id.settingsNone);
        rbLowCarb = view.findViewById(R.id.settingsLowCarb);
        rbLowFat = view.findViewById(R.id.settingsLowFat);
        rbHighProtein = view.findViewById(R.id.settingsHighProtein);
        rbHighFiber = view.findViewById(R.id.settingsHighFiber);
        rbLowSodium = view.findViewById(R.id.settingsLowSodium);
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
        UserPreferences userPrefs = myDB.getPreferences();

        calorieLow.setText(Integer.toString(userPrefs.calorieLow));
        calorieHigh.setText(Integer.toString(userPrefs.calorieHigh));
        maxTimeInMinutes.setText(Integer.toString(userPrefs.maxTimeInMinutes));
        int dietLabel = userPrefs.getDietLabel();
        switch (dietLabel){
            case(0):
                rbNone.setChecked(true);
                break;
            case(1):
                rbLowCarb.setChecked(true);
                break;
            case(2):
                rbLowFat.setChecked(true);
                break;
            case(3):
                rbHighProtein.setChecked(true);
                break;
            case(4):
                rbHighFiber.setChecked(true);
                break;
            case(5):
                rbLowSodium.setChecked(true);
                break;
        }
        vegetarian.setChecked(userPrefs.isVegetarian());
        vegan.setChecked(userPrefs.isVegan());
        pescatarian.setChecked(userPrefs.isPescatarian());
        kosher.setChecked(userPrefs.isKosher());
        gluten.setChecked(userPrefs.isGluten());
        paleo.setChecked(userPrefs.isPaleo());
        shellfish.setChecked(userPrefs.isShellfish());
        dairy.setChecked(userPrefs.isDairy());
        treenut.setChecked(userPrefs.isTreenut());
        peanut.setChecked(userPrefs.isPeanut());
        egg.setChecked(userPrefs.isEgg());
    }

    public void savePreferencesToDB() {
        String tempMinCal, tempMaxCal, tempMaxTime;

        tempMinCal = calorieLow.getText().toString();
        tempMaxCal = calorieHigh.getText().toString();
        tempMaxTime = maxTimeInMinutes.getText().toString();

        int dietLabel = 0;
        if(rbLowCarb.isChecked()) dietLabel =1;
        else if(rbLowFat.isChecked())dietLabel =2;
        else if(rbHighProtein.isChecked()) dietLabel=3;
        else if(rbHighFiber.isChecked()) dietLabel=4;
        else if(rbLowSodium.isChecked()) dietLabel=5;

        lowCalorie = (tempMinCal.isEmpty()) ? 0 : Integer.parseInt(tempMinCal);
        highCalorie = (tempMaxCal.isEmpty()) ? 0 : Integer.parseInt(tempMaxCal);
        maxTime = (tempMaxTime.isEmpty()) ? 0 : Integer.parseInt(tempMaxTime);
        UserPreferences newPreferences = new UserPreferences(lowCalorie, highCalorie, maxTime,
                dietLabel, vegetarian.isChecked(), vegan.isChecked(), pescatarian.isChecked(),
                kosher.isChecked(), gluten.isChecked(), paleo.isChecked(), shellfish.isChecked(),
                dairy.isChecked(), treenut.isChecked(), peanut.isChecked(), egg.isChecked());

        // Set member var with val that can be accessed.
        //this.userPrefs = newPreferences;

        SQLiteUserManager myDB = new SQLiteUserManager(getContext());
        myDB.updatePreferences(newPreferences);
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
                Toast.makeText(getContext(), "Preferences saved", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
