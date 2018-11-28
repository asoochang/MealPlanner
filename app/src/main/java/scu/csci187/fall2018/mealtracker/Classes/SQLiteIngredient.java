package scu.csci187.fall2018.mealtracker.Classes;

public class SQLiteIngredient {
    public String ingredient;
    public boolean isChecked;

    public SQLiteIngredient() {
        this.ingredient = "";
        this.isChecked = false;
    }

    public SQLiteIngredient(String ingredient, Boolean isChecked){
        this.ingredient = ingredient.replaceAll("[^a-zA-Z\\d/.,]", "_");
        this.isChecked = isChecked;
    }

    public String getIngredient(){
        return ingredient;
    }

    public boolean getisChecked(){
        return isChecked;
    }
}
