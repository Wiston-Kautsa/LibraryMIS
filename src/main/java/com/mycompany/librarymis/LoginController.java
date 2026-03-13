package com.mycompany.librarymis;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

public class LoginController implements Initializable {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Label titleLabel;

    private Preferences preference;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        preference = Preferences.getPreferences();
    }

    /*
    --------------------------------
    LOGIN BUTTON
    --------------------------------
    */

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {

        titleLabel.setText("Library Assistant Login");
        titleLabel.setStyle("-fx-background-color:black; -fx-text-fill:white;");

        String uname = username.getText().trim();
        String pword = password.getText().trim();

        if (uname.isEmpty() || pword.isEmpty()) {

            titleLabel.setText("Enter Username & Password");
            titleLabel.setStyle("-fx-background-color:#d32f2f; -fx-text-fill:white;");
            return;
        }

        if (uname.equals(preference.getUsername()) &&
            pword.equals(preference.getPassword())) {

            loadMain();

        } else {

            titleLabel.setText("Invalid Credentials");
            titleLabel.setStyle("-fx-background-color:#d32f2f; -fx-text-fill:white;");
        }
    }

    /*
    --------------------------------
    CANCEL BUTTON
    --------------------------------
    */

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {

        System.exit(0);
    }

    /*
    --------------------------------
    LOAD MAIN WINDOW
    --------------------------------
    */

    private void loadMain() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mycompany/librarymis/Main.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) username.getScene().getWindow();
            stage.setTitle("Library Assistant");
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (IOException ex) {

            Logger.getLogger(LoginController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}