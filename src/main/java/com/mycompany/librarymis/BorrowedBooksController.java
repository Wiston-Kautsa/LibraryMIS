package com.mycompany.librarymis;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class BorrowedBooksController implements Initializable {

    @FXML
    private TableView<BorrowedBook> tableView;

    @FXML
    private TableColumn<BorrowedBook, String> bookIDCol;

    @FXML
    private TableColumn<BorrowedBook, String> titleCol;

    @FXML
    private TableColumn<BorrowedBook, String> memberIDCol;

    @FXML
    private TableColumn<BorrowedBook, String> memberNameCol;

    @FXML
    private TableColumn<BorrowedBook, String> issueTimeCol;

    private ObservableList<BorrowedBook> list = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initCol();
        loadData();
    }

    private void initCol() {

        bookIDCol.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        memberIDCol.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        memberNameCol.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        issueTimeCol.setCellValueFactory(new PropertyValueFactory<>("issueTime"));
    }

    private void loadData() {

        list.clear();

        DatabaseHandler handler = DatabaseHandler.getInstance();

        String query =
                "SELECT b.id AS bookID, b.title, m.id AS memberID, m.name, i.issueTime "
              + "FROM ISSUE i "
              + "JOIN BOOK b ON i.bookID = b.id "
              + "JOIN MEMBER m ON i.memberID = m.id";

        ResultSet rs = handler.execQuery(query);

        try {

            while (rs.next()) {

                String bookID = rs.getString("bookID");
                String title = rs.getString("title");
                String memberID = rs.getString("memberID");
                String name = rs.getString("name");
                String issueTime = rs.getString("issueTime");

                list.add(new BorrowedBook(bookID, title, memberID, name, issueTime));
            }

        } catch (SQLException ex) {

            Logger.getLogger(BorrowedBooksController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

        tableView.setItems(list);
    }

    public static class BorrowedBook {

        private final SimpleStringProperty bookID;
        private final SimpleStringProperty title;
        private final SimpleStringProperty memberID;
        private final SimpleStringProperty memberName;
        private final SimpleStringProperty issueTime;

        public BorrowedBook(String bookID, String title, String memberID, String memberName, String issueTime) {

            this.bookID = new SimpleStringProperty(bookID);
            this.title = new SimpleStringProperty(title);
            this.memberID = new SimpleStringProperty(memberID);
            this.memberName = new SimpleStringProperty(memberName);
            this.issueTime = new SimpleStringProperty(issueTime);
        }

        public String getBookID() { return bookID.get(); }
        public String getTitle() { return title.get(); }
        public String getMemberID() { return memberID.get(); }
        public String getMemberName() { return memberName.get(); }
        public String getIssueTime() { return issueTime.get(); }
    }
}