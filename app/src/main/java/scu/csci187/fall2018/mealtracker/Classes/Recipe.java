package scu.csci187.fall2018.mealtracker.Classes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.System.exit;


public class Recipe {

    private JSONObject value;

    public Recipe(JSONObject recipe) {
        value = recipe;
    }



    public Ingredients ingredients() {
        JSONArray ingredients;
        Ingredients ret = null;
        try {
            ingredients = value.getJSONArray("ingredients");
            ret = new Ingredients(ingredients);
        } catch (JSONException e) {
            exit(500);
        }

        return ret;
    }

    public String name () {
        try {
            return value.getString("label");
        } catch (JSONException e) {
            return "";
        } catch (NullPointerException e) {
            return "";
        }
    }
    public String linkInAPI () {
        try {
            return value.getString("uri");
        } catch (JSONException e) {
            return "";
        }
    }

    public String imageUrl () {
        try {
            return value.getString("image");
        } catch (JSONException e) {
            return "";
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String linkToInstructions () {
        try {
            return value.getString("url");
        } catch (JSONException e) {
            return "";
        }
    }

    public String shareLink () {
        try {
            return value.getString("shareAs");
        } catch (JSONException e) {
            return "";
        }
    }

    public double timeToCook () {
            return (double) value.optDouble("totalTime");
    }

    public int calories () {
        try {
            return value.getJSONObject("totalNutrients").getJSONObject("ENERC_KCAL").optInt("quantity");
        } catch (JSONException e) {
            return -1;
        }
    }

    public int fat () {
        try {
            return value.getJSONObject("totalNutrients").getJSONObject("FAT").optInt("quantity");
        } catch (JSONException e) {
            return -1;
        }
    }

    public int carbs () {
        try {
            return value.getJSONObject("totalNutrients").getJSONObject("CHOCDF").optInt("quantity");
        } catch (JSONException e) {
            return -1;
        }
    }

    public int fiber () {
        try {
            return value.getJSONObject("totalNutrients").getJSONObject("FIBTG").optInt("quantity");
        } catch (JSONException e) {
            return -1;
        }
    }

    public int sugar () {
        try {
            return value.getJSONObject("totalNutrients").getJSONObject("SUGAR").optInt("quantity");
        } catch (JSONException e) {
            return -1;
        }
    }

    public int protein () {
        try {
            return value.getJSONObject("totalNutrients").getJSONObject("PROCNT").optInt("quantity");
        } catch (JSONException e) {
            return -1;
        }
    }

    public int cholesterol () {
        try {
            return value.getJSONObject("totalNutrients").getJSONObject("CHOLE").optInt("quantity");
        } catch (JSONException e) {
            return -1;
        }
    }

    public int sodium () {
        try {
            return value.getJSONObject("totalNutrients").getJSONObject("NA").optInt("quantity");
        } catch (JSONException e) {
            return -1;
        }
    }

}
