module com.mycompany.librarymis {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires java.base;

    opens com.mycompany.librarymis to javafx.fxml, javafx.base, com.google.gson;

    exports com.mycompany.librarymis;
}