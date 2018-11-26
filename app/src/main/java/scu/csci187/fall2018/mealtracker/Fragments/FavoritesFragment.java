package scu.csci187.fall2018.mealtracker.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import scu.csci187.fall2018.mealtracker.Classes.APIHandler;
import scu.csci187.fall2018.mealtracker.Classes.FavoritesRecyclerViewAdapter;
import scu.csci187.fall2018.mealtracker.Classes.Recipe;
import scu.csci187.fall2018.mealtracker.Classes.RecipeRecord;
import scu.csci187.fall2018.mealtracker.R;


public class FavoritesFragment extends Fragment {
    private RecyclerView rvFavorites;
    private List<String> meals;
    private List<String> pics;
    private ArrayList<String> bookmarkURLs;
    private List<RecipeRecord> recipeRecords = new ArrayList<>();
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
        populateFavoritesListFromAPI();
        createAndAttachRVAdapter();

        return view;
    }

    public void populateFavoritesListFromAPI() {
        meals = new ArrayList<>();
        pics = new ArrayList<>();

        /*
            TODO: DB call to get list of Favorited Meals (primaryKey is bookmarkURL)
         */
        // DB Calls to build List<string> meals/pics for search
        recipeRecords = /* TODO GET FROM DB */ new ArrayList<>();

        // TODO REMOVE HARDCODED VALUES WHEN WE HAVE DB
        recipeRecords.add(new RecipeRecord("http://www.edamam.com/ontologies/edamam.owl#recipe_3da1169eb633a5e4607890ebf7dee89f",
                "11/26/2018", 0));
        recipeRecords.add(new RecipeRecord("http://www.edamam.com/ontologies/edamam.owl#recipe_3da1169eb633a5e4607890ebf7dee89f",
                "11/24/2018", 1));
        recipeRecords.add(new RecipeRecord("http://www.edamam.com/ontologies/edamam.owl#recipe_d81795fb677ba4f12ab1a104e10aac98",
                "11/26/2018", 1));
        recipeRecords.add(new RecipeRecord("http://www.edamam.com/ontologies/edamam.owl#recipe_d81795fb677ba4f12ab1a104e10aac98",
                "11/24/2018", 0));

        recipes = new ArrayList<>();
        bookmarkURLs = new ArrayList<>();
        for (RecipeRecord rr : recipeRecords) {
            bookmarkURLs.add(rr.getBookmarkURL());
        }

        recipes = new APIHandler().getRecipesFromBookmarks(bookmarkURLs);


        for (Recipe r: recipes) {
            meals.add(r.name());
            pics.add(r.imageUrl());
        }

    }

    public void createAndAttachRVAdapter() {
        ArrayList<String> bookmarkURL = new ArrayList<>();
        for (RecipeRecord rr : recipeRecords) {
            bookmarkURL.add(rr.getBookmarkURL());
        }

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