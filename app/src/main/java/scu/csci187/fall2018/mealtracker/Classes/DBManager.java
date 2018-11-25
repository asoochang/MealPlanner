package scu.csci187.fall2018.mealtracker.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shoppingList";
    private static final int DATABASE_VERSION = 1;

    DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS shoppingList(_id integer primary key autoincrement, meal text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int v1, int v2) {
        String sql = "DROP TABLE IF EXISTS shoppingList";
        db.execSQL(sql);
        onCreate(db);
    }

    /*
    void addEntry(SQLiteMeal meal) {
        ContentValues cv = new ContentValues();
        cv.put("meal", recipe.name());
        SQLiteDatabase db = getWritableDatabase();
        db.insert("shoppingList", null, cv);

        String sql = "CREATE TABLE IF NOT EXISTS " + recipe.name() + "(ingredient text, checkmark integer)";
        db.execSQL(sql);

        Ingredients ingredients = recipe.ingredients();

        for (int i = 0; i < ingredients.length(); i++) {
            sql = "insert into " + recipe.name() + " values (" + recipe.name() + ", "
                    + ingredients.getIngredientAtIndex(i).text() + ", false)";
            db.execSQL(sql);
        }
    }
    */

    void writeToDB(ArrayList<SQLiteMeal> meals){
        clearDatabase(meals);
        SQLiteDatabase db = getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS shoppingList(_id integer primary key autoincrement, meal text)";
        db.execSQL(sql);
        for(int i = 0; i < meals.size(); i++) {
            String currentMeal = meals.get(i).getMeal();
            ArrayList<SQLiteIngredient> currentIngredients;
            currentIngredients = meals.get(i).getIngredients();
            ContentValues cv = new ContentValues();
            cv.put("meal", currentMeal);
            db.insert("shoppingList", null, cv);
            sql = "CREATE TABLE IF NOT EXISTS " + currentMeal + "(ingredient text, checkmark integer)";
            db.execSQL(sql);
            for(int j = 0; j < currentIngredients.size(); j++) {
                sql = "insert into " + currentMeal + " values ("
                        + currentIngredients.get(i).ingredient + ", "
                        + currentIngredients.get(i).isChecked + ")";
                db.execSQL(sql);
            }
        }
    }

    private void clearDatabase(ArrayList<SQLiteMeal> meals) {
        SQLiteDatabase db = getWritableDatabase();
        String sql;
        for(int i = 0; i < meals.size(); i++) {
            String currentMeal = meals.get(i).getMeal();
            sql = "drop table if exists " + currentMeal;
            db.execSQL(sql);
        }
        sql = "drop table shoppingList";
        db.execSQL(sql);
    }

    ArrayList<SQLiteMeal> getMeals(){
        Cursor cursor = getReadableDatabase().rawQuery("SELECT meals FROM shoppingList", null);
        ArrayList<SQLiteMeal> meals = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            ArrayList<SQLiteIngredient> ingredients;
            while (!cursor.moveToNext()) {
                ingredients = new ArrayList<>();
                String currentMeal = cursor.getString(cursor.getColumnIndexOrThrow("meal"));
                Cursor Icursor = getReadableDatabase().rawQuery("SELECT * FROM " + currentMeal, null);
                if(Icursor != null) {
                    Icursor.moveToFirst();
                    while(!Icursor.moveToNext()) {
                        String ingredientName = Icursor.getString(Icursor.getColumnIndexOrThrow(currentMeal));
                        boolean isChecked = (Icursor.getInt(Icursor.getColumnIndexOrThrow("checkmark")) != 0);
                        SQLiteIngredient ingredient = new SQLiteIngredient(ingredientName, isChecked);
                        ingredients.add(ingredient);
                    }
                }
                SQLiteMeal meal = new SQLiteMeal(currentMeal, ingredients);
                meals.add(meal);
                Icursor.close();
            }
        }
        cursor.close();
        return meals;
    }
}