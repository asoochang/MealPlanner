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

import java.util.ArrayList;
import java.util.List;

import scu.csci187.fall2018.mealtracker.Classes.APIHandler;
import scu.csci187.fall2018.mealtracker.Classes.FavoritesRecyclerViewAdapter;
import scu.csci187.fall2018.mealtracker.Classes.Recipe;
import scu.csci187.fall2018.mealtracker.Classes.RecipeRecord;
import scu.csci187.fall2018.mealtracker.Classes.SQLiteUserManager;
import scu.csci187.fall2018.mealtracker.R;


public class FavoritesFragment extends Fragment {
    private RecyclerView rvFavorites;
    private List<String> meals;
    private List<String> pics;
    private ArrayList<String> bookmarkURLs;
    private List<Recipe> recipes;


    public FavoritesFragment() {
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
        View view = inflater.inflate(R.layout.favorites_layout, container, false);
        rvFavorites = view.findViewById(R.id.rvFavorites);
        populateFavoritesListFromDB();
        createAndAttachRVAdapter();

        return view;
    }

    public void populateFavoritesListFromDB() {
        meals = new ArrayList<>();
        pics = new ArrayList<>();

        SQLiteUserManager myDB = new SQLiteUserManager(getContext());
        ArrayList<String> myBookmarkURLs = myDB.getFavorites();
        for (String bookmark : myBookmarkURLs){
            System.out.println("Bookmark holds: " + bookmark);
            System.out.flush();
        }

        recipes = new ArrayList<>();
        recipes = new APIHandler().getRecipesFromBookmarks(myBookmarkURLs);

        if (recipes.size() > 0) {
            this.bookmarkURLs = new ArrayList<>(myBookmarkURLs);
            for (Recipe r : recipes) {
                meals.add(r.name());
                pics.add(r.imageUrl());
            }
        }

    }

    public void createAndAttachRVAdapter() {
        FavoritesRecyclerViewAdapter favoritesAdapter = new FavoritesRecyclerViewAdapter(getContext(),
                meals, pics, bookmarkURLs, this);
        rvFavorites.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFavorites.setAdapter(favoritesAdapter);
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
}