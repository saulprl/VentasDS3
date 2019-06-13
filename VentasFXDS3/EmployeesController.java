//Autor: Saúl Ramos Laborín.
//Expediente: 217200160.
//Clase: Desarrollo de Sistemas 3.

package VentasFXDS3;

import AuxiliaryClasses.Employee;
import Resources.ConnectionMethods;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;

public class EmployeesController {

    // Objeto Employee estático que cambia su valor según el registro seleccionado en la TableView.
    private static Employee selectedEmployee;

    // LocalDate usado para el registro de nuevos empleados.
    private LocalDate date = LocalDate.now();
    // Objeto ConnectionMethods para poder inicializarlo en cualquier parte del programa.
    private ConnectionMethods connection;

    // Objetos de la ventana.
    @FXML
    private GridPane gridPane;

    @FXML
    private TextField nameTextField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox cityComboBox;

    @FXML
    private TextField streetTextField;

    @FXML
    private TextField homeTextField;

    @FXML
    private TextField postalTextField;

    @FXML
    private TextField ssnTextField;

    @FXML
    private ComboBox branchComboBox;

    @FXML
    private ComboBox positionComboBox;

    @FXML
    private TextField phoneTextField;

    @FXML
    private Button registerButton;

    @FXML
    private Button modButton;

    @FXML
    private Button delButton;

    @FXML
    private Button clearButton;

    @FXML
    private TableView tableView;

    // Control del evento de registro.
    @FXML
    private void registerButtonPressed(ActionEvent event) {
        String name = nameTextField.getText();
        LocalDate dob = datePicker.getValue();
        String ssn = ssnTextField.getText();
        String city = cityComboBox.getValue().toString().split("\\(")[0].trim();
        String street = streetTextField.getText();
        String number = homeTextField.getText();
        String postal = postalTextField.getText();
        String address = String.format("%s, %s, %s, %s", city, street, number, postal);
        String branchNo = branchComboBox.getValue().toString();
        String position = positionComboBox.getValue().toString();
        String phone = phoneTextField.getText();

        connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
        String sql = String.format("INSERT INTO employees(name, dob, address, branchno, ssn, position, phone," +
                        " started) VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                name, dob, address, branchNo, ssn, position, phone, date);

        try {
            connection.executeUpdate(sql);

            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Registro insertado");
            dialog.setContentText("El registro ha sido insertado correctamente.");
            dialog.setHeaderText(null);
            dialog.showAndWait();

            clearButtonPressed(new ActionEvent());
            connection.close();
        } catch (SQLException e) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de actualización");
            dialog.setHeaderText(null);
            dialog.setContentText("No ha sido posible insertar el registro. Excepción: " + e.toString());
            dialog.showAndWait();

        }

        updateTable();
    }

    @FXML
    private void modButtonPressed(ActionEvent event) {
        if (modButton.getText().equals("Modificar")) {

            nameTextField.setText(nameTextField.getPromptText());
            streetTextField.setText(streetTextField.getPromptText());
            homeTextField.setText(homeTextField.getPromptText());
            postalTextField.setText(postalTextField.getPromptText());
            phoneTextField.setText(phoneTextField.getPromptText());
            ssnTextField.setText(ssnTextField.getPromptText());

            modButton.setText("Guardar");
            clearButton.setText("Cancelar");

        } else {

            String name = nameTextField.getText();
            LocalDate dob = datePicker.getValue();
            String ssn = ssnTextField.getText();
            String city = cityComboBox.getValue().toString().split("\\(")[0].trim();
            String street = streetTextField.getText();
            String number = homeTextField.getText();
            String postal = postalTextField.getText();
            String address = String.format("%s, %s, %s, %s", city, street, number, postal);
            String branchNo = branchComboBox.getValue().toString();
            String position = positionComboBox.getValue().toString();
            String phone = phoneTextField.getText();
            int empNo = selectedEmployee.empnoProperty().getValue();

            connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
            String sql = String.format("UPDATE employees SET name = '%s', dob = '%s', address = '%s', " +
                            "branchno = '%s', position = '%s', phone = '%s', ssn = '%s' WHERE empno = %d", name,
                    dob, address, branchNo, position, phone, ssn, empNo);

            try {
                connection.executeUpdate(sql);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Registro actualizado");
                dialog.setHeaderText(null);
                dialog.setContentText("La información del registro se ha actualizado correctamente.");
                dialog.showAndWait();

                clearButtonPressed(new ActionEvent());

            } catch (SQLException e) {

                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error de actualización");
                dialog.setContentText("No ha sido posible actualizar la información del registro. Excepción: "
                        + e.toString());
                dialog.setHeaderText(null);
                dialog.showAndWait();

            }

            updateTable();
        }
    }

