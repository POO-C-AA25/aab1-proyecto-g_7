package Datos;

import Model.Parada;
import Model.Ruta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Ruta y sus Paradas.
 * Gestiona todas las operaciones CRUD relacionadas con rutas y paradas en la
 * base de datos.
 */
public class RutaDAO {

    public RutaDAO() {
    }

    /**
     * Carga todas las rutas y sus paradas desde la base de datos.
     *
     * @return Una lista de objetos Ruta.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public List<Ruta> cargarRutas() throws SQLException {
        List<Ruta> rutas = new ArrayList<>();
        // Primero cargar las rutas
        String sqlRutas = "SELECT id, nombre_ruta FROM Rutas";
        try (Connection conn = DBConnection.getConnection(); // Obtener la conexión
                Statement stmtRutas = conn.createStatement();
                ResultSet rsRutas = stmtRutas.executeQuery(sqlRutas)) {

            while (rsRutas.next()) {
                int rutaId = rsRutas.getInt("id");
                String nombreRuta = rsRutas.getString("nombre_ruta");
                Ruta ruta = new Ruta(rutaId, nombreRuta);
                rutas.add(ruta);
            }
        }

        // Luego cargar las paradas y asignarlas a sus rutas
        String sqlParadas = "SELECT id, ruta_id, nombre_parada FROM Paradas ORDER BY ruta_id, id";
        try (Connection conn = DBConnection.getConnection(); // Re-obtener la conexión (puede ser la misma)
                Statement stmtParadas = conn.createStatement();
                ResultSet rsParadas = stmtParadas.executeQuery(sqlParadas)) {

            while (rsParadas.next()) {
                int paradaId = rsParadas.getInt("id");
                int rutaId = rsParadas.getInt("ruta_id");
                String nombreParada = rsParadas.getString("nombre_parada");
                Parada parada = new Parada(paradaId, rutaId, nombreParada);

                for (Ruta r : rutas) {
                    if (r.getId() == rutaId) {
                        r.agregarParada(parada);
                        break;
                    }
                }
            }
        }
        System.out.println("Rutas y paradas cargadas desde la DB: " + rutas.size());
        return rutas;
    }

    /**
     * Guarda una lista de rutas y sus paradas en la base de datos.
     * Si una ruta/parada ya tiene ID, la actualiza; de lo contrario, la inserta.
     *
     * @param rutas La lista de rutas a guardar.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void guardarRutas(List<Ruta> rutas) throws SQLException {
        String insertRutaSql = "INSERT INTO Rutas (nombre_ruta) VALUES (?)";
        String updateRutaSql = "UPDATE Rutas SET nombre_ruta = ? WHERE id = ?";
        String insertParadaSql = "INSERT INTO Paradas (ruta_id, nombre_parada) VALUES (?, ?)";
        String updateParadaSql = "UPDATE Paradas SET nombre_parada = ? WHERE id = ?";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement insertRutaStmt = conn.prepareStatement(insertRutaSql, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement updateRutaStmt = conn.prepareStatement(updateRutaSql);
                PreparedStatement insertParadaStmt = conn.prepareStatement(insertParadaSql,
                        Statement.RETURN_GENERATED_KEYS);
                PreparedStatement updateParadaStmt = conn.prepareStatement(updateParadaSql)) {

            for (Ruta ruta : rutas) {
                if (ruta.getId() == 0) { // Nueva ruta
                    insertRutaStmt.setString(1, ruta.getNombreruta());
                    insertRutaStmt.executeUpdate();
                    try (ResultSet generatedKeys = insertRutaStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            ruta.setId(generatedKeys.getInt(1));
                        }
                    }
                } else { // Ruta existente
                    updateRutaStmt.setString(1, ruta.getNombreruta());
                    updateRutaStmt.setInt(2, ruta.getId());
                    updateRutaStmt.executeUpdate();
                }

                // Guardar paradas de la ruta
                for (Parada parada : ruta.getParadas()) {
                    parada.setRutaId(ruta.getId()); // Asegurar que la parada tiene el ID de la ruta
                    if (parada.getId() == 0) { // Nueva parada
                        insertParadaStmt.setInt(1, parada.getRutaId());
                        insertParadaStmt.setString(2, parada.getNombreparada());
                        insertParadaStmt.executeUpdate();
                        try (ResultSet generatedKeys = insertParadaStmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                parada.setId(generatedKeys.getInt(1));
                            }
                        }
                    } else { // Parada existente
                        updateParadaStmt.setString(1, parada.getNombreparada());
                        updateParadaStmt.setInt(2, parada.getId());
                        updateParadaStmt.executeUpdate();
                    }
                }
            }
        }
        System.out.println("Rutas y paradas guardadas/actualizadas en la base de datos.");
    }

    /**
     * Elimina todas las rutas y sus paradas de la base de datos.
     * 
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void eliminarTodasRutasYParadas() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Paradas"); // Eliminar paradas primero por FK
            stmt.executeUpdate("DELETE FROM Rutas");
            System.out.println("Todas las rutas y paradas eliminadas de la DB.");
        }
    }
}