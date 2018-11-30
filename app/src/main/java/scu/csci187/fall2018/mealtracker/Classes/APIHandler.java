package scu.csci187.fall2018.mealtracker.Classes;
        import org.json.JSONException;
        import org.json.JSONObject;
        import org.json.JSONTokener;
        import org.json.JSONArray;
        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.net.MalformedURLException;
        import java.net.URL;
        import java.util.ArrayList;
        import java.util.concurrent.ExecutionException;

        import android.os.AsyncTask;



public class APIHandler extends AsyncTask<String, Void, JSONObject> {

    boolean isSearch = false;

    public ArrayList<Recipe> getRecipesFromBookmarks(ArrayList<String> bookmarkedMeals) {

        ArrayList<Recipe> recipes = new ArrayList<>();
        if (bookmarkedMeals.size() > 0) {


            QueryParam qp = new QueryParam();
            for (int i = 0; i < bookmarkedMeals.size(); ++i) {
                String currentMealLink = bookmarkedMeals.get(i);
                String formattedLink = qp.getFormattedBookmarkURL(currentMealLink);
                JSONObject json;

                try {
                    json = new APIHandler().execute( formattedLink ).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    json = new JSONObject();
                }

                Recipe returnedRecipe = new Recipe(json);
                if (returnedRecipe != null) {
                    recipes.add(returnedRecipe);
                }
            }

        }
        return recipes;
    }
  
    public Query search(QueryParam qp) {
        isSearch = true;

        // TODO For reference. Please remove later. String test = "https://api.edamam.com/search?q=chicken&app_id=b957081d&app_key=889e79d32df59ed1621b6247b075e26a&from=0&to=3&calories=591-722&health=alcohol-free";

        // Declare necessary variables for getting JSON from API based on search

        JSONObject jsonObject = null;


        String assembledQuery = qp.assembleSearchURL();

        try {
            jsonObject = this.execute(assembledQuery).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // Get values. Currently a test to get recipe data:

        String resultsKeyword = "hits";
        JSONArray searchResults;
        if (jsonObject == null) {
            try {
                jsonObject = new JSONObject("{weewoo: -1}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            searchResults = jsonObject.getJSONArray(resultsKeyword);
        } catch (JSONException e) {
            return null;
        }

        Query query = new Query(searchResults);
        isSearch = false;
        return query;

    }


    @Override
    protected JSONObject doInBackground(String... assembledQuery) {

        StringBuilder stringBuilder = new StringBuilder();

        // Get JSON from API
        try {

            // Create the URL
            URL url = new URL(assembledQuery[0]);

            // Access URL and save output to a buffer
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {

                // Read every line of the buffer and put it into the string builder
                for (String line; (line = buffer.readLine()) != null;) {
                    stringBuilder.append(line);
                }

                if (this.isSearch) {
                    try {
                        JSONObject jsonObject = ( (JSONObject) new JSONTokener(stringBuilder.toString()).nextValue() );
                        return jsonObject;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                    }
                } else {
                    try {
                        JSONArray jsonArray = ( (JSONArray) new JSONTokener(stringBuilder.toString()).nextValue() );
                         return jsonArray.getJSONObject(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

            }
        } catch (MalformedURLException e) {
            System.out.println("Error = " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("Error with IO in URL accession");
            return null;
        }
    }

}