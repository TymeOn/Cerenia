package com.anmvg.cerenia;

import com.anmvg.cerenia.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.FileNotFoundException;
import java.io.IOException;


public class LoginController {

    @FXML
    private Button backButton;

    @FXML
    private TextFlow errorAlert;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    public void initialize() {
        backButton.setGraphic(new FontIcon("fa-arrow-circle-left"));
        backButton.setOnAction(event -> {
            navigateToDashboard();
        });

        errorAlert.setVisible(false);

        loginButton.setOnAction(event -> {
            if (AuthService.getInstance().login(emailField.getText(), passwordField.getText())) {
                navigateToDashboard();
            } else {
                errorAlert.setVisible(true);
            }
        });
    }

    public void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 1280, 720);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            stage.setScene(scene);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

}
