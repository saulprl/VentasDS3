package VentasFXDS3;

import AuxiliaryClasses.Branch;
import Resources.ConnectionMethods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BranchController {

    private ConnectionMethods connection;
    private static Branch selectedBranch;

    @FXML
    private TextField branchTextField;

    @FXML
    private ComboBox cityComboBox;

    @FXML
    private TextField streetTextField;

    @FXML
    private TextField homeTextField;

    @FXML
    private TextField postalTextField;

    @FXML
    private ComboBox ownerComboBox;

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

    @FXML
    private void registerButtonPressed(ActionEvent event) {
        String branch = String.format("B%s", branchTextField.getText());
        String city = cityComboBox.getSelectionModel().getSelectedItem().toString().split("\\(")[0].trim();
        String street = streetTextField.getText();
        String number = homeTextField.getText();
        String postal = postalTextField.getText();
        String address = String.format("'%s', '%s', '%s, '%s'", city, street, number, postal);
        String owner = ownerComboBox.getSelectionModel().getSelectedItem().toString();

        connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);

        try {

            String sql = String.format("INSERT INTO branch(branchno, address, owner) VALUES('%s', '%s', '%s');",
                    branch, address, owner);
            connection.executeUpdate(sql);

            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Registro insertado");
            dialog.setContentText("El registro ha sido insertado correctamente.");
            dialog.setHeaderText(null);
            dialog.showAndWait();

            clearButtonPressed(new ActionEvent());

        } catch (SQLException e) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de registro");
            dialog.setContentText("No fue posible insertar el registro. Excepción: " + e.toString());
            dialog.setHeaderText(null);
            dialog.showAndWait();

        }

        updateTable();
    }

    @FXML
    private void modButtonPressed(ActionEvent event) {
        if (modButton.getText().equals("Modificar")) {

            modButton.setText("Guardar");
            clearButton.setText("Cancelar");

            branchTextField.setText(branchTextField.getPromptText());
            branchTextField.setDisable(true);
            streetTextField.setText(streetTextField.getPromptText());
            homeTextField.setText(homeTextField.getPromptText());
            postalTextField.setText(postalTextField.getPromptText());

        } else {

            String city = cityComboBox.getSelectionModel().getSelectedItem().toString().split("\\(")[0].trim();
            String street = streetTextField.getText();
            String number = homeTextField.getText();
            String postal = postalTextField.getText();
            String address = String.format("%s, %s, %s, %s", city, street, number, postal);

            try {
                connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
                String sql = String.format("UPDATE branch SET address = '%s', owner = '%s' WHERE branchno = '%s';",
                        address, ownerComboBox.getSelectionModel().getSelectedItem().toString(),
                        selectedBranch.branchnoProperty().getValue());

                connection.executeUpdate(sql);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Registro modificado");
                dialog.setContentText("La información del registro se ha actualizado correctamente.");
                dialog.setHeaderText(null);
                dialog.showAndWait();

                branchTextField.setDisable(false);
                clearButtonPressed(new ActionEvent());

            } catch (SQLException e) {

                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error de actualización");
                dialog.setHeaderText(null);
                dialog.setContentText("No fue posible actualizar el regsitro. Excepción: " + e.toString());
                dialog.showAndWait();

            }

            updateTable();
        }
    }

    @FXML
    private void delButtonPressed(ActionEvent event) {

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Eliminar registro");
        dialog.setContentText("¿Seguro de que desea eliminar este registro?");
        dialog.setHeaderText(null);
        dialog.showAndWait();

        ButtonType result = dialog.getResult();

        if (result == ButtonType.OK) {
            try {

                connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
                String sql = String.format("DELETE FROM branch WHERE branchno = '%s';",
                        selectedBranch.branchnoProperty().getValue());

                connection.executeUpdate(sql);

                Alert dialog2 = new Alert(Alert.AlertType.INFORMATION);
                dialog2.setTitle("Registro eliminado");
                dialog2.setContentText("El registro ha sido eliminado correctamente.");
                dialog2.setHeaderText(null);
                dialog2.showAndWait();

            } catch (SQLException e) {

                Alert dialog2 = new Alert(Alert.AlertType.ERROR);
                dialog2.setTitle("Error");
                dialog2.setContentText("No ha sido posible eliminar el registro seleccionado. Excepción: "
                        + e.toString());
                dialog2.setHeaderText(null);
                dialog2.showAndWait();

            }

            updateTable();
        } else {

            Alert dialog2 = new Alert(Alert.AlertType.INFORMATION);
            dialog2.setTitle("Eliminar registro");
            dialog2.setHeaderText(null);
            dialog2.setContentText("No se ha hecho ningún cambio.");
            dialog2.showAndWait();

        }
    }

    @FXML
    private void clearButtonPressed(ActionEvent event) {
        modButton.setText("Modificar");
        clearButton.setText("Limpiar");

        branchTextField.setDisable(false);
        branchTextField.clear();
        branchTextField.setPromptText("#");
        cityComboBox.getSelectionModel().select(null);
        cityComboBox.setPromptText("Ciudad");
        streetTextField.clear();
        streetTextField.setPromptText("Calle");
        homeTextField.clear();
        homeTextField.setPromptText("#");
        postalTextField.clear();
        postalTextField.setPromptText("Código postal");
        ownerComboBox.getSelectionModel().select(null);
        ownerComboBox.setPromptText("Empleado");

        tableView.getSelectionModel().select(null);

    }

    @FXML
    private void initialize() {
        updateTable();
        updateCombos();

        branchTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue.matches("\\d{0,3}")) {
                branchTextField.setText(oldValue);
            } else if (!newValue.isEmpty()) {
                branchTextField.setText(newValue.trim());
            }

        });

        homeTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue.matches("\\d{0,3}([-]\\w{0,2})?")) {
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

        tableView.getSelectionModel().selectedItemProperty().addListener((observable) -> {

            if (tableView.getSelectionModel().getSelectedItem() != null) {
                selectedBranch = (Branch) (tableView.getSelectionModel().getSelectedItem());

                String[] address = selectedBranch.addressProperty().getValue().split(",");

                branchTextField.setPromptText(selectedBranch.branchnoProperty().getValue().split("B")[1].trim());
                cityComboBox.getSelectionModel().select(address[0].trim());
                streetTextField.setPromptText(address[1].trim());
                homeTextField.setPromptText(address[2].trim());
                postalTextField.setPromptText(address[3].trim());
                ownerComboBox.getSelectionModel().select(selectedBranch.ownerProperty().getValue());

            } else {

                selectedBranch = null;

            }

        });
    }

    private void updateCombos() {
        cityComboBox.getItems().clear();
        ownerComboBox.getItems().clear();

        connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
        String sql = String.format("SELECT cityname FROM city;");

        try {
            ResultSet resultSet = connection.executeQuery(sql);

            while (resultSet.next())
                cityComboBox.getItems().add(resultSet.getString("cityname"));

            sql = String.format("SELECT empno FROM employees WHERE position = 'Gerente';");
            resultSet = connection.executeQuery(sql);

            while (resultSet.next())
                ownerComboBox.getItems().add(resultSet.getInt("empno"));

        } catch (SQLException e) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de conexión");
            dialog.setContentText("No ha sido posible recuperar la información. Excepción: " + e.toString());
            dialog.setHeaderText(null);
            dialog.showAndWait();

        }
    }

    private void updateTable() {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);

        try {
            String sql = "SELECT * FROM branch;";
            ResultSet resultSet = connection.executeQuery(sql);

            if (resultSet != null) {
                ObservableList<Branch> data = FXCollections.observableArrayList(dataBaseArrayList(resultSet));

                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (resultSet.getMetaData().getColumnName(i + 1)) {
                        case "branchno":
                            column.setText("ID");
                            break;
                        case "address":
                            column.setText("Dirección");
                            break;
                        case "owner":
                            column.setText("Gerente");
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
        } catch (SQLException e) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de conexión");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error de conexión. Excepción: " + e.toString());
            dialog.showAndWait();

        }

    }

    private ArrayList<Branch> dataBaseArrayList(ResultSet resultSet) {
        try {
            ArrayList<Branch> data = new ArrayList<>();

            while (resultSet.next()) {
                Branch branch = new Branch();

                branch.branchno.set(resultSet.getString("branchno"));
                branch.address.set(resultSet.getString("address"));
                branch.owner.set(resultSet.getInt("owner"));

                data.add(branch);
            }

            return data;
        } catch (SQLException e) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ocurrió un error inesperado. Excepción: " + e.toString());
            dialog.showAndWait();

            return null;
        }
    }

}
