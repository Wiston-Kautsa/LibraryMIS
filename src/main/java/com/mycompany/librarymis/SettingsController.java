package com.mycompany.librarymis;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

public class SettingsController implements Initializable {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private TextField nDaysWithoutFine;

    @FXML
    private TextField finePerDay;

    @FXML
    private Button cancel;

    // store original values
    private String originalUsername;
    private String originalPassword;
    private String originalDays;
    private String originalFine;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPreferences();
    }

    /*
    -----------------------------
    LOAD SETTINGS
    -----------------------------
    */
    private void loadPreferences() {

        Preferences preferences = Preferences.getPreferences();

        if (preferences == null) {
            preferences = new Preferences();
        }

        originalUsername = preferences.getUsername();
        originalPassword = preferences.getPassword();
        originalDays = String.valueOf(preferences.getnDaysWithoutFine());
        originalFine = String.valueOf(preferences.getFinePerDay());

        username.setText(originalUsername);
        password.setText(originalPassword);
        nDaysWithoutFine.setText(originalDays);
        finePerDay.setText(originalFine);
    }

    /*
    -----------------------------
    SAVE SETTINGS
    -----------------------------
    */
    @FXML
    private void saveButtonAction(ActionEvent event) {

        try {

            int days = Integer.parseInt(nDaysWithoutFine.getText());
            float fine = Float.parseFloat(finePerDay.getText());

            Preferences preferences = new Preferences();

            preferences.setUsername(username.getText().trim());
            preferences.setPassword(password.getText().trim());
            preferences.setnDaysWithoutFine(days);
            preferences.setFinePerDay(fine);

            Preferences.writePreferences(preferences);

            // update originals
            originalUsername = username.getText();
            originalPassword = password.getText();
            originalDays = nDaysWithoutFine.getText();
            originalFine = finePerDay.getText();

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Settings Saved",
                    "Settings were saved successfully."
            );

        } catch (NumberFormatException e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Invalid Input",
                    "Fine settings must be numeric values."
            );
        }
    }

    /*
    -----------------------------
    CANCEL BUTTON
    -----------------------------
    */
    @FXML
    private void cancelButtonAction(ActionEvent event) {

        if (settingsChanged()) {

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Unsaved Changes");
            confirm.setHeaderText(null);
            confirm.setContentText(
                    "You have unsaved changes.\nClose without saving?"
            );

            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                goToMain();
            }

        } else {
            goToMain();
        }
    }

    /*
    -----------------------------
    CHECK IF SETTINGS CHANGED
    -----------------------------
    */
    private boolean settingsChanged() {

        return !username.getText().equals(originalUsername)
                || !password.getText().equals(originalPassword)
                || !nDaysWithoutFine.getText().equals(originalDays)
                || !finePerDay.getText().equals(originalFine);
    }

    /*
    -----------------------------
    RETURN TO MAIN
    -----------------------------
    */
    private void goToMain() {

        try {

            Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));

            Stage stage = (Stage) cancel.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    -----------------------------
    ALERT HELPER
    -----------------------------
    */
    private void showAlert(Alert.AlertType type, String title, String message) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}