package scu.csci187.fall2018.mealtracker.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SQLiteDBManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shoppingListDB";
    private static final int DATABASE_VERSION = 1;
    private static String email = "null";
    Context c;

    public SQLiteDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.c = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS shoppingList(_id integer primary key autoincrement, meal text, email text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int v1, int v2) {
        onCreate(db);
    }

    public String getEmail(Context c){
        SharedPreferences pref = c.getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String email = pref.getString("userEmail", null);
        return email;
    }

    public void initShoppingList() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS shoppingList(_id integer primary key autoincrement, meal text, email text)";
        db.execSQL(sql);
    }
    public void addEntry(SQLiteMeal meal) {
        ContentValues cv = new ContentValues();
        cv.put("meal", meal.getMealName());
        cv.put("email", getEmail(c));
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.insert("shoppingList",null,  cv);


        String dropCurrentTable = "DROP TABLE IF EXISTS " + meal.getMealName();
        db.execSQL(dropCurrentTable);
        String sql = "CREATE TABLE IF NOT EXISTS " + meal.getMealName() + "(ingredient text PRIMARY KEY, checkmark integer)";
        db.execSQL(sql);

        ArrayList<SQLiteIngredient> ingredients = meal.getIngredients();

        for (int i = 0; i < ingredients.size(); i++) {
            //String sql1 = "insert into " + meal.getMealName() + "(ingredient, checkmark) values (" + ingredients.get(i).getIngredient()+ ", 0)";
            //Log.d("DBMANAGER", ingredients.get(i));
            ContentValues cv1 = new ContentValues();
            Log.d("DBMANAGER", ingredients.get(i).getIngredient());
            cv1.put("ingredient", ingredients.get(i).getIngredient());
            cv1.put("checkmark", 0);

            db.insert(meal.getMealName(),null, cv1);

        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void writeToDB(ArrayList<SQLiteMeal> meals){


        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        String sql = "CREATE TABLE IF NOT EXISTS shoppingList(_id integer primary key autoincrement, meal text, email text)";
        db.execSQL(sql);
        for(int i = 0; i < meals.size(); i++) {
            String currentMeal = meals.get(i).getMealName();
            ArrayList<SQLiteIngredient> currentIngredients;
            currentIngredients = meals.get(i).getIngredients();
            ContentValues cv = new ContentValues();
            cv.put("meal", currentMeal);
            cv.put("email", getEmail(c));
            db.insert("shoppingList", null, cv);
            sql = "CREATE TABLE IF NOT EXISTS " + currentMeal + "(ingredient text PRIMARY KEY, checkmark integer)";
            db.execSQL(sql);
            for(int j = 0; j < currentIngredients.size(); j++) {
                Log.d("sqldbman", currentIngredients.get(j).getIngredient());
                ContentValues cv2 = new ContentValues();
                cv2.put("ingredient", currentIngredients.get(j).getIngredient());
                cv2.put("checkmark", currentIngredients.get(j).getisChecked());
                db.insert(currentMeal, null, cv2);
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void clearDatabase(ArrayList<SQLiteMeal> meals) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        for (SQLiteMeal meal: meals){
            String mealName = meal.getMealName();
            String sql;
            sql = "drop table if exists " + mealName;
            db.execSQL(sql);
            sql = "delete from shoppingList where meal="+"\""+mealName+"\"" + " and email = " + "\"" + getEmail(c) + "\"";
            db.execSQL(sql);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public ArrayList<SQLiteMeal> getMeals(){
        Cursor cursor;
        try {
            cursor = this.getReadableDatabase().rawQuery("SELECT meal FROM shoppingList where email ="+ "\"" + getEmail(c) + "\"", null);
        } catch (Exception e) {
            cursor = null;
            e.getStackTrace();
        }
        ArrayList<SQLiteMeal> meals = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            ArrayList<SQLiteIngredient> ingredients;
            for(boolean cBoundsCheck = true; cBoundsCheck ;cBoundsCheck = cursor.moveToNext()) {
                ingredients = new ArrayList<>();
                Cursor Icursor;
                String currentMeal;
                try {
                    currentMeal = cursor.getString(cursor.getColumnIndexOrThrow("meal"));
                    Icursor = getReadableDatabase().rawQuery("SELECT * FROM " + currentMeal, null);
                } catch (CursorIndexOutOfBoundsException e) {
                    e.getStackTrace();
                    Icursor = null;
                    currentMeal = null;
                }
                if(Icursor != null) {
                    Icursor.moveToFirst();
                    for(boolean iCBoundsCheck = true; iCBoundsCheck ;iCBoundsCheck = Icursor.moveToNext()) {
                        try {
                            String ingredientName = Icursor.getString(Icursor.getColumnIndexOrThrow("ingredient"));
                            boolean isChecked = (Icursor.getInt(Icursor.getColumnIndexOrThrow("checkmark")) != 0);
                            SQLiteIngredient ingredient = new SQLiteIngredient(ingredientName, isChecked);
                            ingredients.add(ingredient);
                        } catch (CursorIndexOutOfBoundsException e) {
                            e.getStackTrace();
                        }
                    }
                }
                if (currentMeal != null) {
                    SQLiteMeal meal = new SQLiteMeal(currentMeal, ingredients);
                    meals.add(meal);
                    Icursor.close();
                }
            }
        }
        if (cursor != null){
            cursor.close();
        }
        return meals;
    }
}