package sample;

import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Transaction Utility
 * Contains functionality for manipulating transactions in the data set
 */
public class TransactionUtility {

    /**
     * Applies substring map to all transactions in the data set
     * @param manager Manager instance
     * @param substringMap Substring map to be applied
     */
    public static void applySubstringMap(TransactionAnalyzerManager manager, HashMap<String, String> substringMap) {

        ArrayList<MoveTransaction> moved = new ArrayList<>(); // Contains transactions that require moving

        // Iterate through each substring in the map
        for (String substring : substringMap.keySet()) {
            // Get destination category from substring map
            String category = substringMap.get(substring);
            // Iterate through all categories
            for (String catName : manager.getCategories().keySet()) {
                // Only search categories different from the destination category
                if (catName != category) {
                    // Get transaction list from categories
                    ObservableList<Transaction> list = manager.getCategories().get(catName);

                    // Iterate through transaction list
                    for (Transaction t : list) {
                        // Check to see if substring is contained within transaction ID
                        if (t.getId().contains(substring)) {
                            moved.add(new MoveTransaction(t, catName, category));
                        }
                    }
                }
            }
        }

        // Move transactions in moved ArrayList
        for (MoveTransaction mt : moved) {
            // Assign new categories
            assignCategory(manager, mt.t, mt.currentCat, mt.newCat);
        }
    }

    /**
     * MoveTransaction
     * Helper class to hold transactions that require moving
     */
    public static class MoveTransaction {
        public Transaction t;
        public String currentCat;
        public String newCat;

        /**
         * Constructor
         * @param t Transaction to be moved
         * @param currentCat Transaction's current category
         * @param newCat Transaction's new category
         */
        public MoveTransaction(Transaction t, String currentCat, String newCat) {
            this.t = t;
            this.currentCat = currentCat;
            this.newCat = newCat;
        }
    }

    /**
     * Moves transaction from existing category to new category
     * @param manager Manager instance
     * @param t Transaction to be moved
     * @param currentCat Current category name
     * @param newCat New category Name
     */
    public static void assignCategory(TransactionAnalyzerManager manager, Transaction t, String currentCat, String newCat) {
        // Remove transaction from current category list
        manager.getCategories().get(currentCat).remove(t);
        // Add transaction to desired category list
        manager.getCategories().get(newCat).add(t);
        // Modify idMap to contain new mapping
        if (manager.getIdMap().keySet().contains(t.getId())) {
            // Remove old map element
            manager.getIdMap().remove(t.getId());
        }
        // Add new map element
        manager.getIdMap().put(t.getId(), newCat);
    }
}
