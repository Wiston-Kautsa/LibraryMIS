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

private Connection conn = null;

private DatabaseHandler() {
    createConnection();
    setupBookTable();
    setupIssueTable();
    setupMemberTable();
}

public static DatabaseHandler getInstance() {

    if (handler == null) {
        handler = new DatabaseHandler();
    }

    return handler;
}

private void createConnection() {

    try {

        conn = DriverManager.getConnection(DB_URL);

        if (conn != null) {
            System.out.println("Database connected successfully.");
        }

    } catch (SQLException e) {

        System.err.println("Database connection failed.");
        e.printStackTrace();
    }
}

private void setupBookTable() {

    String TABLE_NAME = "BOOK";

    try {

        Statement stmt = conn.createStatement();

        DatabaseMetaData meta = conn.getMetaData();
        ResultSet tables = meta.getTables(null, null, TABLE_NAME.toUpperCase(), null);

        if (tables.next()) {

            System.out.println("Table " + TABLE_NAME + " already exists.");

        } else {

            stmt.executeUpdate(
                    "CREATE TABLE " + TABLE_NAME + " ("
                    + "id TEXT PRIMARY KEY, "
                    + "title TEXT, "
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

private void setupIssueTable() {

    String TABLE_NAME = "ISSUE";

    try {

        Statement stmt = conn.createStatement();

        DatabaseMetaData meta = conn.getMetaData();
        ResultSet tables = meta.getTables(null, null, TABLE_NAME.toUpperCase(), null);

        if (tables.next()) {

            System.out.println("Table " + TABLE_NAME + " already exists.");

        } else {

            stmt.executeUpdate(
                    "CREATE TABLE " + TABLE_NAME + " ("
                    + "bookID TEXT PRIMARY KEY, "
                    + "memberID TEXT, "
                    + "issueTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "renew_count INTEGER DEFAULT 0, "
                    + "FOREIGN KEY(bookID) REFERENCES BOOK(id))"
            );

            System.out.println("ISSUE table created.");
        }

    } catch (SQLException e) {

        System.err.println("Error creating ISSUE table.");
        e.printStackTrace();
    }
}

private void setupMemberTable() {

    String TABLE_NAME = "MEMBER";

    try {

        Statement stmt = conn.createStatement();

        DatabaseMetaData dbm = conn.getMetaData();
        ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

        if (tables.next()) {

            System.out.println("Table " + TABLE_NAME + " already exists.");

        } else {

            stmt.executeUpdate(
                    "CREATE TABLE " + TABLE_NAME + " ("
                    + "id TEXT PRIMARY KEY, "
                    + "name TEXT, "
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

public ResultSet execQuery(String query) {

    try {

        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);

    } catch (SQLException ex) {

        System.err.println("Error executing query: " + query);
        ex.printStackTrace();
        return null;
    }
}

public boolean execAction(String query) {

    try {

        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);

        return true;

    } catch (SQLException e) {

        System.err.println("Error executing action: " + query);
        e.printStackTrace();
        return false;
    }
}

public Connection getConnection() {
    return conn;
}


}
