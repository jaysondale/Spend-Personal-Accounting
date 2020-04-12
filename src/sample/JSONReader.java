package sample;

import com.google.gson.reflect.TypeToken;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.google.gson.Gson;

public class JSONReader {

    public HashMap<String, String> readIDMAp(String fName) {
        /**
         * Reads JSON file and returns idMap object
         * @param fName Path to json file
         */
        // Create return object
        HashMap<String, String> idMap = new HashMap<>();
        JSONParser parser = new JSONParser();

        // Attempt to parse JSON file
        try {
            Object obj = parser.parse(new FileReader(fName));
            JSONObject json = (JSONObject) obj;

            // Iterate through all mapped pairs in the json file
            for (Map.Entry entry : (Set<Map.Entry>) json.entrySet()) {
                // Get key and value
                Object key = entry.getKey();
                Object value = entry.getValue();

                // Convert key and value to String type
                String keyString = String.valueOf(key);
                String valueString = String.valueOf(value);

                // Add mapping to return object
                idMap.put(keyString, valueString);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return idMap;
    }

    public void saveIDMap(String fName, HashMap<String, String> idMap) throws IOException {
        /**
         * Save idMap object to JSON file
         * @param fName Destination file name
         * @param idMap idMap to be saved to JSON
         * @throws IOException
         */
        // Use Gson library to get string version of json structure
        Gson gson = new Gson();
        Type gsonType = new TypeToken<HashMap>(){}.getType();

        // Create gsonString object
        String gsonString = gson.toJson(idMap, gsonType);

        // Use FileWriter to write the gson string to JSON file
        // TODO: Handle FileNotFoundError (create file if it does not exist)
        FileWriter fw = new FileWriter(fName);
        PrintWriter pw = new PrintWriter(fw);
        pw.write(gsonString);

        pw.close();
        System.out.println("Json saved to " + fName);
    }
}
