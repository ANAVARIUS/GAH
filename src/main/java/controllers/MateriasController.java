package controllers;

import database.Database;
import database.MateriaCRUD;
import gestiondehorarios.Materia;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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
    private Button anadirMateria;

    @FXML
    private TableView<Materia> materiasTable;
    @FXML
    private TableColumn<Materia, String> columnaNombre;
    @FXML
    private TableColumn<Materia, Integer> columnaCreditos;
    private final ObservableList<Materia> listaDeMaterias = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Populate the ObservableList
        try{
            Connection myconn = Database.getConnection();
            List<Materia> materias = MateriaCRUD.getAllMaterias(myconn);
            if (materias == null || materias.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informacion");
                alert.setHeaderText(null);
                alert.setContentText("No hay materias disponibles en la base de datos.");
                alert.showAndWait(); // Blocks until the user closes the dialog
            } else {
                listaDeMaterias.addAll(materias);
            }
        } catch (SQLException s){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("SQLExeption: " + s.toString());
            alert.showAndWait(); // Blocks until the user closes the dialog
        }

        // Link columns to Person attributes
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombreMateria"));
        columnaCreditos.setCellValueFactory(new PropertyValueFactory<>("noCreditos"));

        // Set the data in the TableView
        materiasTable.setItems(listaDeMaterias);
    }

    @FXML
    void switchToAddMateriaView(ActionEvent event) {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/iteso/gah/AddMateria.fxml"));
            Parent newRoot = loader.load();

            // Get the current stage
            Stage currentStage = (Stage) anadirMateria.getScene().getWindow();

            // Set the new scene
            Scene newScene = new Scene(newRoot);
            currentStage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    @FXML
    protected void switchToGruposView() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/iteso/gah/Grupos.fxml"));
            Parent newRoot = loader.load();

            // Get the current stage
            Stage currentStage = (Stage) Grupos.getScene().getWindow();

            // Set the new scene
            Scene newScene = new Scene(newRoot);
            currentStage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void switchToPlantillasView() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/iteso/gah/Plantillas.fxml"));
            Parent newRoot = loader.load();

            // Get the current stage
            Stage currentStage = (Stage) Plantillas.getScene().getWindow();

            // Set the new scene
            Scene newScene = new Scene(newRoot);
            currentStage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
