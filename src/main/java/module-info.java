module mx.iteso.gah {
    requires javafx.controls;
    requires javafx.fxml;


    opens mx.iteso.gah to javafx.fxml;
    exports mx.iteso.gah;
}