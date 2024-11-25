module mx.iteso.gah {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens mx.iteso.gah to javafx.fxml;
    exports mx.iteso.gah;
    exports controllers;
    opens controllers to javafx.fxml;
    opens gestiondehorarios to javafx.base, javafx.fxml;
}