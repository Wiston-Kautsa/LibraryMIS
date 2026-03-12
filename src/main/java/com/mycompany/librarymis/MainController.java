package com.mycompany.librarymis;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainController implements Initializable {

    @FXML private Button addMember;
    @FXML private Button addBook;
    @FXML private Button viewMember;
    @FXML private Button viewBook;
    @FXML private Button issue;
    @FXML private Button submission;

    @FXML private HBox bookinfo;
    @FXML private HBox memberinfo;

    @FXML private TextField bookidinput;
    @FXML private TextField memberidinput;
    @FXML private TextField bookID2;

    @FXML private Text bookname;
    @FXML private Text bookauthor;
    @FXML private Text bookstatus;

    @FXML private Text membername;
    @FXML private Text membercontact;

    @FXML private ListView<String> issuedatalist;

    DatabaseHandler databaseHandler;

    Boolean isReadyForSubmission = false;

    @FXML private Button renewloadSubmissionOp;
    @FXML private Button settings;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        databaseHandler = DatabaseHandler.getInstance();
        bookinfo.setVisible(true);
        memberinfo.setVisible(true);
    }

    /*
    --------------------------------
    Window Loader
    --------------------------------
    */

    private void loadWindow(String loc, String title) {

        try {

            Parent parent = FXMLLoader.load(getClass().getResource(loc));

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();

        } catch (IOException ex) {

            Logger.getLogger(MainController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    /*
    --------------------------------
    Navigation Buttons
    --------------------------------
    */

    @FXML
    public void loadAddMember(ActionEvent event) {
        loadWindow("/com/mycompany/librarymis/AddMember.fxml", "Add New Member");
    }

    @FXML
    public void loadAddBook(ActionEvent event) {
        loadWindow("/com/mycompany/librarymis/AddBook.fxml", "Add New Book");
    }

    @FXML
    public void loadViewMember(ActionEvent event) {
        loadWindow("/com/mycompany/librarymis/ListMember.fxml", "Member List");
    }

    @FXML
    public void loadViewBook(ActionEvent event) {
        loadWindow("/com/mycompany/librarymis/Booklist.fxml", "Book List");
    }

    /*
    --------------------------------
    Load Book Information
    --------------------------------
    */

    @FXML
    private void loadBookInfo(ActionEvent event) {

        String id = bookidinput.getText().trim();

        if (id.isEmpty()) {
            bookname.setText("Enter Book ID");
            return;
        }

        String query = "SELECT * FROM BOOK WHERE LOWER(id)=LOWER('" + id + "')";
        ResultSet rs = databaseHandler.execQuery(query);

        try {

            if (rs != null && rs.next()) {

                String bName = rs.getString("title");
                String bAuthor = rs.getString("author");
                boolean bStatus = rs.getBoolean("isAvail");

                bookname.setText(bName);
                bookauthor.setText(bAuthor);
                bookstatus.setText(bStatus ? "Available" : "Not Available");

            } else {

                bookname.setText("No Such Book Available");
                bookauthor.setText("");
                bookstatus.setText("");
            }

        } catch (SQLException ex) {

            Logger.getLogger(MainController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    /*
    --------------------------------
    Load Member Information
    --------------------------------
    */

    @FXML
    private void loadMemberInfo(ActionEvent event) {

        String id = memberidinput.getText().trim();

        if (id.isEmpty()) {
            membername.setText("Enter Member ID");
            return;
        }

        String query = "SELECT * FROM MEMBER WHERE LOWER(id)=LOWER('" + id + "')";
        ResultSet rs = databaseHandler.execQuery(query);

        try {

            if (rs != null && rs.next()) {

                String mName = rs.getString("name");
                String mContact = rs.getString("mobile");

                membername.setText(mName);
                membercontact.setText(mContact);

            } else {

                membername.setText("No Such Member Available");
                membercontact.setText("");
            }

        } catch (SQLException ex) {

            Logger.getLogger(MainController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    /*
    --------------------------------
    Issue Book
    --------------------------------
    */

    @FXML
    private void loadIssueBook(ActionEvent event) {

        String bookID = bookidinput.getText().trim();
        String memberID = memberidinput.getText().trim();

        if (bookID.isEmpty() || memberID.isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter both Book ID and Member ID.");
            alert.showAndWait();
            return;
        }

        try {

            ResultSet rs = databaseHandler.execQuery(
                    "SELECT isAvail FROM BOOK WHERE LOWER(id)=LOWER('" + bookID + "')");

            if (rs == null || !rs.next()) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Book Not Found");
                alert.setHeaderText(null);
                alert.setContentText("The entered Book ID does not exist.");
                alert.showAndWait();
                return;
            }

            boolean available = rs.getBoolean("isAvail");

            if (!available) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Book Not Available");
                alert.setHeaderText(null);
                alert.setContentText("This book has already been issued.");
                alert.showAndWait();
                return;
            }

            String insertQuery =
                    "INSERT INTO ISSUE(memberID, bookID) VALUES ('"
                    + memberID + "','" + bookID + "')";

            String updateQuery =
                    "UPDATE BOOK SET isAvail=false WHERE LOWER(id)=LOWER('" + bookID + "')";

            if (databaseHandler.execAction(insertQuery)
                    && databaseHandler.execAction(updateQuery)) {

                bookstatus.setText("Not Available");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Book issued successfully.");
                alert.showAndWait();

                bookidinput.clear();
                memberidinput.clear();
            }

        } catch (SQLException ex) {

            Logger.getLogger(MainController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    /*
    --------------------------------
    Renew / Submission Tab
    --------------------------------
    */

@FXML
private void loadbookID2(ActionEvent event) {

    ObservableList<String> issueData = FXCollections.observableArrayList();

    isReadyForSubmission = false;

    String id = bookID2.getText().trim();

    if(id.isEmpty()){
        return;
    }

    String qu = "SELECT * FROM ISSUE WHERE LOWER(bookID)=LOWER('" + id + "')";
    ResultSet rs = databaseHandler.execQuery(qu);

    try {

        if(rs != null && rs.next()) {

            String mBookID = rs.getString("bookID");
            String mMemberID = rs.getString("memberID");
            Timestamp mIssueTime = rs.getTimestamp("issueTime");
            int mRenewCount = rs.getInt("renew_count");

            issueData.add("Issue Date and Time : " + mIssueTime.toString());
            issueData.add("Renew Count : " + mRenewCount);

            issueData.add("----------------------");
            issueData.add("Book Information");

            qu = "SELECT * FROM BOOK WHERE LOWER(id)=LOWER('" + mBookID + "')";
            ResultSet r1 = databaseHandler.execQuery(qu);

            if(r1 != null && r1.next()) {

                issueData.add("Title : " + r1.getString("title"));
                issueData.add("ID : " + r1.getString("id"));
                issueData.add("Author : " + r1.getString("author"));
                issueData.add("Publisher : " + r1.getString("publisher"));
            }

            issueData.add("----------------------");
            issueData.add("Member Information");

            qu = "SELECT * FROM MEMBER WHERE LOWER(id)=LOWER('" + mMemberID + "')";
            r1 = databaseHandler.execQuery(qu);

            if(r1 != null && r1.next()) {

                issueData.add("Name : " + r1.getString("name"));
                issueData.add("Mobile : " + r1.getString("mobile"));
                issueData.add("Email : " + r1.getString("email"));
            }

            isReadyForSubmission = true;

        } else {

            issueData.add("No issue record found for this book.");
        }

    } catch (SQLException ex) {

        Logger.getLogger(MainController.class.getName())
                .log(Level.SEVERE, null, ex);
    }

    issuedatalist.getItems().setAll(issueData);
}

    /*
    --------------------------------
    Submission Operation
    --------------------------------
    */

    @FXML
    private void loadSubmissionOp(ActionEvent event) {

        if(!isReadyForSubmission){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select a book to submit");
            alert.showAndWait();
            return;
        }

        String id = bookID2.getText();

        String ac1 = "DELETE FROM ISSUE WHERE BOOKID = '" + id + "'";
        String ac2 = "UPDATE BOOK SET ISAVAIL = TRUE WHERE ID = '" + id + "'";

        if (databaseHandler.execAction(ac1) && databaseHandler.execAction(ac2)) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Book Has Been Submitted");
            alert.showAndWait();

            issuedatalist.getItems().clear();
        }
    }

    /*
    --------------------------------
    Renew Operation
    --------------------------------
    */

    @FXML
    private void loadRenewOp(ActionEvent event) {

        if (!isReadyForSubmission) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select a book to renew");
            alert.showAndWait();
            return;
        }

        String id = bookID2.getText();

        String ac = "UPDATE ISSUE SET issueTime = CURRENT_TIMESTAMP, "
                + "renew_count = renew_count + 1 "
                + "WHERE BOOKID = '" + id + "'";

        if (databaseHandler.execAction(ac)) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Book Has Been Renewed");
            alert.showAndWait();

            loadbookID2(null);
        }
    }
}