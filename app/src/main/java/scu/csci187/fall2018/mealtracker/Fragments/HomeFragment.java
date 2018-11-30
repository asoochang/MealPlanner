package scu.csci187.fall2018.mealtracker.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import scu.csci187.fall2018.mealtracker.Classes.APIHandler;
import scu.csci187.fall2018.mealtracker.Classes.SQLiteUserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scu.csci187.fall2018.mealtracker.Classes.HomeRecyclerViewAdapter;
import scu.csci187.fall2018.mealtracker.Classes.Recipe;
import scu.csci187.fall2018.mealtracker.Classes.RecipeRecord;
import scu.csci187.fall2018.mealtracker.Classes.RecipeRecordComparator;
import scu.csci187.fall2018.mealtracker.Classes.UpcomingRecyclerViewAdapter;
import scu.csci187.fall2018.mealtracker.R;


public class HomeFragment extends Fragment  {
    private RecyclerView rvUpcoming, rvHistory;

    private List<String> upcomingMeals, upcomingDates, upcomingPics, upcomingBookmarks,
            historyMeals, historyDates, historyPics, historyBookmarks;

    private List<Integer> upcomingBlds, historyBlds;
    private UpcomingRecyclerViewAdapter upcomingAdapter;
    private HomeRecyclerViewAdapter historyAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homescreen_layout, container, false);

        bindViews(view);
        populateListDataFromDB();
        createAndAttachRVAdapters();

        return view;
    }

    private void bindViews(View view) {
        rvUpcoming = view.findViewById(R.id.rvUpcoming);
        rvHistory = view.findViewById(R.id.rvHistory);
    }

    public void populateListDataFromDB() {

        SQLiteUserManager myDB = new SQLiteUserManager(getContext());
        ArrayList<RecipeRecord> recipeRecords = myDB.getMeals();
        ArrayList<String> bookmarkedMeals = new ArrayList<>();
        ArrayList<Recipe> recipes;

        // Initialize lists that correspond to UI elements (Parallel)
        // Parallel set -> (Upcoming)
        upcomingMeals = new ArrayList<>();
        upcomingDates = new ArrayList<>();
        upcomingPics = new ArrayList<>();
        upcomingBlds = new ArrayList<>();
        upcomingBookmarks = new ArrayList<>();

        // Parallel set -> (History)
        historyMeals = new ArrayList<>();
        historyDates = new ArrayList<>();
        historyPics = new ArrayList<>();
        historyBlds = new ArrayList<>();
        historyBookmarks = new ArrayList<>();


        Collections.sort(recipeRecords, new RecipeRecordComparator());

        for (RecipeRecord rr: recipeRecords) {
            bookmarkedMeals.add(rr.getBookmarkURL());
        }

        recipes = new APIHandler().getRecipesFromBookmarks(bookmarkedMeals);

        for (int i = 0; i < recipes.size(); ++i) {
            Recipe currentRecipe = recipes.get(i);
            if ( !recipeRecords.get(i).isInHistory() ) {
                upcomingMeals.add(currentRecipe.name());
                upcomingDates.add(recipeRecords.get(i).getDateString());
                upcomingPics.add(currentRecipe.imageUrl());
                upcomingBlds.add(recipeRecords.get(i).getTime());
                upcomingBookmarks.add(currentRecipe.linkInAPI());
            } else {
                historyMeals.add(currentRecipe.name());
                historyDates.add(recipeRecords.get(i).getDateString());
                historyPics.add(currentRecipe.imageUrl());
                historyBlds.add(recipeRecords.get(i).getTime());
                historyBookmarks.add(currentRecipe.linkInAPI());
            }
        }
    }

    public void createAndAttachRVAdapters() {
        upcomingAdapter = new UpcomingRecyclerViewAdapter(getContext(),
                                    upcomingMeals, upcomingDates, upcomingPics, upcomingBookmarks, upcomingBlds, this);
        rvUpcoming.setLayoutManager(new LinearLayoutManager(getActivity(),
                                 LinearLayoutManager.HORIZONTAL, false));
        rvUpcoming.setAdapter(upcomingAdapter);

        historyAdapter = new HomeRecyclerViewAdapter(getContext(),
                                    historyMeals, historyDates, historyPics, historyBookmarks, historyBlds, this);
        rvHistory.setLayoutManager(new LinearLayoutManager(getActivity(),
                                        LinearLayout.HORIZONTAL, false));
        rvHistory.setAdapter(historyAdapter);
    }

    public void showMealDetail(String bookmarkURL) {
        MealDetailFragment newFragment = new MealDetailFragment();
        Bundle b = new Bundle();
        b.putString("bookmarkURL", bookmarkURL);
        newFragment.setArguments(b);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(getId(), newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showUpcomingMealDetail(String bookmarkURL, int index) {
        MealDetailFragment newFragment = new MealDetailFragment();
        Bundle b = new Bundle();
        b.putString("bookmarkURL", bookmarkURL);
        b.putInt("index", index);
        b.putBoolean("madeThis", true);
        newFragment.setArguments(b);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(getId(), newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}