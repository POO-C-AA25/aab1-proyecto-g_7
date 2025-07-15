package Datos;

import Model.Bus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Bus.
 * Gestiona todas las operaciones CRUD relacionadas con los buses en la base de
 * datos.
 */
public class BusDAO {

    public BusDAO() {
    }

    /**
     * Carga todos los buses y sus asignaciones desde la base de datos.
     *
     * @return Una lista de objetos Bus.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public List<Bus> cargarBuses() throws SQLException {
        List<Bus> buses = new ArrayList<>();
        String sql = "SELECT id, identificador, asignaciones FROM Buses";
        try (Connection conn = DBConnection.getConnection(); // Obtener la conexión
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String identificador = rs.getString("identificador");
                String asignacionesStr = rs.getString("asignaciones");

                Bus bus = new Bus(id, identificador);
                if (asignacionesStr != null && !asignacionesStr.isEmpty()) {
                    List<String> parsedAsignaciones = Arrays.asList(asignacionesStr.split("\t"));
                    for (String asignacion : parsedAsignaciones) {
                        if (!asignacion.trim().isEmpty()) {
                            String[] partes = asignacion.split("->");
                            if (partes.length == 2) {
                                bus.agregarAsignacion(partes[0].trim(), partes[1].trim());
                            } else {
                                System.err.println("Advertencia: Formato de asignación inesperado en bus ID " + id
                                        + ": " + asignacion);
                            }
                        }
                    }
                }
                buses.add(bus);
            }
        }
        System.out.println("Buses cargados desde la DB: " + buses.size());
        return buses;
    }

    /**
     * Guarda una lista de buses en la base de datos.
     * Si un bus ya tiene ID, lo actualiza; de lo contrario, lo inserta.
     * Las asignaciones se guardan como una cadena JSON simple o un formato
     * delimitado.
     *
     * @param buses La lista de buses a guardar.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void guardarBuses(List<Bus> buses) throws SQLException {
        String insertSql = "INSERT INTO Buses (identificador, asignaciones) VALUES (?, ?)";
        String updateSql = "UPDATE Buses SET identificador = ?, asignaciones = ? WHERE id = ?";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            for (Bus bus : buses) {
                String asignacionesStr = String.join("\t", bus.getAsignacion()); // Asumiendo que getAsignacion devuelve
                                                                                 // List<String> como "hora -> linea"

                if (bus.getId() == 0) { // Nuevo bus, insertar
                    insertStmt.setString(1, bus.getIdentificador());
                    insertStmt.setString(2, asignacionesStr);
                    insertStmt.executeUpdate();

                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            bus.setId(generatedKeys.getInt(1)); // Asignar el ID generado
                        }
                    }
                } else { // Bus existente, actualizar
                    updateStmt.setString(1, bus.getIdentificador());
                    updateStmt.setString(2, asignacionesStr);
                    updateStmt.setInt(3, bus.getId());
                    updateStmt.executeUpdate();
                }
            }
        }
        System.out.println("Buses guardados/actualizados en la base de datos.");
    }

    /**
     * Elimina todos los buses de la base de datos.
     * 
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void eliminarTodosBuses() throws SQLException {
        String sql = "DELETE FROM Buses";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Todos los buses eliminados de la DB.");
        }
    }
}