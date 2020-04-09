package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class TransactionAnalyzerManager {

    // List containing all categories pulled from the id map
    private ObservableList<String> categoryNames;
    // Current category being viewed
    private String selectedCategory;
    // Mapping each list of transactions to their appropriate category name
    private HashMap<String, ObservableList<Transaction>> categories;

    // ID Mapping
    private HashMap<String, String> idMap; // ID Map
    private HashMap<String, String> ssMap; // Substring Map

    // Source file constants
    public String IDMAP_NAME;
    public String SSMAP_NAME;

    // Default category name
    public final String DEFAULT_CATEGORY = "Unclassified";

    // Standard date format for system
    public static final SimpleDateFormat DateFormat = new SimpleDateFormat("MM/dd/yyyy");

    // Standard currency format
    public static final NumberFormat CurrencyFormat = NumberFormat.getCurrencyInstance();

    public TransactionAnalyzerManager(String idMap, String ssMap) {
        IDMAP_NAME = idMap;
        SSMAP_NAME = ssMap;

        categories = new HashMap<>();
        categoryNames = FXCollections.observableArrayList();
        this.idMap = new HashMap<>();
        this.ssMap = new HashMap<>();
    }

    public HashMap<String, ObservableList<Transaction>> getCategories() {
        return categories;
    }

    public HashMap<String, String> getIdMap() {
        return idMap;
    }

    public HashMap<String, String> getSsMap() {
        return ssMap;
    }

    public ObservableList<String> getCategoryNames() {
        return categoryNames;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setCategories(HashMap<String, ObservableList<Transaction>> categories) {
        this.categories = categories;
    }

    public void setCategoryNames(ObservableList<String> categoryNames) {
        this.categoryNames = categoryNames;
    }

    public void setIdMap(HashMap<String, String> idMap) {
        this.idMap = idMap;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public void setSsMap(HashMap<String, String> ssMap) {
        this.ssMap = ssMap;
    }
}
