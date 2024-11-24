package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MateriasController {
    @FXML
    private Button Inicio;

    @FXML
    private Button Grupos;

    @FXML
    private Button Materias;

    @FXML
    private Button Plantillas;

    @FXML
    void switchToInicioView(ActionEvent event) {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/iteso/gah/hello-view.fxml"));
            Parent newRoot = loader.load();

            // Get the current stage
            Stage currentStage = (Stage) Inicio.getScene().getWindow();

            // Set the new scene
            Scene newScene = new Scene(newRoot);
            currentStage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
