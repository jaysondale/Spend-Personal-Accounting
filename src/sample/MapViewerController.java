package sample;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MapViewerController implements Initializable {

    // FXML Links
    @FXML private TableView<Mapping> mapTableView;
    @FXML private TableColumn<Mapping, String> keyCol;
    @FXML private TableColumn<Mapping, String> catCol;
    @FXML private ComboBox<String> catComboBox;

    private ObservableList<Mapping> mapList;
    private ObservableList<String> categoryNames;

    private TransactionAnalyzerManager manager;

    private boolean isSsMap;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mapList = FXCollections.observableArrayList();

        keyCol.setCellValueFactory(new PropertyValueFactory<>("key"));
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        mapTableView.setRowFactory(tv -> {
            TableRow<Mapping> row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                editMapping();
            }
        });
        return row ;
        });
    }

    public void initCatList() {
        // Initialize category list
        categoryNames = FXCollections.observableArrayList();
        categoryNames.add("All Categories");
        categoryNames.addAll(manager.getCategoryNames());

        catComboBox.setItems(categoryNames);
        catComboBox.getSelectionModel().select(0);
    }

    public void setModel(TransactionAnalyzerManager manager) { this.manager = manager; initCatList(); }

    public void setSsMap() { isSsMap = true; setMap(); }

    public void setIdMap() { isSsMap = false; setMap(); }

    /**
     * Assigns map to be analyzed
     */
    public void setMap() {
        mapList.removeAll(mapList);
        HashMap<String, String> map;
        if (isSsMap)
        {
            map = manager.getSsMap();

        } else {
            map = manager.getIdMap();
        }
        for (String key : map.keySet()) {
            mapList.add(new Mapping(key, map.get(key)));
        }


        updateTable();
    }

    public void updateTable() {
        String category = catComboBox.getSelectionModel().getSelectedItem();
        if (category != "All Categories") {
            ObservableList<Mapping> focusedList = FXCollections.observableArrayList();
            for (Mapping map : mapList) {
                if (map.getCategory().equals(category)) {
                    focusedList.add(map);
                }
            }
            mapTableView.setItems(focusedList);
        } else {
            mapTableView.setItems(mapList);
        }
        mapTableView.refresh();
    }

    public void editMapping() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditMap.fxml"));
            Parent root = loader.load();
            EditMapController controller = loader.getController();

            // Set Model
            controller.setModel(manager, (isSsMap ? manager.getSsMap() : manager.getIdMap()), this);
            Mapping selected = mapTableView.getSelectionModel().getSelectedItem();
            controller.setMap(selected.getKey(), selected.getCategory());


            Stage stage = new Stage();
            stage.setTitle("View Map");
            stage.setScene(new Scene(root, 350 , 250));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mapping
     * Class used to represent each mapping instance being analyzed
     */
    public class Mapping {
        private final SimpleStringProperty key;
        private final SimpleStringProperty category;

        public Mapping(String key, String category) {
            this.key = new SimpleStringProperty(key);
            this.category = new SimpleStringProperty(category);
        }

        public String getCategory() {
            return category.get();
        }

        public String getKey() {
            return key.get();
        }
    }

}
