package controllers;

import database.Database;
import database.GrupoCRUD;
import database.MateriaCRUD;
import gestiondehorarios.Grupo;
import gestiondehorarios.Materia;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public class GruposController {
    @FXML
    private Button Inicio;

    @FXML
    private Button Grupos;

    @FXML
    private Button Materias;

    @FXML
    private Button Plantillas;

    @FXML
    private Button añadirGrupo;

    @FXML
    private TableColumn<Grupo, String> columnaHorario;

    @FXML
    private TableColumn<Grupo, String> columnaMateria;

    @FXML
    private TableColumn<Grupo, String> columnaNombreProfesor;

    @FXML
    private TableView<Grupo> gruposTable;
    private final ObservableList<Grupo> listaDeGrupos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Populate the ObservableList
        try{
            Connection myconn = Database.getConnection();
            List<Grupo> misgrupos = GrupoCRUD.getAllGrupos(myconn);
            if (misgrupos == null || misgrupos.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informacion");
                alert.setHeaderText(null);
                alert.setContentText("No hay grupos disponibles en la base de datos.");
                alert.showAndWait(); // Blocks until the user closes the dialog
            } else {
                listaDeGrupos.addAll(misgrupos);
            }
        } catch (SQLException s){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("SQLExeption: " + s.toString());
            alert.showAndWait(); // Blocks until the user closes the dialog
        }

        // Link columns to Person attributes
        columnaMateria.setCellValueFactory(data -> {
            // Convert Materia object to its nombreMateria attribute
            String nombreMateria = data.getValue().getMateria().getNombreMateria();
            return new SimpleStringProperty(nombreMateria);
        });
        columnaNombreProfesor.setCellValueFactory(data -> {
            // Directly get the professor's name
            return new SimpleStringProperty(data.getValue().getNombreProfesor());
        });
        columnaHorario.setCellValueFactory(data -> {
            // Convert the HashMap<DayOfWeek, Horario> to a readable string
            Map<DayOfWeek, Grupo.Horario> horarios = data.getValue().obtenerHorario();
            StringBuilder horarioString = new StringBuilder();
            horarios.forEach((day, horario) -> {
                horarioString.append(day.name())
                        .append(": ")
                        .append(horario.getHoraInicio())
                        .append("-")
                        .append(horario.getHoraFin())
                        .append(", ");
            });

            // Remove the trailing comma and space, if present
            if (horarioString.length() > 2) {
                horarioString.setLength(horarioString.length() - 2);
            }

            return new SimpleStringProperty(horarioString.toString());
        });


        // Set the data in the TableView
        gruposTable.setItems(listaDeGrupos);
    }

    @FXML
    void switchToAddGrupoView(ActionEvent event) {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/iteso/gah/AddGrupos.fxml"));
            Parent newRoot = loader.load();

            // Get the current stage
            Stage currentStage = (Stage) añadirGrupo.getScene().getWindow();

            // Set the new scene
            Scene newScene = new Scene(newRoot);
            currentStage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void switchToMateriasView() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/iteso/gah/Materias.fxml"));
            Parent newRoot = loader.load();

            // Get the current stage
            Stage currentStage = (Stage) Materias.getScene().getWindow();

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
