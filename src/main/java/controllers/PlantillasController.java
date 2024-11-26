package controllers;

import database.Database;
import database.GrupoCRUD;
import database.PlantillaHorarioCRUD;
import gestiondehorarios.GeneradorHorarios;
import gestiondehorarios.Grupo;
import gestiondehorarios.PlantillaHorario;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

public class PlantillasController {

    @FXML
    private Button Inicio;

    @FXML
    private Button Grupos;

    @FXML
    private Button Materias;

    @FXML
    private Button Plantillas;

    @FXML
    private TableColumn<PlantillaHorario, Integer> creditosColumn;

    @FXML
    private TableView<PlantillaHorario> tableViewGrupos;

    @FXML
    private TableColumn<PlantillaHorario, String> materiasColumn;

    @FXML
    private TableColumn<PlantillaHorario, Integer> plantillaColumn;
    private final ObservableList<PlantillaHorario> listaDePlantillas = FXCollections.observableArrayList();

    @FXML
    void displayPlantilla(MouseEvent event) {
        if (event.getClickCount() == 2) { // Doble clic
            PlantillaHorario selectedPlantilla = tableViewGrupos.getSelectionModel().getSelectedItem();
            if (selectedPlantilla != null) {
                switchToPlantillaDetailView(selectedPlantilla);
            }
        }
    }

    @FXML
    private void switchToPlantillaDetailView(PlantillaHorario plantilla) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/iteso/gah/PlantillaDetail.fxml"));
            Parent newRoot = loader.load();

            // Obtener el controlador de la nueva vista
            PlantillaDetailController detailController = loader.getController();
            detailController.setPlantilla(plantilla); // Pasar el objeto seleccionado

            // Mostrar la nueva vista
            Stage currentStage = (Stage) Materias.getScene().getWindow();
            Scene newScene = new Scene(newRoot);
            currentStage.setScene(newScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        Connection myconn = null;
        Map<PlantillaHorario, Integer> plantillaIdCache = new HashMap<>();

        try {
            myconn = Database.getConnection();
            // Retrieve all groups and prepare schedules
            List<Grupo> list = GrupoCRUD.getAllGrupos(myconn);
            Set<Grupo> set = new HashSet<>(list);
            List<PlantillaHorario> plantillasPreparadas = GeneradorHorarios.Backtracking(set);
            plantillasPreparadas.removeLast();
            int ialsda = 0;

            // Validate and update database
            PlantillaHorarioCRUD.eliminarTodasLasPlantillas(myconn);
            for (PlantillaHorario plantilla : plantillasPreparadas) {
                if (plantilla.getGrupos() == null || plantilla.getGrupos().isEmpty()) {
                    System.err.println("Skipping invalid plantilla without grupos.");
                    continue;
                }
                Integer id = PlantillaHorarioCRUD.addPlantilla(myconn, plantilla);
                plantillaIdCache.put(plantilla, id);
            }

            // Reload plantillas from database
            List<PlantillaHorario> updatedPlantillas = PlantillaHorarioCRUD.obtenerTodasPlantillas(myconn);
            listaDePlantillas.setAll(updatedPlantillas);

            if (listaDePlantillas.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Información");
                alert.setHeaderText(null);
                alert.setContentText("No hay plantillas disponibles en la base de datos.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Base de Datos");
            alert.setHeaderText(null);
            alert.setContentText("Ocurrió un error al interactuar con la base de datos: " + e.getMessage());
            alert.showAndWait();
        } catch (Exception w) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Base de Datos");
            alert.setHeaderText(null);
            alert.setContentText("Ocurrió un error al obtener el ID de las plantillas: " + w.getMessage());
            alert.showAndWait();
        }

        // Bind columns to attributes
        creditosColumn.setCellValueFactory(data -> {
            int totalCreditos = data.getValue().getGrupos().stream()
                    .mapToInt(grupo -> grupo.getMateria().getNoCreditos())
                    .sum();
            return new SimpleIntegerProperty(totalCreditos).asObject();
        });

        materiasColumn.setCellValueFactory(data -> {
            String materiasList = data.getValue().getGrupos().stream()
                    .map(grupo -> grupo.getMateria().getNombreMateria())
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(materiasList);
        });

        plantillaColumn.setCellValueFactory(data -> {
            Integer id = plantillaIdCache.getOrDefault(data.getValue(), -1);
            return new SimpleIntegerProperty(id).asObject();
        });

        tableViewGrupos.setItems(listaDePlantillas);
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

