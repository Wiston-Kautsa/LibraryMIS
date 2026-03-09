package com.mycompany.librarymis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHandler {

    private static DatabaseHandler handler = null;

    private static final String DB_URL = "jdbc:sqlite:library.db";

    private static Connection conn = null;
    private static Statement stmt = null;

    private DatabaseHandler() {
        createConnection();
        setupBookTable();
        setupIssueTable();
    }

    public static DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }

    void createConnection() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void setupBookTable() {
        String TABLE_NAME = "BOOK";

        try {
            stmt = conn.createStatement();

            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, TABLE_NAME, null);

            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists.");
            } else {

                stmt.execute(
                        "CREATE TABLE " + TABLE_NAME + " ("
                        + "id TEXT PRIMARY KEY, "
                        + "title TEXT, "
                        + "author TEXT, "
                        + "publisher TEXT, "
                        + "isAvail BOOLEAN DEFAULT TRUE"
                        + ")"
                );

                System.out.println("BOOK table created.");
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage() + " --- setupBookTable");
        }
    }

    void setupIssueTable() {
        String TABLE_NAME = "ISSUE";

        try {
            stmt = conn.createStatement();

            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, TABLE_NAME, null);

            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists.");
            } else {

                stmt.execute(
                        "CREATE TABLE " + TABLE_NAME + " ("
                        + "bookID TEXT PRIMARY KEY, "
                        + "memberID TEXT, "
                        + "issueTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "renew_count INTEGER DEFAULT 0, "
                        + "FOREIGN KEY(bookID) REFERENCES BOOK(id)"
                        + ")"
                );

                System.out.println("ISSUE table created.");
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage() + " --- setupIssueTable");
        }
    }

    public boolean execAction(String query) {
        try {
            stmt = conn.createStatement();
            stmt.execute(query);
            return true;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }
}