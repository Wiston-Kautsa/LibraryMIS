module com.mycompany.librarymis {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.mycompany.librarymis to javafx.fxml;
    exports com.mycompany.librarymis;

}