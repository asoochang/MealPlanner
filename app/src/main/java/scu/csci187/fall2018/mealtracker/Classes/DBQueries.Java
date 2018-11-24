package scu.csci187.fall2018.mealtracker.Classes;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DBQueries {

    public int userID;

    public DBQueries(int userID) {
        this.userID = userID;
    }

    //Return the days Meals as an array of "Rows"
    public static ArrayList<Row> daily(String date) {
        ArrayList<Row> table = new ArrayList<Row>();
        PreparedStatement p = null;
        try {
            String sql = " Select * from mealHistory where Date = ?";
            p = DBConnect.prepareState(sql);
            p.setString(1, date);
            ResultSet rs = p.executeQuery();
            Row.formTable(rs, table);
        } catch (Exception e) {
        }
        return table;
    }

    //Return all favorite meals as Array of "Rows"
    public static ArrayList<Row> GetFavoriteMeals() {
        ArrayList<Row> table = new ArrayList<Row>();
        PreparedStatement p = null;
        try {
            String q1 = "Select url from Meals where MealID in (Select MealID from UserMeals where IsFavorite = 1 and UserID = ?)";
            p.setInt(1, userID);
            p = DBConnect.prepareState(q1);
            ResultSet rs = p.executeQuery();
            Row.formTable(rs, table);
        } catch (Exception e) {
        }
        return table;
    }

    //Get weight
    public static double GetWeight(String date) {
        double weight = 0;
        PreparedStatement p = null;
        try {
            String sql = "Select weight from UserHistory where UserID = ?";
            p = DBConnect.prepareState(sql);
            p.setInt(1, userID);
            ResultSet rs = p.executeQuery();
            while (rs.next())
                weight = rs.getDouble("weight");
        } catch (Exception e) {
        }
        return weight;
    }

    //Get height
    public static double GetWeight() {
        double height = 0;
        PreparedStatement p = null;
        try {
            String sql = "Select weight from UserHistory where UserID = ?";
            p = DBConnect.prepareState(sql);
            p.setInt(1, userID);
            ResultSet rs = p.executeQuery();
            while (rs.next())
                height = rs.getDouble("height");
        } catch (Exception e) {
        }
        return height;
    }

    //Get the URL of a meal in history
    public static String GetURL(String mealName, String date) {
        String url = "NoURL";
        PreparedStatement p = null;
        try {
            String sql = "Select url from Meals where mealID in (Select MealID from MealHistory where date = ? and mealNumber = ? and userID = ?)";
            p = DBConnect.prepareState(sql);
            p.setString(1, date);
            p.setString(2, mealName);
            p.setInt(3,userID);
            ResultSet rs = p.executeQuery();
            while (rs.next())
                url = rs.getString("url");
        } catch (Exception e) { }
        return url;
    }

    //add meal from a recipe object
    public static void addToMeals(Recipe myMeal) {
        String name = myMeal.sourceName();
        String url = myMeal.sourceUrl;
        String rurl = null;

        PreparedStatement p = null;
        try {
            String sql1 = "Select url from Meals where name = ?";
            p = DBConnect.prepareState(sql1);
            p.setString(1, name);
            ResultSet rs = p.executeQuery();
            while (rs.next())
                url = rs.getString("rurl");
        } catch (Exception e) { }
        if (rurl != null) {
            try {
                //0 enables auto increment in the database
                String sql2 = "Insert into Meals (mealID, name, url)" + "values (0 ,? ,?)";
                p = DBConnect.prepareState(sql2);
                p.setString(1, name);
                p.setString(2, url);
                p.executeUpdate();

            } catch (Exception e) { }
        }
    }

    public static void AddToFavorites(Recipe myMeal){
        addToMeals(myMeal);
        String name = myMeal.sourceName();
        try {
            String q2 = "delete from UserMeals where Name = ? and userID =?";
            p = DBConnect.prepareState(q2);
            p.setString(1, mealName);
            p.setInt(2, userID);
            p.executeUpdate();
        } catch (Exception e) {
        }
        try {
            String q3 = "update UserMeals set IsFavorite = 1 where Name = ? and userID =?";
            p = DBConnect.prepareState(q3);
            p.setString(1, mealName);
            p.setInt(2, userID);
            p.executeUpdate();
        } catch (Exception e) {
        }

    }
    public static int getRating(Recipe myMeal){
        int rating = -1;
        String mealName = myMeal.sourceName();
        PreparedStatement p = null;
        try {
            String q1 = "Select rating from UserMeals where Name = ? and userID =?";
            p = DBConnect.prepareState(q1);
            p.setString(1, mealName);
            p.setInt(2, userID);
            ResultSet rs = p.executeQuery();
            while (rs.next())
                rating = rs.getInt("rating");
        } catch (Exception e) {
        }
        return rating;
    }

    public static void RemoveFromFavorites(Recipe myMeal) {
        String mealName = myMeal.sourceName();
        PreparedStatement p = null;
        int rating = getRating(myMeal);

        if (rating == -1) {
            try {
                String q2 = "delete from UserMeals where Name = ? and userID =?";
                p = DBConnect.prepareState(q2);
                p.setString(1, mealName);
                p.setInt(2, userID);
                p.executeUpdate();
            } catch (Exception e) {
            }
        } else {
            try {
                String q3 = "update UserMeals set IsFavorite = 0 where Name = ? and userID =?";
                p = DBConnect.prepareState(q3);
                p.setString(1, mealName);
                p.setInt(2, userID);
                p.executeUpdate();
            } catch (Exception e) {
            }
        }
    }
}
