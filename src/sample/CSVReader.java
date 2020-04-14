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

        System.out.println("Opening file: " + fName);

        // Create return transaction list and initialize with empty ObservableList
        ObservableList tList = FXCollections.observableArrayList();
        BufferedReader br = null;
        String line;

        // Define csv delimiter
        String cvsSplitBy = ",";

        // Selected bank format
        BankFormat format = BankFormat.TD;

        // Attempt to read file
        try {
            br = new BufferedReader(new FileReader(URLDecoder.decode(fName, java.nio.charset.StandardCharsets.UTF_8.toString())));
            // Iterate through lines in csv file
            while ((line = br.readLine()) != null) {
                // Split into array of values
                String[] rawTransaction = line.split(cvsSplitBy);

                // Check to ensure the line is in the proper format (loop until size is proper)
                while (rawTransaction.length > format.getLineSize()) {
                    // Hold rawTransaction in temp variable
                    String[] tempTransaction = rawTransaction;
                    // Reinitialize rawTransaction to be 1 unit shorter
                    rawTransaction = new String[rawTransaction.length - 1];

                    int readIndex = 0;
                    for (int i = 0; i < rawTransaction.length; i++) {
                        if (i == format.getIdIndex()) {
                            rawTransaction[i] = tempTransaction[readIndex] + tempTransaction[readIndex + 1]; // Merge id and adjacent value
                            readIndex++;
                        } else {
                            rawTransaction[i] = tempTransaction[readIndex];
                        }
                        readIndex++;
                    }
                }

                // Create new transaction object and add values
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
