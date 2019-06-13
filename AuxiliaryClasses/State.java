package AuxiliaryClasses;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class State {

    public IntegerProperty idstate = new SimpleIntegerProperty();
    public StringProperty statekey = new SimpleStringProperty();
    public StringProperty statename = new SimpleStringProperty();
    public IntegerProperty idcountry = new SimpleIntegerProperty();

    public IntegerProperty idstateProperty() { return idstate; }
    public StringProperty statekeyProperty() { return statekey; }
    public StringProperty statenameProperty() { return statename; }
    public IntegerProperty idcountryProperty() { return idcountry; }

    public State(int idstate, String statekey, String statename, int idcountry) {
        this.idstate.set(idstate);
        this.statekey.set(statekey);
        this.statename.set(statename);
        this.idcountry.set(idcountry);
    }

    public State() { }

    public State(State state) { }

}
