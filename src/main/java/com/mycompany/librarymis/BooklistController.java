package com.mycompany.librarymis;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.layout.AnchorPane;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.event.ActionEvent;

import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import javafx.scene.layout.GridPane;

public class BooklistController implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TableView<Book> tableView;

    @FXML
    private TableColumn<Book, String> titleCol;

    @FXML
    private TableColumn<Book, String> idCol;

    @FXML
    private TableColumn<Book, String> authorCol;

    @FXML
    private TableColumn<Book, String> publisherCol;

    @FXML
    private TableColumn<Book, Boolean> availabilityCol;

    private ObservableList<Book> list = FXCollections.observableArrayList();

    @FXML
    private MenuItem deleteBook;

    @FXML
    private MenuItem editBook;

    @FXML
    private MenuItem refreshBook;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initCol();
        loadData();
    }

    // ---------------- INITIALIZE TABLE COLUMNS ----------------

    private void initCol() {

        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

    // ---------------- LOAD DATA ----------------

    private void loadData() {

        list.clear();

        DatabaseHandler handler = DatabaseHandler.getInstance();

        String query = "SELECT * FROM BOOK";
        ResultSet rs = handler.execQuery(query);

        if (rs == null) {
            System.out.println("Database query failed.");
            return;
        }

        try {

            while (rs.next()) {

                String title = rs.getString("title");
                String id = rs.getString("id");
                String author = rs.getString("author");
                String publisher = rs.getString("publisher");
                Boolean avail = rs.getBoolean("isAvail");

                list.add(new Book(title, id, author, publisher, avail));
            }

        } catch (SQLException ex) {

            Logger.getLogger(BooklistController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

        tableView.setItems(list);
    }

    // ---------------- DELETE BOOK ----------------

    @FXML
    private void loadDeleteBook(ActionEvent event) {

        Book selectedBook = tableView.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {

            showAlert(Alert.AlertType.WARNING, "No Book Selected", "Please select a book to delete.");
            return;
        }

        if (DatabaseHandler.getInstance().isBookAlreadyIssued(selectedBook)) {

            showAlert(Alert.AlertType.ERROR, "Book Issued",
                    "This book is currently issued and cannot be deleted.");

            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);

        confirm.setTitle("Delete Book");
        confirm.setContentText("Delete book: " + selectedBook.getTitle());

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            boolean deleted = DatabaseHandler.getInstance().deleteBook(selectedBook);

            if (deleted) {

                showAlert(Alert.AlertType.INFORMATION, "Success", "Book deleted successfully.");

                loadData();

            } else {

                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete the book.");
            }
        }
    }

    // ---------------- EDIT BOOK (MULTI FIELD WINDOW) ----------------

    @FXML
    private void loadEditBook(ActionEvent event) {

        Book selectedBook = tableView.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {

            showAlert(Alert.AlertType.WARNING, "No Book Selected", "Please select a book to edit.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Book");

        ButtonType saveButton = new ButtonType("Save", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField titleField = new TextField(selectedBook.getTitle());
        TextField authorField = new TextField(selectedBook.getAuthor());
        TextField publisherField = new TextField(selectedBook.getPublisher());

        grid.add(new Label("Book Title:"), 0, 0);
        grid.add(titleField, 1, 0);

        grid.add(new Label("Book Author:"), 0, 1);
        grid.add(authorField, 1, 1);

        grid.add(new Label("Publisher:"), 0, 2);
        grid.add(publisherField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == saveButton) {

            Book updatedBook = new Book(
                    titleField.getText(),
                    selectedBook.getId(),
                    authorField.getText(),
                    publisherField.getText(),
                    selectedBook.getAvailability()
            );

            boolean success = DatabaseHandler.getInstance().updateBook(updatedBook);

            if (success) {

                showAlert(Alert.AlertType.INFORMATION, "Success", "Book updated successfully.");

                loadData();

            } else {

                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update book.");
            }
        }
    }

    // ---------------- REFRESH TABLE ----------------

    @FXML
    private void loadRefreshBook(ActionEvent event) {

        loadData();

        showAlert(Alert.AlertType.INFORMATION, "Refreshed", "Book list refreshed.");
    }

    // ---------------- ALERT HELPER ----------------

    private void showAlert(Alert.AlertType type, String title, String message) {

        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    // ---------------- BOOK MODEL ----------------

    public static class Book {

        private final SimpleStringProperty title;
        private final SimpleStringProperty id;
        private final SimpleStringProperty author;
        private final SimpleStringProperty publisher;
        private final SimpleBooleanProperty availability;

        public Book(String title, String id, String author, String publisher, Boolean availability) {

            this.title = new SimpleStringProperty(title);
            this.id = new SimpleStringProperty(id);
            this.author = new SimpleStringProperty(author);
            this.publisher = new SimpleStringProperty(publisher);
            this.availability = new SimpleBooleanProperty(availability);
        }

        public String getTitle() { return title.get(); }
        public String getId() { return id.get(); }
        public String getAuthor() { return author.get(); }
        public String getPublisher() { return publisher.get(); }
        public Boolean getAvailability() { return availability.get(); }
    }
}