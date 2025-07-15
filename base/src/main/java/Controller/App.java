package Controller;

import Model.DataManager;
import Model.Horario;
import Model.Ruta;
import View.Vista;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase principal de la aplicación. Actúa como el controlador que
 * coordina la interacción entre la Vista, el DataManager y los servicios de
 * búsqueda/optimización.
 * Se enfoca en las funcionalidades de búsqueda y optimización.
 */
public class App {

    private final DataManager dataManager;
    private final Vista vista;

    // Los controladores se declaran aquí pero se instancian en run()
    // después de que los datos se hayan cargado.
    private Buscadores buscadores;
    private OptimizadorRutas optimizador;

    // appData guardará los datos cargados en memoria.
    private DataManager.AppData appData;

    public App() {
        this.dataManager = new DataManager();
        this.vista = new Vista();
        // Se inicializan como null porque dependen de datos que aún no se han cargado.
        this.buscadores = null;
        this.optimizador = null;
        this.appData = null;
    }

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    /**
     * Ejecuta el ciclo principal de la aplicación.
     * Carga los datos de la base de datos al inicio y luego presenta el menú.
     */
    public void run() {
        // --- PASO 1: Cargar todos los datos desde la DB al iniciar ---
        try {
            vista.mostrarMensaje("Iniciando aplicación... Cargando datos desde la base de datos.");
            appData = dataManager.cargarTodosDatos();

            if (appData.getHorarios().isEmpty() || appData.getRutas().isEmpty() || appData.getBuses().isEmpty()) {
                vista.mostrarMensaje(
                        "Advertencia: No se encontraron horarios, rutas o buses en la base de datos.");
                vista.mostrarMensaje(
                        "Asegúrese de que la base de datos ha sido poblada previamente.");
            } else {
                vista.mostrarMensaje("Datos cargados exitosamente desde la DB.");
            }

            // --- PASO 2: Instanciar los controladores con los datos ya cargados ---
            this.buscadores = new Buscadores(appData.getHorarios(), appData.getRutas());
            this.optimizador = new OptimizadorRutas(appData.getHorarios(), appData.getRutas(), appData.getBuses());

        } catch (SQLException e) {
            vista.mostrarError("Error crítico al cargar datos iniciales: " + e.getMessage());
            e.printStackTrace(); // Para depuración
            vista.mostrarMensaje("La aplicación no puede continuar sin datos. Saliendo.");
            return; // Termina la aplicación si no se pueden cargar los datos
        }
        boolean salir = false;
        while (!salir) {
            vista.mostrarMenuPrincipal();
            int opcion = vista.leerOpcion();

            try {
                switch (opcion) {
                    case 1: // Buscar Horarios por Línea
                        if (appData.getHorarios().isEmpty()) {
                            vista.mostrarMensaje("No hay horarios cargados para buscar.");
                            break;
                        }
                        String lineaBusqueda = vista.pedirLineaBusqueda();
                        List<Horario> horariosPorLinea = buscadores.buscarHorariosPorLinea(lineaBusqueda);
                        vista.mostrarHorariosParaLinea(horariosPorLinea, lineaBusqueda);
                        Ruta rutaEncontrada = buscadores.obtenerRutaPorNombre(lineaBusqueda);
                        if (rutaEncontrada != null) {
                            vista.mostrarDetalleRuta(rutaEncontrada);
                        } else {
                            vista.mostrarMensaje(
                                    "\nNo se encontró una ruta detallada para la línea '" + lineaBusqueda + "'.");
                        }
                        break;
                    case 2: 
                        if (appData.getHorarios().isEmpty()) {
                            vista.mostrarMensaje("No hay horarios cargados para buscar.");
                            break;
                        }
                        String horaBusqueda = vista.pedirHoraBusqueda();
                        // El método busca en un rango de 30 minutos a partir de la hora ingresada
                        List<Horario> horariosPorHora = buscadores.buscarHorariosDisponibles(horaBusqueda);
                        vista.mostrarHorarios(horariosPorHora);
                        break;
                    case 3: // Optimizar y Mostrar Rutas
                        if (appData.getBuses().isEmpty()) {
                            vista.mostrarMensaje("No hay buses cargados para optimizar.");
                            break;
                        }
                        // La "optimización" es responsabilidad de la clase OptimizadorRutas.
                        // Esta pide un número y muestra el resultado directamente en consola.
                        int promedioPersonas = vista.pedirPromedioPersonas();
                        optimizador.optimizarRutasPorPromedioPersonas(promedioPersonas);
                        break;
                    case 0:
                        salir = true;
                        vista.mostrarMensaje("Saliendo de la aplicación. ¡Hasta luego!");
                        break;
                    default:
                        vista.mostrarMensaje("Opción no válida. Por favor, intente de nuevo.");
                }
            } catch (Exception e) { // Captura cualquier error inesperado
                vista.mostrarError("Ocurrió un error inesperado: " + e.getMessage());
                e.printStackTrace(); // Para depuración
            }
        }

        vista.cerrarScanner();
        dataManager.closeDBConnection(); // Cierra la conexión a la DB limpiamente
    }
}