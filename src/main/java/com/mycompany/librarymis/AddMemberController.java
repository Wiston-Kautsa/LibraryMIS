package com.mycompany.librarymis;

import java.net.URL;
import java.util.ResourceBundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddMemberController implements Initializable {

DatabaseHandler handler;

@FXML
private TextField name;

@FXML
private TextField id;

@FXML
private TextField mobile;

@FXML
private TextField email;

@FXML
private Button save;

@FXML
private Button cancel;

@Override
public void initialize(URL url, ResourceBundle rb) {

    handler = DatabaseHandler.getInstance();

    // Automatically generate Member ID
    id.setText(handler.generateMemberID());
    id.setEditable(false);
}

// ---------------- ADD SINGLE MEMBER ----------------

@FXML
private void saveButton(ActionEvent event) {

    String mName = name.getText().trim();
    String mID = id.getText();
    String mMobile = mobile.getText().trim();
    String mEmail = email.getText().trim();

    boolean flag = mName.isEmpty() || mMobile.isEmpty() || mEmail.isEmpty();

    if (flag) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText("Please enter all fields");
        alert.showAndWait();
        return;
    }

    String query = "INSERT INTO MEMBER VALUES ("
            + "'" + mID + "',"
            + "'" + mName + "',"
            + "'" + mMobile + "',"
            + "'" + mEmail + "'"
            + ")";

    boolean result = handler.execAction(query);

    if (result) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Member Added Successfully");
        alert.showAndWait();

        clearFields();

        // Generate next Member ID
        id.setText(handler.generateMemberID());

    } else {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText("Failed to add member");
        alert.showAndWait();
    }
}

// ---------------- CANCEL BUTTON ----------------

@FXML
private void cancelButton(ActionEvent event) {

    loadMainScreen(event);
}

// ---------------- BULK MEMBER IMPORT ----------------

private void bulkEnrolment(ActionEvent event) {

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select CSV File");

    File file = fileChooser.showOpenDialog(null);

    if (file == null) {
        return;
    }

    int successCount = 0;
    int failCount = 0;

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {

        String line;
        boolean firstLine = true;

        while ((line = br.readLine()) != null) {

            if (firstLine) {
                firstLine = false;
                continue;
            }

            String[] data = line.split(",");

            if (data.length < 3) {
                failCount++;
                continue;
            }

            String memberID = handler.generateMemberID();
            String mName = data[0].trim();
            String mMobile = data[1].trim();
            String mEmail = data[2].trim();

            String query = "INSERT INTO MEMBER VALUES ("
                    + "'" + memberID + "',"
                    + "'" + mName + "',"
                    + "'" + mMobile + "',"
                    + "'" + mEmail + "'"
                    + ")";

            boolean result = handler.execAction(query);

            if (result) {
                successCount++;
            } else {
                failCount++;
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Bulk Import Completed\nMembers added: "
                + successCount + "\nFailed: " + failCount);
        alert.showAndWait();

    } catch (Exception e) {

        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText("Bulk import failed");
        alert.showAndWait();
    }
}

// ---------------- CLEAR INPUT FIELDS ----------------

private void clearFields() {

    name.clear();
    mobile.clear();
    email.clear();
}

// ---------------- LOAD MAIN SCREEN ----------------

private void loadMainScreen(ActionEvent event) {

    try {

        Parent root = FXMLLoader.load(
                getClass().getResource("/com/mycompany/librarymis/main.fxml")
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);

    } catch (Exception e) {
        e.printStackTrace();
    }
}

}