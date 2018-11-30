package scu.csci187.fall2018.mealtracker.Classes;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class QueryParam {

    private String diet;
    private ArrayList<String> healthLabels = new ArrayList<String>();
    private int calorieMax;
    private int calorieMin;
    private int timeMax;
    private int timeMin;
    private String query;

    public QueryParam (){
        // No Initialization. Must do manually through getters and setters
    }
    public void setQuery (String q) {
        this.query = q;
    }
    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public ArrayList<String> getHealthLabels() {
        return healthLabels;
    }

    public void addHealthLabels(String healthLabel) {
        this.healthLabels.add(healthLabel);
    }

    public int getCalorieMax() {
        return calorieMax;
    }

    public void setCalorieMax(int calorieMax) {
        this.calorieMax = calorieMax;
    }

    public int getCalorieMin() {
        return calorieMin;
    }

    public void setCalorieMin(int calorieMin) {
        this.calorieMin = calorieMin;
    }

    public int getTimeMax() {
        return timeMax;
    }

    public void setTimeMax(int timeMax) {
        this.timeMax = timeMax;
    }

    public int getTimeMin() {
        return timeMin;
    }

    public void setTimeMin(int timeMin) {
        this.timeMin = timeMin;
    }

    public String assembleSearchURL () {
        String result = "https://api.edamam.com/search?";

        // Set diet params
        if (this.diet != "") {
            result = result + "diet=" + this.diet + "&";
        } else {

        }

        // Set Calorie range
        if (this.calorieMax > this.calorieMin) {
            result = result + "calories=" + this.calorieMin + "-" + this.calorieMax + "&";
        } else {
            // Do not add a calorie range
        }

        // Set time range
        if (this.timeMax > this.timeMin) {
            result = result + "time=" + this.timeMin + "-" + this.timeMax + "&";
        } else {
            // Do not add a time range
        }

        // Set query param
        result = result + "q=" + URLEncoder.encode(this.query);

        if (this.healthLabels.size() < 1) {
            // Do Nothing
        } else {
            result = result + "&";
            // Add in health labels
            for (int i = 0; i < this.healthLabels.size(); ++i) {
                String andSign = "&";
                if (i == 0) {
                    andSign = "";
                }
                result = result + andSign + "health=" + this.healthLabels.get(i);
            }

        }

        // Add API ID and APP ID to URL
        String apiKeys = "&app_id=b957081d&app_key=889e79d32df59ed1621b6247b075e26a";
        result = result + apiKeys;
        return result;
    }

    public String getFormattedBookmarkURL (String bookmarkURL) {
        String baseString = "https://api.edamam.com/search?";
        String apiKeys = "&app_id=b957081d&app_key=889e79d32df59ed1621b6247b075e26a";
        String bookmarkKeyword = "r=";

        String assembledQuery = null;
        try {
            assembledQuery = baseString + bookmarkKeyword + URLEncoder.encode(bookmarkURL, "UTF-8") + apiKeys;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            assembledQuery = "";
        }
        return assembledQuery;
    }
}