    @FXML
    private void delButtonPressed(ActionEvent event) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar empleado");
        confirm.setContentText("¿Seguro de que desea eliminar este registro?");
        confirm.setHeaderText(null);
        confirm.showAndWait();


        if (confirm.getResult() == ButtonType.OK) {

            connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
            String sql = String.format("DELETE FROM employees WHERE empno = '%d';",
                    selectedEmployee.empnoProperty().getValue());

            try {

                connection.executeUpdate(sql);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Registro eliminado");
                dialog.setContentText("El registro se ha eliminado correctamente.");
                dialog.setHeaderText(null);
                dialog.showAndWait();

                clearButtonPressed(new ActionEvent());
            } catch (SQLException e) {

                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error de actualización");
                dialog.setContentText("No ha sido posible eliminar el registro. Excepción: " + e.toString());
                dialog.setHeaderText(null);
                dialog.showAndWait();

            }

        } else {

            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Eliminar empleado");
            dialog.setContentText("No se ha realizado ningún cambio.");
            dialog.setHeaderText(null);
            dialog.showAndWait();

        }
        updateTable();
    }

    @FXML
    private void clearButtonPressed(ActionEvent event) {
        nameTextField.clear();
        datePicker.setValue(null);
        cityComboBox.getSelectionModel().select(null);
        cityComboBox.setPromptText("Ciudad");
        streetTextField.clear();
        homeTextField.clear();
        postalTextField.clear();
        ssnTextField.clear();
        branchComboBox.getSelectionModel().select(null);
        branchComboBox.setPromptText("Sucursal");
        positionComboBox.getSelectionModel().select(null);
        positionComboBox.setPromptText("Puesto");
        phoneTextField.clear();
        tableView.getSelectionModel().clearSelection();

        nameTextField.setPromptText("Nombre");
        streetTextField.setPromptText("Calle");
        homeTextField.setPromptText("#");
        postalTextField.setPromptText("Código postal");
        ssnTextField.setPromptText("NSS");
        phoneTextField.setPromptText("Teléfono");

        clearButton.setText("Limpiar");
        modButton.setText("Modificar");
    }

    @FXML
    private void initialize() {
        updateCombos();
        updateTable();

        datePicker = new DatePicker();
        gridPane.add(datePicker, 1, 1);

        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue.length() > 40) {
                nameTextField.setText(oldValue);
            }

        });

        homeTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue.matches("\\d{0,3}([-]\\w?)?")) {
                homeTextField.setText(oldValue);
            } else if (!newValue.isEmpty()) {
                homeTextField.setText(newValue.trim());
            }

        });

        postalTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue.matches("\\d{0,6}")) {
                postalTextField.setText(oldValue);
            } else if (!newValue.isEmpty()) {
                postalTextField.setText(newValue.trim());
            }

        });

        phoneTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue.matches("\\d{0,10}")) {
                phoneTextField.setText(oldValue);
            } else if (!newValue.isEmpty()) {
                phoneTextField.setText(newValue.trim());
            }

        });

        ssnTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue.matches("\\d{0,11}")) {
                ssnTextField.setText(oldValue);
            } else if (!newValue.isEmpty()) {
                ssnTextField.setText(newValue.trim());
            }

        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable) -> {

            if (tableView.getSelectionModel().getSelectedItem() != null) {

                selectedEmployee = (Employee) (tableView.getSelectionModel().getSelectedItem());

                String city = selectedEmployee.addressProperty().getValue().split(",")[0].trim();
                String street = selectedEmployee.addressProperty().getValue().split(",")[1].trim();
                String number = selectedEmployee.addressProperty().getValue().split(",")[2].trim();
                String postal = selectedEmployee.addressProperty().getValue().split(",")[3].trim();

                nameTextField.setPromptText(selectedEmployee.nameProperty().getValue());
                datePicker.setValue(selectedEmployee.dobProperty().getValue());
                cityComboBox.getSelectionModel().select(city);
                streetTextField.setPromptText(street);
                homeTextField.setPromptText(number);
                postalTextField.setPromptText(postal);
                branchComboBox.getSelectionModel().select(selectedEmployee.branchnoProperty().getValue());
                positionComboBox.getSelectionModel().select(selectedEmployee.positionProperty().getValue());
                phoneTextField.setPromptText(selectedEmployee.phoneProperty().getValue());
                ssnTextField.setPromptText(selectedEmployee.ssnProperty().getValue());

                modButton.setDisable(false);
                delButton.setDisable(false);

            } else {

                modButton.setDisable(true);
                delButton.setDisable(true);
                selectedEmployee = null;

            }

        });


    }

    private void updateTable() {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);

        try {
            String sql = String.format("SELECT * FROM employees_view;");
            ResultSet resultSet = connection.executeQuery(sql);

            if (resultSet != null) {
                ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(resultSet));

                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (resultSet.getMetaData().getColumnName(i + 1)) {
                        case "empno":
                            column.setText("ID");
                            break;
                        case "name":
                            column.setText("Nombre");
                            break;
                        case "dob":
                            column.setText("Fecha de nacimiento");
                            break;
                        case "address":
                            column.setText("Dirección");
                            break;
                        case "branchno":
                            column.setText("Surcursal");
                            break;
                        case "ssn":
                            column.setText("NSS");
                            break;
                        case "position":
                            column.setText("Puesto");
                            break;
                        case "salary":
                            column.setText("Salario");
                            break;
                        case "phone":
                            column.setText("Teléfono");
                            break;
                        case "started":
                            column.setText("Inicio");
                            break;
                        default:
                            column.setText(resultSet.getMetaData().getColumnName(i + 1));
                            break;
                    }

                    column.setCellValueFactory(new PropertyValueFactory<>(resultSet.getMetaData().getColumnName(i + 1)));
                    tableView.getColumns().add(column);

                }

                tableView.setItems(data);
            }

            resultSet.close();
            connection.close();
        } catch (SQLException sqlEx) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setContentText("Ha ocurrido un error inesperado. Excepción: " + sqlEx.toString());
            dialog.setHeaderText(null);
            dialog.showAndWait();

        }
    }

    private void updateCombos() {
        cityComboBox.getItems().clear();
        branchComboBox.getItems().clear();
        positionComboBox.getItems().clear();

        connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);

        try {
            ResultSet resultSet = connection.executeQuery("SELECT citykey, cityname FROM city;");

            while (resultSet.next()) {
                String city = String.format("%s (%s)", resultSet.getString("cityname"),
                        resultSet.getString("citykey"));

                cityComboBox.getItems().add(city);
            }

            resultSet.close();
            resultSet = connection.executeQuery("SELECT branchno FROM branch;");

            while (resultSet.next()) {
                branchComboBox.getItems().add(resultSet.getString("branchno"));
            }

            resultSet.close();
            resultSet = connection.executeQuery("SELECT position FROM position;");

            while (resultSet.next()) {
                positionComboBox.getItems().add(resultSet.getString("position"));
            }

            resultSet.close();
            connection.close();
        } catch (SQLException sqlEx) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de conexión");
            dialog.setContentText("No ha sido posible conectarse a la base de datos. Excepción: " + sqlEx.toString());
            dialog.setHeaderText(null);
            dialog.showAndWait();

        }
    }

    private ArrayList dataBaseArrayList(ResultSet resultSet) {
        try {
            ArrayList<Employee> data = new ArrayList<>();

            while (resultSet.next()) {
                Employee employee = new Employee();

                employee.empno.set(resultSet.getInt("empno"));
                employee.name.set(resultSet.getString("name"));
                employee.dob.set(resultSet.getObject("dob", LocalDate.class));
                employee.address.set(resultSet.getString("address"));
                employee.branchno.set(resultSet.getString("branchno"));
                employee.ssn.set(resultSet.getString("ssn"));
                employee.position.set(resultSet.getString("position"));
                employee.salary.set(resultSet.getDouble("salary"));
                employee.phone.set(resultSet.getString("phone"));
                employee.started.set(resultSet.getObject("started", LocalDate.class));

                data.add(employee);
            }

            return data;
        } catch (SQLException sqlEx) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setContentText("Ocurrió un error inesperado. Excepción: " + sqlEx.toString());
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.showAndWait();

            return null;

        }
    }

}
