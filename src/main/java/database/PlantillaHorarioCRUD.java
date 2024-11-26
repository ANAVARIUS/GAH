package database;
import java.sql.*;
import java.util.Collections;
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
                        throw new SQLException("El Grupo con los detalles proporcionados no existe en la base de datos: " + grupo);
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
    public static List<PlantillaHorario> obtenerTodasPlantillas(Connection conn) throws SQLException{
        List<PlantillaHorario> plantillas = new ArrayList<>();
        String queryPlantillas = "SELECT id FROM PlantillaHorario";

        try (
             PreparedStatement stmtPlantillas = conn.prepareStatement(queryPlantillas);
             ResultSet rsPlantillas = stmtPlantillas.executeQuery()) {

            while (rsPlantillas.next()) {
                int plantillaId = rsPlantillas.getInt("id");
                List<Grupo> grupos = obtenerGruposPorPlantilla(conn, plantillaId);
                PlantillaHorario plantilla = new PlantillaHorario(grupos);
                plantillas.add(plantilla);
            }

        }

        return plantillas;
    }

    private static List<Grupo> obtenerGruposPorPlantilla(Connection conn, int plantillaId) throws SQLException {
        List<Grupo> grupos = new ArrayList<>();
        String queryGrupos = "SELECT Grupo_id " +
                "FROM PlantillaHorario_Junction " +
                "WHERE PlantillaHorario_id = ?";

        try (PreparedStatement stmtGrupos = conn.prepareStatement(queryGrupos)) {
            stmtGrupos.setInt(1, plantillaId);

            try (ResultSet rsGrupos = stmtGrupos.executeQuery()) {
                while (rsGrupos.next()) {
                    int grupoId = rsGrupos.getInt("Grupo_id");
                    Grupo grupo = GrupoCRUD.getGrupoById(conn, grupoId);
                    grupos.add(grupo);
                }
            }
        }

        return grupos;
    }
    public static Integer getPlantillaIdByPlantilla(Connection conn, PlantillaHorario plantilla) throws Exception {
        // Ensure the plantilla has groups
        if (plantilla.getGrupos() == null || plantilla.getGrupos().isEmpty()) {
            throw new IllegalArgumentException("The plantilla must have at least one Grupo.");
        }

        // Prepare the list of Grupo IDs from the provided plantilla
        List<Integer> grupoIds = new ArrayList<>();
        try {
            for (Grupo grupo : plantilla.getGrupos()) {
                Integer grupoId = GrupoCRUD.getGrupoId(conn, grupo); // Reuse GrupoCRUD for ID resolution
                if (grupoId == null) {
                    System.err.println("No ID found for Grupo: " + grupo.getNombreProfesor());
                    continue; // Skip this Grupo
                }
                grupoIds.add(grupoId);
            }
            if (grupoIds.isEmpty()) {
                return null; // No PlantillaHorario can match
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to resolve Grupo IDs for PlantillaHorario: " + e.getMessage(), e);
        }

        // Create placeholders for the SQL IN clause
        String placeholders = String.join(",", Collections.nCopies(grupoIds.size(), "?"));

        // Query to find a matching PlantillaHorario
        String query = String.format(
                "SELECT pj.PlantillaHorario_id " +
                        "FROM PlantillaHorario_Junction pj " +
                        "WHERE pj.Grupo_id IN (%s) " +
                        "GROUP BY pj.PlantillaHorario_id " +
                        "HAVING COUNT(*) = ? AND " +
                        "SUM(CASE WHEN pj.Grupo_id NOT IN (%s) THEN 1 ELSE 0 END) = 0",
                placeholders, placeholders
        );

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            int paramIndex = 1;

            // Bind the Grupo IDs for the IN clause
            for (Integer id : grupoIds) {
                stmt.setInt(paramIndex++, id);
            }

            // Bind the count for the HAVING clause
            stmt.setInt(paramIndex++, grupoIds.size());

            // Bind the Grupo IDs again for the NOT IN clause in HAVING
            for (Integer id : grupoIds) {
                stmt.setInt(paramIndex++, id);
            }

            // Execute the query
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("PlantillaHorario_id");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to execute query: " + e.getMessage(), e);
        }

        // If no matching PlantillaHorario is found, return null
        return null;
    }

    public static void eliminarTodasLasPlantillas(Connection conn) throws SQLException {
        String sql = "DELETE FROM PlantillaHorario_Junction";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }
}
