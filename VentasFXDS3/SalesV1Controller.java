package VentasFXDS3;

// Nombre del artículo, precio, cantidad, importe total.

import AuxiliaryClasses.Sale;
import Resources.ConnectionMethods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class SalesV1Controller {

    private static Sale selectedSale;
    private ConnectionMethods connection;
    private LocalDate nowDate = LocalDate.now();
    private DatePicker datePicker;

    @FXML
    private GridPane gridPane;

    @FXML
    private ComboBox empNoCombo;

    @FXML
    private ComboBox branchNoCombo;

    @FXML
    private TextField amountTextField;

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
        int empNo = (Integer) (empNoCombo.getValue());
        String branchNo = branchNoCombo.getValue().toString();
        double amount = Double.parseDouble(amountTextField.getText());
        LocalDate saleDate = datePicker.getValue();

        connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
        String sql = String.format("INSERT INTO sales(empno, branchno, amount, saledate) " +
                "VALUES(%d, '%s', %f, '%s');", empNo, branchNo, amount, saleDate);

        try {
            connection.executeUpdate(sql);

            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Registro insertado");
            dialog.setHeaderText(null);
            dialog.setContentText("El registro ha sido insertado correctamente.");
            dialog.showAndWait();

            connection.close();
        } catch (SQLException sqlEx) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de actualización");
            dialog.setHeaderText(null);
            dialog.setContentText("No ha sido posible insertar el registro. Excepción: " + sqlEx.toString());
            dialog.showAndWait();

        }

        updateTable();
    }

    @FXML
    private void modButtonPressed(ActionEvent event) {
        if (modButton.getText().equals("Modificar")) {

            amountTextField.setText(amountTextField.getPromptText());
            datePicker.setDisable(false);

            modButton.setText("Guardar");
            clearButton.setText("Cancelar");

        } else {

            int empNo = (Integer) (empNoCombo.getValue());
            String branchNo = branchNoCombo.getValue().toString();
            double amount = Double.parseDouble(amountTextField.getText());
            int saleID = selectedSale.idsaleProperty().getValue();
            LocalDate saleDate = datePicker.getValue();

            connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
            String sql = String.format("UPDATE sales SET empno = %d, branchno = '%s', amount = %f, saledate = '%s'" +
                    " WHERE idsale = %d;", empNo, branchNo, amount, saleDate, saleID);

            try {
                connection.executeUpdate(sql);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Registro actualizado");
                dialog.setHeaderText(null);
                dialog.setContentText("El registro ha sido modificado correctamente.");
                dialog.showAndWait();

                connection.close();
                clearButtonPressed(new ActionEvent());
            } catch (SQLException sqlEx) {

                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error de actualización");
                dialog.setHeaderText(null);
                dialog.setContentText("No ha sido posible modificar la información del registro. Excepción: "
                        + sqlEx.toString());
                dialog.showAndWait();

            }

            updateTable();
        }
    }

    @FXML
    private void delButtonPressed(ActionEvent event) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar registro");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro de que desea eliminar este registro?");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.OK) {
            connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
            String sql = String.format("DELETE FROM sales WHERE idsale = %d;",
                    selectedSale.idsaleProperty().getValue());

            try {
                connection.executeUpdate(sql);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Registro eliminado");
                dialog.setHeaderText(null);
                dialog.setContentText("El registro ha sido eliminado correctamente.");
                dialog.showAndWait();

                connection.close();
                clearButtonPressed(new ActionEvent());
            } catch (SQLException sqlEx) {

                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error de actualización");
                dialog.setHeaderText(null);
                dialog.setContentText("No ha sido posible eliminar el registro. Excepción: " + sqlEx.toString());
                dialog.showAndWait();

            }

        } else {

            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Eliminar registro");
            dialog.setHeaderText(null);
            dialog.setContentText("No se ha realizado ningún cambio.");
            dialog.showAndWait();

        }

        updateTable();
    }

    @FXML
    private void clearButtonPressed(ActionEvent event) {
        empNoCombo.getSelectionModel().clearSelection();
        branchNoCombo.getSelectionModel().clearSelection();
        amountTextField.clear();
        datePicker.setValue(null);
        tableView.getSelectionModel().clearSelection();

        empNoCombo.setPromptText("Empleado");
        branchNoCombo.setPromptText("Sucursal");
        amountTextField.setPromptText("Importe");
        datePicker.setValue(nowDate);
        datePicker.setDisable(true);

        modButton.setText("Modificar");
        clearButton.setText("Limpiar");
    }

    @FXML
    private void initialize() {
        updateCombos();
        updateTable();
        modButton.setDisable(true);
        delButton.setDisable(true);

        datePicker = new DatePicker();
        gridPane.add(datePicker, 1, 3);
        datePicker.setDisable(true);
        datePicker.setValue(nowDate);

        amountTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue.matches("\\d{0,13}([.]\\d{0,2})?")) {
                amountTextField.setText(oldValue);
            } else if (!newValue.isEmpty()) {
                amountTextField.setText(newValue.trim());
            }

        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable) -> {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                selectedSale = (Sale) (tableView.getSelectionModel().getSelectedItem());

                empNoCombo.getSelectionModel().select(Integer.valueOf(getEmployeeNumber(selectedSale.nameProperty().getValue())));
                branchNoCombo.getSelectionModel().select(selectedSale.branchnoProperty().getValue());
                amountTextField.setPromptText(selectedSale.amountProperty().getValue().toString());
                datePicker.setValue(selectedSale.saledateProperty().getValue());

                modButton.setDisable(false);
                delButton.setDisable(false);

            } else {

                modButton.setDisable(true);
                delButton.setDisable(true);
                selectedSale = null;

            }
        });
    }

    private void updateCombos() {
        empNoCombo.getItems().clear();
        branchNoCombo.getItems().clear();

        try {
            connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
            ResultSet resultSet = connection.executeQuery("SELECT empno FROM employees;");

            while (resultSet.next()) {
                empNoCombo.getItems().add(resultSet.getInt("empno"));
            }

            resultSet.close();
            resultSet = connection.executeQuery("SELECT branchno FROM branch;");

            while (resultSet.next()) {
                branchNoCombo.getItems().add(resultSet.getString("branchno"));
            }

            resultSet.close();
            connection.close();
        } catch (SQLException e) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de conexión");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error al obtener información. Excepción: " + e.toString());
            dialog.showAndWait();

        }

    }

    private void updateTable() {
        tableView.getColumns().clear();
        tableView.getItems().clear();

        connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);

        try {
            String sql = String.format("SELECT * FROM sales_view;");
            ResultSet resultSet = connection.executeQuery(sql);

            if (resultSet != null) {
                ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(resultSet));

                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (resultSet.getMetaData().getColumnName(i + 1)) {
                        case "idsale":
                            column.setText("ID");
                            break;
                        case "name":
                            column.setText("Nombre de empleado");
                            break;
                        case "branchno":
                            column.setText("# sucursal");
                            break;
                        case "address":
                            column.setText("Dirección");
                            break;
                        case "amount":
                            column.setText("Monto");
                            break;
                        case "saledate":
                            column.setText("Fecha");
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
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error inesperado. Excepción: " + sqlEx.toString());
            dialog.setTitle("Error");
            dialog.showAndWait();

        }
    }

    private ArrayList<Sale> dataBaseArrayList(ResultSet resultSet) {
        try {
            ArrayList<Sale> data = new ArrayList<>();

            while (resultSet.next()) {
                Sale sale = new Sale();
                sale.idsaleProperty().set(resultSet.getInt("idsale"));
                sale.branchnoProperty().set(resultSet.getString("branchno"));
                sale.amountProperty().set(resultSet.getDouble("amount"));
                sale.nameProperty().set(resultSet.getString("name"));
                sale.saledateProperty().set(resultSet.getObject("saledate", LocalDate.class));
                sale.addressProperty().set(resultSet.getString("address"));

                data.add(sale);
            }

            return data;
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de conexión");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error de conexión. Excepción: " + sqlEx.toString());
            dialog.showAndWait();

            return null;
        }
    }

    private int getEmployeeNumber(String name) {
        int empNo = 0;

        try {
            connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
            String sql = String.format("SELECT empno FROM employees_view WHERE name = '%s' LIMIT 1;", name);

            ResultSet resultSet = connection.executeQuery(sql);

            if (resultSet.next()) {
                empNo = resultSet.getInt("empno");
            }

            connection.close();
        } catch (SQLException sqlEx) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error inesperado.");
            dialog.showAndWait();

        }

        return empNo;
    }
}
