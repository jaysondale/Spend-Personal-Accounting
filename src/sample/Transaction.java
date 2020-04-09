package sample;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Transaction {

    private final SimpleStringProperty id;
    private final SimpleStringProperty date;
    private final SimpleDoubleProperty debit;
    private final SimpleDoubleProperty credit;


    public Transaction(String date, String ID, double debit, double credit) {
        this.id = new SimpleStringProperty(ID);
        this.date = new SimpleStringProperty(date);
        this.debit = new SimpleDoubleProperty(debit);
        this.credit = new SimpleDoubleProperty(credit);
    }

    public String getId() {
        return id.get();
    }

    public String getDate() {
        return date.get();
    }

    public Double getDebit() {
        return debit.get();
    }

    public Double getCredit() {
        return credit.get();
    }
}
