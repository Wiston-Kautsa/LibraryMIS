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
import javafx.stage.StageStyle;

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

        String uname = username.getText();
        String pword = password.getText();

        if (uname.equals(preference.getUsername()) &&
            pword.equals(preference.getPassword())) {

            closeStage();
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
    CLOSE LOGIN WINDOW
    --------------------------------
    */

    private void closeStage() {

        Stage stage = (Stage) username.getScene().getWindow();
        stage.close();
    }

    /*
    --------------------------------
    LOAD MAIN WINDOW
    --------------------------------
    */

    private void loadMain() {

        try {

            Parent parent = FXMLLoader.load(
                    getClass().getResource("/com/mycompany/librarymis/Main.fxml")
            );

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Library Assistant");
            stage.setScene(new Scene(parent));
            stage.show();

        } catch (IOException ex) {

            Logger.getLogger(LoginController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}