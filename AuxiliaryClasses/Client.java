package AuxiliaryClasses;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Client {

    public IntegerProperty id = new SimpleIntegerProperty();
    public StringProperty name = new SimpleStringProperty();
    public StringProperty address = new SimpleStringProperty();
    public StringProperty tel_num = new SimpleStringProperty();

    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty addressProperty() { return address; }
    public StringProperty tel_numProperty() { return tel_num; }

    public Client(int id, String name, String address, String tel_num) {
        this.id.set(id);
        this.name.set(name);
        this.address.set(address);
        this.tel_num.set(tel_num);
    }

    public Client() { }

    public Client(Client client) { }

}