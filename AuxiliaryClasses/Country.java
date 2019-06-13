package AuxiliaryClasses;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Country {

    public IntegerProperty idcountry = new SimpleIntegerProperty();
    public StringProperty countrykey = new SimpleStringProperty();
    public StringProperty countryname = new SimpleStringProperty();

    public IntegerProperty idcountryProperty() { return idcountry; }
    public StringProperty countrykeyProperty() { return countrykey; }
    public StringProperty countrynameProperty() { return countryname; }

    public Country(int idValue, String keyValue, String nameValue) {
        idcountry.set(idValue);
        countrykey.set(keyValue);
        countryname.set(nameValue);
    }

    public Country() { }

    public Country(Country country) { }

}
