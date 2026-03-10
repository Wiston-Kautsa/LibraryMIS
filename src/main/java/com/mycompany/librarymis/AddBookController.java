package com.mycompany.librarymis;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddBookController implements Initializable {

    @FXML
    private TextField bookID;

    @FXML
    private TextField bookTitle;

    @FXML
    private TextField bookAuthor;

    @FXML
    private TextField publisher;

    @FXML
    private Button save;

    @FXML
    private Button cancel;

    private DatabaseHandler databaseHandler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        databaseHandler = DatabaseHandler.getInstance();

        // Test database connection
        checkData();
    }

    @FXML
    private void saveButton(ActionEvent event) {
        addBook();
    }

    @FXML
    private void cancelButton(ActionEvent event) {
        closeWindow();
    }

    private void addBook() {

        String id = bookID.getText().trim();
        String title = bookTitle.getText().trim();
        String author = bookAuthor.getText().trim();
        String pub = publisher.getText().trim();

        if (id.isEmpty() || title.isEmpty() || author.isEmpty() || pub.isEmpty()) {
            showAlert("Error", "Please fill all fields.");
            return;
        }

        String query = "INSERT INTO BOOK VALUES ('"
                + id + "','"
                + title + "','"
                + author + "','"
                + pub + "',"
                + "true"
                + ")";

        boolean result = databaseHandler.execAction(query);

        if (result) {

            showAlert("Success", "Book added successfully.");
            clearFields();

            // Show database contents after inserting
            checkData();

        } else {

            showAlert("Database Error", "Could not insert data.");
        }
    }

    private void closeWindow() {

        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    private void clearFields() {

        bookID.clear();
        bookTitle.clear();
        bookAuthor.clear();
        publisher.clear();
    }

    private void showAlert(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void checkData() {

    System.out.println("Checking database...");

    String query = "SELECT title FROM BOOK";

    ResultSet rs = databaseHandler.execQuery(query);

    if (rs == null) {
        System.out.println("Query failed.");
        return;
    }

    try {

        while (rs.next()) {

            String title = rs.getString("title");
            System.out.println("Book: " + title);

        }

        rs.close();   // IMPORTANT

    } catch (SQLException ex) {

        ex.printStackTrace();
    }
    }
}