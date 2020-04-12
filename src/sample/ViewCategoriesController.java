package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.HashMap;

public class ViewCategoriesController {

    @FXML private ListView<String> catListView;
    @FXML private ListView<String> ssListView;

    private ObservableList<String> substrings;

    private TransactionAnalyzerManager manager;

    /**
     * Set manager model and populate transaction list
     * @param manager Manager instance
     */
    public void setModel(TransactionAnalyzerManager manager) {
        this.manager = manager;
        catListView.setItems(manager.getCategoryNames());
        catListView.getSelectionModel().selectFirst();
        updateSsList();
    }

    /**
     * Update list view to contain substrings mapped to selected category
     */
    public void updateSsList() {
        String selectedCat = catListView.getSelectionModel().getSelectedItem();
        substrings = FXCollections.observableArrayList();
        HashMap<String, String> ssMap = manager.getSsMap();
        for (String substring : ssMap.keySet()) {
            String category = ssMap.get(substring);
            if (category.equals(selectedCat)) {
                substrings.add(substring);
            }
        }
        ssListView.setItems(substrings);
    }
}
