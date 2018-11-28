package scu.csci187.fall2018.mealtracker.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import scu.csci187.fall2018.mealtracker.Classes.APIHandler;
import scu.csci187.fall2018.mealtracker.Classes.PreferencesTranslator;
import scu.csci187.fall2018.mealtracker.Classes.Query;
import scu.csci187.fall2018.mealtracker.Classes.QueryParam;
import scu.csci187.fall2018.mealtracker.Classes.Recipe;
import scu.csci187.fall2018.mealtracker.Classes.SearchRecyclerViewAdapter;
import scu.csci187.fall2018.mealtracker.Classes.UserPreferences;
import scu.csci187.fall2018.mealtracker.R;


public class SearchFragment extends Fragment  {

    private RecyclerView rvSearch;
    private EditText searchText;
    private Button buttonSearch;
    private ImageButton buttonFilters;
    private UserPreferences inputtedFilters = null;

    private SearchFragmentListener mCallback;
    private List<String> meals, pics;
    private List<Recipe> recipes;
    private ArrayList<String> bookmarkURLs = new ArrayList<>();

    private String myInputString = "";

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            myInputString = savedInstanceState.getString("inputString");
        }
        meals = new ArrayList<>();
        pics = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_layout, container, false);

        bindViews(view);
        addButtonListeners();

        /*
            TODO: SAVE/RESTORE RVSEARCH STATE NOT WORKING
         */
        if(savedInstanceState != null) {
            rvSearch.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable("RV_STATE"));
        }

        return view;
    }

    // Ensures that Activity has implemented SearchFragmentListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchFragment.SearchFragmentListener) {
            mCallback = (SearchFragment.SearchFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SearchFragmentListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    /*
        TODO: SAVE/RESTORE RVSEARCH STATE NOT WORKING
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("RV_STATE", rvSearch.getLayoutManager().onSaveInstanceState());
    }

    private void bindViews(View view) {
        rvSearch = view.findViewById(R.id.rvSearch);
        searchText = view.findViewById(R.id.inputSearchTerms);
        buttonFilters = view.findViewById(R.id.buttonSearchFilters);
        buttonSearch = view.findViewById(R.id.buttonSearchGo);
    }

    private void addButtonListeners() {
        buttonFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFiltersFragment();
                collapseKeyboard();
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeSearch();
                collapseKeyboard();
            }
        });
    }

    private void collapseKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException e) {
            // Do nothing
        }
    }

    private void openFiltersFragment() {
        myInputString = searchText.getText().toString();
        mCallback.goToFilters(myInputString);
    }

    /*
        TODO: implement executeSearch()
     */
    private void executeSearch() {
        String searchString;

        searchString = searchText.getText().toString();

        // Construct Query
        QueryParam queryParam = new QueryParam();


        // TODO implement once I have the necessary methods.
        // configure queryParams from userPrefs
        queryParam.setQuery(searchString);
        PreferencesTranslator preferencesTranslator = new PreferencesTranslator();

        preferencesTranslator.setDietInQueryParam(inputtedFilters, queryParam);
        preferencesTranslator.setHealthLabelsInQueryParam(inputtedFilters, queryParam);

        // execute the search

        Query query = new APIHandler().search(queryParam);
        populateSearchListFromAPI(query);
        createAndAttachRVAdapter();
    }

    private void populateSearchListFromAPI(Query query) {

        // get Recipes from query and input into lists for visual representation

        meals = new ArrayList<>();
        pics = new ArrayList<>();
        recipes = new ArrayList<>();
        bookmarkURLs = new ArrayList<>();

        for(int i = 0; i < query.getValue().length(); ++i) {
            Recipe currentRecipe = query.getRecipeAtIndex(i);
            meals.add(currentRecipe.name());
            pics.add(currentRecipe.imageUrl());
            recipes.add(currentRecipe);
        }
        for (Recipe r : recipes){
            bookmarkURLs.add(r.linkInAPI());
        }
    }

    private void createAndAttachRVAdapter() {

        SearchRecyclerViewAdapter searchAdapter = new SearchRecyclerViewAdapter(getContext(),
                meals, pics, bookmarkURLs, this);
        rvSearch.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSearch.setAdapter(searchAdapter);
    }

    public void applyFiltersToSearch(String inputText, UserPreferences filters) {
        inputtedFilters = filters;
        searchText.setText(inputText);
    }

    // Create then display Meal Detail fragment using mealName
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

    public void sendMealToShoppingList(String bookmarkURL, String mealName) {
        mCallback.addToShoppingList(bookmarkURL, mealName);
    }

    //public void sendMealToFavorites(String bookmarkURL, String mealName) {
    //    mCallback.addToFavorites(bookmarkURL);
    //}

    public interface SearchFragmentListener {
        void goToFilters(String inputString);
        void addToShoppingList(String bookmarkURL, String mealName);
    }

    public interface SearchToFavoritesListener {
        void addMealFromSearchToFavorites(String bookmarkURL, String mealName);
    }
}