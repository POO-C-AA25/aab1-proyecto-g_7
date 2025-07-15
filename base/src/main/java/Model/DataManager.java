package Model; // O podrías moverlo a un paquete 'Service' o 'Manager' si lo prefieres

import Datos.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase que gestiona la carga y persistencia general de datos para Horarios,
 * Rutas y Buses.
 * Actúa como un coordinador, delegando las operaciones específicas a los DAOs y
 * utilidades.
 * Cumple con el Principio de Responsabilidad Única (SRP) al delegar las
 * preocupaciones.
 */
public class DataManager {

    private final HorarioDAO horarioDAO;
    private final RutaDAO rutaDAO;
    private final BusDAO busDAO;
    private final CSVLoader csvLoader;
    private final DataGenerator dataGenerator;

    public DataManager() {
        this.horarioDAO = new HorarioDAO();
        this.rutaDAO = new RutaDAO();
        this.busDAO = new BusDAO();
        this.csvLoader = new CSVLoader();
        this.dataGenerator = new DataGenerator();
    }

    /**
     * Carga todos los datos (Horarios, Rutas, Buses) desde la base de datos.
     * 
     * @return Un objeto DataContainer (o similar) que contenga todos los datos
     *         cargados.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public AppData cargarTodosDatos() throws SQLException {
        System.out.println("\n--- Cargando todos los datos desde la Base de Datos ---");
        List<Horario> horarios = horarioDAO.cargarHorarios();
        List<Ruta> rutas = rutaDAO.cargarRutas();
        List<Bus> buses = busDAO.cargarBuses();
        System.out.println("--- Carga de datos completada ---");
        return new AppData(horarios, rutas, buses);
    }

    /**
     * Guarda todos los datos (Horarios, Rutas, Buses) en la base de datos.
     * 
     * @param appData El objeto que contiene los datos a guardar.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void guardarTodosDatos(AppData appData) throws SQLException {
        System.out.println("\n--- Guardando todos los datos en la Base de Datos ---");
        horarioDAO.guardarHorarios(appData.getHorarios());
        rutaDAO.guardarRutas(appData.getRutas());
        busDAO.guardarBuses(appData.getBuses());
        System.out.println("--- Guardado de datos completado ---");
    }

    /**
     * Elimina todos los datos de las tablas de la base de datos.
     * Útil para limpiar antes de una nueva carga.
     * 
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void limpiarBaseDeDatos() throws SQLException {
        System.out.println("\n--- Limpiando la Base de Datos ---");
        busDAO.eliminarTodosBuses(); // Dependencias inversas: Bus depende de Horario y Ruta, por ejemplo, Paradas
                                     // depende de Rutas.
        horarioDAO.eliminarTodosHorarios(); // Eliminar horarios antes que rutas si hay FK indirectas
        rutaDAO.eliminarTodasRutasYParadas();
        System.out.println("--- Limpieza de DB completada ---");
    }

    /**
     * Carga datos desde archivos CSV.
     * 
     * @param horariosCSV Ruta al archivo CSV de horarios.
     * @param rutasCSV    Ruta al archivo CSV de rutas.
     * @return Objeto AppData con los datos cargados.
     * @throws IOException Si ocurre un error al leer los archivos.
     */
    public AppData cargarDatosDesdeCSV(String horariosCSV, String rutasCSV) throws IOException {
        System.out.println("\n--- Cargando datos desde archivos CSV ---");
        List<Horario> horarios = csvLoader.leerHorariosDesdeCSV(horariosCSV);
        List<Ruta> rutas = csvLoader.leerRutasDesdeCSV(rutasCSV);

        // Si se necesitan buses iniciales, se pueden generar aquí
        List<Bus> buses = dataGenerator.crearBuses(10); // Por ejemplo, 10 buses
        dataGenerator.asignarHorariosABuses(buses, horarios); // Asignarles horarios

        System.out.println("--- Carga desde CSV completada ---");
        return new AppData(horarios, rutas, buses);
    }

    /**
     * Clase interna o separada para encapsular todos los datos de la aplicación.
     * Facilita el paso de datos entre capas.
     */
    public static class AppData {
        private List<Horario> horarios;
        private List<Ruta> rutas;
        private List<Bus> buses;

        public AppData(List<Horario> horarios, List<Ruta> rutas, List<Bus> buses) {
            this.horarios = horarios;
            this.rutas = rutas;
            this.buses = buses;
        }

        public List<Horario> getHorarios() {
            return horarios;
        }

        public List<Ruta> getRutas() {
            return rutas;
        }

        public List<Bus> getBuses() {
            return buses;
        }

        // Métodos setters si es necesario modificar las listas después de la carga
        public void setHorarios(List<Horario> horarios) {
            this.horarios = horarios;
        }

        public void setRutas(List<Ruta> rutas) {
            this.rutas = rutas;
        }

        public void setBuses(List<Bus> buses) {
            this.buses = buses;
        }
    }

    public void closeDBConnection() {
        DBConnection.closeConnection();
    }
}