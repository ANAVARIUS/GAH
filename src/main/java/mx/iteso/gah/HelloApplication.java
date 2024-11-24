package mx.iteso.gah;

import database.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import gestiondehorarios.*;

public class HelloApplication extends Application {
    private Database db;
    private Connection conn;
    @Override
    public void start(Stage stage) throws IOException {
        db = new Database();
        conn = db.connect();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Gestor de Alternativas Horarias");
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void stop() throws Exception {
        if (conn != null) {
            conn.close();
        }
        super.stop();
    }
    public static void main(String[] args) {
        launch();
    }
}