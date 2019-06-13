package AuxiliaryClasses;

import javafx.beans.property.*;

public class Item {

    public IntegerProperty idproducto = new SimpleIntegerProperty();
    public StringProperty nombre = new SimpleStringProperty();
    public DoubleProperty precio = new SimpleDoubleProperty();
    public IntegerProperty cantidad = new SimpleIntegerProperty();
    public StringProperty activo = new SimpleStringProperty();

    public IntegerProperty idproductoProperty() { return idproducto; }
    public StringProperty nombreProperty() { return nombre; }
    public DoubleProperty precioProperty() { return precio; }
    public IntegerProperty cantidadProperty() { return cantidad; }
    public StringProperty activoProperty() { return activo; }

    public Item(int id, String name, double price, int quantity, String active) {
        idproducto.set(id);
        nombre.set(name);
        precio.set(price);
        cantidad.set(quantity);
        activo.set(active);
    }

    public Item() { }

    public Item(Item item) { }

}
