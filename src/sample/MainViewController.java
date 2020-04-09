package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {


    // **FXML Links**

    // Main borderPane
    @FXML private BorderPane borderPane;

    // UI Tables
    @FXML private TableView<Transaction> transactionTableView;
    @FXML private TableColumn<Transaction, String> dateCol;
    @FXML private TableColumn<Transaction, String> idCol;
    @FXML private TableColumn<Transaction, Double> debitCol;
    @FXML private TableColumn<Transaction, Double> creditCol;

    // Category listView
    @FXML private ListView<String> categoryListView;

    // Date range selection
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    // Statistics labels
    @FXML private Label balanceLabel;
    @FXML private Label avgTransLabel;
    @FXML private Label numTransLabel;

    // New category creator
    @FXML private TextField newCatTextField;

    // Substring Mapper
    @FXML private TextField substringTextField;
    @FXML private ComboBox<String> catComboBox;
    @FXML private Button setButton;

    // **Data Members**

    // Dates
    private Date startDate;
    private Date endDate;

    // Current statistics
    private double balance;
    private int numTransactions;

    private TransactionAnalyzerManager manager;

    public void setModel(TransactionAnalyzerManager model) {
        this.manager = model;

        initElements();
    }

    public void initElements() {

        // Initializing category lists
        loadCategories();

        // Load Maps from files
        // TODO: Allow user to specify location of mapping files (allows for multiple user profiles)
        loadMap(manager.IDMAP_NAME, manager.getIdMap());
        loadMap(manager.SSMAP_NAME, manager.getSsMap());

        // Get categories from idMap
        for (String key : manager.getIdMap().keySet()) {
            String testCat = manager.getIdMap().get(key);
            // Check to see if category has already been added to category names
            if (!manager.getCategoryNames().contains(testCat)) {
                // Add category to categoryNames
                manager.getCategoryNames().add(testCat);
                // Add category to categories and initialize with an empty observable list
                manager.getCategories().put(testCat, FXCollections.observableArrayList());
            }
        }

        // Populate categories listView with all categories found from id map
        categoryListView.setItems(manager.getCategoryNames());

        // Link table columns with their respective properties from Transaction class
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        debitCol.setCellValueFactory(new PropertyValueFactory<>("debit"));
        creditCol.setCellValueFactory(new PropertyValueFactory<>("credit"));



        // Highlight rows within specified date range
        // TODO: TableView should reload so that items are highlighted as soon as dates are modified
        transactionTableView.setRowFactory(tv -> new TableRow<Transaction>(){
            @Override
            public void updateItem(Transaction item, boolean empty) {
                super.updateItem(item, empty);
                // Check to see that dates are selected
                if (startDate != null && endDate != null && item != null) {
                    try {
                        Date transactionDate = manager.DateFormat.parse(item.getDate());
                        // See if date of current transaction is within the specified range
                        if (transactionDate.after(startDate) && transactionDate.before(endDate)) {
                            // Highlight transactions in date range
                            setStyle("-fx-background-color: #33F6FF");
                        } else {
                            // Remove highlight from transactions that are out of range
                            setStyle("");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Add key listener to transactionTableView to allow for category assignment
        transactionTableView.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.C) {
                setCategory();
            }
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    public void updateDates() {
        /**
         * Reassigns start and end date variables and computes statistics on the selected category
         */
        startDate = convertToDateViaSqlDate(startDatePicker.getValue());
        endDate = convertToDateViaSqlDate(endDatePicker.getValue());
        transactionTableView.refresh();
        computeStatistics();
    }

    // Helper function for date type conversions
    public Date convertToDateViaSqlDate(LocalDate dateToConvert) {
        return java.sql.Date.valueOf(dateToConvert);
    }

    private void computeStatistics() {
        /**
         * Calculates the balance, number of transactions, and average transaction amount and updates their respective
         * labels.
         */

        // Temporary to hold current stats
        balance = 0.0;
        numTransactions = 0;

        // Iterate through transactions in selected category list
        for (Transaction t : manager.getCategories().get(manager.getSelectedCategory())) {
            // Attempt to parse the date from the transaction
            try {
                Date tDate = manager.DateFormat.parse(t.getDate());
                // Check if the transaction is within the date range
                if (tDate.before(endDate) && tDate.after(startDate)) {
                    // Add transactions that are within range
                    balance += t.getDebit() - t.getCredit();
                    numTransactions ++;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Update Labels
        balanceLabel.setText("Balance: $" + round(balance, 2));
        numTransLabel.setText("Number of Transactions: " + numTransactions);
        avgTransLabel.setText("Average transaction: $" + round((balance/numTransactions),2));
    }

    private static double round(double value, int places) {
        /**
         * Helper function that rounds double-type numbers to a specified number of decimal places
         * @param value The number to be rounded
         * @param places The number of decimal places to be rounded to (must be greater than or equal to 0)
         * @return The rounded number
         * @throws IllegalArgumentException
         */
        // Only accept places parameter that is greater than or equal to zero
        if (places < 0) throw new IllegalArgumentException();

        // Move the number over until all important digits are to the left of the decimal
        long factor = (long) Math.pow(10, places);
        value = value * factor;

        // Round to the nearest whole number
        long tmp = Math.round(value);

        // Divide by the power of 10 to move back to the original decimal location
        return (double) tmp / factor;
    }

    public void updateTransactionList() {
        /**
         * Updates the items in transactionTableView to contain all transactions in the selected category
         */
        transactionTableView.setItems(manager.getCategories().get(manager.getSelectedCategory()));
        transactionTableView.refresh();
    }

    public void updateCategoryList() {
        /**
         * Update categoryListView to display all items in categoryNames
         * Update the category selector box to contain the current list of category names
         */
        // TODO: Makes sure catComboBox is updated even if no new category has been created by the user
        categoryListView.setItems(manager.getCategoryNames());
        catComboBox.setItems(manager.getCategoryNames());
    }

    public void updateSelectedCategory() {
        /**
         * Get the selected category from the listView UI element and update the transaction list
         */
        // Get selected category
        manager.setSelectedCategory(categoryListView.getSelectionModel().getSelectedItem());
        // Update the transaction list
        updateTransactionList();
        // If the dates are specified, update the statistics to use transaction data from current category
        if (startDate != null && endDate != null) {
            computeStatistics();
        }
    }

    public void loadCategories() {
        /**
         * Add the default category to both categoryNames and categories HashMap
         */
        manager.getCategoryNames().add(manager.DEFAULT_CATEGORY);
        manager.getCategories().put(manager.DEFAULT_CATEGORY, FXCollections.observableArrayList());
    }

    public void addCategory() {
        /**
         * Adds new category from UI category adder
         */
        // Get the text from newCatTextField and temporarily save it
        String name = newCatTextField.getText();
        // Check to make sure the category is unique
        if (!manager.getCategoryNames().contains(name)) {
            // Add category
            // TODO: Make category adding modular (replace loadCategories)
            manager.getCategoryNames().add(name);
            manager.getCategories().put(name, FXCollections.observableArrayList());
        }
        // Clear the text field and update UI category list
        newCatTextField.clear();
        updateCategoryList();
    }

    public void createSubstringMap() {
        /**
         * Adds new substring map from UI to ssMap
         */
        // Get the mapped category from catComboBox
        String cat = catComboBox.getSelectionModel().getSelectedItem();
        // Get substring
        String substring = substringTextField.getText();
        // Create a temporary hashmap containing the new mapping for efficient implementation using applySubstringMap
        HashMap<String, String> tempMap = new HashMap<>();
        tempMap.put(substring, cat);
        applySubstringMap(tempMap);

        // Add to main substring map data member
        manager.getSsMap().put(substring, cat);
    }

    public void assignCategory(Transaction t, String currentCat, String newCat) {
        /**
         * Maps transaction ID to category and saves in idMap
         */
        // Remove transaction from current category list
        manager.getCategories().get(currentCat).remove(t);
        // Add transaction to desired category list
        manager.getCategories().get(newCat).add(t);
        // Modify idMap to contain new mapping
        if (manager.getIdMap().keySet().contains(t.getId())) {
            // Remove old map element and replace with new one
            manager.getIdMap().remove(t.getId());
            manager.getIdMap().put(t.getId(), newCat);
        }
    }

    public void applySubstringMap(HashMap<String, String> map) {
        /**
         * Applies given substring map to all transactions in all categories and moves them accordingly
         * @param map Substring map to be applied
         */
        // Iterate through each substring in the map
        for (String substring : map.keySet()) {
            // Get destination category from substring map
            String category = map.get(substring);
            // Iterate through all categories
            for (String catName : manager.getCategories().keySet()) {
                // Only search categories different from the destination category
                if (catName != category) {
                    // Get transaction list from categories
                    ObservableList<Transaction> list = manager.getCategories().get(catName);
                    // Temporary ArrayList to contain all transactions that require moving
                    ArrayList<Transaction> moved = new ArrayList<>();
                    // Iterate through transaction list
                    for (Transaction t : list) {
                        // Check to see if substring is contained within transaction ID
                        if (t.getId().contains(substring)) {
                            moved.add(t);
                            // Remove transaction map from idMap if exists
                            if(manager.getIdMap().containsKey(t.getId())) {
                                manager.getIdMap().remove(t.getId());
                            }
                            // Add new mapping to idMap
                            manager.getIdMap().put(t.getId(), category);
                        }
                    }
                    // Move transactions in moved ArrayList
                    for (Transaction t : moved) {
                        assignCategory(t, catName, category);
                    }
                }
            }
        }
    }

    public void setCategory() {
        /**
         * Allow user to map a specific transaction ID to desired category
         */
        // Create new Popup object
        Popup catSelector = new Popup();
        // Create listView to hold categories
        // TODO: Try using categoryListView object in popup instead of making a new one
        ListView<String> catList = new ListView();
        catList.setItems(manager.getCategoryNames());
        catSelector.getContent().add(catList);
        // Add key listener to catList and wait for enter key to be pressed
        catList.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                // If enter key is pressed, get currently selected category
                String newCat = catList.getSelectionModel().getSelectedItem();
                Transaction selected = transactionTableView.getSelectionModel().getSelectedItem();

                // Move category to new category
                // TODO: Apply new mapping to all transactions
                assignCategory(selected, manager.getSelectedCategory(), newCat);

                // Close popup
                catSelector.hide();
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                // Close popup if escape key is pressed
                catSelector.hide();
            }
        });
        // Display popup window
        catSelector.show(borderPane.getScene().getWindow());
    }

    public void importStatements() {
        /**
         * Imports all files with CSV suffix and uses loadStatement to add transactions
         */
        // Create and display DirectoryChooser
        DirectoryChooser dirChoose = new DirectoryChooser();
        dirChoose.setTitle("Select the directory where all statements are stored");
        File newDir = dirChoose.showDialog(borderPane.getScene().getWindow());

        // Iterate through all files in directory and get ones with csv suffix
        // TODO: Add error handling if unsupported csv files exist in the specified directory
        for (File file : newDir.listFiles()) {
            if (file.getName().contains(".csv")) {
                loadStatement(file.getPath());
            }
        }
    }

    private void loadMap(String map, HashMap<String, String> hashMap) {
        /**
         * Loads a JSON-type idMap
         * @param map idMap file path
         * @param hashMap Destination map for all transactions
         */
        JSONReader reader = new JSONReader();
        hashMap.putAll(reader.readIDMAp(map));
        updateCategoryList();
    }

    private void saveMap(String map, HashMap<String, String> hashMap) {
        /**
         * Saves idMap to destination folder
         * @param map idMap file path
         * @param hashMap HashMap to be stored in json file
         */
        JSONReader reader = new JSONReader();
        try {
            reader.saveIDMap(map, hashMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadStatement(String filePath) {
        /**
         * Load a csv-type bank statement from given file
         * @param filePath Path to csv file
         */
        // Read transactions from statement csv file
        ObservableList<Transaction> rawTransactions = CSVReader.readStatement(filePath);

        // Attempt to categorize transactions
        for (Transaction t : rawTransactions) {
            // Start with ID map
            if (manager.getIdMap().containsKey(t.getId())) {
                manager.getCategories().get(manager.getIdMap().get(t.getId())).add(t);
            } else {
                // Try substring map
                FOUND :
                {
                    for (String subStr : manager.getSsMap().keySet()) {
                        if (t.getId().contains(subStr)) {
                            manager.getCategories().get(manager.getSsMap().get(subStr)).add(t);
                            break FOUND;
                        }
                    }
                    // If no mapping is found, assign to default category
                    manager.getCategories().get(manager.DEFAULT_CATEGORY).add(t);
                }
            }
        }
    }

    public void saveMaps() {
        /**
         * Saves both the idMap and ssMap HashMaps to json filetype
         */
        saveMap(manager.IDMAP_NAME, manager.getIdMap());
        saveMap(manager.SSMAP_NAME, manager.getSsMap());
    }

    public void viewIdMap() {
        viewMap(false);
    }

    public void viewSsMap() {
        viewMap(true);
    }

    private void viewMap(boolean viewSsMap) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MapViewer.fxml"));
            Parent root = loader.load();
            MapViewerController controller = (MapViewerController) loader.getController();

            // Set Model
            controller.setModel(manager);
            if (viewSsMap)
                controller.setSsMap();
            else
                controller.setIdMap();


            Stage stage = new Stage();
            stage.setTitle("View Map");
            stage.setScene(new Scene(root, 400, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void viewAnnualSummary()
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MonthlySummaryView.fxml"));
            Parent root = loader.load();
            MonthlySummaryController controller = (MonthlySummaryController) loader.getController();

            // Set Model
            controller.setModel(manager);

            Stage stage = new Stage();
            stage.setTitle("Annual Summary");
            stage.setScene(new Scene(root, 1100, 800));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
