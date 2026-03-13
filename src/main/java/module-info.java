module com.mycompany.librarymis {

    /* ================= JAVA FX ================= */

    requires javafx.controls;
    requires javafx.fxml;

    /* ================= DATABASE ================= */

    requires java.sql;
    requires org.xerial.sqlitejdbc;

    /* ================= JSON ================= */

    requires com.google.gson;

    /* ================= APACHE POI ================= */

    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.apache.commons.collections4;
    requires org.apache.xmlbeans;

    /* ================= EXPORT ================= */

    exports com.mycompany.librarymis;

    /* ================= REFLECTION ================= */

    opens com.mycompany.librarymis to
            javafx.fxml,
            com.google.gson;
}