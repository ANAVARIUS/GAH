module mx.iteso.gah {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;


    opens mx.iteso.gah to javafx.fxml;
    exports mx.iteso.gah;
}