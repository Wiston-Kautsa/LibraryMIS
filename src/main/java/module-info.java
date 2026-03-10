module com.mycompany.librarymis {

requires javafx.controls;
requires javafx.fxml;
requires java.sql;
    requires java.base;

opens com.mycompany.librarymis to javafx.fxml, javafx.base;

exports com.mycompany.librarymis;

}