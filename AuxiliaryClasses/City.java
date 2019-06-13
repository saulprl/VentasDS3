package AuxiliaryClasses;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class City {

    public IntegerProperty idcity = new SimpleIntegerProperty();
    public StringProperty citykey = new SimpleStringProperty();
    public StringProperty cityname = new SimpleStringProperty();
    public IntegerProperty idstate = new SimpleIntegerProperty();

    public IntegerProperty idcityProperty() { return idcity; }
    public StringProperty citykeyProperty() { return citykey; }
    public StringProperty citynameProperty() { return cityname; }
    public IntegerProperty idstateProperty() { return idstate; }

    public City(int idcity, String key, String name, int idstate) {
        this.idcity.set(idcity);
        citykey.set(key);
        cityname.set(name);
        this.idstate.set(idstate);
    }

    public City() { }

    public City(City city) { }

}
