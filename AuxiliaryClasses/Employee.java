package AuxiliaryClasses;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Employee {

    public IntegerProperty empno = new SimpleIntegerProperty();
    public StringProperty name = new SimpleStringProperty();
    public ObjectProperty<LocalDate> dob = new SimpleObjectProperty<>();
    public StringProperty address = new SimpleStringProperty();
    public StringProperty ssn = new SimpleStringProperty();
    public StringProperty branchno = new SimpleStringProperty();
    public DoubleProperty salary = new SimpleDoubleProperty();
    public StringProperty position = new SimpleStringProperty();
    public ObjectProperty<LocalDate> started = new SimpleObjectProperty<>();
    public StringProperty phone = new SimpleStringProperty();

    public IntegerProperty empnoProperty() { return empno; }
    public StringProperty nameProperty() { return name; }
    public ObjectProperty<LocalDate> dobProperty() { return dob; }
    public StringProperty addressProperty() { return address; }
    public StringProperty ssnProperty() { return ssn; }
    public StringProperty branchnoProperty() { return branchno; }
    public DoubleProperty salaryProperty() { return salary; }
    public StringProperty positionProperty() { return position; }
    public ObjectProperty<LocalDate> startedProperty() { return started; }
    public StringProperty phoneProperty() { return phone; }

    public Employee(int id, String name, LocalDate dob, String address, String branch, double salary, String position,
                    String phone, LocalDate started) {
        this.empno.set(id);
        this.name.set(name);
        this.dob.set(dob);
        this.address.set(address);
        this.branchno.set(branch);
        this.salary.set(salary);
        this.position.set(position);
        this.started.set(started);
        this.phone.set(phone);
    }

    public Employee() { }

    public Employee(Employee employee) { }

}
