package database;

import gestiondehorarios.Grupo;
import gestiondehorarios.Materia;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.*;

public class GrupoCRUD {
    public static int addGrupo(Connection connection, Grupo grupo) throws SQLException {
        String sqlGrupo = "INSERT INTO Grupo (nombreProfesor, materia_id, lunes_id, martes_id, miercoles_id, jueves_id, viernes_id, sabado_id, domingo_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlHorario = "INSERT INTO Horario (hora_inicio, hora_fin) VALUES (?, ?)";
        try (PreparedStatement stmtGrupo = connection.prepareStatement(sqlGrupo, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stmtHorario = connection.prepareStatement(sqlHorario, Statement.RETURN_GENERATED_KEYS)) {

            // Insertar el grupo
            Integer materiaID = MateriaCRUD.getIdByNombre(connection, grupo.getMateria().getNombreMateria());
            if(materiaID == null) throw new SQLException("La materia a la que se relaciona este grupo no se encuentra en la base de datos.");
            stmtGrupo.setString(1, grupo.getNombreProfesor());
            stmtGrupo.setInt(2, materiaID); // Asegúrate de tener el ID de la materia

            // Insertar horarios y obtener sus IDs
            Map<DayOfWeek, Integer> horarioIds = new HashMap<>();
            for (Map.Entry<DayOfWeek, Grupo.Horario> entry : grupo.obtenerHorario().entrySet()) {
                Grupo.Horario horario = entry.getValue();
                stmtHorario.setString(1, horario.getHoraInicio().toString());
                stmtHorario.setString(2, horario.getHoraFin().toString());
                stmtHorario.executeUpdate();

                try (ResultSet generatedKeys = stmtHorario.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        horarioIds.put(entry.getKey(), generatedKeys.getInt(1));
                    }
                }
            }



            // Configurar los IDs de horario (o null si no aplica)
            for (DayOfWeek dia : DayOfWeek.values()) {
                Integer horarioId = horarioIds.getOrDefault(dia, null);
                stmtGrupo.setObject(dia.ordinal() + 3, horarioId); // Las columnas de lunes a domingo inician en el índice 3
            }

            stmtGrupo.executeUpdate();
            try (ResultSet generatedKeys = stmtGrupo.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID generado para la nueva plantilla de horario.");
                }
            }
        }
    }

    public static void deleteGrupo(Connection connection, int id) throws SQLException {
        String sqlGrupo = "DELETE FROM Grupo WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlGrupo)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    public static Grupo getGrupoById(Connection conn, int grupoId) throws SQLException {
        String sqlGrupo = "SELECT * FROM Grupo WHERE id = ?";
        String sqlHorario = "SELECT * FROM Horario WHERE id = ?";

        try (PreparedStatement stmtGrupo = conn.prepareStatement(sqlGrupo)) {
            stmtGrupo.setInt(1, grupoId);

            try (ResultSet rsGrupo = stmtGrupo.executeQuery()) {
                if (!rsGrupo.next()) {
                    return null; // No se encontró el grupo
                }

                String nombreProfesor = rsGrupo.getString("nombreProfesor");
                int materiaId = rsGrupo.getInt("materia_id");
                Materia materia = MateriaCRUD.getMateriaById(conn, materiaId); // Implementa este método en MateriaCRUD

                Map<DayOfWeek, Grupo.Horario> diasyHoras = new HashMap<>();
                for (DayOfWeek dia : DayOfWeek.values()) {
                    int horarioId = rsGrupo.getInt(dia.toString().toLowerCase() + "_id");
                    if (horarioId != 0) {
                        try (PreparedStatement stmtHorario = conn.prepareStatement(sqlHorario)) {
                            stmtHorario.setInt(1, horarioId);
                            try (ResultSet rsHorario = stmtHorario.executeQuery()) {
                                if (rsHorario.next()) {
                                    LocalTime horaInicio = LocalTime.parse(rsHorario.getString("hora_inicio"));
                                    LocalTime horaFin = LocalTime.parse(rsHorario.getString("hora_fin"));
                                    diasyHoras.put(dia, new Grupo.Horario(horaInicio, horaFin));
                                }
                            }
                        }
                    }
                }

                return new Grupo(nombreProfesor, materia, diasyHoras);
            }
        }
    }
    public static Integer getGrupoId(Connection conn, Grupo grupo) throws SQLException {
        String sqlGrupo = "SELECT id FROM Grupo "
                + "WHERE nombreProfesor = ? AND materia_id = ? "
                + "AND lunes_id = ? AND martes_id = ? AND miercoles_id = ? "
                + "AND jueves_id = ? AND viernes_id = ? AND sabado_id = ? AND domingo_id = ?";

        String sqlHorario = "SELECT id FROM Horario WHERE hora_inicio = ? AND hora_fin = ?";

        try (PreparedStatement stmtGrupo = conn.prepareStatement(sqlGrupo);
             PreparedStatement stmtHorario = conn.prepareStatement(sqlHorario)) {

            // Obtener los IDs de los horarios asociados al grupo
            Map<DayOfWeek, Integer> horarioIds = new HashMap<>();
            for (Map.Entry<DayOfWeek, Grupo.Horario> entry : grupo.obtenerHorario().entrySet()) {
                Grupo.Horario horario = entry.getValue();
                stmtHorario.setString(1, horario.getHoraInicio().toString());
                stmtHorario.setString(2, horario.getHoraFin().toString());
                try (ResultSet rsHorario = stmtHorario.executeQuery()) {
                    if (rsHorario.next()) {
                        horarioIds.put(entry.getKey(), rsHorario.getInt("id"));
                    } else {
                        // Si no existe un horario que coincida, el grupo no puede estar en la base de datos
                        return null;
                    }
                }
            }

            // Configurar los parámetros para buscar el grupo
            Integer materiaID = MateriaCRUD.getIdByNombre(conn, grupo.getMateria().getNombreMateria());
            if(materiaID == null) throw new SQLException("La materia a la que pertenece el grupo aun no esta en la base de datos.");
            stmtGrupo.setString(1, grupo.getNombreProfesor());
            stmtGrupo.setInt(2, materiaID); // Asegúrate de tener el ID de la materia

            for (DayOfWeek dia : DayOfWeek.values()) {
                Integer horarioId = horarioIds.getOrDefault(dia, null);
                stmtGrupo.setObject(dia.ordinal() + 3, horarioId); // Los IDs de lunes a domingo empiezan en el índice 3
            }

            // Ejecutar la consulta y devolver el ID si existe
            try (ResultSet rsGrupo = stmtGrupo.executeQuery()) {
                if (rsGrupo.next()) {
                    return rsGrupo.getInt("id");
                } else {
                    return null; // No se encontró el grupo
                }
            }
        }
    }
    public static List<Grupo> getAllGrupos(Connection conn) throws SQLException {
        String sqlGrupo = "SELECT * FROM Grupo";
        String sqlHorario = "SELECT * FROM Horario WHERE id = ?";
        Map<DayOfWeek, String> diaSemanaMap = Map.of(
                DayOfWeek.MONDAY, "lunes",
                DayOfWeek.TUESDAY, "martes",
                DayOfWeek.WEDNESDAY, "miercoles",
                DayOfWeek.THURSDAY, "jueves",
                DayOfWeek.FRIDAY, "viernes",
                DayOfWeek.SATURDAY, "sabado",
                DayOfWeek.SUNDAY, "domingo"
        );
        List<Grupo> grupos = new ArrayList<>();

        try (PreparedStatement stmtGrupo = conn.prepareStatement(sqlGrupo);
             ResultSet rsGrupo = stmtGrupo.executeQuery()) {

            while (rsGrupo.next()) {
                String nombreProfesor = rsGrupo.getString("nombreProfesor");
                int materiaId = rsGrupo.getInt("materia_id");
                Materia materia = MateriaCRUD.getMateriaById(conn, materiaId); // Implementa este método en MateriaCRUD

                Map<DayOfWeek, Grupo.Horario> diasyHoras = new HashMap<>();

                for (DayOfWeek dia : DayOfWeek.values()) {
                    String columna = diaSemanaMap.get(dia) + "_id"; // Convierte el día al formato correcto
                    int horarioId = rsGrupo.getInt(columna);
                    if (horarioId != 0) {
                        try (PreparedStatement stmtHorario = conn.prepareStatement(sqlHorario)) {
                            stmtHorario.setInt(1, horarioId);
                            try (ResultSet rsHorario = stmtHorario.executeQuery()) {
                                if (rsHorario.next()) {
                                    LocalTime horaInicio = LocalTime.parse(rsHorario.getString("hora_inicio"));
                                    LocalTime horaFin = LocalTime.parse(rsHorario.getString("hora_fin"));
                                    diasyHoras.put(dia, new Grupo.Horario(horaInicio, horaFin));
                                }
                            }
                        }
                    }
                }

                Grupo grupo = new Grupo(nombreProfesor, materia, diasyHoras);
                grupos.add(grupo);
            }
        }

        return grupos;
    }


}
