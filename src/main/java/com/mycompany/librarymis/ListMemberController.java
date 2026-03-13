package com.mycompany.librarymis;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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

    @FXML
    private MenuItem deleteMember;

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

    @FXML
    private void loadDeleteMember(ActionEvent event) {

        Member selectedMember = tableView.getSelectionModel().getSelectedItem();

        if (selectedMember == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Member Selected");
            alert.setContentText("Please select a member to delete.");
            alert.showAndWait();
            return;
        }

        if (DatabaseHandler.getInstance().isMemberHasBooks(selectedMember)) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Member Has Borrowed Books");
            alert.setContentText("This member still has borrowed books and cannot be deleted.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Member");
        confirm.setContentText("Delete member: " + selectedMember.getName());

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            boolean deleted = DatabaseHandler.getInstance().deleteMember(selectedMember);

            if (deleted) {
                list.remove(selectedMember);
            }
        }
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

        public String getName() { return name.get(); }
        public String getId() { return id.get(); }
        public String getMobile() { return mobile.get(); }
        public String getEmail() { return email.get(); }
        public String getBorrowed() { return borrowed.get(); }
    }
}