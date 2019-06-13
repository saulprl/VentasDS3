package AuxiliaryClasses;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Sale {

    public IntegerProperty idsale = new SimpleIntegerProperty();
    public IntegerProperty empno = new SimpleIntegerProperty();
    public StringProperty name = new SimpleStringProperty();
    public StringProperty empname = new SimpleStringProperty();
    public StringProperty branchno = new SimpleStringProperty();
    public DoubleProperty amount = new SimpleDoubleProperty();
    public ObjectProperty<LocalDate> saledate = new SimpleObjectProperty<>();
    public IntegerProperty client_id = new SimpleIntegerProperty();
    public StringProperty clientname = new SimpleStringProperty();
    public StringProperty address = new SimpleStringProperty();

    public IntegerProperty idsaleProperty() { return idsale; }
    public IntegerProperty empnoProperty() {
        return empno;
    }
    public StringProperty nameProperty() { return name; }
    public StringProperty empnameProperty() {
        return empname;
    }
    public StringProperty branchnoProperty() { return branchno; }
    public DoubleProperty amountProperty() { return amount; }
    public ObjectProperty<LocalDate> saledateProperty() { return saledate; }
    public IntegerProperty client_idProperty() {
        return client_id;
    }
    public StringProperty clientnameProperty() {
        return clientname;
    }
    public StringProperty addressProperty() { return address; }

    public Sale(int idSale, String name, String branchNo, double amount, LocalDate saleDate, String address) {
        this.idsale.set(idSale);
        this.name.set(name);
        this.branchno.set(branchNo);
        this.amount.set(amount);
        this.saledate.set(saleDate);
        this.address.set(address);
    }

    public Sale() { }

    public Sale(Sale sale) { }

}
