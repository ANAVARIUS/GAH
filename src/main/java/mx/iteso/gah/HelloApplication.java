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
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Gestor de Alternativas Horarias");
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void stop() throws Exception {
        // Close the database connection when the application exits
        Database.closeConnection();
        System.out.println("Application has stopped, and the database connection has been closed.");
        super.stop();
    }
    public static void main(String[] args) {
        launch();
    }
}