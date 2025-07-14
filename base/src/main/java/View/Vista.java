package View;

import Model.*;
import Controller.Buscadores;
import Controller.OptimizadorRutas;
import Model.DataManager;
import Datos.DBConnection;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.sql.SQLException; // Importar para manejar excepciones de SQL

public class Vista {
    public Scanner scanner;
    private DataManager dataManager; // Inyección de dependencia para DataManager

    public Vista() {
        scanner = new Scanner(System.in);
        this.dataManager = new DataManager(); // Instanciar DataManager
    }

    public void solicitarOpcion(Buscadores buscadores, OptimizadorRutas optimizador) {
        int opcion = -1;
        boolean entradaValida = false;
        do {
            System.out.println("\n¿Qué desea buscar o hacer?");
            System.out.println("1. Información de una Línea de bus (horarios y paradas)");
            System.out.println("2. Horarios disponibles por Hora");
            System.out.println("3. Optimizar Rutas por Promedio de Personas"); // Nueva opción
            System.out.print("Seleccione una opción (1, 2 o 3): ");
            try {
                if (scanner.hasNextInt()) {
                    opcion = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea

                    switch (opcion) {
                        case 1:
                            buscarPorLinea(buscadores);
                            entradaValida = true;
                            break;
                        case 2:
                            buscarPorHora(buscadores);
                            entradaValida = true;
                            break;
                        case 3: // Nueva opción de optimización
                            optimizarRutas(optimizador);
                            entradaValida = true;
                            break;
                        default:
                            System.out.println("Opción no válida. Intente de nuevo (1, 2 o 3).");
                            break;
                    }
                } else {
                    System.out.println("Entrada no válida. Por favor, ingrese un número (1, 2 o 3).");
                    scanner.nextLine(); // Consumir la entrada inválida
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Error de entrada. Por favor, ingrese un número (1, 2 o 3).");
                scanner.nextLine(); // Consumir la entrada inválida
            }
        } while (!entradaValida);
    }

    /**
     * Solicita al usuario que ingrese una hora.
     * return La hora ingresada como String.
     */
    public String pedirHora() {
        String hora;
        do {
            System.out.print("Ingrese la hora (ejemplo: 4:15 PM): ");
            hora = scanner.nextLine().trim();
            if (hora.isEmpty()) {
                System.out.println("La hora no puede estar vacía. Intente de nuevo.");
            }
        } while (hora.isEmpty());
        return hora;
    }

    /**
     * Solicita al usuario que ingrese el nombre de una línea de bus.
     * return El nombre de la línea de bus como String.
     */
    public String pedirLineaBus() {
        String lineaBus;
        do {
            System.out.print("Ingrese la línea de bus (ej. Linea 01): ");
            lineaBus = scanner.nextLine().trim();
            if (lineaBus.isEmpty()) {
                System.out.println("La línea de bus no puede estar vacía. Intente de nuevo.");
            }
        } while (lineaBus.isEmpty());
        return lineaBus;
    }

    /**
     * Implementa la lógica para buscar horarios y paradas por línea de bus.
     * para buscadores El objeto Buscadores.
     */
    public void buscarPorLinea(Buscadores buscadores) {
        String lineaBus = pedirLineaBus();
        List<Horario> horarios = buscadores.buscarHorariosPorLinea(lineaBus);
        mostrarHorariosConDetallesDeLinea(horarios, lineaBus, buscadores);
    }

    /**
     * Implementa la lógica para buscar horarios disponibles por hora.
     * para buscadores El objeto Buscadores.
     */
    public void buscarPorHora(Buscadores buscadores) {
        String horaInput = pedirHora();
        List<Horario> horarios = buscadores.buscarHorariosDisponibles(horaInput);

        if (Buscadores.parsearHora(horaInput) == null && horarios.isEmpty()) {
            System.out.println("Formato de hora ingresado no es válido. Por favor, use el formato 'h:mm AM/PM'.");
            return;
        }

        if (horarios.isEmpty()) {
            System.out.println(
                    "No se encontraron horarios disponibles para la hora ingresada o próximos según los criterios.");
        } else {
            mostrarHorariosConSusLineasYParadas(horarios, buscadores);
        }
    }

    /**
     * Implementa la lógica para optimizar rutas por promedio de personas.
     * para optimizador El objeto OptimizadorRutas.
     */
    public void optimizarRutas(OptimizadorRutas optimizador) {
        int promedio = -1;
        boolean inputValido = false;
        do {
            System.out.print("Ingrese el promedio de personas para la optimización (ej. 30): ");
            try {
                if (scanner.hasNextInt()) {
                    promedio = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea
                    if (promedio >= 0) {
                        inputValido = true;
                    } else {
                        System.out.println("El promedio debe ser un número no negativo.");
                    }
                } else {
                    System.out.println("Entrada no válida. Por favor, ingrese un número entero.");
                    scanner.nextLine(); // Consumir la entrada inválida
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Error de entrada. Por favor, ingrese un número entero.");
                scanner.nextLine(); // Consumir la entrada inválida
            }
        } while (!inputValido);

        optimizador.optimizarRutasPorPromedioPersonas(promedio);
    }

    /**
     * Muestra los horarios de una línea de bus específica, incluyendo los detalles
     * de su ruta y paradas.
     * para horarios La lista de horarios encontrados.
     * para lineaBuscada El nombre de la línea de bus que se buscó.
     * para buscadores El objeto Buscadores para obtener detalles de la ruta.
     */
    public void mostrarHorariosConDetallesDeLinea(List<Horario> horarios, String lineaBuscada, Buscadores buscadores) {
        if (horarios == null || horarios.isEmpty()) {
            System.out.println("No se encontraron horarios para la línea: " + lineaBuscada);
            return;
        }
        System.out.println("\nHorarios para la línea " + lineaBuscada + ":");

        Ruta rutaDeLaLinea = buscadores.obtenerRutaPorNombre(lineaBuscada);
        for (Horario h : horarios) {
            if (h.getLineas().stream().anyMatch(l -> l.equalsIgnoreCase(lineaBuscada))) {
                System.out.println("------------------------------------");
                System.out.printf("Hora de Salida: %s\n", h.getHoraTexto());
                if (rutaDeLaLinea != null) {
                    System.out.println(rutaDeLaLinea.toString());
                } else {
                    System.out.println("   (No se encontró información detallada de la ruta para esta línea)");
                }
            }
        }
        System.out.println("------------------------------------");
        System.out.println("Nota: Los buses pueden tener un tiempo de espera adicional en paradas.");
    }

    /**
     * Muestra una lista de horarios junto con las líneas y paradas asociadas a cada
     * horario.
     * 
     * @param horarios   La lista de horarios a mostrar.
     * @param buscadores El objeto Buscadores para obtener detalles de las paradas.
     */
    public void mostrarHorariosConSusLineasYParadas(List<Horario> horarios, Buscadores buscadores) {
        if (horarios == null || horarios.isEmpty()) {
            System.out.println("No se encontraron horarios disponibles.");
            return;
        }
        System.out.println("\nHorarios disponibles (entre 0 y 30 minutos después de la hora ingresada):");

        for (Horario h : horarios) {
            System.out.println("-----------------------------");
            System.out.println("Hora de Salida Programada: " + h.getHoraTexto());
            if (h.getLineas().isEmpty()) {
                System.out.println("  (Este horario no tiene líneas de bus asignadas)");
            } else {
                for (String linea : h.getLineas()) {
                    System.out.println("  - Línea: " + linea);
                    List<String> paradas = buscadores.buscarParadasPorLinea(linea);
                    if (!paradas.isEmpty()) {
                        System.out.println("    Paradas:");
                        for (String parada : paradas) {
                            System.out.println("      -> " + parada);
                        }
                    } else {
                        System.out.println("    (No se encontraron paradas para esta línea)");
                    }
                }
            }
        }
        System.out.println("-----------------------------");
        System.out.println("Nota: Los buses pueden tener un tiempo de espera adicional en paradas.");
    }

    public static void main(String[] args) {
        Scanner mainScanner = new Scanner(System.in); // Usar un scanner diferente para el main
        int opcion;

        // Rutas de los archivos CSV (para carga inicial si la DB está vacía o se desea
        // recrear)
        String rutaHorariosCSV = "C:\\Users\\Lenin\\Desktop\\aab1-proyecto-g_7\\Solucion_Codigo\\Sistema_Bus\\src\\Datos\\Horarios.csv";
        String rutaRutasCSV = "C:\\Users\\Lenin\\Desktop\\aab1-proyecto-g_7\\Solucion_Codigo\\Sistema_Bus\\src\\Datos\\Lineasbu.csv";

        List<Horario> horarios = new java.util.ArrayList<>();
        List<Ruta> rutas = new java.util.ArrayList<>();
        List<Bus> buses = new java.util.ArrayList<>();

        // Iniciar DataManager
        DataManager dataManager = new DataManager();

        try {
            // Intentar cargar datos de la base de datos
            horarios = dataManager.cargarHorarios();
            rutas = dataManager.cargarRutas();
            buses = dataManager.cargarBuses();

            if (horarios.isEmpty() || rutas.isEmpty() || buses.isEmpty()) {
                System.out.println(
                        "No se encontraron datos en la base de datos o están incompletos. Cargando desde CSVs y guardando en DB...");

                // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> INICIO DE LA SOLUCIÓN
                // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                // Limpiar la DB para evitar conflictos de claves duplicadas antes de insertar
                dataManager.limpiarBaseDeDatos();
                // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> FIN DE LA SOLUCIÓN
                // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

                // Si la DB está vacía, cargar desde CSVs y guardar en la DB
                horarios = dataManager.leerHorariosDesdeCSV(rutaHorariosCSV);
                rutas = dataManager.leerRutasDesdeCSV(rutaRutasCSV);
                buses = dataManager.crearBuses(5); // Crear un número fijo de buses

                if (!horarios.isEmpty() && !buses.isEmpty()) {
                    dataManager.asignarHorariosABuses(buses, horarios);
                } else {
                    System.out
                            .println("No se pudieron asignar horarios a los buses debido a falta de horarios o buses.");
                }

                // Guardar los datos leídos del CSV y generados en la base de datos
                dataManager.guardarHorarios(horarios);
                dataManager.guardarRutas(rutas);
                dataManager.guardarBuses(buses, horarios); // Se pasa horarios para mapear las asignaciones
            } else {
                System.out.println("Datos cargados exitosamente desde la base de datos.");
            }
        } catch (IOException e) {
            System.err.println("Error de I/O al leer archivos CSV: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error de base de datos al cargar/guardar datos: " + e.getMessage());
            e.printStackTrace();
            // Asegurarse de que las listas no sean nulas en caso de error de DB
            horarios = new java.util.ArrayList<>();
            rutas = new java.util.ArrayList<>();
            buses = new java.util.ArrayList<>();
        }

        // Crear instancias de los buscadores y el optimizador
        Buscadores buscadores = new Buscadores(horarios, rutas);
        OptimizadorRutas optimizador = new OptimizadorRutas(horarios, rutas, buses); // Pasar los buses al optimizador

        Vista vista = new Vista();

        do {
            vista.solicitarOpcion(buscadores, optimizador); // Pasar ambos objetos al método de la vista

            System.out.println("\n¿Desea realizar otra operación? (1: Sí, cualquier otro número o entrada para salir)");
            if (mainScanner.hasNextInt()) {
                opcion = mainScanner.nextInt();
            } else {
                opcion = 0; // Para salir si la entrada no es un número
            }
            mainScanner.nextLine(); // Consumir el salto de línea
        } while (opcion == 1);

        System.out.println("Gracias por usar el Sistema de Información de Buses. ¡Hasta pronto!");
        mainScanner.close();
        vista.scanner.close();
        DBConnection.closeConnection(); // Cerrar la conexión a la base de datos al finalizar
    }
}