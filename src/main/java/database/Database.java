package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    /*
    private static final String URL = "jdbc:sqlite:appdatabase.db";

    public Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL);
            System.out.println("Conexión a la base de datos establecida.");
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
        return connection;
    }
     */
    private static final String URL = "jdbc:sqlite:appdatabase.db";
    private static Connection connection = null;

    // Private constructor to prevent instantiation
    private Database() {}

    // Method to get the single instance of the connection
    public static Connection getConnection() {
        if (connection == null) {
            try {
                if (connection == null || connection.isClosed()) {
                    connection = DriverManager.getConnection(URL);
                    System.out.println("Conexión a la base de datos establecida.");
                }
            } catch (SQLException e) {
                System.out.println("Error de conexión: " + e.getMessage());
            }
        }
        return connection;
    }

    // Optional: Method to close the connection when the application exits
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión a la base de datos cerrada.");
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}

