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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import scu.csci187.fall2018.mealtracker.Classes.SQLiteUserManager;
import scu.csci187.fall2018.mealtracker.Classes.UserPreferences;
import scu.csci187.fall2018.mealtracker.R;

public class FiltersFragment extends Fragment {
    private EditText calorieLow, calorieHigh, maxTimeInMinutes;
    private RadioGroup radioDietLabels;
    private RadioButton rbNone, rbLowCarb, rbLowFat, rbHighProtein, rbHighFiber, rbLowSodium;
    private int selectedRadioId = -1;
    private CheckBox vegetarian, vegan, pescatarian, kosher, gluten, paleo, shellfish,
            dairy, treenut, peanut, egg;
    int lowCalorie, highCalorie, maxTime;
    Button buttonSavePrefs;
    public static UserPreferences tempFilters = null;

    private FiltersFragmentListener mCallback;

    public FiltersFragment() {
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
        View view = inflater.inflate(R.layout.search_filters, container, false);
        bindViews(view);
        addUIListeners();

        if (tempFilters!=null)
            populatePreferences(tempFilters);
        else
            populatePreferences(getDBPrefs());
        return view;
    }

    // Ensures that Activity has implemented FiltersFragmentListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FiltersFragmentListener) {
            mCallback = (FiltersFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FiltersFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public UserPreferences getDBPrefs() {
        SQLiteUserManager myDB = new SQLiteUserManager(getContext());
        UserPreferences userPrefs = myDB.getPreferences(getContext());
        return userPrefs;
    }

    public void populatePreferences(UserPreferences userPrefs){
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

    private void bindViews(View view) {
        calorieLow = view.findViewById(R.id.sfCalorieLow);
        calorieHigh = view.findViewById(R.id.sfCalorieHigh);
        maxTimeInMinutes = view.findViewById(R.id.sfTimeHigh);
        radioDietLabels = view.findViewById(R.id.sfRadioDietLabels);
        rbNone = view.findViewById(R.id.sfSettingsNone);
        rbLowCarb = view.findViewById(R.id.sfSettingsLowCarb);
        rbLowFat = view.findViewById(R.id.sfSettingsLowFat);
        rbHighProtein = view.findViewById(R.id.sfSettingsHighProtein);
        rbHighFiber  = view.findViewById(R.id.sfSettingsHighFiber);
        rbLowSodium = view.findViewById(R.id.sfSettingsLowSodium);
        vegetarian = view.findViewById(R.id.sfCheckboxVegetarian);
        vegan = view.findViewById(R.id.sfCheckboxVegan);
        pescatarian = view.findViewById(R.id.sfCheckboxPescatarian);
        kosher = view.findViewById(R.id.sfCheckboxKosher);
        gluten = view.findViewById(R.id.sfCheckboxGluten);
        paleo = view.findViewById(R.id.sfCheckboxPaleo);
        shellfish = view.findViewById(R.id.sfCheckboxShellfish);
        dairy = view.findViewById(R.id.sfCheckboxDairy);
        treenut = view.findViewById(R.id.sfCheckboxTreenut);
        peanut = view.findViewById(R.id.sfCheckboxPeanut);
        egg = view.findViewById(R.id.sfCheckboxEgg);
        buttonSavePrefs = view.findViewById(R.id.sfButtonDone);
    }

    private void addUIListeners() {
        addRadioListener();
        addButtonDoneListener();
    }

    private void addRadioListener() {
        final RadioGroup radio = radioDietLabels;
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = radio.findViewById(checkedId);
                selectedRadioId = radio.indexOfChild(radioButton);
            }
        });
    }

    private void addButtonDoneListener() {
        buttonSavePrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFiltersToSearch();
            }
        });
    }

    private void saveFiltersToSearch() {
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
        UserPreferences filters = new UserPreferences(lowCalorie, highCalorie, maxTime,
                dietLabel, vegetarian.isChecked(), vegan.isChecked(), pescatarian.isChecked(),
                kosher.isChecked(), gluten.isChecked(), paleo.isChecked(), shellfish.isChecked(),
                dairy.isChecked(), treenut.isChecked(), peanut.isChecked(), egg.isChecked());

        this.tempFilters = filters;

        //send filters to search fragment, go back to search
        mCallback.sendFiltersToSearch(filters);
    }

    public interface FiltersFragmentListener {
        void sendFiltersToSearch(UserPreferences filters);
    }

}