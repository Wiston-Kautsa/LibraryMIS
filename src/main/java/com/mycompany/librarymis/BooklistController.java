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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initCol();
        loadData();
    }

    private void initCol() {

        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

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

    @FXML
    private void loadDeleteBook(ActionEvent event) {

        Book selectedBook = tableView.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Book Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a book to delete.");
            alert.showAndWait();

            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Book");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete the book:\n" + selectedBook.getTitle());

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            boolean deleted = DatabaseHandler.getInstance().deleteBook(selectedBook);

            if (deleted) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Book deleted successfully.");
                alert.showAndWait();

                // remove from table without reloading database
                list.remove(selectedBook);

            } else {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to delete the book.");
                alert.showAndWait();
            }
        }
    }

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

        public String getTitle() {
            return title.get();
        }

        public String getId() {
            return id.get();
        }

        public String getAuthor() {
            return author.get();
        }

        public String getPublisher() {
            return publisher.get();
        }

        public Boolean getAvailability() {
            return availability.get();
        }
    }
}