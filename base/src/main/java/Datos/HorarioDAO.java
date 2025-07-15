package Datos;

import Model.Horario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Data Access Object (DAO) para la entidad Horario.
 * Gestiona todas las operaciones CRUD (Crear, Leer, Actualizar, Borrar)
 * relacionadas con los horarios en la base de datos.
 */
public class HorarioDAO {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.US);

    public HorarioDAO() {
    }

    /**
     * Carga todos los horarios desde la base de datos.
     *
     * @return Una lista de objetos Horario.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public List<Horario> cargarHorarios() throws SQLException {
        List<Horario> horarios = new ArrayList<>();
        String sql = "SELECT id, hora_original, hora_local, lineas FROM Horarios";
        try (Connection conn = DBConnection.getConnection(); // Obtener la conexión
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String horaOriginal = rs.getString("hora_original");
                String horaLocalStr = rs.getString("hora_local");
                List<String> lineas = new ArrayList<>();
                String lineasStr = rs.getString("lineas");
                if (lineasStr != null && !lineasStr.isEmpty()) {
                    lineas = Arrays.asList(lineasStr.split(","));
                }

                LocalTime horaLocal = null;
                try {
                    if (horaLocalStr != null && !horaLocalStr.isEmpty()) {
                        horaLocal = LocalTime.parse(horaLocalStr, FORMATTER);
                    }
                } catch (DateTimeParseException e) {
                    System.err.println("Error al parsear hora_local de DB para Horario ID " + id + ": " + horaLocalStr);
                }
                horarios.add(new Horario(id, horaOriginal, horaLocal, lineas));
            }
        }
        System.out.println("Horarios cargados desde la DB: " + horarios.size());
        return horarios;
    }

    /**
     * Guarda una lista de horarios en la base de datos.
     * Si un horario ya tiene ID, lo actualiza; de lo contrario, lo inserta.
     *
     * @param horarios La lista de horarios a guardar.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void guardarHorarios(List<Horario> horarios) throws SQLException {
        String insertSql = "INSERT INTO Horarios (hora_original, hora_local, lineas) VALUES (?, ?, ?)";
        String updateSql = "UPDATE Horarios SET hora_original = ?, hora_local = ?, lineas = ? WHERE id = ?";

        Connection conn = DBConnection.getConnection();
        // Usar try-with-resources para PreparedStatement
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            for (Horario horario : horarios) {
                String lineasStr = String.join(",", horario.getLineas());
                String horaLocalStr = horario.getHora() != null ? horario.getHora().format(FORMATTER) : null;

                if (horario.getId() == 0) { // Nuevo horario, insertar
                    insertStmt.setString(1, horario.getHoraTexto());
                    insertStmt.setString(2, horaLocalStr);
                    insertStmt.setString(3, lineasStr);
                    insertStmt.executeUpdate();

                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            horario.setId(generatedKeys.getInt(1)); // Asignar el ID generado
                        }
                    }
                } else { // Horario existente, actualizar
                    updateStmt.setString(1, horario.getHoraTexto());
                    updateStmt.setString(2, horaLocalStr);
                    updateStmt.setString(3, lineasStr);
                    updateStmt.setInt(4, horario.getId());
                    updateStmt.executeUpdate();
                }
            }
        }
        System.out.println("Horarios guardados/actualizados en la base de datos.");
    }

    /**
     * Elimina todos los horarios de la base de datos.
     * 
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void eliminarTodosHorarios() throws SQLException {
        String sql = "DELETE FROM Horarios";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Todos los horarios eliminados de la DB.");
        }
    }
}