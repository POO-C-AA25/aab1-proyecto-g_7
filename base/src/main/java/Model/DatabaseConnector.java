package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {
    static final String DB_URL = "jdbc:mysql://localhost:3306/prueba1";
    static final String USER = "root";
    static final String PASS = "1150";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            System.out.println("Intentando conectar a la base de datos...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("¡Conexión exitosa a la base de datos!");
            stmt = conn.createStatement();
            String sql = "SELECT id, name FROM users";
            rs = stmt.executeQuery(sql);

            System.out.println("\n--- Resultados de la consulta ---");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                System.out.println("ID: " + id + ", Nombre: " + name);
            }
            System.out.println("--------------------------------\n");

        } catch (SQLException se) {
            System.err.println("Error de SQL:");
            se.printStackTrace(); // Imprime la traza completa del error para depuración
        } catch (Exception e) {
            // Manejo de otros tipos de excepciones
            System.err.println("Ocurrió un error inesperado:");
            e.printStackTrace();
        } finally {
            // Se cierran en orden inverso a su creación para evitar dependencias.
            try {
                if (rs != null) {
                    rs.close();
                    System.out.println("ResultSet cerrado.");
                }
            } catch (SQLException se2) {
                se2.printStackTrace();
            }
            try {
                if (stmt != null) {
                    stmt.close();
                    System.out.println("Statement cerrado.");
                }
            } catch (SQLException se2) {
                se2.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                    System.out.println("Conexión a la base de datos cerrada.");
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
