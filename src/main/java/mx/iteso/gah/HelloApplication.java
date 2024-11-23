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
        Materia POO = new Materia("POO", 8);
        try{
            MateriaCRUD.addMateria(conn, POO);
        }catch (SQLException s){
            System.out.println("Error SQL:" + s.toString());
        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
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