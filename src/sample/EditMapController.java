package sample;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashMap;

public class EditMapController {

    private TransactionAnalyzerManager manager;

    private String key;
    private String category;
    private HashMap<String, String> map;

    private MapViewerController mapViewerController;

    // FXML Links
    @FXML ComboBox<String> catComboBox;
    @FXML TextField keyField;

    public void setModel(TransactionAnalyzerManager manager, HashMap<String, String> map, MapViewerController mapViewerController) {
        this.manager = manager;
        this.map = map;
        this.mapViewerController = mapViewerController;

        initElements();
    }

    public void setMap(String key, String category) {
        this.key = key;
        this.category = category;

        keyField.setText(key);
        catComboBox.getSelectionModel().select(category);
    }

    public void initElements() {
        catComboBox.setItems(manager.getCategoryNames());
        catComboBox.getSelectionModel().select(category);
        keyField.setText(key);
    }

    public void saveMap() {
        // Remove old mapping
        map.remove(key);

        key = keyField.getText();
        category = catComboBox.getSelectionModel().getSelectedItem();
        map.put(key, category);

        // Update transaction data set
        HashMap<String, String> map = new HashMap<>();
        map.put(key, category);
        TransactionUtility.applySubstringMap(manager, map);

        // Update map stored in map view controller
        mapViewerController.setMap();

        exit();
    }

    public void exit() {
        Stage stage = (Stage) catComboBox.getScene().getWindow();
        stage.close();
    }
}
