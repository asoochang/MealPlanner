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

import org.json.JSONObject;

import scu.csci187.fall2018.mealtracker.Activities.MainActivity;
import scu.csci187.fall2018.mealtracker.Classes.APIHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import scu.csci187.fall2018.mealtracker.Classes.HomeRecyclerViewAdapter;
import scu.csci187.fall2018.mealtracker.Classes.QueryParam;
import scu.csci187.fall2018.mealtracker.Classes.Recipe;
import scu.csci187.fall2018.mealtracker.Classes.RecipeRecord;
import scu.csci187.fall2018.mealtracker.Classes.RecipeRecordComparator;
import scu.csci187.fall2018.mealtracker.Classes.UpcomingRecyclerViewAdapter;
import scu.csci187.fall2018.mealtracker.R;


public class HomeFragment extends Fragment  {
    //private OnFragmentInteractionListener mListener;

    private TextView todaysCalories, macroCarb, macroProtein, macroFat;
    private RecyclerView rvUpcoming, rvHistory;

    private List<String> upcomingMeals, upcomingDates, upcomingPics, upcomingBookmarks,
            historyMeals, historyDates, historyPics, historyBookmarks;

    private UpcomingRecyclerViewAdapter upcomingAdapter;
    private HomeRecyclerViewAdapter historyAdapter;

    public HomeFragment() {
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
        View view = inflater.inflate(R.layout.homescreen_layout, container, false);
        bindViews(view);
        populateListDataFromDB();
        createAndAttachRVAdapters();

        return view;
    }

    private void bindViews(View view) {
        todaysCalories = view.findViewById(R.id.todaysCalories);
        macroCarb = view.findViewById(R.id.macroCarb);
        macroProtein = view.findViewById(R.id.macroProtein);
        macroFat = view.findViewById(R.id.macroFat);
        rvUpcoming = view.findViewById(R.id.rvUpcoming);
        rvHistory = view.findViewById(R.id.rvHistory);
    }

    public void populateListDataFromDB() {
        /*
            TODO:
            Call the DB accession file;
            Return list here as meals;
         */

        // Meals should be given DB values (bookmark links)
        ArrayList<String> bookmarkedMeals = new ArrayList<>();
        ArrayList<Recipe> recipes;

        bookmarkedMeals.add("http://www.edamam.com/ontologies/edamam.owl#recipe_3da1169eb633a5e4607890ebf7dee89f");
        bookmarkedMeals.add("http://www.edamam.com/ontologies/edamam.owl#recipe_d81795fb677ba4f12ab1a104e10aac98");


        recipes = new APIHandler().getRecipesFromBookmarks(bookmarkedMeals);

        // Initialize lists that correspond to UI elements (Parallel)

        // Parallel set -> (Upcoming)
        upcomingMeals = new ArrayList<>();
        upcomingDates = new ArrayList<>();
        upcomingPics = new ArrayList<>();
        upcomingBookmarks = new ArrayList<>();

        // Parallel set -> (History)
        historyMeals = new ArrayList<>();
        historyDates = new ArrayList<>();
        historyPics = new ArrayList<>();
        historyBookmarks = new ArrayList<>();


        ArrayList<RecipeRecord> recipeRecords = new ArrayList<>();

        // TODO fix DB call (hardcoded date) when we get the SQL Query functions.
        // Currently this line is incomplete because r.getDataFromDBAsString()
        // is a placeholder and not functional.
        for (Recipe r : recipes) {
            recipeRecords.add(new RecipeRecord(r.linkInAPI(), r.name(),"12/1/2018", r.imageUrl()));
        }


        // Sort the recipe records to make it easier to input them in order
        // into their respective ArrayLists.
        Collections.sort(recipeRecords, new RecipeRecordComparator());


        // Separating the recipe records into parallel ArrayLists is
        // important because the UI is currently built this way.
        // Note: The compareTo is untested and might be backwards
        // TODO when we have data check that this works
        for (RecipeRecord rr : recipeRecords) {
            System.out.println("    1 Date Values. new first and then mine");
            System.out.println(new Date().toString());
            System.out.println(rr.getDate().toString());
            if (rr.getDate().compareTo(new Date()) >= 0) {

                upcomingMeals.add(rr.getName());
                upcomingDates.add(rr.getDateString());
                upcomingPics.add(rr.getPicURL());
                upcomingBookmarks.add(rr.getBookmarkURL());
            }
        }
        for (RecipeRecord rr : recipeRecords) {
            System.out.println("    2 Date Values. new first and then mine");
            System.out.println(new Date().toString());
            System.out.println(rr.getDate().toString());
            if (rr.getDate().compareTo(new Date()) < 0) {
                historyMeals.add(rr.getName());
                historyDates.add(rr.getDateString());
                historyPics.add(rr.getPicURL());
                upcomingBookmarks.add(rr.getBookmarkURL());
            }
        }



    }

    public void createAndAttachRVAdapters() {
        upcomingAdapter = new UpcomingRecyclerViewAdapter(getContext(),
                                    upcomingMeals, upcomingDates, upcomingPics, upcomingBookmarks, this);
        rvUpcoming.setLayoutManager(new LinearLayoutManager(getActivity(),
                                 LinearLayoutManager.HORIZONTAL, false));
        rvUpcoming.setAdapter(upcomingAdapter);

        historyAdapter = new HomeRecyclerViewAdapter(getContext(),
                                    historyMeals, historyDates, historyPics, historyBookmarks, this);
        rvHistory.setLayoutManager(new LinearLayoutManager(getActivity(),
                                        LinearLayout.HORIZONTAL, false));
        rvHistory.setAdapter(historyAdapter);
    }

    // Create then display Meal Detail fragment using bookmarkURL
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

    // Create then display Meal Detail fragment using bookmarkURL
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

    /*
        TODO: Both RecyclerViews are not properly updating with new data
     */
    public void notifyAdaptersDataChanged(int index) {
        String meal, date, pic;

        meal = upcomingMeals.get(index);
        date = upcomingDates.get(index);
        pic = upcomingPics.get(index);


        // Remove item from data lists
        upcomingMeals.remove(index);
        upcomingDates.remove(index);
        upcomingPics.remove(index);
        Toast.makeText(getContext(), "size meals " + upcomingMeals.size(), Toast.LENGTH_SHORT).show();

        // Remove item from Upcoming List view
        upcomingAdapter = new UpcomingRecyclerViewAdapter(getContext(), upcomingMeals, upcomingDates, upcomingPics, upcomingBookmarks, this);
        rvUpcoming.setAdapter(upcomingAdapter);
        //rvUpcoming.removeViewAt(index);
        //upcomingAdapter.notifyItemRemoved(index);
        //upcomingAdapter.notifyItemRangeChanged(index, upcomingMeals.size());
        //upcomingAdapter.notifyDataSetChanged();

        // Add item to History List view



        historyMeals.add(0, meal);
        historyDates.add(0, date);
        historyPics.add(0, pic);
        historyAdapter = new HomeRecyclerViewAdapter(getContext(),
                historyMeals, historyDates, historyPics, historyBookmarks, this);
        rvHistory.setAdapter(historyAdapter);
        //

    }


}