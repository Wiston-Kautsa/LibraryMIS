package com.mycompany.librarymis;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AddBookController implements Initializable {

    @FXML
    private TextField bookTitle;

    @FXML
    private TextField bookID;

    @FXML
    private TextField bookAuthor;

    @FXML
    private TextField Publisher;

    @FXML
    private Button save;

    @FXML
    private Button cancel;

    DatabaseHandler databaseHandler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
    }

    @FXML
    private void saveButton(ActionEvent event) {

        String title = bookTitle.getText();
        String id = bookID.getText();
        String author = bookAuthor.getText();
        String publisher = Publisher.getText();

        if (title.isEmpty() || id.isEmpty() || author.isEmpty() || publisher.isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please fill all fields.");
            alert.showAndWait();
            return;
        }

        String query = "INSERT INTO BOOK VALUES ('"
                + id + "','"
                + title + "','"
                + author + "','"
                + publisher + "',"
                + "true"
                + ")";

        if (databaseHandler.execAction(query)) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Book Added Successfully");
            alert.showAndWait();

            clearFields();

        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Operation Failed");
            alert.showAndWait();

        }
    }

    @FXML
    private void cancelButton(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        bookTitle.clear();
        bookID.clear();
        bookAuthor.clear();
        Publisher.clear();
    }
}