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
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import javafx.scene.layout.GridPane;

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

    @FXML
    private MenuItem editMember1;

    @FXML
    private MenuItem refreshMember;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }

    // ---------------- INITIALIZE TABLE COLUMNS ----------------

    private void initCol() {

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        borrowedCol.setCellValueFactory(new PropertyValueFactory<>("borrowed"));
    }

    // ---------------- LOAD DATA FROM DATABASE ----------------

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

    // ---------------- DELETE MEMBER ----------------

    @FXML
    private void loadDeleteMember(ActionEvent event) {

        Member selectedMember = tableView.getSelectionModel().getSelectedItem();

        if (selectedMember == null) {

            showAlert("No Member Selected", "Please select a member to delete.");
            return;
        }

        if (DatabaseHandler.getInstance().isMemberHasBooks(selectedMember)) {

            showAlert("Member Has Borrowed Books",
                    "This member still has borrowed books and cannot be deleted.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);

        confirm.setTitle("Delete Member");
        confirm.setContentText("Delete member: " + selectedMember.getName());

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            boolean deleted = DatabaseHandler.getInstance().deleteMember(selectedMember);

            if (deleted) {

                showAlert("Success", "Member deleted successfully.");

                loadData();
            }
        }
    }

    // ---------------- EDIT MEMBER (MULTI FIELD WINDOW) ----------------

    @FXML
    private void loadEditMember(ActionEvent event) {

        Member selectedMember = tableView.getSelectionModel().getSelectedItem();

        if (selectedMember == null) {

            showAlert("No Member Selected", "Please select a member to edit.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Member");

        ButtonType saveButton = new ButtonType("Save", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField(selectedMember.getName());
        TextField mobileField = new TextField(selectedMember.getMobile());
        TextField emailField = new TextField(selectedMember.getEmail());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Mobile:"), 0, 1);
        grid.add(mobileField, 1, 1);

        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == saveButton) {

            Member updatedMember = new Member(
                    nameField.getText(),
                    selectedMember.getId(),
                    mobileField.getText(),
                    emailField.getText(),
                    selectedMember.getBorrowed()
            );

            boolean success = DatabaseHandler.getInstance().updateMember(updatedMember);

            if (success) {

                showAlert("Success", "Member updated successfully.");

                loadData();
            } else {

                showAlert("Error", "Failed to update member.");
            }
        }
    }

    // ---------------- REFRESH MEMBER TABLE ----------------

    @FXML
    private void loadRefreshMember(ActionEvent event) {

        loadData();

        showAlert("Refreshed", "Member list refreshed.");
    }

    // ---------------- ALERT HELPER ----------------

    private void showAlert(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    // ---------------- MEMBER MODEL ----------------

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