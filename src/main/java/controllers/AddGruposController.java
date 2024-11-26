package controllers;

import database.Database;
import database.GrupoCRUD;
import database.MateriaCRUD;
import gestiondehorarios.Grupo;
import gestiondehorarios.Materia;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddGruposController {

    @FXML
    private Button atrasButton;

    @FXML
    private Button guardarButton;

    @FXML
    private CheckBox juevesBool;

    @FXML
    private ChoiceBox<String> juevesFin;

    @FXML
    private ChoiceBox<String> juevesInicio;

    @FXML
    private CheckBox lunesBool;

    @FXML
    private ChoiceBox<String> lunesFin;

    @FXML
    private ChoiceBox<String> lunesInicio;

    @FXML
    private CheckBox martesBool;

    @FXML
    private ChoiceBox<String> martesFin;

    @FXML
    private ChoiceBox<String> martesInicio;

    @FXML
    private CheckBox miercolesBool;

    @FXML
    private ChoiceBox<String> miercolesFin;

    @FXML
    private ChoiceBox<String> miercolesInicio;

    @FXML
    private TextField nombreProfesorField;

    @FXML
    private CheckBox sabadoBool;

    @FXML
    private ChoiceBox<String> sabadoFin;

    @FXML
    private ChoiceBox<String> sabadoInicio;

    @FXML
    private ChoiceBox<String> selectMAteria;

    @FXML
    private CheckBox viernesBool;

    @FXML
    private ChoiceBox<String> viernesFin;

    @FXML
    private ChoiceBox<String> viernesInicio;

    private static final List<String> TIME_SLOTS_INICIO = List.of(
            "07:00", "09:00", "11:00", "13:00", "16:00", "18:00", "20:00"
    );
    private static final List<String> TIME_SLOTS_FIN = List.of(
            "09:00", "11:00", "13:00", "15:00", "18:00", "20:00", "22:00"
    );

    @FXML
    private void initialize() {
        int rowIndex = 0;
        List<ChoiceBox<String>> allChoiceBoxes_I = List.of(
                lunesInicio,
                martesInicio,
                miercolesInicio,
                juevesInicio,
                viernesInicio,
                sabadoInicio
        );
        List<ChoiceBox<String>> allChoiceBoxes_F = List.of(
                lunesFin,
                martesFin,
                miercolesFin,
                juevesFin,
                viernesFin,
                sabadoFin
        );

        for (ChoiceBox<String> choiceBox : allChoiceBoxes_I) {
            choiceBox.getItems().addAll(TIME_SLOTS_INICIO);
            choiceBox.setDisable(true); // Disable by default
        }
        for (ChoiceBox<String> choiceBox : allChoiceBoxes_F) {
            choiceBox.getItems().addAll(TIME_SLOTS_FIN);
            choiceBox.setDisable(true); // Disable by default
        }
        setupCheckBoxBehavior(lunesBool, lunesInicio, lunesFin);
        setupCheckBoxBehavior(martesBool, martesInicio, martesFin);
        setupCheckBoxBehavior(miercolesBool, miercolesInicio, miercolesFin);
        setupCheckBoxBehavior(juevesBool, juevesInicio, juevesFin);
        setupCheckBoxBehavior(viernesBool, viernesInicio, viernesFin);
        setupCheckBoxBehavior(sabadoBool, sabadoInicio, sabadoFin);
        try {
            Connection conn = Database.getConnection();
            List<Materia> materias = MateriaCRUD.getAllMaterias(conn);
            selectMAteria.getItems().addAll(materias.stream().map(Materia::getNombreMateria).toList());
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al cargar materias");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    private void setupCheckBoxBehavior(CheckBox checkBox, ChoiceBox<String> inicio, ChoiceBox<String> fin) {
        checkBox.setOnAction(event -> {
            boolean isSelected = checkBox.isSelected();
            inicio.setDisable(!isSelected);
            fin.setDisable(!isSelected);
        });
    }

    @FXML
    void guardarGrupo(ActionEvent event) {
        try {
            // Validate Materia selection
            String selectedMateria = (String) selectMAteria.getValue();
            if (selectedMateria == null) {
                throw new IllegalArgumentException("Debe seleccionar una materia.");
            }
            Materia materia = MateriaCRUD.getMateriaByNombre(Database.getConnection(), selectedMateria);

            // Validate Nombre Profesor
            String nombreProfesor = nombreProfesorField.getText();
            if (nombreProfesor == null || nombreProfesor.trim().isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar el nombre del profesor.");
            }

            // Collect schedule
            Map<DayOfWeek, Grupo.Horario> horarioMap = new HashMap<>();
            collectDaySchedule(horarioMap, DayOfWeek.MONDAY, lunesBool, lunesInicio, lunesFin);
            collectDaySchedule(horarioMap, DayOfWeek.TUESDAY, martesBool, martesInicio, martesFin);
            collectDaySchedule(horarioMap, DayOfWeek.WEDNESDAY, miercolesBool, miercolesInicio, miercolesFin);
            collectDaySchedule(horarioMap, DayOfWeek.THURSDAY, juevesBool, juevesInicio, juevesFin);
            collectDaySchedule(horarioMap, DayOfWeek.FRIDAY, viernesBool, viernesInicio, viernesFin);
            collectDaySchedule(horarioMap, DayOfWeek.SATURDAY, sabadoBool, sabadoInicio, sabadoFin);

            // Create Grupo object
            Grupo nuevoGrupo = new Grupo(nombreProfesor, materia, horarioMap);

            // Save to database
            Connection conn = Database.getConnection();
            GrupoCRUD.addGrupo(conn, nuevoGrupo);

            this.switchToGruposView(event);
        } catch (Exception e) {
            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo guardar el grupo");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    private void collectDaySchedule(Map<DayOfWeek, Grupo.Horario> horarioMap, DayOfWeek day,
                                    CheckBox dayBool, ChoiceBox<String> inicio, ChoiceBox<String> fin) {
        if (dayBool.isSelected()) {
            String horaInicio = inicio.getValue();
            String horaFin = fin.getValue();

            if (horaInicio == null || horaFin == null) {
                throw new IllegalArgumentException("Debe seleccionar horas de inicio y fin para " + day.name());
            }

            LocalTime start = LocalTime.parse(horaInicio);
            LocalTime end = LocalTime.parse(horaFin);

            if (start.isAfter(end) || start.equals(end)) {
                throw new IllegalArgumentException("La hora de inicio debe ser antes de la hora de fin para " + day.name());
            }

            horarioMap.put(day, new Grupo.Horario(start, end));
        }
    }

    @FXML
    void switchToGruposView(ActionEvent event) {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/iteso/gah/Grupos.fxml"));
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
