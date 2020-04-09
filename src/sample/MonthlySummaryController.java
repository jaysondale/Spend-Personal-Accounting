package sample;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class MonthlySummaryController implements Initializable {

    private TransactionAnalyzerManager manager;

    // Connect FXML Elements
    @FXML private TableView mainTable;
    @FXML private TableColumn<AnnualSummary, String> catCol;
    @FXML private TableColumn<AnnualSummary, Double> janCol, febCol, marCol, aprCol, mayCol, junCol, julCol, augCol, sepCol, octCol, novCol, decCol, totCol;
    @FXML private ComboBox<Integer> yearComboBox;

    // Structure: Year -> Categories -> Months -> Balances
    private HashMap<Integer, HashMap<String, HashMap<Integer, Double>>> totals;

    private ObservableList<Integer> years;

    /**
     * Set system model object
     * @param manager Manager model object
     */
    public void setModel(TransactionAnalyzerManager manager)
    {
        this.manager = manager;
        computeTotals();

        // Set and sort years list
        years.addAll(totals.keySet());

        // Sorts by increasing value
        Comparator<Integer> comparator = (o1, o2) -> (o1 > o2 ? o1 : o2);

        FXCollections.sort(years, comparator);

        // Set years combo box elements
        yearComboBox.setItems(years);
        yearComboBox.getSelectionModel().select(0);
        updateTable();
    }

    /**
     * Update table to show content from selected year
     */
    public void updateTable()
    {
        Integer selectedYear = yearComboBox.getSelectionModel().getSelectedItem();
        Set<String> catSet = totals.get(selectedYear).keySet();
        ObservableList<AnnualSummary> displayList = FXCollections.observableArrayList();

        for (String category : catSet) {
            displayList.add(new AnnualSummary(selectedYear, category));
        }

        mainTable.setItems(displayList);
    }

    /**
     * Computes annual totals for each category
     */
    private void computeTotals()
    {
        HashMap<String, ObservableList<Transaction>> categoryMap = manager.getCategories();
        for (String category : categoryMap.keySet()) {
            for (Transaction t : categoryMap.get(category)) {
                try {
                    // Update totals
                    Date transactionDate = manager.DateFormat.parse(t.getDate());
                    LocalDate localDate = transactionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (totals.containsKey(localDate.getYear())) {
                        if (totals.get(localDate.getYear()).containsKey(category)){
                            double balance = (totals.get(localDate.getYear()).get(category).containsKey(localDate.getMonthValue()) ? totals.get(localDate.getYear()).get(category).get(localDate.getMonthValue()) : 0.0);
                            totals.get(localDate.getYear()).get(category).put(localDate.getMonthValue(), balance + t.getDebit() - t.getCredit());
                        } else {
                            // Add new category to year
                            HashMap<Integer, Double> monthMap = new HashMap();
                            monthMap.put(localDate.getMonthValue(), t.getDebit() - t.getCredit());
                            totals.get(localDate.getYear()).put(category, monthMap);
                        }

                    } else {
                        // Add new year to totals
                        // TODO: Make this more efficient!
                        HashMap<String, HashMap<Integer, Double>> catMap = new HashMap();
                        HashMap<Integer, Double> monthMap = new HashMap();
                        monthMap.put(localDate.getMonthValue(), t.getDebit() - t.getCredit());
                        catMap.put(category, monthMap);
                        totals.put(localDate.getYear(), catMap);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        totals = new HashMap<>();
        years = FXCollections.observableArrayList();

        // Set columns
        catCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        janCol.setCellValueFactory(new PropertyValueFactory<>("janBal"));
        febCol.setCellValueFactory(new PropertyValueFactory<>("febBal"));
        marCol.setCellValueFactory(new PropertyValueFactory<>("marBal"));
        aprCol.setCellValueFactory(new PropertyValueFactory<>("aprBal"));
        mayCol.setCellValueFactory(new PropertyValueFactory<>("mayBal"));
        junCol.setCellValueFactory(new PropertyValueFactory<>("junBal"));
        julCol.setCellValueFactory(new PropertyValueFactory<>("julBal"));
        augCol.setCellValueFactory(new PropertyValueFactory<>("augBal"));
        sepCol.setCellValueFactory(new PropertyValueFactory<>("sepBal"));
        octCol.setCellValueFactory(new PropertyValueFactory<>("octBal"));
        novCol.setCellValueFactory(new PropertyValueFactory<>("novBal"));
        decCol.setCellValueFactory(new PropertyValueFactory<>("decBal"));
        totCol.setCellValueFactory(new PropertyValueFactory<>("totBal"));

        // Set column formatting
        ObservableList<TableColumn> colList = FXCollections.observableArrayList(janCol, febCol, marCol, aprCol, mayCol, junCol, julCol, augCol, sepCol, octCol, novCol, decCol, totCol);
        for (TableColumn col : colList) {
            col.setCellFactory(tableColumn -> new TableCell<AnnualSummary, Double>() {
                @Override
                protected void updateItem(Double price, boolean empty) {
                    super.updateItem(price, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(TransactionAnalyzerManager.CurrencyFormat.format(price));
                    }
                }
            });
        }
    }

    /**
     * AnnualSummary
     * Used to display monthly data on the main table view
     */
    public class AnnualSummary {

        private final SimpleStringProperty categoryName;
        private final SimpleDoubleProperty janBal, febBal, marBal, aprBal, mayBal, junBal, julBal, augBal, sepBal, octBal, novBal, decBal, totBal;

        /**
         * Constructor
         * @param year Year to extract data from
         * @param category Category to extract data from
         */
        public AnnualSummary(int year, String category) {

            // Initialize all to 0
            categoryName = new SimpleStringProperty(category);
            janBal = new SimpleDoubleProperty(0.0);
            febBal = new SimpleDoubleProperty(0.0);
            marBal = new SimpleDoubleProperty(0.0);
            aprBal = new SimpleDoubleProperty(0.0);
            mayBal = new SimpleDoubleProperty(0.0);
            junBal = new SimpleDoubleProperty(0.0);
            julBal = new SimpleDoubleProperty(0.0);
            augBal = new SimpleDoubleProperty(0.0);
            sepBal = new SimpleDoubleProperty(0.0);
            octBal = new SimpleDoubleProperty(0.0);
            novBal = new SimpleDoubleProperty(0.0);
            decBal = new SimpleDoubleProperty(0.0);
            totBal = new SimpleDoubleProperty(0.0);


            if (!totals.containsKey(year)) {
                System.out.println("Invalid year: " + year);
                return;
            } else if (!totals.get(year).containsKey(category)) {
                System.out.println("Invalid category: " + category + " for year: " + year);
                return;
            }

            HashMap<Integer, Double> monthMap = totals.get(year).get(category);

            for (Integer key : monthMap.keySet()) {
                // Increment each month accordingly
                switch (key){
                    case 1:
                        janBal.set(monthMap.get(key));
                        break;
                    case 2:
                        febBal.set(monthMap.get(key));
                        break;
                    case 3:
                        marBal.set(monthMap.get(key));
                        break;
                    case 4:
                        aprBal.set(monthMap.get(key));
                        break;
                    case 5:
                        mayBal.set(monthMap.get(key));
                        break;
                    case 6:
                        julBal.set(monthMap.get(key));
                        break;
                    case 7:
                        julBal.set(monthMap.get(key));
                        break;
                    case 8:
                        augBal.set(monthMap.get(key));
                        break;
                    case 9:
                        sepBal.set(monthMap.get(key));
                        break;
                    case 10:
                        octBal.set(monthMap.get(key));
                        break;
                    case 11:
                        novBal.set(monthMap.get(key));
                        break;
                    case 12:
                        decBal.set(monthMap.get(key));
                        break;
                }

                // Increment total
                totBal.set(totBal.get() + monthMap.get(key));
            }
        }

        // Getters


        public double getAugBal() {
            return augBal.get();
        }

        public double getFebBal() {
            return febBal.get();
        }

        public double getJanBal() {
            return janBal.get();
        }

        public double getJulBal() {
            return julBal.get();
        }

        public double getDecBal() {
            return decBal.get();
        }

        public double getJunBal() {
            return junBal.get();
        }

        public double getMarBal() {
            return marBal.get();
        }

        public double getMayBal() {
            return mayBal.get();
        }

        public double getNovBal() {
            return novBal.get();
        }

        public double getOctBal() {
            return octBal.get();
        }

        public double getSepBal() {
            return sepBal.get();
        }

        public String getCategoryName() {
            return categoryName.get();
        }

        public double getTotBal() {
            return totBal.get();
        }

        public double getAprBal() {
            return aprBal.get();
        }
    }
}
