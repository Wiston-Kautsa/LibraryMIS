package com.mycompany.librarymis;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    // Automatically generate Book ID
    bookID.setText(databaseHandler.generateBookID());
    bookID.setEditable(false);

    // Test database connection
    checkData();
}

@FXML
private void saveButton(ActionEvent event) {
    addBook();
}

@FXML
private void cancelButton(ActionEvent event) {
    loadMainScreen(event);
}

// ---------------- ADD SINGLE BOOK ----------------

private void addBook() {

    String id = bookID.getText();
    String title = bookTitle.getText().trim();
    String author = bookAuthor.getText().trim();
    String pub = publisher.getText().trim();

    if (title.isEmpty() || author.isEmpty() || pub.isEmpty()) {
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

        // Generate next Book ID
        bookID.setText(databaseHandler.generateBookID());

        checkData();

    } else {

        showAlert("Database Error", "Could not insert data.");
    }
}

// ---------------- BULK BOOK IMPORT ----------------

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

            String id = databaseHandler.generateBookID();
            String title = data[0].trim();
            String author = data[1].trim();
            String pub = data[2].trim();

            String query = "INSERT INTO BOOK VALUES ('"
                    + id + "','"
                    + title + "','"
                    + author + "','"
                    + pub + "',"
                    + "true"
                    + ")";

            boolean result = databaseHandler.execAction(query);

            if (result) {
                successCount++;
            } else {
                failCount++;
            }
        }

        showAlert("Bulk Import Completed",
                "Books added: " + successCount + "\nFailed: " + failCount);

    } catch (Exception e) {

        e.printStackTrace();
        showAlert("Error", "Failed to import books.");
    }
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

// ---------------- CLEAR FIELDS ----------------

private void clearFields() {

    bookTitle.clear();
    bookAuthor.clear();
    publisher.clear();
}

// ---------------- ALERT MESSAGE ----------------

private void showAlert(String title, String message) {

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

// ---------------- DATABASE CHECK ----------------

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

        rs.close();

    } catch (SQLException ex) {

        ex.printStackTrace();
    }
}

}