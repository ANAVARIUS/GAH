package database;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import gestiondehorarios.PlantillaHorario;
import gestiondehorarios.Grupo;

public class PlantillaHorarioCRUD {
    public static Integer addPlantilla(Connection conn, PlantillaHorario plantilla) throws SQLException {
        String sqlPlantillaHorario = "INSERT INTO PlantillaHorario DEFAULT VALUES";
        String sqlJunction = "INSERT INTO PlantillaHorario_Junction (PlantillaHorario_id, Grupo_id) VALUES (?, ?)";
        Integer plantillaId = null;
        conn.setAutoCommit(false);
        try {
            // Insertar la nueva plantilla de horario
            try (PreparedStatement stmt = conn.prepareStatement(sqlPlantillaHorario, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.executeUpdate();
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        plantillaId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID generado para la nueva plantilla de horario.");
                    }
                }
            }

            // Insertar relaciones en PlantillaHorario_Junction
            try (PreparedStatement stmt = conn.prepareStatement(sqlJunction)) {
                for (Grupo grupo : plantilla.getGrupos()) {
                    // Obtener o crear el ID del grupo
                    Integer grupoId = GrupoCRUD.getGrupoId(conn, grupo);
                    if (grupoId == null) {
                        grupoId = GrupoCRUD.addGrupo(conn, grupo);
                    }

                    // Agregar la relación en PlantillaHorario_Junction
                    stmt.setInt(1, plantillaId);
                    stmt.setInt(2, grupoId);
                    stmt.addBatch();
                }

                // Ejecutar el lote de inserciones
                stmt.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            // Revertir cambios en caso de error
            conn.rollback();
            throw e; // Rethrow para manejarlo en un nivel superior
        } finally {
            conn.setAutoCommit(true); // Restaurar estado de auto-commit
        }
        return plantillaId;
    }
    public static void deletePlantilla(Connection conn, int plantillaHorarioId) throws SQLException {
        String sql = "DELETE FROM PlantillaHorario_Junction WHERE PlantillaHorario_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, plantillaHorarioId);
            int filasAfectadas = stmt.executeUpdate();
            System.out.println("Se eliminaron " + filasAfectadas + " relaciones de la plantilla con ID: " + plantillaHorarioId);
        }
    }
    public static List<Integer> obtenerTodosLosIdsPlantillas(Connection conn) throws SQLException {
        String sql = "SELECT id FROM PlantillaHorario";
        List<Integer> plantillaIds = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                plantillaIds.add(rs.getInt("id"));
            }
        }

        return plantillaIds;
    }
}
