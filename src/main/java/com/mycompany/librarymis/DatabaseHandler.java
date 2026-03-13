package com.mycompany.librarymis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import javafx.scene.control.Alert;

public class DatabaseHandler {

    private static DatabaseHandler handler = null;
    private static final String DB_URL = "jdbc:sqlite:library.db";

    private Connection conn = null;

    private DatabaseHandler() {
        createConnection();
        setupBookTable();
        setupMemberTable();
        setupIssueTable();
    }

    public static DatabaseHandler getInstance() {

        if (handler == null) {
            handler = new DatabaseHandler();
        }

        return handler;
    }

    // ---------------- CREATE CONNECTION ----------------

    void createConnection() {

        try {

            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);

            System.out.println("Database connected.");

        } catch (Exception e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Can't load database");
            alert.showAndWait();

            System.exit(0);
        }
    }

    // ---------------- ENSURE CONNECTION ----------------

    private void ensureConnection() {

        try {

            if (conn == null || conn.isClosed()) {
                createConnection();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------- BOOK TABLE ----------------

    private void setupBookTable() {

        String TABLE_NAME = "BOOK";

        try (Statement stmt = conn.createStatement()) {

            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, TABLE_NAME, null);

            if (tables.next()) {

                System.out.println("BOOK table already exists.");

            } else {

                stmt.executeUpdate(
                        "CREATE TABLE " + TABLE_NAME + " ("
                        + "id TEXT PRIMARY KEY, "
                        + "title TEXT NOT NULL, "
                        + "author TEXT, "
                        + "publisher TEXT, "
                        + "isAvail BOOLEAN DEFAULT TRUE)"
                );

                System.out.println("BOOK table created.");
            }

        } catch (SQLException e) {

            System.err.println("Error creating BOOK table.");
            e.printStackTrace();
        }
    }

    // ---------------- MEMBER TABLE ----------------

    private void setupMemberTable() {

        String TABLE_NAME = "MEMBER";

        try (Statement stmt = conn.createStatement()) {

            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, TABLE_NAME, null);

            if (tables.next()) {

                System.out.println("MEMBER table already exists.");

            } else {

                stmt.executeUpdate(
                        "CREATE TABLE " + TABLE_NAME + " ("
                        + "id TEXT PRIMARY KEY, "
                        + "name TEXT NOT NULL, "
                        + "mobile TEXT, "
                        + "email TEXT)"
                );

                System.out.println("MEMBER table created.");
            }

        } catch (SQLException e) {

            System.err.println("Error creating MEMBER table.");
            e.printStackTrace();
        }
    }

    // ---------------- ISSUE TABLE ----------------

    private void setupIssueTable() {

        String TABLE_NAME = "ISSUE";

        try (Statement stmt = conn.createStatement()) {

            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, TABLE_NAME, null);

            if (tables.next()) {

                System.out.println("ISSUE table already exists.");

            } else {

                stmt.executeUpdate(
                        "CREATE TABLE " + TABLE_NAME + " ("
                        + "issueID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "bookID TEXT NOT NULL, "
                        + "memberID TEXT NOT NULL, "
                        + "issueTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "renew_count INTEGER DEFAULT 0, "
                        + "FOREIGN KEY(bookID) REFERENCES BOOK(id), "
                        + "FOREIGN KEY(memberID) REFERENCES MEMBER(id))"
                );

                System.out.println("ISSUE table created.");
            }

        } catch (SQLException e) {

            System.err.println("Error creating ISSUE table.");
            e.printStackTrace();
        }
    }

    // ---------------- GENERATE BOOK ID ----------------

    public String generateBookID() {

        String query = "SELECT id FROM BOOK ORDER BY id DESC LIMIT 1";

        try {

            ResultSet rs = execQuery(query);

            if (rs.next()) {

                String lastID = rs.getString("id");
                int num = Integer.parseInt(lastID.substring(1));
                num++;

                return String.format("B%03d", num);

            } else {

                return "B001";
            }

        } catch (Exception e) {

            e.printStackTrace();
            return "B001";
        }
    }

    // ---------------- GENERATE MEMBER ID ----------------

    public String generateMemberID() {

        String query = "SELECT id FROM MEMBER ORDER BY id DESC LIMIT 1";

        try {

            ResultSet rs = execQuery(query);

            if (rs.next()) {

                String lastID = rs.getString("id");
                int num = Integer.parseInt(lastID.substring(1));
                num++;

                return String.format("M%03d", num);

            } else {

                return "M001";
            }

        } catch (Exception e) {

            e.printStackTrace();
            return "M001";
        }
    }

    // ---------------- EXECUTE QUERY ----------------

    public ResultSet execQuery(String query) {

        ensureConnection();

        try {

            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);

        } catch (SQLException ex) {

            System.err.println("Error executing query: " + query);
            ex.printStackTrace();
            return null;
        }
    }

    // ---------------- EXECUTE ACTION ----------------

    public boolean execAction(String query) {

        ensureConnection();

        try (Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(query);
            return true;

        } catch (SQLException e) {

            System.err.println("Error executing action: " + query);
            e.printStackTrace();
            return false;
        }
    }

    public Connection getConnection() {

        ensureConnection();
        return conn;
    }

    // ---------------- DELETE BOOK ----------------

    public boolean deleteBook(BooklistController.Book book) {

        String deleteStatement = "DELETE FROM BOOK WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(deleteStatement)) {

            stmt.setString(1, book.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException ex) {

            ex.printStackTrace();
            return false;
        }
    }

    // ---------------- UPDATE BOOK ----------------

    public boolean updateBook(BooklistController.Book book) {

        String updateStatement =
                "UPDATE BOOK SET title = ?, author = ?, publisher = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updateStatement)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getPublisher());
            stmt.setString(4, book.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException ex) {

            ex.printStackTrace();
            return false;
        }
    }

    // ---------------- DELETE MEMBER ----------------

    public boolean deleteMember(ListMemberController.Member member) {

        String deleteStatement = "DELETE FROM MEMBER WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(deleteStatement)) {

            stmt.setString(1, member.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException ex) {

            ex.printStackTrace();
            return false;
        }
    }

    // ---------------- UPDATE MEMBER ----------------

    public boolean updateMember(ListMemberController.Member member) {

        String updateStatement =
                "UPDATE MEMBER SET name = ?, mobile = ?, email = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updateStatement)) {

            stmt.setString(1, member.getName());
            stmt.setString(2, member.getMobile());
            stmt.setString(3, member.getEmail());
            stmt.setString(4, member.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException ex) {

            ex.printStackTrace();
            return false;
        }
    }

    // ---------------- CHECK BOOK ISSUED ----------------

    public boolean isBookAlreadyIssued(BooklistController.Book book) {

        String checkStmt = "SELECT COUNT(*) FROM ISSUE WHERE bookID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(checkStmt)) {

            stmt.setString(1, book.getId());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException ex) {

            ex.printStackTrace();
        }

        return false;
    }

    // ---------------- CHECK MEMBER BORROWED BOOKS ----------------

    public boolean isMemberHasBooks(ListMemberController.Member member) {

        String checkStmt = "SELECT COUNT(*) FROM ISSUE WHERE memberID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(checkStmt)) {

            stmt.setString(1, member.getId());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException ex) {

            ex.printStackTrace();
        }

        return false;
    }

}