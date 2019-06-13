package AuxiliaryClasses;

import javafx.beans.property.*;

public class ItemSale {

    public IntegerProperty idsale = new SimpleIntegerProperty();
    public StringProperty nombre = new SimpleStringProperty();
    public IntegerProperty quantity = new SimpleIntegerProperty();
    public DoubleProperty price = new SimpleDoubleProperty();

    public IntegerProperty idsaleProperty() {
        return idsale;
    }
    public StringProperty nombreProperty() {
        return nombre;
    }
    public IntegerProperty quantityProperty() {
        return quantity;
    }
    public DoubleProperty priceProperty() {
        return price;
    }

    public ItemSale() {

    }
}
