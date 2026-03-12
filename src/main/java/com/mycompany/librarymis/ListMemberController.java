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

public class ListMemberController implements Initializable {

    @FXML
    private TableView<Member> tableView;

    @FXML
    private TableColumn<Member, String> nameCol;

    @FXML
    private TableColumn<Member, String> idCol;

    @FXML
    private TableColumn<Member, String> mobileCol;

    @FXML
    private TableColumn<Member, String> emailCol;

    @FXML
    private TableColumn<Member, String> borrowedCol;

    private ObservableList<Member> list = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initCol();
        loadData();
    }

    private void initCol() {

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        borrowedCol.setCellValueFactory(new PropertyValueFactory<>("borrowed"));
    }

    private void loadData() {

        list.clear();

        DatabaseHandler handler = DatabaseHandler.getInstance();

        String query =
                "SELECT m.id, m.name, m.mobile, m.email, " +
                "GROUP_CONCAT(i.bookID, ', ') AS borrowed " +
                "FROM MEMBER m " +
                "LEFT JOIN ISSUE i ON m.id = i.memberID " +
                "GROUP BY m.id";

        ResultSet rs = handler.execQuery(query);

        if (rs == null) {
            System.out.println("Query execution failed.");
            return;
        }

        try {

            while (rs.next()) {

                String name = rs.getString("name");
                String id = rs.getString("id");
                String mobile = rs.getString("mobile");
                String email = rs.getString("email");

                String borrowed = rs.getString("borrowed");

                if (borrowed == null) {
                    borrowed = "None";
                }

                list.add(new Member(name, id, mobile, email, borrowed));
            }

        } catch (SQLException ex) {

            Logger.getLogger(ListMemberController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

        tableView.setItems(list);
    }

    public static class Member {

        private final SimpleStringProperty name;
        private final SimpleStringProperty id;
        private final SimpleStringProperty mobile;
        private final SimpleStringProperty email;
        private final SimpleStringProperty borrowed;

        public Member(String name, String id, String mobile, String email, String borrowed) {

            this.name = new SimpleStringProperty(name);
            this.id = new SimpleStringProperty(id);
            this.mobile = new SimpleStringProperty(mobile);
            this.email = new SimpleStringProperty(email);
            this.borrowed = new SimpleStringProperty(borrowed);
        }

        public String getName() {
            return name.get();
        }

        public String getId() {
            return id.get();
        }

        public String getMobile() {
            return mobile.get();
        }

        public String getEmail() {
            return email.get();
        }

        public String getBorrowed() {
            return borrowed.get();
        }
    }
}