package VentasFXDS3;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {

    static String user, pass;

    @FXML
    private MenuBar menuBar;

    @FXML
    private void menuItemsPressed(ActionEvent event) {
        try {

            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/FXMLViews/CatalogView.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Catálogo");
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(menuBar.getScene().getWindow());
            stage.show();

        } catch (IOException e) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ocurrió un error inesperado. Excepción: " + e.toString());
            dialog.showAndWait();

        }
    }

    @FXML
    private void menuClientsPressed(ActionEvent event) {
        try {

            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/FXMLViews/ClientsView.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Clientes");
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(menuBar.getScene().getWindow());
            stage.show();

        } catch (IOException e) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setContentText("Ocurrió un error inesperado. Excepción: " + e.toString());
            dialog.setHeaderText(null);
            dialog.showAndWait();

        }
    }

    @FXML
    private void menuEmpPressed(ActionEvent event) throws Exception {
        try {

            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/FXMLViews/EmployeesView.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Empleados");
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(menuBar.getScene().getWindow());
            stage.show();

        } catch (IOException e) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setContentText("Ocurrió un error inesprado. Excepción: " + e.toString());
            dialog.setHeaderText(null);
            dialog.showAndWait();

        }
    }

    @FXML
    private void menuBranchesPressed(ActionEvent event) {
        try {

            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/FXMLViews/BranchView.fxml"));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(menuBar.getScene().getWindow());
            stage.show();

        } catch (IOException e) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ocurrió un error inesperado. Excepción: " + e.toString());
            dialog.showAndWait();

        }
    }

    @FXML
    private void menuLocationPressed(ActionEvent event) {
        try {

            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/FXMLViews/EntitiesView.fxml"));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(menuBar.getScene().getWindow());
            stage.show();

        } catch (IOException e) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ocurrió un error inesperado. Excepción: " + e.toString());
            dialog.showAndWait();

        }
    }

    @FXML
    private void menuExitPressed(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void menuOrdersPressed(ActionEvent event) {
        try {

            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/FXMLViews/SalesView.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Ventas");
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(menuBar.getScene().getWindow());
            stage.show();

        } catch (IOException e) {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ocurrió un error inesperado. Excepción: " + e.toString());
            dialog.showAndWait();

        }
    }

    @FXML
    private void initialize() {

        /*Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Iniciar sesión");
        dialog.setHeaderText("Inicia sesión");

        dialog.setGraphic(new ImageView(this.getClass().getResource("/Resources/login.png").toString()));

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField userTextField = new TextField();
        userTextField.setPromptText("Nombre de usuario");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");

        gridPane.add(new Label("Nombre de usuario:"), 0, 0);
        gridPane.add(userTextField, 1, 0);
        gridPane.add(new Label("Contraseña:"), 0, 1);
        gridPane.add(passwordField, 1, 1);

        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        userTextField.textProperty().addListener((observable, oldValue, newValue) -> {
           loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(() -> userTextField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(userTextField.getText(), passwordField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
            user = usernamePassword.getKey();
            pass = usernamePassword.getValue();
        });*/

    }

}
