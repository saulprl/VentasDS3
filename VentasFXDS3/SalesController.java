package VentasFXDS3;

import AuxiliaryClasses.Item;
import AuxiliaryClasses.ItemSale;
import AuxiliaryClasses.Sale;
import Resources.ConnectionMethods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class SalesController {

    private static ObservableList<Item> toBuy = FXCollections.observableArrayList();
    private static ConnectionMethods connection;
    private static Sale selectedSale;
    private static Item selectedToAdd;
    private static Item selectedToBuy;

    @FXML
    private ComboBox saleComboBox;

    @FXML
    private ComboBox empComboBox;

    @FXML
    private ComboBox branchComboBox;

    @FXML
    private ComboBox clientComboBox;

    @FXML
    private Button registerButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    private GridPane gridPane;

    @FXML
    private TableView itemsTableView;

    @FXML
    private TableView toBuyTableView;

    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    @FXML
    private void registerButtonPressed(ActionEvent event) {
        if (empComboBox.getValue() != null && branchComboBox.getValue() != null && clientComboBox.getValue() != null) {
            if (!toBuyTableView.getItems().isEmpty()) {
                String emp = empComboBox.getValue().toString();
                String branch = branchComboBox.getValue().toString();
                String client = clientComboBox.getValue().toString();
                double amount = 0.00;

                for (Object object : toBuyTableView.getItems()) {
                    Item item = (Item) (object);

                    amount += item.precio.getValue();
                }

                connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);

                HashMap<Integer, String> inParamMap = new HashMap<>();
                ArrayList<String> inParam = new ArrayList<>();
                HashMap<Integer, String> outParamMap = new HashMap<>();

                inParamMap.put(1, "int");
                inParamMap.put(2, "string");
                inParamMap.put(3, "numeric");
                inParamMap.put(4, "int");

                outParamMap.put(5, "int");

                inParam.add(emp);
                inParam.add(branch);
                inParam.add(Double.toString(amount));
                inParam.add(client);

                String query = "{ ? = call insertsale(?, ?, ?, ?) }";

                try {
                    int lastId = Integer.parseInt(connection.prepareCall(query, inParamMap, inParam, outParamMap));

                    inParamMap.clear();
                    inParam.clear();
                    outParamMap.clear();

                    inParamMap.put(1, "int");
                    inParamMap.put(2, "int");
                    inParamMap.put(3, "int");
                    inParamMap.put(4, "numeric");

                    outParamMap.put(5, "bit");

                    for (Object object : toBuyTableView.getItems()) {
                        Item item = (Item) (object);

                        inParam.add(Integer.toString(item.idproducto.getValue()));
                        inParam.add(Integer.toString(lastId));
                        inParam.add("1");
                        inParam.add(Double.toString(item.precio.getValue()));

                        query = "{ ? = call insert_items(?, ?, ?, ?) }";
                        connection.prepareCall(query, inParamMap, inParam, outParamMap);
                        inParam.clear();
                    }

                    Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                    dialog.setTitle("Venta insertada");
                    dialog.setHeaderText(null);
                    dialog.setContentText("La venta y los artículos han sido insertados correctamente.");
                    dialog.showAndWait();

                    clearButtonPressed(new ActionEvent());
                } catch (SQLException sqlEx) {
                    Alert dialog = new Alert(Alert.AlertType.ERROR);
                    dialog.setTitle("Error");
                    dialog.setHeaderText(null);
                    dialog.setContentText("Ha ocurrido un error durante el proceso. Excepción: " + sqlEx.getMessage());
                    dialog.showAndWait();
                }
            } else {
                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Artículos");
                dialog.setHeaderText(null);
                dialog.setContentText("Asegúrate de ingresar artículos a la venta.");
                dialog.showAndWait();
            }
        } else {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Parámetros de registro");
            dialog.setHeaderText(null);
            dialog.setContentText("Asegúrate de haber ingresado todos los parámetros de registro (excepto la venta).");
            dialog.showAndWait();
        }
    }

    @FXML
    private void deleteButtonPressed(ActionEvent event) {
        if (saleComboBox.getValue() != null) {
            String saleId = saleComboBox.getValue().toString();

            HashMap<Integer, String> inParamMap = new HashMap<>();
            HashMap<Integer, String> outParamMap = new HashMap<>();
            ArrayList<String> inParam = new ArrayList<>();

            inParamMap.put(1, "int");
            outParamMap.put(2, "bit");
            inParam.add(saleId);

            connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
            String query = "{ ? = call delete_sale(?) }";

            try {
                connection.prepareCall(query, inParamMap, inParam, outParamMap);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Eliminar venta");
                dialog.setHeaderText(null);
                dialog.setContentText("Se han eliminado todos los registros relacionados con la venta.");
                dialog.showAndWait();

                clearButtonPressed(new ActionEvent());
            } catch (SQLException sqlEx) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error");
                dialog.setHeaderText(null);
                dialog.setContentText("Ha ocurrido un error al eliminar los registros.");
                dialog.showAndWait();
            }
        }
    }

    @FXML
    private void clearButtonPressed(ActionEvent event) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof ComboBox) {
                node.setDisable(false);
                ((ComboBox) node).getSelectionModel().select(null);
            }
        }
        toBuy.clear();
        updateCombos();
        updateTables();
        toBuyTableView.setItems(toBuy);
    }

    @FXML
    private void addButtonPressed(ActionEvent event) {
        toBuy.add(selectedToAdd);
        toBuyTableView.setItems(toBuy);
    }

    @FXML
    private void removeButtonPressed(ActionEvent event) {
        toBuy.remove(selectedToBuy);
        toBuyTableView.setItems(toBuy);
    }

    @FXML
    private void initialize() {
        updateCombos();
        updateTables();
        deleteButton.setDisable(true);

        saleComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                for (Node node : gridPane.getChildren()) {
                    if (node instanceof ComboBox && node != saleComboBox) {
                        node.setDisable(true);
                    } else if (node instanceof Button && node.getId().contains("register")) {
                        node.setDisable(true);
                    } else if (node instanceof Button && node.getId().contains("delete")) {
                        node.setDisable(false);
                    }
                }
                addButton.setDisable(true);
                removeButton.setDisable(true);
                switchTables();
            } else {
                for (Node node : gridPane.getChildren()) {
                    if (node instanceof ComboBox && node != saleComboBox) {
                        node.setDisable(false);
                    } else if (node instanceof Button && node.getId().contains("register")) {
                        node.setDisable(false);
                    } else if (node instanceof Button && node.getId().contains("delete")) {
                        node.setDisable(true);
                    }
                }
                addButton.setDisable(false);
                removeButton.setDisable(false);
            }
        });

        empComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                saleComboBox.setDisable(true);
            }
        });

        branchComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                saleComboBox.setDisable(true);
            }
        });

        clientComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                saleComboBox.setDisable(true);
            }
        });

        itemsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedToAdd = (Item) (newValue);
            } else {
                selectedToAdd = null;
            }
        });

        toBuyTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedToBuy = (Item) (newValue);
            } else {
                selectedToBuy = null;
            }
        });

    }

    private void updateCombos() {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof ComboBox) {
                ((ComboBox) node).getItems().clear();
            }
        }

        connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);

        try {
            ResultSet salesSet = connection.executeQuery("SELECT idsale FROM sales;");
            ResultSet empSet = connection.executeQuery("SELECT empno FROM employees;");
            ResultSet branchSet = connection.executeQuery("SELECT branchno FROM branch;");
            ResultSet clientSet = connection.executeQuery("SELECT id FROM clients;");

            while (salesSet.next()) {
                saleComboBox.getItems().add(salesSet.getInt("idsale"));
            }

            while (empSet.next()) {
                empComboBox.getItems().add(empSet.getInt("empno"));
            }

            while (branchSet.next()) {
                branchComboBox.getItems().add(branchSet.getString("branchno"));
            }

            while (clientSet.next()) {
                clientComboBox.getItems().add(clientSet.getInt("id"));
            }

            connection.close();
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error de conexión. Excepción: " + sqlEx.getMessage());
            dialog.showAndWait();
        }
    }

    private void updateTables() {
        itemsTableView.setDisable(false);
        toBuyTableView.setDisable(false);

        itemsTableView.getItems().clear();
        itemsTableView.getColumns().clear();
        toBuyTableView.getItems().clear();
        toBuyTableView.getColumns().clear();

        connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);

        try {
            ResultSet resultSet = connection.executeQuery("SELECT * FROM items WHERE activo = '1' AND cantidad > 0;");

            if (resultSet != null) {
                ObservableList<Item> data = FXCollections.observableArrayList(dataBaseArrayList(resultSet));

                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();
                    TableColumn another = new TableColumn();

                    switch (resultSet.getMetaData().getColumnName(i + 1)) {
                        case "idproducto":
                            column.setText("ID");
                            break;
                        case "nombre":
                            column.setText("Nombre");
                            break;
                        case "cantidad":
                            column.setText("Cantidad");
                            break;
                        case "precio":
                            column.setText("Precio");
                            break;
                        case "activo":
                            column.setText("Disponible");
                            break;
                        default:
                            column.setText(resultSet.getMetaData().getColumnName(i + 1));
                    }

                    column.setCellValueFactory(new PropertyValueFactory<>(resultSet.getMetaData().getColumnName(i + 1)));
                    itemsTableView.getColumns().add(column);

                    if (!column.getText().equals("Cantidad")) {
                        another.setText(column.getText());
                        another.setCellValueFactory(new PropertyValueFactory<>(resultSet.getMetaData().getColumnName(i + 1)));
                        toBuyTableView.getColumns().add(another);
                    }
                }

                itemsTableView.setItems(data);
            }

            connection.close();
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error de conexión. Excepción: " + sqlEx.getMessage());
            dialog.showAndWait();
        }
    }

    private ArrayList<Item> dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<Item> data = new ArrayList<>();

        while (resultSet.next()) {
            Item item = new Item();

            item.idproducto.setValue(resultSet.getInt("idproducto"));
            item.nombre.setValue(resultSet.getString("nombre"));
            item.cantidad.setValue(resultSet.getInt("cantidad"));
            item.precio.setValue(resultSet.getDouble("precio"));
            item.activo.setValue(resultSet.getString("activo"));

            data.add(item);
        }

        return data;
    }

    private void switchTables() {
        toBuyTableView.getItems().clear();
        toBuyTableView.getColumns().clear();
        itemsTableView.getItems().clear();
        itemsTableView.getColumns().clear();

        connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
        String headerQuery = String.format("SELECT * FROM sales_header_view WHERE idsale = %s;",
                saleComboBox.getValue().toString());
        String itemsQuery = String.format("SELECT * FROM item_sales_view WHERE idsale = %s;",
                saleComboBox.getValue().toString());

        try {
            ResultSet header = connection.executeQuery(headerQuery);
            ResultSet items = connection.executeQuery(itemsQuery);

            ArrayList<Sale> headerSaleData = new ArrayList<>();
            while (header.next()) {
                Sale sale = new Sale();

                sale.idsale.setValue(header.getInt("idsale"));
                sale.empname.setValue(header.getString("empname"));
                sale.clientname.setValue(header.getString("clientname"));
                sale.branchno.setValue(header.getString("branchno"));
                sale.saledate.setValue(header.getObject("saledate", LocalDate.class));
                sale.amount.setValue(header.getDouble("amount"));

                headerSaleData.add(sale);
            }

            ArrayList<ItemSale> itemSaleData = new ArrayList<>();
            while (items.next()) {
                ItemSale item = new ItemSale();

                item.idsale.setValue(items.getInt("idsale"));
                item.nombre.setValue(items.getString("nombre"));
                item.quantity.setValue(items.getInt("quantity"));
                item.price.setValue(items.getDouble("price"));

                itemSaleData.add(item);
            }

            header.beforeFirst();
            items.beforeFirst();

            ObservableList headerData = FXCollections.observableArrayList(headerSaleData);
            ObservableList itemsData = FXCollections.observableArrayList(itemSaleData);

            for (int i = 0; i < header.getMetaData().getColumnCount(); i++) {
                TableColumn column = new TableColumn();

                switch (header.getMetaData().getColumnName(i + 1)) {
                    case "idsale":
                        column.setText("ID de venta");
                        break;
                    case "empname":
                        column.setText("Empleado");
                        break;
                    case "clientname":
                        column.setText("Cliente");
                        break;
                    case "branchno":
                        column.setText("Sucursal");
                        break;
                    case "saledate":
                        column.setText("Fecha");
                        break;
                    case "amount":
                        column.setText("Costo total");
                        break;
                    default:
                        column.setText(header.getMetaData().getColumnName(i + 1));
                }

                column.setCellValueFactory(new PropertyValueFactory<>(header.getMetaData().getColumnName(i + 1)));
                itemsTableView.getColumns().add(column);
            }

            for (int i = 0; i < items.getMetaData().getColumnCount(); i++) {
                TableColumn column = new TableColumn();

                switch (items.getMetaData().getColumnName(i + 1)) {
                    case "idsale":
                        column.setText("ID de venta");
                        break;
                    case "nombre":
                        column.setText("Artículo");
                        break;
                    case "quantity":
                        column.setText("Cantidad");
                        break;
                    case "price":
                        column.setText("Precio");
                        break;
                    default:
                        column.setText(items.getMetaData().getColumnName(i + 1));
                }

                column.setCellValueFactory(new PropertyValueFactory<>(items.getMetaData().getColumnName(i + 1)));
                toBuyTableView.getColumns().add(column);
            }

            itemsTableView.setItems(headerData);
            toBuyTableView.setItems(itemsData);
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error de conexión. Excepción: " + sqlEx.getMessage());
            dialog.showAndWait();
        }
    }
}
