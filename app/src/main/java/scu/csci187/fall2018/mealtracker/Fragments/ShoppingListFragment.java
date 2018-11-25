package scu.csci187.fall2018.mealtracker.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import scu.csci187.fall2018.mealtracker.R;

public class ShoppingListFragment extends Fragment {

    private ArrayList<String> mealList;
    private ArrayList<Integer> mealIds;
    private ArrayList<ArrayList<CheckBox>> data;
    private int mealNameTvIdCounter = 678;  // set to # higher than expected # of checkboxes to avoid ID overlap
    private ViewGroup listContainer;

    public ShoppingListFragment() {
        // required empty constructor
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
        View view = inflater.inflate(R.layout.shoppinglist_layout, container, false);

        container = view.findViewById(R.id.altContainer);

        listContainer = container;

        data = new ArrayList<>();
        mealIds = new ArrayList<>();

        /*
            TODO: getShoppingListFromDisk() = returns ArrayList<SQLiteMeal>
                  SQLiteMeal =  mealName (string) + ingredients (ArrayList<SQLiteIngredient>)
         */
        mealList = new ArrayList<>();
        mealList.add("Borscht");
        mealList.add("Shakshuka");
        mealList.add("Green Eggs and Ham");

        for(String mealName : mealList) {
            final String fMealName = mealName;
            ArrayList<CheckBox> boxes = new ArrayList<>();
            ArrayList<String> ingredients = new ArrayList<>();

            TextView name = new TextView(getContext());
            name.setId(mealNameTvIdCounter++);
            name.setTextSize(15);
            name.setText(mealName);
            container.addView(name);
            mealIds.add(name.getId());

            ingredients.add("1/2 oz chicken");
            ingredients.add("5 lbs salt");
            ingredients.add("1 bay leaf");
            ingredients.add("23 ibuprofen");
            ingredients.add("6 rabbit's foot");
            ingredients.add("1/2 can Red Bull");
            ingredients.add("2 spinach leaves");

            for(String item : ingredients) {
                CheckBox cb = new CheckBox(getContext());
                int y = View.generateViewId();
                cb.setText(item);
                cb.setId(y);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int indexToRemove = getIndexOfFullyCheckedMeal();
                        if(getIndexOfFullyCheckedMeal() != -1) {
                            showRemovalToast(fMealName);
                            removeCheckboxes(indexToRemove);
                            removeMeal(indexToRemove);
                            removeData(indexToRemove);
                        }
                    }
                });
                boxes.add(cb);
                container.addView(cb);
            }
            data.add(boxes);
        }

        return view;
    }

    private int getIndexOfFullyCheckedMeal() {
        for(int a = 0; a < data.size(); a++) {
            ArrayList<CheckBox> alCb = data.get(a);
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
        ArrayList<CheckBox> boxes = data.get(index);

        for(CheckBox cb : boxes) {
            int id = cb.getId();
            listContainer.removeView(listContainer.findViewById(id));
        }
    }

    private void removeMeal(int index) {
        listContainer.removeView(listContainer.findViewById(mealIds.get(index)));
    }

    // set values to null instead of removing to maintain indices
    private void removeData(int index) {
        mealList.set(index, null);
        data.set(index, null);
    }

    private void showRemovalToast(String mealName) {
        Toast toast = Toast.makeText(getContext(), "Removing " + mealName, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /*
        TODO: SQLite read/write shoppinglist to disk
            done through Peter's DBManager class
     */
    private ArrayList<String> readShoppingListFromDisk() {

        return new ArrayList<>();
    }

    private void writeShoppingListToDisk() {

    }
}