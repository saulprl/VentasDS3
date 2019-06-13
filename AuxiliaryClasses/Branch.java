package AuxiliaryClasses;

import javafx.beans.property.*;
import org.jetbrains.annotations.Contract;

public class Branch {

    public StringProperty branchno = new SimpleStringProperty();
    public StringProperty address = new SimpleStringProperty();
    public IntegerProperty owner = new SimpleIntegerProperty();

    public StringProperty branchnoProperty() { return branchno; }
    public StringProperty addressProperty() { return address; }
    public IntegerProperty ownerProperty() { return owner; }

    public Branch(String branch, String address, int owner) {
        this.branchno.set(branch);
        this.address.set(address);
        this.owner.set(owner);
    }

    public Branch() { }

    public Branch(Branch branch) { }

    @Contract(value = "null -> false", pure = true)
    public boolean equals(Object branch) {
        if (branch instanceof Branch) return true;

        return false;
    }

}
