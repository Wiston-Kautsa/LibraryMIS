package com.mycompany.librarymis;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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

}

@FXML
private void saveButton(ActionEvent event) {

    String mName = name.getText();
    String mID = id.getText();
    String mMobile = mobile.getText();
    String mEmail = email.getText();

    boolean flag = mName.isEmpty() || mID.isEmpty() || mMobile.isEmpty() || mEmail.isEmpty();

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

    System.out.println(query);

    if (handler.execAction(query)) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Member Added Successfully");
        alert.showAndWait();

        clearFields();

    } else {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText("Failed to add member");
        alert.showAndWait();
    }

}

@FXML
private void cancelButton(ActionEvent event) {

    closeWindow();

}

private void clearFields() {

    name.clear();
    id.clear();
    mobile.clear();
    email.clear();

}

private void closeWindow() {

    Stage stage = (Stage) cancel.getScene().getWindow();
    stage.close();

}

}
