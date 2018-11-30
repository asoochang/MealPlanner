package scu.csci187.fall2018.mealtracker.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SQLiteUserManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "nutritionDB";
    private static final int DATABASE_VERSION = 2;
    private static String email = "null";
    Context c;

    public SQLiteUserManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.c=context;
    }
    public String getEmail(Context c){
        SharedPreferences pref = c.getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String email = pref.getString("userEmail", null);
        return email;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String sql1 = "CREATE TABLE IF NOT EXISTS User (email text primary key, password text, " +
                "calLow integer, calHigh integer, dietLabel integer, maxTime integer, healthLabel text)";
        String sql2 = "CREATE TABLE IF NOT EXISTS UserMeals (email text, url text, rating integer, " +
                "isFavorite integer, made integer, primary key(url, email))";
        String sql3 = "CREATE TABLE IF NOT EXISTS History (email text, day text, mealNo integer, " +
                "url text, historyID integer primary key autoincrement)";

        db.beginTransaction();
        try {
            db.execSQL(sql1);
            db.execSQL(sql2);
            db.execSQL(sql3);
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int v1, int v2) {
        onCreate(db);
    }

    public void initUser(String emailInput, String password){
        ContentValues cv = new ContentValues();
        cv.put("email", emailInput);
        cv.put("password", password);
        cv.put("calLow", 0);
        cv.put("calHigh", 1000);
        cv.put("dietLabel", 0);
        cv.put("maxTime", 60);
        cv.put("healthLabel", "00000000000");
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.insert("User", null, cv);
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    public boolean login(String emailInput, String password) {
        String user = "null";
        Cursor cursor = getReadableDatabase().rawQuery("SELECT email FROM User where email = " +
                "\"" + emailInput + "\"" + "and password = " + "\""+ password +"\"", null);

        if (cursor != null) {
            cursor.moveToFirst();
            for(boolean cursorBounds = true; cursorBounds; cursorBounds = cursor.moveToNext()) {
                try {
                    user = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                }catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }
        cursor.close();
        if(!(("null").equals(user)))
            return true;
        return false;
    }

    public void updatePreferences(UserPreferences myPreference){
        int calLow = myPreference.getCalorieLow();
        int calHigh = myPreference.getCalorieHigh();
        int dietLabel = myPreference.getDietLabel();
        int maxTime = myPreference.getMaxTimeInMinutes();
        String healthLabel = myPreference.healthLabelToString();

        SQLiteDatabase db = getWritableDatabase();
        String sql = "update User set calLow ="+ calLow +", calHigh ="+ calHigh +", dietLabel ="+
                dietLabel + ", maxTime ="+ maxTime +", healthLabel ="+ "\"" + healthLabel + "\"" +
                " where email = " + "\"" + getEmail(c) + "\"";
        db.beginTransaction();
        try {
            db.execSQL(sql);
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    public void addMeal (String day, String url, int mealNo){
        ContentValues cv = new ContentValues();
        cv.put("email", getEmail(c));
        cv.put("day", day);
        cv.put("mealNo", mealNo);
        cv.put("url", url);

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.insert("History", null, cv);
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }
    public void flagMeal (String url){
        String sql = "update UserMeals set made = 1 where url = " + "\"" + url + "\""+
                " and email = " + "\"" + getEmail(c) + "\"";
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL(sql);
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    public ArrayList<RecipeRecord> getMeals (){
        ArrayList<RecipeRecord> list = new ArrayList<>();
        String dayR;
        int mealNoR;
        String urlR;

        Cursor cursor = getReadableDatabase().rawQuery("SELECT day, mealNo, url FROM History" +
                " where email = " + "\"" + getEmail(c) + "\"", null);

        if (cursor != null) {
            cursor.moveToFirst();
            for(boolean cursorBounds = true; cursorBounds; cursorBounds = cursor.moveToNext()) {
                try {
                    dayR = cursor.getString(cursor.getColumnIndexOrThrow("day"));
                    urlR = cursor.getString(cursor.getColumnIndexOrThrow("url"));
                    mealNoR = cursor.getInt(cursor.getColumnIndexOrThrow("mealNo"));

                    list.add(new RecipeRecord(urlR, dayR, mealNoR));
                }catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }
        if (cursor!=null)
            cursor.close();
        return list;
    }

    public ArrayList<String> getFavorites (){
        ArrayList<String> list = new ArrayList<>();
        String urlR;

        Cursor cursor = getReadableDatabase().rawQuery("SELECT url FROM UserMeals where isFavorite = 1 and email = " + "\"" + getEmail(c) + "\"", null);
        if (cursor != null) {
            cursor.moveToFirst();
            for(boolean cursorBounds = true; cursorBounds; cursorBounds = cursor.moveToNext()) {
                try {
                    urlR = cursor.getString(cursor.getColumnIndexOrThrow("url"));
                    list.add(urlR);
                }catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }
        if (cursor!=null)
            cursor.close();
        return list;
    }

    public void addToFavorites(String url){
        int rating = 0;
        int favorite = 0;
        int made = 0;

        Cursor cursor = getReadableDatabase().rawQuery("SELECT rating, isFavorite, made FROM UserMeals where url = " +
                "\"" + url + "\""+ " and email = " + "\"" + getEmail(c) + "\"", null);

        if (cursor != null) {
            cursor.moveToFirst();
            for(boolean cursorBounds = true; cursorBounds; cursorBounds = cursor.moveToNext()) {
                try {
                    rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"));
                    favorite = cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite"));
                    made = cursor.getInt(cursor.getColumnIndexOrThrow("made"));
                }catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }
        if (cursor!=null)
            cursor.close();
        if (rating == 0 && favorite == 0 && made == 0) {
            ContentValues cv = new ContentValues();
            cv.put("email", getEmail(c));
            cv.put("url", url);
            cv.put("rating", 0); // Changed from -1 to 0
            cv.put("isFavorite", 1);
            cv.put("made", 0);
            SQLiteDatabase db = getWritableDatabase();
            try {
                db.beginTransaction();
                db.insert("UserMeals", null, cv);
                db.setTransactionSuccessful();
            }
            finally {
                db.endTransaction();
            }
        }
        else if (rating != 0 || made != 0) {
            String sql = "update UserMeals set isFavorite = 1 where url = " + "\"" +  url + "\"" +
                    " and email = " + "\"" + getEmail(c) + "\"";
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            try {
                db.execSQL(sql);
                db.setTransactionSuccessful();
            }
            finally {
                db.endTransaction();
            }
        }
    }

    public void removeFromFavorites(String url) {
        int rating = 0;
        int favorite = 0;
        int made = 0;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT rating, isFavorite, made FROM UserMeals where url = " +
                "\"" + url + "\""+ " and email = " + "\"" + getEmail(c) + "\"", null);

        if (cursor != null) {
            cursor.moveToFirst();
            for(boolean cursorBounds = true; cursorBounds; cursorBounds = cursor.moveToNext()) {
                try {
                    rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"));
                    favorite = cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite"));
                    made = cursor.getInt(cursor.getColumnIndexOrThrow("made"));
                }catch (CursorIndexOutOfBoundsException e) {
                    e.getStackTrace();
                }
            }
        }
        if (cursor!=null)
            cursor.close();
        if (rating == 0 && favorite == 1 && made == 0) {
            String sql = "delete from UserMeals where url =" + "\"" + url + "\""+ " where email = " +
                    "\"" + getEmail(c) + "\"";
            db.beginTransaction();
            try {
                db.execSQL(sql);
                db.setTransactionSuccessful();
            }
            finally {
                db.endTransaction();
            }
        } else if (rating != 0 || made != 0) {
            String sql = "update UserMeals set isFavorite = 0 where url = " + "\"" + url + "\"" +
                    " and email = " + "\"" + getEmail(c) + "\"";
            db.beginTransaction();
            try {
                db.execSQL(sql);
                db.setTransactionSuccessful();
            }
            finally {
                db.endTransaction();
            }
        }
    }
    public void updateRating(String url, int newRating){
        int rating = 0;
        int favorite = 0;
        int made = 0;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT rating, isFavorite, made FROM UserMeals where url = " +
                "\"" + url + "\""+ " and email = " + "\"" + getEmail(c) + "\"", null);

        if (cursor != null) {
            cursor.moveToFirst();
            for(boolean cursorBounds = true; cursorBounds; cursorBounds = cursor.moveToNext()) {
                try {
                    rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"));
                    favorite = cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite"));
                    made = cursor.getInt(cursor.getColumnIndexOrThrow("made"));
                }catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }
        if (cursor!=null)
            cursor.close();
        if (rating == 0 && favorite == 0 && made ==0) {
            ContentValues cv = new ContentValues();
            cv.put("email", getEmail(c));
            cv.put("url", url);
            cv.put("rating", newRating);
            cv.put("isFavorite", 0);
            cv.put("made", 0);
            db.beginTransaction();
            try {
                db.insert("UserMeals", null, cv);
                db.setTransactionSuccessful();
            }
            finally {
                db.endTransaction();
            }
        } else if (rating != 0 || made !=0 || favorite != 0) {
            String sql = "update UserMeals set rating =" +newRating+ " where url = " + "\"" + url +
                    "\""+ " and email = " + "\"" + getEmail(c) + "\"";
            db.beginTransaction();
            try {
                db.execSQL(sql);
                db.setTransactionSuccessful();
            }
            finally {
                db.endTransaction();
            }
        }
    }

    public boolean isFavorite (String url){
        int favorite = 0;
        Cursor cursor;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT isFavorite FROM UserMeals where url = " +
                    "\"" + url + "\""+ " and email = " + "\"" + getEmail(c) + "\"", null);
        } catch (Exception e) {
            cursor = null;
            e.getStackTrace();
        }

        if (cursor != null) {
            cursor.moveToFirst();
            for(boolean cursorBounds = true; cursorBounds; cursorBounds = cursor.moveToNext()) {
                try {
                    favorite = cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite"));
                }catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }
        if (cursor!=null)
            cursor.close();
        return (favorite==1);
    }
    public int getRating (String url){
        int rating = 0;

        Cursor cursor = getReadableDatabase().rawQuery("SELECT rating FROM UserMeals where url = " +
                "\""  + url + "\""+ " and email = " + "\"" + getEmail(c) + "\"", null);
        if (cursor != null) {
            cursor.moveToFirst();
            for(boolean cursorBounds = true; cursorBounds; cursorBounds = cursor.moveToNext()) {
                try {
                    rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"));
                }catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }
        if (cursor!=null)
            cursor.close();
        return rating;
    }
    public UserPreferences getPreferences(){
        int calLow = 0;
        int calHigh = 1000;
        int dietLabel = 0;
        int maxTime = 60;
        String healthLabel = "00000000000";

        Cursor cursor = getReadableDatabase().rawQuery("SELECT calLow, calHigh, dietLabel, maxTime, healthLabel FROM User where email =" +
                "\"" + getEmail(c) + "\"", null);
        if (cursor != null) {
            cursor.moveToFirst();
            for(boolean cursorBounds = true; cursorBounds; cursorBounds = cursor.moveToNext()) {
                try {
                    calLow = cursor.getInt(cursor.getColumnIndexOrThrow("calLow"));
                    calHigh = cursor.getInt(cursor.getColumnIndexOrThrow("calHigh"));
                    dietLabel = cursor.getInt(cursor.getColumnIndexOrThrow("dietLabel"));
                    maxTime = cursor.getInt(cursor.getColumnIndexOrThrow("maxTime"));
                    healthLabel = cursor.getString(cursor.getColumnIndexOrThrow("healthLabel"));
                }catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }
        if (cursor!=null)
            cursor.close();

        boolean[] arr = new boolean[11];
        for (int i = 0; i<11; i++)
            arr[i] = ((healthLabel.charAt(i)) == '1');
        UserPreferences myPreference = new UserPreferences(calLow, calHigh, maxTime, dietLabel, arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8], arr[9], arr[10]);
        return myPreference;
    }
}
