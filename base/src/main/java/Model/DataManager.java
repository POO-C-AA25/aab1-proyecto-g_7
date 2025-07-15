package Model;

import Datos.DBConnection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
import java.util.Random;

/**
 * Clase que gestiona la carga y persistencia de datos para Horarios, Rutas y
 * Buses.
 * Interactúa con la base de datos y puede leer desde archivos CSV.
 */
public class DataManager {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.US);

    public DataManager() {
    }

    // --- Métodos para Cargar Datos desde la Base de Datos ---
    public List<Horario> cargarHorarios() throws SQLException {
        List<Horario> horarios = new ArrayList<>();
        String sql = "SELECT id, hora_original, hora_local, lineas FROM Horarios";
        try (Statement stmt = DBConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String horaOriginal = rs.getString("hora_original");
                String horaLocalStr = rs.getString("hora_local");
                List<String> lineas = new ArrayList<>();
                String lineasStr = rs.getString("lineas");
                if (lineasStr != null && !lineasStr.isEmpty()) {
                    // Asegurarse de que el delimitador de guardado sea ","
                    lineas = Arrays.asList(lineasStr.split(","));
                }

                LocalTime horaLocal = null;
                try {
                    horaLocal = LocalTime.parse(horaLocalStr, FORMATTER);
                } catch (DateTimeParseException e) {
                    System.err.println("Error al parsear hora_local de DB para Horario ID " + id + ": " + horaLocalStr);
                }
                horarios.add(new Horario(id, horaOriginal, horaLocal, lineas));
            }
        }
        return horarios;
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
        try (Statement stmtRutas = DBConnection.getConnection().createStatement();
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
        try (Statement stmtParadas = DBConnection.getConnection().createStatement();
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
        return rutas;
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
        try (Statement stmt = DBConnection.getConnection().createStatement();
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
                            // Asegurarse de que hay al menos dos partes antes de intentar acceder a los
                            // índices
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
        return buses;
    }

    // --- Métodos para Guardar Datos en la Base de Datos ---

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
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            for (Horario horario : horarios) {
                // Convertir List<String> de lineas a una cadena separada por comas para guardar
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
     * Guarda una lista de buses en la base de datos.
     * Si un bus ya tiene ID, lo actualiza; de lo contrario, lo inserta.
     * Las asignaciones se guardan como una cadena JSON simple.
     * 
     * @param buses    La lista de buses a guardar.
     * @param horarios La lista de horarios para mapear asignaciones (si es
     *                 necesario).
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void guardarBuses(List<Bus> buses, List<Horario> horarios) throws SQLException {
        String insertSql = "INSERT INTO Buses (identificador, asignaciones) VALUES (?, ?)";
        String updateSql = "UPDATE Buses SET identificador = ?, asignaciones = ? WHERE id = ?";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            for (Bus bus : buses) {
                // Convertir List<String> de asignaciones a una cadena para guardar
                // Asumiendo que cada asignación es "hora -> linea" y se unen con un tab "\t"
                String asignacionesStr = String.join("\t", bus.getAsignacion());

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

    // --- Métodos para Leer Datos desde CSV (como respaldo o carga inicial) ---

    /**
     * Lee horarios desde un archivo CSV.
     * 
     * @param rutaArchivoCSV La ruta al archivo CSV de horarios.
     * @return Una lista de objetos Horario.
     * @throws IOException Si ocurre un error de I/O al leer el archivo.
     */
    public List<Horario> leerHorariosDesdeCSV(String rutaArchivoCSV) throws IOException {
        List<Horario> horarios = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivoCSV))) {
            String line;
            // Saltar la primera línea (encabezado)
            br.readLine();
            while ((line = br.readLine()) != null) {
                // Asumimos que el delimitador es punto y coma (;)
                String[] parts = line.split(";");
                if (parts.length >= 2) {
                    String horaOriginal = parts[0].trim();
                    LocalTime hora = parsearHora(horaOriginal);
                    List<String> lineas = new ArrayList<>();
                    if (parts.length > 1) {
                        for (int i = 1; i < parts.length; i++) {
                            lineas.add(parts[i].trim());
                        }
                    }
                    if (hora != null) {
                        horarios.add(new Horario(horaOriginal, hora, lineas));
                    } else {
                        System.err.println("Advertencia: No se pudo parsear la hora del CSV: " + horaOriginal);
                    }
                }
            }
        }
        System.out.println("Horarios leídos desde CSV: " + horarios.size());
        return horarios;
    }

    /**
     * Lee rutas y sus paradas desde un archivo CSV.
     * Asume que cada línea del CSV es: "NombreRuta;Parada1;Parada2;..."
     * 
     * @param rutaArchivoCSV La ruta al archivo CSV de rutas.
     * @return Una lista de objetos Ruta.
     * @throws IOException Si ocurre un error de I/O al leer el archivo.
     */
    public List<Ruta> leerRutasDesdeCSV(String rutaArchivoCSV) throws IOException {
        List<Ruta> rutas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivoCSV))) {
            String line;
            // Saltar la primera línea (encabezado)
            br.readLine();
            while ((line = br.readLine()) != null) {
                // Asumimos que el delimitador es punto y coma (;)
                String[] parts = line.split(";");
                if (parts.length > 0) {
                    String nombreRuta = parts[0].trim();
                    // Eliminar BOM si existe en el primer carácter del nombre de la ruta
                    if (nombreRuta.length() > 0 && nombreRuta.charAt(0) == '\uFEFF') {
                        nombreRuta = nombreRuta.substring(1);
                    }

                    Ruta ruta = new Ruta(nombreRuta);
                    for (int i = 1; i < parts.length; i++) {
                        ruta.agregarParada(new Parada(parts[i].trim()));
                    }
                    rutas.add(ruta);
                }
            }
        }
        System.out.println("Rutas leídas desde CSV: " + rutas.size());
        return rutas;
    }

    /**
     * Parsea una cadena de texto que representa una hora a un objeto LocalTime.
     * Método auxiliar, duplicado del de Buscadores para evitar dependencia
     * circular.
     * 
     * @param horaTexto La cadena de la hora a parsear.
     * @return Un objeto LocalTime si el parseo es exitoso, o null si hay un error.
     */
    private LocalTime parsearHora(String horaTexto) {
        try {
            String estandarizada = horaTexto.replaceAll("\\s+", " ").trim();
            estandarizada = estandarizada.toUpperCase().replace(".", "");
            int amPmIndex = estandarizada.indexOf("AM");
            if (amPmIndex == -1) {
                amPmIndex = estandarizada.indexOf("PM");
            }
            if (amPmIndex != -1 && estandarizada.charAt(amPmIndex - 1) != ' ') {
                estandarizada = estandarizada.substring(0, amPmIndex) + " " + estandarizada.substring(amPmIndex);
            }
            return LocalTime.parse(estandarizada, FORMATTER);
        } catch (DateTimeParseException e) {
            System.err.println(
                    "Error de parseo en DataManager.parsearHora: " + horaTexto + " (Causa: " + e.getMessage() + ")");
            return null;
        }
    }

    /**
     * Crea un número de objetos Bus con identificadores simples.
     * 
     * @param cantidad La cantidad de buses a crear.
     * @return una lista de objetos Bus.
     */
    public List<Bus> crearBuses(int cantidad) {
        List<Bus> buses = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            buses.add(new Bus("Bus-" + String.format("%02d", i)));
        }
        System.out.println("Buses creados: " + buses.size());
        return buses;
    }

    /**
     * Asigna horarios aleatorios a los buses.
     * 
     * @param buses    La lista de buses.
     * @param horarios La lista de horarios disponibles.
     */
    public void asignarHorariosABuses(List<Bus> buses, List<Horario> horarios) {
        if (buses.isEmpty() || horarios.isEmpty()) {
            System.out.println("No hay buses u horarios para asignar.");
            return;
        }
        Random random = new Random();
        for (Bus bus : buses) {
            // Asignar 1 a 3 horarios por bus
            int numAsignaciones = random.nextInt(3) + 1;
            for (int i = 0; i < numAsignaciones; i++) {
                Horario horarioAleatorio = horarios.get(random.nextInt(horarios.size()));
                // Asegurarse de que el horario tenga al menos una línea para la asignación
                if (!horarioAleatorio.getLineas().isEmpty()) {
                    String lineaAsignada = horarioAleatorio.getLineas()
                            .get(random.nextInt(horarioAleatorio.getLineas().size()));
                    bus.agregarAsignacion(horarioAleatorio.getHoraTexto(), lineaAsignada);
                }
            }
        }
        System.out.println("Horarios asignados a buses.");
    }

    /**
     * Elimina todos los datos de las tablas de la base de datos.
     * Útil para limpiar antes de una nueva carga desde CSV.
     * 
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void limpiarBaseDeDatos() throws SQLException {
        try (Statement stmt = DBConnection.getConnection().createStatement()) {
            // Eliminar en orden inverso de dependencia para evitar errores de FK
            stmt.executeUpdate("DELETE FROM Buses");
            stmt.executeUpdate("DELETE FROM Paradas");
            stmt.executeUpdate("DELETE FROM Rutas");
            stmt.executeUpdate("DELETE FROM Horarios");
            System.out.println("Base de datos limpiada.");
        }
    }
}
