package scu.csci187.fall2018.mealtracker.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class SQLiteUserManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "nutritionDB";
    private static final int DATABASE_VERSION = 2;
    private static String email = "null";

    public SQLiteUserManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void setEmail(String email){
        //this.email = email;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String sql1 = "CREATE TABLE IF NOT EXISTS User (email text primary key, password text, calLow integer, calHigh integer, dietLabel integer, maxTime integer, healthLabel text)";
        String sql2 = "CREATE TABLE IF NOT EXISTS UserMeals (email text, url text, rating integer, isFavorite integer, made integer, primary key(url, email), foreign key (email) references User(email), foreign key (url) references Bookmarked(url))";
        String sql3 = "CREATE TABLE IF NOT EXISTS History (email text, day text, mealNo integer, url text, primary key (day, mealNo, email), foreign key (email) references User(email), foreign key (url) references Bookmarked(url))";

        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
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
        cv.put("dietLabel", 1);
        cv.put("maxTime", 60);
        cv.put("healthLabel", "00000000000");
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.insert("User", null, cv);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public boolean login(String emailInput, String password) {
        String user = "null";
        Cursor cursor = getReadableDatabase().rawQuery("SELECT email FROM User where email = " + "\"" + emailInput + "\"" + "and password = " + "\""+ password +"\"", null);

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

    public void updatePreferences(String email, int calHigh, int calLow, int dietLabel, int maxTime, String healthLabel){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "update History set calLow ="+ calLow +", calHigh ="+ calHigh +", dietLabel ="+ dietLabel +
                ", maxTime ="+ maxTime +", healthLabel ="+ "\"" + healthLabel + "\"" +"where email =" + "\"" + email + "\"";
        db.execSQL(sql);
    }

    //Addmeal to history
    public void addMeal (String day, String url, int mealNO){
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("day", day);
        cv.put("url", url);
        cv.put("mealNO", mealNO);
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.insert("History", null, cv);
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    public void flagMeal (String url){
        String sql = "update UserMeals set made = 1 where url = " + "\"" + url + "\"" +" and email = " + "\"" + email + "\"";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }

    //Meals on a given date as an ArrayList of RecipeRecords
    public ArrayList<RecipeRecord> getMealsOn (String day){
        ArrayList<RecipeRecord> list = new ArrayList<RecipeRecord>();
        String dayR;
        int mealNOR;
        String urlR;

        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM History where day = " + "\"" + day + "\"" + "and email =" + "\"" + email + "\"", null);
        if (cursor != null) {
            cursor.moveToFirst();
            for(boolean cursorBounds = true; cursorBounds; cursorBounds = cursor.moveToNext()) {
                try {
                    dayR = cursor.getString(cursor.getColumnIndexOrThrow("day"));
                    mealNOR = cursor.getInt(cursor.getColumnIndexOrThrow("mealNO"));
                    urlR = cursor.getString(cursor.getColumnIndexOrThrow("url"));
                    list.add(new RecipeRecord(urlR, dayR, mealNOR));
                }catch (Exception e) {
                    e.getStackTrace();
                }
            };
        }
        if (cursor!=null)
            cursor.close();
        return list;
    }
    public ArrayList<RecipeRecord> getMeals (){
        ArrayList<RecipeRecord> list = new ArrayList<RecipeRecord>();
        String dayR;
        int mealNOR;
        String urlR;

        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM History where email =" + "\"" + email + "\"", null);

        if (cursor != null) {
            cursor.moveToFirst();
            for(boolean cursorBounds = true; cursorBounds; cursorBounds = cursor.moveToNext()) {
                try {
                    dayR = cursor.getString(cursor.getColumnIndexOrThrow("day"));
                    mealNOR = cursor.getInt(cursor.getColumnIndexOrThrow("mealNO"));
                    urlR = cursor.getString(cursor.getColumnIndexOrThrow("url"));
                    list.add(new RecipeRecord(urlR, dayR, mealNOR));
                }catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }
        if (cursor!=null)
            cursor.close();
        return list;
    }
    //Favorite Meals as an ArrayList of urls
    public ArrayList<String> getFavorites (){
        ArrayList<String> list = new ArrayList<String>();
        String urlR;

        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM UserMeals where isFavorite = 1 and email = " + "\"" + email + "\"", null);
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
        int rating = -1;
        int favorite = 0;
        int made = 0;

        Cursor cursor = getReadableDatabase().rawQuery("SELECT rating, isFavorite, made FROM UserMeals where url = " + "\"" + url + "\"", null);

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
        if (rating == -1 && favorite == 0 && made == 0) {
            ContentValues cv = new ContentValues();
            cv.put("email", email);
            cv.put("url", url);
            cv.put("rating", -1);
            cv.put("isFavorite", 1);
            cv.put("made", 0);
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            db.insert("UserMeals", null, cv);
            db.setTransactionSuccessful();
            db.endTransaction();
        } else if (rating != -1) {
            String sql = "update UserMeals set isFavorite = 1 where url = " + "\"" +  url + "\"" + "and email = " + "\"" + email + "\"";
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            db.execSQL(sql);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    public void removeFromFavorites(String url) {
        int rating = -1;
        int favorite = 0;
        int made = 0;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT rating, isFavorite, made FROM UserMeals where url = " + "\"" + url + "\"", null);

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
        if (rating == -1 && favorite == 1 && made == 0) {
            String sql = "delete from UserMeals where url =" + "\"" + url + "\"" + "and email =" + "\"" + email + "\"";
            db.execSQL(sql);
        } else if (rating != -1 || made != 0) {
            String sql = "update UserMeals set isFavorite = 0 where url = " + "\"" + url + "\"" + "and email = " + "\"" + email + "\"";
            db.execSQL(sql);
        }
    }
    public void updateRating(String url, int newRating){
        int rating = -1;
        int favorite = 0;
        int made = 0;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT rating, isFavorite FROM UserMeals where url = " + "\"" + url + "\"", null);

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
        if (rating == -1 && favorite == 0) {
            ContentValues cv = new ContentValues();
            cv.put("email", email);
            cv.put("url", url);
            cv.put("rating", newRating);
            cv.put("isFavorite", 0);
            cv.put("made", 0);
            db.beginTransaction();
            db.insert("UserMeals", null, cv);
            db.setTransactionSuccessful();
            db.endTransaction();
        } else if (rating != -1 || made !=0) {
            String sql = "update UserMeals set rating =" +newRating+ " where url = " + "\"" + url + "\"" + "and email = " + "\"" + email + "\"";
            db.execSQL(sql);
        }
    }

    public boolean isFavorite (String url){
        ArrayList<RecipeRecord> list = new ArrayList<RecipeRecord>();
        int favorite = 0;
        Cursor cursor;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT isFavorite FROM userMeals where url = " + "\"" + url + "\"", null);
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
        ArrayList<RecipeRecord> list = new ArrayList<RecipeRecord>();
        int rating = -1;

        Cursor cursor = getReadableDatabase().rawQuery("SELECT rating FROM userMeals where url = " + "\""  + url + "\"", null);
        if (cursor != null) {
            cursor.moveToFirst();
            for(boolean cursorBounds = true; cursorBounds; cursorBounds = cursor.moveToNext()) {
                try {
                    rating = cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite"));
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

        //required defaults
        int calLow = 0;
        int calHigh = 1000;
        int dietLabel = 0;
        int maxTime = 60;
        String healthLabel = "00000000000";

        Cursor cursor = getReadableDatabase().rawQuery("SELECT calLow, calHigh, dietLabel, maxTime FROM User where email =" + email, null);
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
