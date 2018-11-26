package scu.csci187.fall2018.mealtracker.Fragments;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

import scu.csci187.fall2018.mealtracker.Classes.APIHandler;
import scu.csci187.fall2018.mealtracker.Classes.Ingredient;
import scu.csci187.fall2018.mealtracker.Classes.Ingredients;
import scu.csci187.fall2018.mealtracker.Classes.Recipe;
import scu.csci187.fall2018.mealtracker.Classes.SQLiteDBManager;
import scu.csci187.fall2018.mealtracker.Classes.SQLiteIngredient;
import scu.csci187.fall2018.mealtracker.Classes.SQLiteMeal;
import scu.csci187.fall2018.mealtracker.Classes.SearchRecyclerViewAdapter;
import scu.csci187.fall2018.mealtracker.R;

public class ShoppingListFragment extends Fragment implements
                        SearchRecyclerViewAdapter.SearchShoppingListener {

    private ArrayList<Integer> mealIds;
    private ArrayList<SQLiteMeal> mealData;
    private ArrayList<ArrayList<CheckBox>> cbData;
    private int mealNameTvIdCounter = 678;  // set to # higher than expected # of checkboxes to avoid ID overlap
    private ViewGroup listContainer;

    RefreshShoppingList mCallback;

    public ShoppingListFragment() {
        // required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        SQLiteDBManager dbmanager = new SQLiteDBManager(getContext());
        dbmanager.initShoppingList();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shoppinglist_layout, container, false);

        container = view.findViewById(R.id.altContainer);

        listContainer = container;

        cbData = new ArrayList<>();
        mealIds = new ArrayList<>();

        Log.d("shoppinglistfragment", "on create called");
        getShoppingListFromLocalStorage();

        //Load shopping list into UI
        if(mealData.size() > 0) {
            for(final SQLiteMeal meal : mealData) {
                final String fMealName = meal.getMealName();
                ArrayList<CheckBox> boxes = new ArrayList<>();

                TextView name = new TextView(getContext());
                name.setId(mealNameTvIdCounter++);
                name.setTextSize(15);
                name.setText(fMealName);
                container.addView(name);
                mealIds.add(name.getId());

                for(SQLiteIngredient ingredient : meal.getIngredients()) {
                    CheckBox cb = new CheckBox(getContext());
                    int y = View.generateViewId();
                    cb.setText(ingredient.getIngredient());
                    cb.setId(y);
                    cb.setChecked(ingredient.isChecked);

                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            int indexToRemove = getIndexOfFullyCheckedMeal();
                            if(getIndexOfFullyCheckedMeal() != -1) {

                                showRemovalToast(fMealName);
                                removeCheckboxes(indexToRemove);
//                                mealData.remove(indexToRemove);
                                final SQLiteMeal passedMeal = meal;
                                new SQLiteDBManager(getContext()).clearDatabase(mealData);
                                removeDataAndRefresh(passedMeal);
                            }
                        }
                    });
                    boxes.add(cb);
                    container.addView(cb);
                }
                cbData.add(boxes);
            }
        }

        return view;
    }

    // Ensures that Activity has implemented FiltersFragmentListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getShoppingListFromLocalStorage();
        if (context instanceof ShoppingListFragment.RefreshShoppingList) {
            mCallback = (ShoppingListFragment.RefreshShoppingList) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RefreshShoppingList interface");
        }
    }

    private int getIndexOfFullyCheckedMeal() {
        for(int a = 0; a < cbData.size(); a++) {
            ArrayList<CheckBox> alCb = cbData.get(a);
            if(alCb == null)
                continue;
            else {
                if(allIngredientsMarked(alCb))
                    return a;
            }
        }
        return -1;
    }

    private boolean allIngredientsMarked(ArrayList<CheckBox> cbList) {

        for(int x = 0; x < cbList.size(); x++) {
            CheckBox cb = cbList.get(x);
            if(cb == null)
                continue;
            if(!cb.isChecked())
                return false;
        }
        return true;
    }

    private void removeCheckboxes(int index) {
        ArrayList<CheckBox> boxes = cbData.get(index);

        for(CheckBox cb : boxes) {
            int id = cb.getId();
            listContainer.removeView(listContainer.findViewById(id));
        }

         cbData.remove(boxes);
    }

    private void removeMeal(int index) {
        listContainer.removeView(listContainer.findViewById(mealIds.get(index)));
    }

    private void removeDataAndRefresh(SQLiteMeal meal) {
        mealData.remove(meal);
        writeShoppingListToDisk(mealData);
        mCallback.refreshShoppingListFragment();
    }

    public void addDataAndRefresh(String bookmarkURL, String mealName) {
        ArrayList<String> ingredientsAsStrings = new ArrayList<>();
        ArrayList<String> bookmarks = new ArrayList<>();
        bookmarks.add(bookmarkURL);

        ArrayList<Recipe> recipes = new APIHandler().getRecipesFromBookmarks(bookmarks);
        Ingredients ingredients = recipes.get(0).ingredients();
        for (int i = 0; i < ingredients.length(); ++i) {
            Ingredient currentIngredient = ingredients.getIngredientAtIndex(i);
            ingredientsAsStrings.add(currentIngredient.food());
        }

        // build ShoppingListItem into SQLiteMeal to add to DB
        ArrayList<SQLiteIngredient> ingredientsList = new ArrayList<>();
        for(String s : ingredientsAsStrings) {
            ingredientsList.add(new SQLiteIngredient(s, false));
        }
        SQLiteMeal thisMeal = new SQLiteMeal(mealName, ingredientsList);
        SQLiteDBManager dbManager = new SQLiteDBManager(getContext());
        dbManager.addEntry(thisMeal);
        mCallback.refreshShoppingListFragment();
    }

    private void showRemovalToast(String mealName) {
        Toast toast = Toast.makeText(getContext(), "Removing " + mealName, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void getShoppingListFromLocalStorage() {
        SQLiteDBManager dbManager = new SQLiteDBManager(getContext());
        mealData = dbManager.getMeals();
    }

    private void writeShoppingListToDisk(ArrayList<SQLiteMeal> mealData) {
        SQLiteDBManager dbManager = new SQLiteDBManager(getContext());
        dbManager.writeToDB(mealData);
    }

    // Implementation of SearchRecyclerViewAdapter.SearchShoppingListener
    public void searchAddToShoppingList(String bookmarkURL, String mealName) {
        ArrayList<String> ingredientsAsStrings = new ArrayList<>();
        ArrayList<String> bookmarks = new ArrayList<>();
        bookmarks.add(bookmarkURL);

        ArrayList<Recipe> recipes = new APIHandler().getRecipesFromBookmarks(bookmarks);
        Ingredients ingredients = recipes.get(0).ingredients();
        for (int i = 0; i < ingredients.length(); ++i) {
            Ingredient currentIngredient = ingredients.getIngredientAtIndex(i);
            ingredientsAsStrings.add(currentIngredient.food());
        }

        // build ShoppingListItem into SQLiteMeal to add to DB
        ArrayList<SQLiteIngredient> ingredientsList = new ArrayList<>();
        for(String s : ingredientsAsStrings) {
            ingredientsList.add(new SQLiteIngredient(s, false));
        }
        SQLiteMeal thisMeal = new SQLiteMeal(mealName, ingredientsList);
        SQLiteDBManager dbManager = new SQLiteDBManager(getContext());
        dbManager.addEntry(thisMeal);
        mCallback.refreshShoppingListFragment();
    }

    public interface RefreshShoppingList {
        void refreshShoppingListFragment();
    }
}