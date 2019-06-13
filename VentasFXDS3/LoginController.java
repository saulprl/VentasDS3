package VentasFXDS3;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LoginController {

    private final String user = "user"; // Usuario de la aplicación.
    private final String pass = "pass"; // Contraseña de la aplicación.

    @FXML
    private TextField userTextField;

    @FXML
    private PasswordField passTextField;

    @FXML
    private Button loginButton;

    @FXML
    private ImageView imageView;

    @FXML
    private void loginButtonPressed(ActionEvent event) {
        if (userTextField.getText().trim().equals(this.user) && passTextField.getText().trim().equals(this.pass)) {
            MainViewController.user = "user"; // Usuario de la base de datos.
            MainViewController.pass = "pass"; // Contraseña de la base de datos.

            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Sesión iniciada");
            dialog.setHeaderText(null);
            dialog.setContentText("¡Bienvenido!");
            dialog.showAndWait();

            Stage stage = (Stage) (imageView.getScene().getWindow());
            stage.close();
        } else {

            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de inicio de sesión");
            dialog.setHeaderText(null);
            dialog.setContentText("Los datos son incorrectos.");
            dialog.showAndWait();

        }
    }

    @FXML
    private void cancelButtonPressed(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void initialize() {
        loginButton.setDisable(true);

        imageView.setImage(new Image(getClass().getResource("/Resources/login.png").toString()));

        userTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (userTextField.getText().isEmpty()) {
                loginButton.setDisable(true);
            } else {
                loginButton.setDisable(false);
            }
        });
    }

}
