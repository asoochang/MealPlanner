package scu.csci187.fall2018.mealtracker.Classes;

import java.util.ArrayList;

public class SQLiteMeal {
    private String meal;
    private ArrayList<SQLiteIngredient> ingredientsList;

    public SQLiteMeal(String meal, ArrayList<SQLiteIngredient> ingredients) {
        this.meal = meal.replaceAll(" ", "_");
        this.ingredientsList = ingredients;
    }

    public String getMealName() { return meal; }
    public ArrayList<SQLiteIngredient> getIngredients(){
        return ingredientsList;
    }
}
