package VentasFXDS3;

import AuxiliaryClasses.Client;
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

public class ClientsController {

    private static Client selectedClient;

    @FXML
    private ComboBox cityComboBox;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField streetTextField;

    @FXML
    private TextField homeTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private TextField postalTextField;

    @FXML
    private TableView tableView;

    @FXML
    private Button modifyButton;

    @FXML
    private Button deleteButton;

    @FXML
    private void registerButtonPressed(ActionEvent event) {
        try {
            ConnectionMethods connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
            String name = nameTextField.getText();
            String phone = phoneTextField.getText();
            String address = String.format("%s, %s, %s, %s", cityComboBox.getValue(), streetTextField.getText(),
                    homeTextField.getText(), postalTextField.getText());

            String sql = String.format("INSERT INTO clients(name, address, tel_num)" +
                    " VALUES('%s', '%s', '%s');", name, address, phone);
            connection.executeUpdate(sql);

            updateCombo();
            updateTable();
            setDefault();

        } catch (SQLException e) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de registro");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error al insertar el registro. Excepción: " + e.toString());
            dialog.showAndWait();
        }
    }

    @FXML
    private void modifyButtonPressed(ActionEvent event) {
        if (nameTextField.getText().isEmpty() || cityComboBox.getValue() == null || streetTextField.getText().isEmpty()
                || homeTextField.getText().isEmpty() || postalTextField.getText().isEmpty() ||
                phoneTextField.getText().isEmpty()) {

            nameTextField.setText(nameTextField.getPromptText());
            cityComboBox.setValue(cityComboBox.getPromptText());
            streetTextField.setText(streetTextField.getPromptText());
            homeTextField.setText(homeTextField.getPromptText());
            postalTextField.setText(postalTextField.getPromptText());
            phoneTextField.setText(phoneTextField.getPromptText());
            modifyButton.setText("Guardar");

        } else {
            try {
                ConnectionMethods connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
                String name = nameTextField.getText();
                String phone = phoneTextField.getText();
                String address = String.format("%s, %s, %s, %s", cityComboBox.getValue(), streetTextField.getText(),
                        homeTextField.getText(), postalTextField.getText());
                String sql = String.format("UPDATE clients SET name = '%s', tel_num = '%s', " +
                        "address = '%s';", name, phone, address);

                connection.executeUpdate(sql);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Registro modificado");
                dialog.setContentText("El registro ha sido modificado correctamente.");
                dialog.setHeaderText(null);
                dialog.showAndWait();

                updateTable();
                updateCombo();
                setDefault();

            } catch (SQLException sqlEx) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error de actualización");
                dialog.setHeaderText(null);
                dialog.setContentText("Ha ocurrido un error al actualizar el registro. Excepción: " + sqlEx.toString());
                dialog.showAndWait();
            }

            modifyButton.setText("Modificar");
            setDefault();
        }
    }

    @FXML
    private void deleteButtonPressed(ActionEvent event) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Eliminar cliente");
        dialog.setHeaderText(null);
        dialog.setContentText("¿Seguro de que desea eliminar este registro?");
        dialog.showAndWait();
        ButtonType result = dialog.getResult();


        if (result == ButtonType.OK) {
            try {
                ConnectionMethods connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
                int id = selectedClient.idProperty().getValue();
                String sql = String.format("DELETE FROM clients WHERE id = %d;", id);
                connection.executeUpdate(sql);

                Alert dialog2 = new Alert(Alert.AlertType.INFORMATION);
                dialog2.setTitle("Cliente eliminado");
                dialog2.setHeaderText(null);
                dialog2.setContentText("El cliente ha sido eliminado de la tabla correctamente.");
                dialog2.showAndWait();

                updateTable();
                updateCombo();
                setDefault();

            } catch (SQLException sqlEx) {
                Alert dialog2 = new Alert(Alert.AlertType.ERROR);
                dialog2.setTitle("Error al eliminar");
                dialog2.setContentText("No ha sido posible eliminar el registro. Excepción: " + sqlEx.toString());
                dialog2.setHeaderText(null);
                dialog2.showAndWait();

                setDefault();
            }
        }
    }

    @FXML
    private void clearButtonPressed(ActionEvent event) {
        setDefault();
    }

    @FXML
    private void initialize() {
        updateTable();
        updateCombo();

        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 40) {
                nameTextField.setText(oldValue);
            }
        });

        homeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,3}([-]\\w{0,2})?")) {
                homeTextField.setText(oldValue);
            }
        });

        phoneTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,10}")) {
                phoneTextField.setText(oldValue);
            }
        });

        postalTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,6}")) {
                postalTextField.setText(oldValue);
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                selectedClient = (Client) (tableView.getSelectionModel().getSelectedItem());
                nameTextField.setPromptText(selectedClient.nameProperty().getValue());
                String address = selectedClient.addressProperty().getValue();
                cityComboBox.setPromptText(address.split(",")[0].trim());
                streetTextField.setPromptText(address.split(",")[1].trim());
                homeTextField.setPromptText(address.split(",")[2].trim());
                postalTextField.setPromptText(address.split(",")[3].trim());
                phoneTextField.setPromptText(selectedClient.tel_numProperty().getValue().trim());

                modifyButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                modifyButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

    }

    private void updateCombo() {
        cityComboBox.getItems().clear();

        try {
            ConnectionMethods connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
            ResultSet resultSet = connection.executeQuery("SELECT cityname FROM city;");

            while (resultSet.next()) {
                cityComboBox.getItems().add(resultSet.getString("cityname"));
            }

            connection.close();
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de conexión");
            dialog.setContentText("Ha ocurrido un error de conexión. Excepción: " + sqlEx.toString());
            dialog.setHeaderText(null);
            dialog.showAndWait();
        }
    }

    private void updateTable() {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        try {
            ConnectionMethods connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
            ResultSet resultSet = connection.executeQuery("SELECT * FROM clients;");
            ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(resultSet));

            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                TableColumn column = new TableColumn();

                switch (resultSet.getMetaData().getColumnName(i + 1)) {
                    case "id":
                        column.setText("ID");
                        break;
                    case "name":
                        column.setText("Nombre");
                        break;
                    case "address":
                        column.setText("Dirección");
                        break;
                    case "tel_num":
                        column.setText("Teléfono");
                        break;
                    default:
                        column.setText(resultSet.getMetaData().getColumnName(i + 1));
                        break;
                }

                column.setCellValueFactory(new PropertyValueFactory<>(resultSet.getMetaData().getColumnName(i + 1)));
                tableView.getColumns().add(column);
            }

            tableView.setItems(data);
            resultSet.close();
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de conexión");
            dialog.setContentText("Ha ocurrido un error de conexión. Excepción: " + sqlEx.toString());
            dialog.setHeaderText(null);
            dialog.showAndWait();
        }
    }

    private ArrayList dataBaseArrayList(ResultSet resultSet) {
        try {
            ArrayList<Client> data = new ArrayList<>();

            while (resultSet.next()) {
                Client client = new Client();
                client.id.set(resultSet.getInt("id"));
                client.name.set(resultSet.getString("name"));
                client.address.set(resultSet.getString("address"));
                client.tel_num.set(resultSet.getString("tel_num"));

                data.add(client);
            }

            return data;
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ocurrió un error inesperado. Excepción: " + sqlEx.toString());
            dialog.showAndWait();
            return null;
        }
    }

    private void setDefault() {
        nameTextField.clear();
        cityComboBox.getSelectionModel().clearSelection();
        streetTextField.clear();
        homeTextField.clear();
        postalTextField.clear();
        phoneTextField.clear();

        nameTextField.setPromptText("Nombre");
        cityComboBox.setPromptText("Ciudad");
        streetTextField.setPromptText("Calle");
        homeTextField.setPromptText("#");
        postalTextField.setPromptText("Código postal");
        phoneTextField.setPromptText("Teléfono");
        modifyButton.setText("Modificar");
    }

}
