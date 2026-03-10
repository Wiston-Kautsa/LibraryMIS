package com.mycompany.librarymis;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    ObservableList<Book> list = FXCollections.observableArrayList();

    DatabaseHandler handler = DatabaseHandler.getInstance();

    String query = "SELECT * FROM BOOK";
    ResultSet rs = handler.execQuery(query);

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

        Logger.getLogger(BooklistController.class.getName()).log(Level.SEVERE, null, ex);

    }

    tableView.getItems().setAll(list);

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
