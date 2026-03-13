package com.mycompany.librarymis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class MainController {

    /* ================= ROOT ================= */

    @FXML
    private AnchorPane rootPane;

    @FXML
    private BorderPane mainBorderPane;

    /* ================= MENU ================= */

    @FXML
    private MenuItem bulkMember;

    @FXML
    private MenuItem bulkAddBook;

    /* ================= BOOK ISSUE ================= */

    @FXML
    private TextField bookidinput;

    @FXML
    private Text bookname;

    @FXML
    private Text bookauthor;

    @FXML
    private Text bookstatus;

    /* ================= MEMBER INFO ================= */

    @FXML
    private TextField memberidinput;

    @FXML
    private Text membername;

    @FXML
    private Text membercontact;

    /* ================= LIST ================= */

    @FXML
    private ListView<String> issuedatalist;

    @FXML
    private TextField bookID2;

    /* ================= LAYOUT ================= */

    @FXML
    private HBox bookinfo;

    @FXML
    private HBox memberinfo;

    /* ================= BUTTONS ================= */

    @FXML
    private Button issue;

    @FXML
    private Button renewloadSubmissionOp;

    @FXML
    private Button submission;

    @FXML
    private Button addMember;

    @FXML
    private Button addBook;

    @FXML
    private Button viewMember;

    @FXML
    private Button viewBook;

    @FXML
    private Button settings;

    /* ================= SERVICES ================= */

    private final BulkService bulkService = new BulkService();

    /* ================= INITIALIZE ================= */

    public void initialize() {

        System.out.println("Library MIS Dashboard Loaded");

    }

    /* ================= SCREEN LOADER ================= */

    private void loadUI(String fxml) {

        try {

            AnchorPane pane = FXMLLoader.load(
                    getClass().getResource("/com/mycompany/librarymis/" + fxml)
            );

            mainBorderPane.setCenter(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ================= SIDE BUTTON ACTIONS ================= */

    @FXML
    private void loadAddMember(ActionEvent event) {
        loadUI("AddMember.fxml");
    }

    @FXML
    private void loadAddBook(ActionEvent event) {
        loadUI("AddBook.fxml");
    }

    @FXML
    private void loadViewMember(ActionEvent event) {
        loadUI("ListMember.fxml");
    }

    @FXML
    private void loadViewBook(ActionEvent event) {
        loadUI("Booklist.fxml");
    }

    @FXML
    private void loadSettings(ActionEvent event) {
        loadUI("Settings.fxml");
    }

    /* ================= MENU ACTIONS ================= */

    @FXML
    private void handleMenuAddBook(ActionEvent event) {
        loadUI("AddBook.fxml");
    }

    @FXML
    private void handleMenuAddMember(ActionEvent event) {
        loadUI("AddMember.fxml");
    }

    @FXML
    private void handleMenuViewBook(ActionEvent event) {
        loadUI("Booklist.fxml");
    }

    @FXML
    private void handleMenuViewMember(ActionEvent event) {
        loadUI("ListMember.fxml");
    }

    @FXML
    private void handleMenuViewBorrowedBooks(ActionEvent event) {
        loadUI("BorrowedBooks.fxml");
    }

    @FXML
    private void handleMenuClose(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleMenuFullScreen(ActionEvent event) {

        showAlert("Fullscreen", "Fullscreen feature not implemented yet.");

    }

    /* ================= BULK IMPORT ================= */

    @FXML
    private void handleBulkAddMember(ActionEvent event) {

        bulkService.importMembers(rootPane.getScene().getWindow());

        showAlert("Bulk Import", "Members successfully imported into the database.");
    }

    @FXML
    private void handleBulkAddBook(ActionEvent event) {

        bulkService.importBooks(rootPane.getScene().getWindow());

        showAlert("Bulk Import", "Books successfully imported into the database.");
    }

    /* ================= BOOK LOOKUP ================= */

    @FXML
    private void loadBookInfo(ActionEvent event) {

        String id = bookidinput.getText();

        if (id == null || id.isBlank()) {
            showAlert("Error", "Enter Book ID.");
            return;
        }

        bookname.setText("Book lookup from DB not implemented yet");
        bookauthor.setText("-");
        bookstatus.setText("-");
    }

    /* ================= MEMBER LOOKUP ================= */

    @FXML
    private void loadMemberInfo(ActionEvent event) {

        String id = memberidinput.getText();

        if (id == null || id.isBlank()) {
            showAlert("Error", "Enter Member ID.");
            return;
        }

        membername.setText("Member lookup from DB not implemented yet");
        membercontact.setText("-");
    }

    /* ================= ISSUE BOOK ================= */

    @FXML
    private void loadIssueBook(ActionEvent event) {

        String bookId = bookidinput.getText();
        String memberId = memberidinput.getText();

        if (bookId.isBlank() || memberId.isBlank()) {

            showAlert("Error", "Enter Book ID and Member ID.");
            return;
        }

        issuedatalist.getItems()
                .add("Book " + bookId + " issued to Member " + memberId);

        bookstatus.setText("Issued");
    }

    /* ================= RENEW ================= */

    @FXML
    private void loadRenewOp(ActionEvent event) {

        String bookId = bookID2.getText();

        if (bookId.isBlank()) {

            showAlert("Error", "Enter Book ID.");
            return;
        }

        issuedatalist.getItems().add("Book " + bookId + " renewed.");
    }

    /* ================= RETURN ================= */

    @FXML
    private void loadSubmissionOp(ActionEvent event) {

        String bookId = bookID2.getText();

        if (bookId.isBlank()) {

            showAlert("Error", "Enter Book ID.");
            return;
        }

        issuedatalist.getItems().add("Book " + bookId + " returned.");
    }

    /* ================= TEXTFIELD ENTER ACTION ================= */

    @FXML
    private void loadBookID2(ActionEvent event) {

        String bookId = bookID2.getText();

        if (bookId == null || bookId.isBlank()) {

            showAlert("Error", "Enter Book ID.");
            return;
        }

        issuedatalist.getItems().add("Selected Book: " + bookId);
    }

    /* ================= ALERT ================= */

    private void showAlert(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

}