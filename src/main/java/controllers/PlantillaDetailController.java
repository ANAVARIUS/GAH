package controllers;

import gestiondehorarios.Grupo;
import gestiondehorarios.PlantillaHorario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

public class PlantillaDetailController {
    private PlantillaHorario plantillaHorario;

    @FXML
    private Button buttonVolver;

    @FXML
    private GridPane timeTableGrid;

    public void setTimeTable() {
        // Iterate through each Grupo in the PlantillaHorario.
        for (Grupo grupo : plantillaHorario.getGrupos()) {
            String materiaNombre = grupo.getMateria().getNombreMateria();
            String profesorNombre = grupo.getNombreProfesor();

            // Get the schedule for each day.
            Map<DayOfWeek, Grupo.Horario> horarios = grupo.obtenerHorario();

            for (Map.Entry<DayOfWeek, Grupo.Horario> entry : horarios.entrySet()) {
                DayOfWeek dia = entry.getKey();
                Grupo.Horario horario = entry.getValue();

                // Convert DayOfWeek to GridPane column index (Monday = 1, Tuesday = 2, ..., Saturday = 6).
                int columnIndex = dia.getValue();

                // Convert horario (e.g., 7-9 AM) to GridPane row index.
                int rowIndex = convertTimeToRowIndex(horario.getHoraInicio(), horario.getHoraFin());

                if (rowIndex != -1) { // Only populate valid rows.
                    String cellContent = materiaNombre + "\n" + profesorNombre;

                    // Add the content to the corresponding cell in the GridPane.
                    Label classLabel = new Label(cellContent);
                    classLabel.setStyle("-fx-alignment: center;");
                    timeTableGrid.add(classLabel, columnIndex, rowIndex);
                }
            }
        }
    }

    private int convertTimeToRowIndex(LocalTime horaInicio, LocalTime horaFin) {
        // Define time slots (7 AM to 10 PM in 2-hour blocks, excluding 3-4 PM).
        LocalTime[] timeSlots = {
                LocalTime.of(7, 0), LocalTime.of(9, 0),
                LocalTime.of(9, 0), LocalTime.of(11, 0),
                LocalTime.of(11, 0), LocalTime.of(13, 0),
                LocalTime.of(13, 0), LocalTime.of(15, 0), // Exclude 3-4 PM (lunch)
                LocalTime.of(16, 0), LocalTime.of(18, 0),
                LocalTime.of(18, 0), LocalTime.of(20, 0),
                LocalTime.of(20, 0), LocalTime.of(22, 0)
        };

        // Map the given time range to a row index in the GridPane.
        for (int i = 0; i < timeSlots.length; i += 2) {
            if (horaInicio.equals(timeSlots[i]) && horaFin.equals(timeSlots[i + 1])) {
                return i / 2 + 1; // Rows start at 1 in the GridPane.
            }
        }

        return -1; // Invalid time range (not found).
    }

    @FXML
    void switchToPlantillasView(ActionEvent event) {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/iteso/gah/Plantillas.fxml"));
            Parent newRoot = loader.load();

            // Get the current stage
            Stage currentStage = (Stage) buttonVolver.getScene().getWindow();

            // Set the new scene
            Scene newScene = new Scene(newRoot);
            currentStage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setPlantilla(PlantillaHorario plantilla) {
        this.plantillaHorario = plantilla;
        if(this.plantillaHorario != null){
            this.setTimeTable();
        }
    }
}
