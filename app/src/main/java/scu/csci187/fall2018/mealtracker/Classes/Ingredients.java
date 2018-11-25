package scu.csci187.fall2018.mealtracker.Classes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Ingredients {

    private JSONArray value;

    public Ingredients(JSONArray in) {
        value = in;
    }

    public Ingredient getIngredientAtIndex(int index) {
        JSONObject temp;
        try {
            temp = value.getJSONObject(index);
        } catch (JSONException e) {
            temp = null;
        }
        Ingredient ingredient = new Ingredient(temp);
        return ingredient;
    }

    public int length() {
        return value.length();
    }
}
