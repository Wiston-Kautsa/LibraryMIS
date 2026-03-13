package com.mycompany.librarymis;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SettingsController implements Initializable {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button save;

    @FXML
    private Button cancel;

    @FXML
    private TextField nDaysWithoutFine;

    @FXML
    private TextField finePerDay;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        loadPreferences();
    }

    /*
    Load existing settings from config file
    */
    private void loadPreferences() {

        Preferences preferences = Preferences.getPreferences();

        if (preferences == null) {
            preferences = new Preferences();
        }

        nDaysWithoutFine.setText(
                String.valueOf(preferences.getnDaysWithoutFine())
        );

        finePerDay.setText(
                String.valueOf(preferences.getFinePerDay())
        );

        username.setText(
                preferences.getUsername()
        );

        password.setText(
                preferences.getPassword()
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings Loaded");
        alert.setHeaderText(null);
        alert.setContentText("Library settings loaded successfully.");
        alert.showAndWait();
    }

    /*
    Save button
    */
    @FXML
    private void saveButtonAction(ActionEvent event) {

        try {

            Preferences preferences = new Preferences();

            preferences.setnDaysWithoutFine(
                    Integer.parseInt(nDaysWithoutFine.getText())
            );

            preferences.setFinePerDay(
                    Float.parseFloat(finePerDay.getText())
            );

            preferences.setUsername(
                    username.getText()
            );

            preferences.setPassword(
                    password.getText()
            );

            Preferences.writePreferences(preferences);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Settings saved successfully.");
            alert.showAndWait();

        } catch (NumberFormatException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter valid numbers for fine settings.");
            alert.showAndWait();
        }
    }

    /*
    Cancel button
    */
    @FXML
    private void cancelButtonAction(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cancelled");
        alert.setHeaderText(null);
        alert.setContentText("Settings changes cancelled.");
        alert.showAndWait();

        ((Button) event.getSource())
                .getScene()
                .getWindow()
                .hide();
    }
}