package controllers;

import database.Database;
import database.MateriaCRUD;
import gestiondehorarios.Materia;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class AddMateriaController {

    @FXML
    private Button atrasButton;

    @FXML
    private Button guardarButton;

    @FXML
    private TextField fieldNombre; // Linked to the TextField in SceneBuilder

    @FXML
    private Spinner<Integer> spinnerCreditos; // Linked to the Spinner in SceneBuilder

    @FXML
    private void initialize() {
        // Configure the Spinner to allow unsigned integers (0 to Integer.MAX_VALUE)
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0);
        spinnerCreditos.setValueFactory(valueFactory);
    }

    @FXML
    private void guardarMateria() {
        // Retrieve data from the TextField and Spinner
        String name = fieldNombre.getText();
        Integer number = spinnerCreditos.getValue();

        // Validate and process the input
        if (name == null || name.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Name cannot be empty");
            alert.showAndWait(); // Blocks until the user closes the dialog
        } else {
            try {
                Connection conn = Database.getConnection();
                MateriaCRUD.addMateria(conn, new Materia(name, number));
                this.switchToMateriasView();
            }
            catch (SQLException e){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("SQLExeption: " + e.toString());
                alert.showAndWait(); // Blocks until the user closes the dialog
            }
        }
    }

    @FXML
    protected void switchToMateriasView() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/iteso/gah/Materias.fxml"));
            Parent newRoot = loader.load();

            // Get the current stage
            Stage currentStage = (Stage) atrasButton.getScene().getWindow();

            // Set the new scene
            Scene newScene = new Scene(newRoot);
            currentStage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
