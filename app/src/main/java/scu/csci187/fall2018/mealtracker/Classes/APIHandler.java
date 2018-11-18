package scu.csci187.fall2018.mealtracker.Classes;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;



public class APIHandler {

    public APIHandler () {
        // Nothing to see here
    }


    // private URL url = new URL("https://api.edamam.com/search?q=chicken&app_id=b957081d&app_key=889e79d32df59ed1621b6247b075e26a&from=0&to=3&calories=591-722&health=alcohol-free");
    public Query queryAPI(String assembledQuery) {

        // TODO For reference. Please remove later. String test = "https://api.edamam.com/search?q=chicken&app_id=b957081d&app_key=889e79d32df59ed1621b6247b075e26a&from=0&to=3&calories=591-722&health=alcohol-free";

        // Declare necessary variables for getting JSON from API based on search
        StringBuilder stringBuilder = new StringBuilder();
        JSONObject json = null;




        // Get JSON from API
        try {

            // Create the URL
            URL url = new URL(assembledQuery);

            // Access URL and save output to a buffer
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {

                // Read every line of the buffer and put it into the string builder
                for (String line; (line = buffer.readLine()) != null;) {
                    stringBuilder.append(line);
                }

                try {
                    json = (JSONObject) new JSONTokener(stringBuilder.toString()).nextValue();
                } catch (JSONException e) {
                    return null;
                }
            }
        } catch (MalformedURLException e) {
            System.out.println("Error = " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error with IO in URL accession");
        }

        // Get values. Currently a test to get recipe data:

        String resultsKeyword = "hits";
        JSONArray searchResults;
        try {
            searchResults = json.getJSONArray(resultsKeyword);
        } catch (JSONException e) {
            return null;
        }

        Query query = new Query(searchResults);
        return query;

    }
}