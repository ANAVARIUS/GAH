package database;
import java.sql.*;
import gestiondehorarios.Materia;

public class MateriaCRUD {
    public static void addMateria(Connection connection, Materia materia) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM Materia WHERE nombre = ?";
        try (PreparedStatement checkPstmt = connection.prepareStatement(checkSql)) {
            checkPstmt.setString(1, materia.getNombreMateria());
            try (ResultSet rs = checkPstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Esta materia ya existe.");
                }
            }
        }

        String sql = "INSERT INTO Materia (nombre, noCreditos) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, materia.getNombreMateria());
            pstmt.setInt(2, materia.getNoCreditos());
            pstmt.executeUpdate();
        }
    }
    public static Materia getMateriaByNombre(Connection connection, String nombreMateria) throws SQLException {
        String sql = "SELECT * FROM Materia WHERE nombreMateria = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nombreMateria);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Materia(rs.getString("nombreMateria"), rs.getInt("noCreditos"));
                }
            }
        }
        return null;
    }
    public static void deleteMateria(Connection connection, int id) throws SQLException {
        String sql = "DELETE FROM Materia WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
    public static Integer getIdByNombre(Connection connection, String nombreMateria) throws SQLException {
        String sql = "SELECT id FROM Materia WHERE nombreMateria = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nombreMateria);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return null;
    }
    public static Materia getMateriaById(Connection connection, int id) throws SQLException {
        String sql = "SELECT * FROM Materia WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Materia(rs.getString("nombreMateria"), rs.getInt("noCreditos"));
                }
            }
        }
        return null;
    }
}

