package VentasFXDS3;

import AuxiliaryClasses.City;
import AuxiliaryClasses.Country;
import AuxiliaryClasses.State;
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
import java.util.HashMap;

public class EntitiesController {

    private ConnectionMethods connection;
    private static Country selectedCountry;
    private static State selectedState;
    private static City selectedCity;

    @FXML
    private TextField keyTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private RadioButton countryRadio;

    @FXML
    private RadioButton stateRadio;

    @FXML
    private RadioButton cityRadio;

    @FXML
    private ComboBox countryComboBox;

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private Button modButton;

    @FXML
    private Button delButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button clearButton;

    @FXML
    private TableView stateTable;

    @FXML
    private TableView cityTable;

    @FXML
    private void registerButtonPressed(ActionEvent event) {
        String key = keyTextField.getText();
        String name = nameTextField.getText();
        connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
        String sql;

        try {
            if (countryRadio.isSelected()) {

                sql = String.format("INSERT INTO country(countrykey, countryname) VALUES('%s', '%s');", key, name);
                connection.executeUpdate(sql);
                updateCombo();

            } else if (stateRadio.isSelected()) {

                int idCountry = selectedCountry.idcountryProperty().getValue();
                sql = String.format("INSERT INTO state(statekey, statename, idcountry) VALUES('%s', '%s', %d);", key, name,
                        idCountry);
                connection.executeUpdate(sql);
                updateStates(selectedCountry.idcountryProperty().getValue());

            } else {

                int idState = selectedState.idstateProperty().getValue();
//                sql = String.format("INSERT INTO city(citykey, cityname, idstate) VALUES('%s', '%s', %d);", key, name,
//                        idState);
//                connection.executeUpdate(sql);
//                updateCities(selectedState.idstateProperty().getValue());
                sql = "{ ? = call insertupdatecity(?, ?, ?, ?) }";

                HashMap<Integer, String> inParamMap = new HashMap<>();
                inParamMap.put(1, "int");
                inParamMap.put(2, "string");
                inParamMap.put(3, "string");
                inParamMap.put(4, "int");

                ArrayList<String> inParam = new ArrayList<>();
                inParam.add("0");
                inParam.add(key);
                inParam.add(name);
                inParam.add(Integer.toString(idState));

                HashMap<Integer, String> outParam = new HashMap<>();
                outParam.put(5, "bit");

                if (Boolean.parseBoolean(connection.prepareCall(sql, inParamMap, inParam, outParam))) {
                    Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                    dialog.setTitle("Registro insertado");
                    dialog.setHeaderText(null);
                    dialog.setContentText("El registro se ha insertado correctamente.");
                    dialog.showAndWait();
                } else {
                    Alert dialog = new Alert(Alert.AlertType.ERROR);
                    dialog.setTitle("Error al insertar");
                    dialog.setHeaderText(null);
                    dialog.setContentText("Ha ocurrido un error inesperado.");
                    dialog.showAndWait();
                }

            }

            setDefault();
        } catch (SQLException   e) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error al insertar");
            dialog.setHeaderText(null);
            dialog.setContentText("Ocurrió un error al intentar insertar el registro. Excepción: " + e.toString());
            dialog.showAndWait();

            setDefault();
        }
    }

    @FXML
    private void modButtonPressed(ActionEvent event) {
        if (modButton.getText().equals("Modificar")) {

            nameTextField.setText(nameTextField.getPromptText());
            keyTextField.setText(keyTextField.getPromptText());
            modButton.setText("Guardar");
            clearButton.setText("Cancelar");

        } else {
            String key = keyTextField.getText();
            String name = nameTextField.getText();
            String sql;
            connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);

            try {
                if (countryRadio.isSelected()) {

                    int idCountry = selectedCountry.idcountryProperty().getValue();
                    sql = String.format("UPDATE country SET countrykey = '%s', countryname = '%s' WHERE idcountry = %d;",
                            key, name, idCountry);
                    connection.executeUpdate(sql);
                    updateCombo();

                } else if (stateRadio.isSelected()) {

                    int idState = selectedState.idstateProperty().getValue();
//                    sql = String.format("UPDATE state SET statekey = '%s', statename = '%s' WHERE idstate = %d", key,
//                            name, idState);
//                    connection.executeUpdate(sql);
//                    updateStates(selectedCountry.idcountryProperty().getValue());
                    sql = "{ ? = call insertupdatestate(?, ?, ?, ?) }";

                    HashMap<Integer, String> inParamMap = new HashMap<>();
                    ArrayList<String> inParam = new ArrayList<>();
                    HashMap<Integer, String> outParam = new HashMap<>();

                    inParamMap.put(1, "int");
                    inParamMap.put(2, "string");
                    inParamMap.put(3, "string");
                    inParamMap.put(4, "int");

                    outParam.put(5, "bit");

                    inParam.add(Integer.toString(idState));
                    inParam.add(key);
                    inParam.add(name);
                    inParam.add(Integer.toString(selectedCountry.idcountryProperty().getValue()));

                    if (Boolean.parseBoolean(connection.prepareCall(sql, inParamMap, inParam, outParam))) {
                        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                        dialog.setHeaderText(null);
                        dialog.setTitle("Registro actualizado");
                        dialog.setContentText("El registro se ha actualizado correctamente.");
                        dialog.showAndWait();
                    } else {
                        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                        dialog.setHeaderText(null);
                        dialog.setTitle("Error de actualización");
                        dialog.setContentText("Ha ocurrido un error inesperado.");
                        dialog.showAndWait();
                    }


                } else {

                    int idCity = selectedCity.idcityProperty().getValue();
                    HashMap<Integer, String> inParamMap = new HashMap<>();
                    ArrayList<String> inParam = new ArrayList<>();
                    HashMap<Integer, String> outParamMap = new HashMap<>();

                    inParamMap.put(1, "int");
                    inParamMap.put(2, "string");
                    inParamMap.put(3, "string");
                    inParamMap.put(4, "int");

                    outParamMap.put(5, "bit");

//                    ArrayList<String> inParam = new ArrayList<>();

                    inParam.add(Integer.toString(idCity));
                    inParam.add(key);
                    inParam.add(name);
                    inParam.add(Integer.toString(selectedState.idstateProperty().getValue()));

//                    String result = connection.citiesProcedure(inParam);
//                    System.out.println(result);
//                    sql = String.format("UPDATE city SET citykey = '%s', cityname = '%s' WHERE idcity = %d", key, name,
//                            idCity);
//                    connection.executeUpdate(sql);

                    sql = "{ ? = call insertupdatecity(?, ?, ?, ?) }";

                    if (Boolean.parseBoolean(connection.prepareCall(sql, inParamMap, inParam, outParamMap))) {
                        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                        dialog.setTitle("Registro actualizado");
                        dialog.setHeaderText(null);
                        dialog.setContentText("El registro se ha actualizado correctamente.");
                        dialog.showAndWait();
                    } else {
                        Alert dialog = new Alert(Alert.AlertType.ERROR);
                        dialog.setTitle("Error de modificación");
                        dialog.setHeaderText(null);
                        dialog.setContentText("Ocurrió un error inesperado.");
                        dialog.showAndWait();
                    }
                    
                    updateCities(selectedState.idstateProperty().getValue());
                }

                connection.close();
                setDefault();
            } catch (SQLException e) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error al actualizar");
                dialog.setHeaderText(null);
                dialog.setContentText("Ocurrió un error al intentar actualizar el registro. Excepción: " + e.toString());
                dialog.showAndWait();
            }

            clearButton.setText("Limpiar");
            modButton.setText("Modificar");
            modButton.setDisable(true);
            delButton.setDisable(true);
        }
    }

    @FXML
    private void delButtonPressed(ActionEvent event) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Borrar registro");
        dialog.setContentText("¿Seguro de que desea eliminar este registro?");
        dialog.setHeaderText(null);
        dialog.showAndWait();
        ButtonType result = dialog.getResult();


        if (result == ButtonType.OK) {
            connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
            String sql;

            try {
                if (countryRadio.isSelected()) {

                    if (selectedCountry != null) {

                        int idCountry = selectedCountry.idcountryProperty().getValue();
                        sql = String.format("DELETE FROM country WHERE idcountry = %d;", idCountry);
                        connection.executeUpdate(sql);
                        updateCombo();

                    } else {

                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Advertencia");
                        alert.setHeaderText(null);
                        alert.setContentText("No existe un país con esos atributos. Asegúrese de tener seleccionada " +
                                "la opción correcta para eliminar.");
                        alert.showAndWait();
                        return;

                    }

                } else if (stateRadio.isSelected()) {

                    if (selectedState != null) {

                        int idState = selectedState.idstateProperty().getValue();
                        sql = String.format("DELETE FROM state WHERE idstate = %d;", idState);
                        connection.executeUpdate(sql);
                        updateStates(selectedCountry.idcountryProperty().getValue());

                    } else {

                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Advertencia");
                        alert.setHeaderText(null);
                        alert.setContentText("No existe un estado con esos atributos. Asegúrese de tener seleccionada" +
                                " la opción correcta para eliminar.");
                        alert.showAndWait();
                        return;

                    }

                } else {
                    if (selectedCity != null) {

                        int idCity = selectedCity.idcityProperty().getValue();
                        sql = String.format("DELETE FROM city WHERE idcity = %d;", idCity);
                        connection.executeUpdate(sql);
                        updateCities(selectedState.idstateProperty().getValue());

                    } else {

                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Advertencia");
                        alert.setHeaderText(null);
                        alert.setContentText("No existe una ciudad con esos atributos. Asegúrese de tener seleccionada "
                                + "la opción correcta para eliminar.");
                        alert.showAndWait();
                        return;

                    }

                }

                Alert dialog2 = new Alert(Alert.AlertType.INFORMATION);
                dialog2.setTitle("Registro borrado");
                dialog2.setHeaderText(null);
                dialog2.setContentText("El registro ha sido borrado correctamente.");
                dialog2.showAndWait();

                setDefault();
            } catch (SQLException e) {

                Alert dialog2 = new Alert(Alert.AlertType.ERROR);
                dialog2.setTitle("Error al borrar");
                dialog2.setHeaderText(null);
                dialog2.setContentText("No fue posible eliminar el registro. Excepción: " + e.toString());
                dialog2.showAndWait();
                setDefault();

            }

        } else {

            Alert dialog2 = new Alert(Alert.AlertType.INFORMATION);
            dialog2.setTitle("Operación cancelada");
            dialog2.setContentText("No se ha realizado ningún movimiento.");
            dialog2.setHeaderText(null);
            dialog2.showAndWait();
            setDefault();

        }
    }

    @FXML
    private void clearButtonPressed(ActionEvent event) {
        if (clearButton.getText().equals("Limpiar")) {

            setDefault();
            countryComboBox.getSelectionModel().clearSelection();
            countryComboBox.setPromptText("País");
            stateTable.getItems().clear();
            stateTable.getColumns().clear();
            cityTable.getItems().clear();
            cityTable.getColumns().clear();

        } else {
            setDefault();
        }
    }

    @FXML
    private void initialize() {
        updateCombo();

        keyTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue.matches("\\w{0,4}")) {
                keyTextField.setText(oldValue);
            } else if (!newValue.isEmpty()) {
                keyTextField.setText(newValue.trim().toUpperCase());
            }

        });

        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue.length() > 50) {
                nameTextField.setText(oldValue);
            } else if (!newValue.isEmpty()) {
                nameTextField.setText(newValue);
            }

        });

        countryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                modButton.setDisable(false);
                delButton.setDisable(false);

                selectedCountry = getSelectedCountry();

                if (selectedCountry == null) {

                    Alert dialog = new Alert(Alert.AlertType.WARNING);
                    dialog.setTitle("Advertencia");
                    dialog.setContentText("No fue posible obtener los datos de las tablas.");
                    dialog.setHeaderText(null);
                    dialog.showAndWait();

                } else {

                    int idCountry = selectedCountry.idcountryProperty().getValue();
                    updateStates(idCountry);

                    if (countryRadio.isSelected()) {
                        keyTextField.setPromptText(selectedCountry.countrykeyProperty().getValue());
                        nameTextField.setPromptText(selectedCountry.countrynameProperty().getValue());
                    }
                }

            } else {
                selectedCountry = null;

                modButton.setDisable(true);
                delButton.setDisable(true);
            }
        });

        stateTable.getSelectionModel().selectedItemProperty().addListener((observable) -> {

            if (stateTable.getSelectionModel().getSelectedItem() != null) {
                selectedState = (State) (stateTable.getSelectionModel().getSelectedItem());

                if (stateRadio.isSelected()) {
                    keyTextField.setPromptText(selectedState.statekeyProperty().getValue());
                    nameTextField.setPromptText(selectedState.statenameProperty().getValue());
                }

                modButton.setDisable(false);
                delButton.setDisable(false);
                updateCities(selectedState.idstateProperty().getValue());
            } else {
                selectedState = null;
            }

        });

        cityTable.getSelectionModel().selectedItemProperty().addListener((observable) -> {

            if (cityTable.getSelectionModel().getSelectedItem() != null) {
                selectedCity = (City) (cityTable.getSelectionModel().getSelectedItem());
                System.out.println(selectedState.idstateProperty().getValue());

                if (cityRadio.isSelected()) {
                    keyTextField.setPromptText(selectedCity.citykeyProperty().getValue());
                    nameTextField.setPromptText(selectedCity.citynameProperty().getValue());
                }

                modButton.setDisable(false);
                delButton.setDisable(false);
            } else {
                selectedCity = null;
            }

        });

        toggleGroup.selectedToggleProperty().addListener((observable) -> {
            if (countryRadio.isSelected()) {

                if (keyTextField.getText().isEmpty() || nameTextField.getText().isEmpty()) {
                    registerButton.setDisable(true);
                } else {
                    registerButton.setDisable(false);
                }

            } else if (stateRadio.isSelected()) {

                if (countryComboBox.getSelectionModel().getSelectedItem() == null) {
                    registerButton.setDisable(true);
                } else {
                    registerButton.setDisable(false);
                }

            } else {

                if (stateTable.getSelectionModel().getSelectedItem() == null) {
                    registerButton.setDisable(true);
                } else {
                    registerButton.setDisable(false);
                }

            }
        });
    }

    private void updateStates(int country) {
        stateTable.getItems().clear();
        stateTable.getColumns().clear();
        cityTable.getColumns().clear();
        cityTable.getItems().clear();

        connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);

        try {
            String sql = String.format("SELECT idstate, statekey, statename FROM state WHERE idcountry = %d;", country);
            ResultSet resultSet = connection.executeQuery(sql);
            ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(resultSet, true));

            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                TableColumn column = new TableColumn();

                switch (resultSet.getMetaData().getColumnName(i + 1)) {
                    case "idstate":
                        column.setText("ID");
                        break;
                    case "statekey":
                        column.setText("Clave");
                        break;
                    case "statename":
                        column.setText("Nombre");
                        break;
                    default:
                        column.setText(resultSet.getMetaData().getColumnName(i + 1));
                        break;
                }

                column.setCellValueFactory(new PropertyValueFactory<>(resultSet.getMetaData().getColumnName(i + 1)));
                stateTable.getColumns().add(column);
            }

            stateTable.setItems(data);
            resultSet.close();
            connection.close();
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de conexión");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error de conexión. Excepción: " + sqlEx.toString());
            dialog.showAndWait();
        }
    }

    private void updateCities(int idState) {
        cityTable.getItems().clear();
        cityTable.getColumns().clear();

        connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);

        try {
            String sql = String.format("SELECT idcity, citykey, cityname FROM city WHERE idstate = %d", idState);
            ResultSet resultSet = connection.executeQuery(sql);

            if (resultSet != null) {
                ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(resultSet, false));

                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (resultSet.getMetaData().getColumnName(i + 1)) {
                        case "idcity":
                            column.setText("ID");
                            break;
                        case "citykey":
                            column.setText("Clave");
                            break;
                        case "cityname":
                            column.setText("Nombre");
                            break;
                        default:
                            column.setText(resultSet.getMetaData().getColumnName(i + 1));
                            break;
                    }

                    column.setCellValueFactory(new PropertyValueFactory<>(resultSet.getMetaData().getColumnName(i + 1)));
                    cityTable.getColumns().add(column);
                }

                cityTable.setItems(data);
            }

            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de conexión");
            dialog.setContentText("Ocurrió un error de conexión. Excepción: " + e.toString());
            dialog.setHeaderText(null);
            dialog.showAndWait();
        }
    }

    private void updateCombo() {
        countryComboBox.getItems().clear();
        cityTable.getItems().clear();
        cityTable.getColumns().clear();
        stateTable.getItems().clear();
        stateTable.getColumns().clear();

        connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);

        try {
            ResultSet resultSet = connection.executeQuery("SELECT * FROM country;");

            while (resultSet.next()) {
                String in = String.format("%s (%s)", resultSet.getString("countryname"),
                        resultSet.getString("countrykey"));
                countryComboBox.getItems().add(in);
            }

            resultSet.close();
            connection.close();
        } catch (SQLException e) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de conexión");
            dialog.setHeaderText(null);
            dialog.setContentText("No ha sido posible conectarse a la base de datos. Excepción: " + e.toString());
            dialog.showAndWait();

        }
    }

    private ArrayList dataBaseArrayList(ResultSet resultSet, boolean control) {
        if (control) {
            try {
                ArrayList<State> data = new ArrayList<>();

                while (resultSet.next()) {
                    State state = new State();
                    state.idstate.set(resultSet.getInt("idstate"));
                    state.statekey.set(resultSet.getString("statekey"));
                    state.statename.set(resultSet.getString("statename"));

                    data.add(state);
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
        } else {
            try {
                ArrayList<City> data = new ArrayList<>();

                while (resultSet.next()) {
                    City city = new City();
                    city.idcity.set(resultSet.getInt("idcity"));
                    city.citykey.set(resultSet.getString("citykey"));
                    city.cityname.set(resultSet.getString("cityname"));

                    data.add(city);
                }

                return data;
            } catch (SQLException e) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error");
                dialog.setContentText("Ocurrió un error inesperado. Excepción: " + e.toString());
                dialog.setHeaderText(null);
                dialog.showAndWait();

                return null;
            }
        }
    }

    private Country getSelectedCountry() {
        String countryName = countryComboBox.getSelectionModel().getSelectedItem().toString().split("\\(")[0].trim();

        try {
            connection = new ConnectionMethods(MainViewController.user, MainViewController.pass);
            String sql = String.format("SELECT * FROM country WHERE countryname = '%s';", countryName);
            ResultSet resultSet = connection.executeQuery(sql);

            Country country = null;
            while (resultSet.next()) {
                country = new Country();
                country.idcountry.set(resultSet.getInt("idcountry"));
                country.countryname.set(resultSet.getString("countryname"));
                country.countrykey.set(resultSet.getString("countrykey"));
            }

            resultSet.close();
            connection.close();

            return country;
        } catch (SQLException e) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setContentText("Ocurrió un error inesperado. Excepción: " + e.toString());
            dialog.setHeaderText(null);
            dialog.showAndWait();

            return null;
        }


    }

    private void setDefault() {
        keyTextField.clear();
        keyTextField.setPromptText("Clave");
        nameTextField.clear();
        nameTextField.setPromptText("Nombre");
        registerButton.setDisable(true);
        modButton.setDisable(true);
        modButton.setText("Modificar");
        delButton.setDisable(true);
        clearButton.setText("Limpiar");
    }

}
