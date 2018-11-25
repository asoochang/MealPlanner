package scu.csci187.fall2018.mealtracker.Classes;

public class SQLiteIngredient {
    public String ingredient;
    public Boolean isChecked;

    public SQLiteIngredient() {
        this.ingredient = "";
        this.isChecked = false;
    }

    public SQLiteIngredient(String ingredient, Boolean isChecked){
        this.ingredient = ingredient;
        this.isChecked = isChecked;
    }

    public String getIngredient(){
        return ingredient;
    }

    public Boolean getisChecked(){
        return isChecked;
    }
}
