package scu.csci187.fall2018.mealtracker.Classes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SQLiteMeal {
    private String meal;
    private ArrayList<SQLiteIngredient> ingredientsList;

    private ArrayList<SQLiteIngredient> removeDuplicates(ArrayList<SQLiteIngredient> ingredients) {
        HashSet<SQLiteIngredient> hs = new HashSet<>();
        hs.addAll(ingredients);
        ingredients.clear();
        ingredients.addAll(hs);
        return ingredients;
    }

    public SQLiteMeal(String meal, ArrayList<SQLiteIngredient> ingredients) {
        this.meal = meal.replaceAll(" ", "_");
        this.ingredientsList = ingredients;
    }

    public String getMealName() { return meal; }
    public ArrayList<SQLiteIngredient> getIngredients(){
        return ingredientsList;
    }
}
