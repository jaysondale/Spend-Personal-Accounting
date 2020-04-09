package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;

public class CSVReader {
    public static ObservableList readStatement(String fName) {
        /**
         * Reads bank statement from specified csv-type file
         * @param fName Path to csv file
         */

        // Create return transaction list and initialize with empty ObservableList
        ObservableList tList = FXCollections.observableArrayList();
        BufferedReader br = null;
        String line = "";

        // Define csv delimiter
        String cvsSplitBy = ",";

        // Attempt to read file
        try {
            br = new BufferedReader(new FileReader(URLDecoder.decode(fName, java.nio.charset.StandardCharsets.UTF_8.toString())));
            // Iterate through lines in csv file
            while ((line = br.readLine()) != null) {
                // Split into array of values
                String[] rawTransaction = line.split(cvsSplitBy);

                // Create new transaction object and add values
                // TODO: Handle different bank statement types
                Transaction newTransaction = new Transaction(
                        rawTransaction[0],
                        rawTransaction[1],
                        (rawTransaction[2].isBlank()) ? 0 : Double.valueOf(rawTransaction[2]),
                        (rawTransaction[3].isBlank()) ? 0 : Double.valueOf(rawTransaction[3]));
                // Add new transaction to temporary list
                tList.add(newTransaction);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return tList;
    }
}
