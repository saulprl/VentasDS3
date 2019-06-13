package VentasFXDS3;

import AuxiliaryClasses.Item;
import Resources.ConnectionMethods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CatalogController {

    private static Item selectedItem;

    @FXML
    private Button exitButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button newItemButton;

    @FXML
    private TextField itemTextField;

    @FXML
    private TextField quantityTextField;

    @FXML
    private TextField priceTextField;

    @FXML
    private RadioButton activeRadio;

    @FXML
    private RadioButton inactiveRadio;

    @FXML
    private ToggleGroup radioGroup;

    @FXML
    private TableView tableView;

    @FXML
    private void newItemButtonPressed(ActionEvent event) {

        setDisable(event, false);
        itemTextField.clear();
        priceTextField.clear();
        quantityTextField.clear();

    }

    @FXML
    private void editButtonPressed(ActionEvent event) {
        setDisable(event, false);
    }

    @FXML
    private void saveButtonPressed(ActionEvent event) {
        if (itemTextField.getText().isEmpty() || priceTextField.getText().isEmpty() ||
                quantityTextField.getText().isEmpty() || radioGroup.getSelectedToggle() == null) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Campos vacíos");
            dialog.setHeaderText(null);
            dialog.setContentText("No puede haber campos vacíos.");
            dialog.showAndWait();

        } else if (editButton.isDisabled()) {

            String item = itemTextField.getText();
            double price = Double.parseDouble(priceTextField.getText());
            int quantity = Integer.parseInt(quantityTextField.getText());
            int active = activeRadio.isSelected() ? 1 : 0;

            String sql = String.format("INSERT INTO items(nombre, precio, cantidad, activo) " +
                    "VALUES('%s', %f, %d, '%d');", item, price, quantity, active);

            try {

                ConnectionMethods connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
                connection.executeUpdate(sql);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Registro insertado");
                dialog.setHeaderText(null);
                dialog.setContentText("Se ha insertado el registro correctamente.");
                dialog.showAndWait();
                updateTable();

            } catch (SQLException sqlEx) {

                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error de actualización");
                dialog.setHeaderText(null);
                dialog.setContentText("Error de actualización. Excepción atrapada: " + sqlEx.toString());
                dialog.showAndWait();

            }
        } else {

            String item = itemTextField.getText();
            double price = Double.parseDouble(priceTextField.getText());
            int quantity = Integer.parseInt(quantityTextField.getText());
            int active = activeRadio.isSelected() ? 1 : 0;
            int id = selectedItem.idproducto.getValue();

            String sql = String.format("UPDATE items SET nombre = '%s', precio = %f, cantidad = %d" +
                    ", activo = '%d' WHERE idproducto = %d;", item, price, quantity, active, id);

            try {

                ConnectionMethods connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
                connection.executeUpdate(sql);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Registro modificado");
                dialog.setHeaderText(null);
                dialog.setContentText("Se ha modificado el registro correctamente.");
                dialog.showAndWait();
                updateTable();

            } catch (SQLException sqlEx) {

                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error de actualización");
                dialog.setHeaderText(null);
                dialog.setContentText("Error de actualización. Excepción atrapada: " + sqlEx.toString());
                dialog.showAndWait();

            }
        }

        setDisable(event, true);
    }

    @FXML
    private void cancelButtonPressed(ActionEvent event) {

        setDisable(event, true);
        itemTextField.clear();
        priceTextField.clear();
        quantityTextField.clear();

    }

    @FXML
    private void deleteButtonPressed(ActionEvent event) {

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Eliminar registro");
        dialog.setHeaderText(null);
        dialog.setContentText("¿Seguro de que desea eliminar este registro?");
        dialog.showAndWait();
        ButtonType result = dialog.getResult();

        if (result == ButtonType.OK) {
            try {

                ConnectionMethods connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
                int id = selectedItem.idproducto.getValue();
                String sql = String.format("DELETE FROM items WHERE idproducto = %d;", id);

                connection.executeUpdate(sql);
                Alert dialog2 = new Alert(Alert.AlertType.INFORMATION);
                dialog2.setTitle("Registro borrado");
                dialog2.setHeaderText(null);
                dialog2.setContentText("El registro se ha borrado correctamente.");
                dialog2.showAndWait();
                updateTable();

            } catch (SQLException e) {

                Alert dialog2 = new Alert(Alert.AlertType.ERROR);
                dialog2.setTitle("Error");
                dialog2.setHeaderText(null);
                dialog2.setContentText("No fue posible borrar el registro. Excepción atrapada: " + e.toString());
                dialog2.showAndWait();

            }

            setDisable(event, true);
        }
    }

    @FXML
    private void exitButtonPressed(ActionEvent event) {
        Stage stage = (Stage) (exitButton.getScene().getWindow());
        stage.close();
    }

    @FXML
    private void initialize() {

        // setDisable(new ActionEvent(), true);
        updateTable();
        // tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Se transforma la fila seleccionada en un objeto Item con sus propiedades.
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                selectedItem = (Item) (tableView.getSelectionModel().getSelectedItem());
                editButton.setDisable(false);
                deleteButton.setDisable(false);

                // Mantiene los nodos desactivados, pero establece sus valores según el objeto Item.
                itemTextField.setText(selectedItem.nombre.getValue());
                priceTextField.setText(selectedItem.precio.getValue().toString());
                quantityTextField.setText(selectedItem.cantidad.getValue().toString());

                if (selectedItem.activo.getValue().equals("t"))
                    activeRadio.setSelected(true);
                else
                    inactiveRadio.setSelected(true);

            }
        });

    }

    private void updateTable() {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        try {
            ConnectionMethods connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
            ResultSet resultSet = connection.executeQuery("SELECT * FROM items;");

            if (resultSet != null) {
                ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(resultSet));

                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (resultSet.getMetaData().getColumnName(i + 1)) {
                        case "idproducto":
                            column.setText("ID");
                            break;
                        case "nombre":
                            column.setText("Nombre");
                            break;
                        case "precio":
                            column.setText("Precio");
                            break;
                        case "cantidad":
                            column.setText("Cantidad");
                            break;
                        case "activo":
                            column.setText("Activo");
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
            dialog.setTitle("Error de conexión");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error de conexión. Excepción atrapada: " + sqlEx.toString());
            dialog.showAndWait();

        }
    }

    private ArrayList dataBaseArrayList(ResultSet resultSet) {
        try {
            ArrayList<Item> data = new ArrayList<>();

            while (resultSet.next()) {
                Item item = new Item();
                item.idproducto.set(resultSet.getInt("idproducto"));
                item.nombre.set(resultSet.getString("nombre"));
                item.precio.set(resultSet.getDouble("precio"));
                item.cantidad.set(resultSet.getInt("cantidad"));
                item.activo.set(resultSet.getString("activo"));

                data.add(item);
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

    private void setDisable(ActionEvent event, boolean disable) {
        Button pressed = (Button) (event.getSource());

        switch (pressed.getText()) {
            case "Nuevo":

                itemTextField.setDisable(disable);
                priceTextField.setDisable(disable);
                quantityTextField.setDisable(disable);
                radioGroup.getToggles().forEach(toggle -> {
                    Node node = (Node) (toggle);
                    node.setDisable(disable);
                });
                saveButton.setDisable(disable);
                cancelButton.setDisable(disable);
                editButton.setDisable(!disable);
                deleteButton.setDisable(!disable);
                exitButton.setDisable(!disable);
                break;

            case "Editar":

                itemTextField.setDisable(disable);
                priceTextField.setDisable(disable);
                quantityTextField.setDisable(disable);
                radioGroup.getToggles().forEach(toggle -> {
                    Node node = (Node) (toggle);
                    node.setDisable(disable);
                });
                saveButton.setDisable(disable);
                cancelButton.setDisable(disable);
                newItemButton.setDisable(!disable);
                exitButton.setDisable(!disable);
                break;

            case "Guardar":

                itemTextField.setDisable(disable);
                priceTextField.setDisable(disable);
                quantityTextField.setDisable(disable);
                radioGroup.getToggles().forEach(toggle -> {
                    Node node = (Node) (toggle);
                    node.setDisable(disable);
                });
                newItemButton.setDisable(!disable);
                saveButton.setDisable(disable);
                cancelButton.setDisable(disable);
                exitButton.setDisable(!disable);
                break;

            case "Cancelar":

                itemTextField.setDisable(disable);
                priceTextField.setDisable(disable);
                quantityTextField.setDisable(disable);
                radioGroup.getToggles().forEach(toggle -> {
                    Node node = (Node) (toggle);
                    node.setDisable(disable);
                });
                newItemButton.setDisable(!disable);
                saveButton.setDisable(disable);
                cancelButton.setDisable(disable);
                exitButton.setDisable(!disable);
                break;

            case "Eliminar":

                itemTextField.setDisable(disable);
                priceTextField.setDisable(disable);
                quantityTextField.setDisable(disable);
                radioGroup.getToggles().forEach(toggle -> {
                    Node node = (Node) (toggle);
                    node.setDisable(disable);
                });
                saveButton.setDisable(disable);
                cancelButton.setDisable(disable);
                deleteButton.setDisable(disable);
                editButton.setDisable(disable);
                break;

            default:

                itemTextField.setDisable(disable);
                priceTextField.setDisable(disable);
                quantityTextField.setDisable(disable);
                radioGroup.getToggles().forEach(toggle -> {
                    Node node = (Node) (toggle);
                    node.setDisable(disable);
                });
                break;
        }
    }
}