package View;
import Model.*;
import Controller.Buscadores;
import java.io.*;
import java.util.List;
import java.util.Scanner;
public class Vista {
    public Scanner scanner;
    public Vista() {
        scanner = new Scanner(System.in);
    }
    /**
     * Guarda las listas de horarios, rutas y buses en archivos serializados.
     * @param horarios Lista de objetos Horario a guardar.
     * @param rutas Lista de objetos Ruta a guardar.
     * @param buses Lista de objetos Bus a guardar.
     * @param pathHorarios Ruta del archivo para horarios.
     * @param pathRutas Ruta del archivo para rutas.
     * @param pathBuses Ruta del archivo para buses.
     */
    public static void guardarDatos(List<Horario> horarios, List<Ruta> rutas, List<Bus> buses,
                                    String pathHorarios, String pathRutas, String pathBuses) {
        // Guardar horarios
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(pathHorarios))) {
            oos.writeObject(horarios);
            System.out.println("Horarios serializados y guardados en: " + pathHorarios);
        } catch (IOException e) {
            System.err.println("Error al serializar horarios: " + e.getMessage());
        }

        // Guardar rutas
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(pathRutas))) {
            oos.writeObject(rutas);
            System.out.println("Rutas serializadas y guardadas en: " + pathRutas);
        } catch (IOException e) {
            System.err.println("Error al serializar rutas: " + e.getMessage());
        }

        // Guardar buses
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(pathBuses))) {
            oos.writeObject(buses);
            System.out.println("Buses serializados y guardados en: " + pathBuses);
        } catch (IOException e) {
            System.err.println("Error al serializar buses: " + e.getMessage());
        }
    }

    /**
     * Carga las listas de horarios, rutas y buses desde archivos serializados.
     * @param pathHorarios Ruta del archivo de horarios a cargar.
     * @param pathRutas Ruta del archivo de rutas a cargar.
     * @param pathBuses Ruta del archivo de buses a cargar.
     * @return Un arreglo de Listas: [horarios, rutas, buses] si la carga es exitosa, o null si falla.
     */
    public static List[] cargarDatos(String pathHorarios, String pathRutas, String pathBuses) {
        List<Horario> horarios = null;
        List<Ruta> rutas = null;
        List<Bus> buses = null;

        // Cargar horarios
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pathHorarios))) {
            horarios = (List<Horario>) ois.readObject();
            System.out.println("Horarios cargados desde: " + pathHorarios);
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de horarios no encontrado, se crearán nuevos datos: " + pathHorarios);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al deserializar horarios: " + e.getMessage());
            return null; // Indica que la carga falló.
        }

        // Cargar rutas
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pathRutas))) {
            rutas = (List<Ruta>) ois.readObject();
            System.out.println("Rutas cargadas desde: " + pathRutas);
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de rutas no encontrado, se crearán nuevos datos: " + pathRutas);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al deserializar rutas: " + e.getMessage());
            return null;
        }

        // Cargar buses
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pathBuses))) {
            buses = (List<Bus>) ois.readObject();
            System.out.println("Buses cargados desde: " + pathBuses);
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de buses no encontrado, se crearán nuevos datos: " + pathBuses);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al deserializar buses: " + e.getMessage());
            return null;
        }

        // Retorna un arreglo de listas. Es importante castear al tipo correcto al usarlo.
        return new List[]{horarios, rutas, buses};
    }

    // --- Métodos existentes (sin cambios significativos, solo ajustes de tildes/caracteres especiales para evitar '?' ) ---
    public void solicitarOpcion(Buscadores buscadores) {
        int opcion = -1;
        boolean entradaValida = false;
        do {
            System.out.println("\n¿Qué desea buscar?");
            System.out.println("1. Información de una Linea de bus (horarios y paradas)"); // 'Línea' -> 'Linea'
            System.out.println("2. Horarios disponibles por Hora");
            System.out.print("Seleccione una opción (1 o 2): ");
            try {
                if (scanner.hasNextInt()) {
                    opcion = scanner.nextInt();
                    scanner.nextLine();
                    switch (opcion) {
                        case 1:
                            buscarPorLinea(buscadores);
                            entradaValida = true;
                            break;
                        case 2:
                            buscarPorHora(buscadores);
                            entradaValida = true;
                            break;
                        default:
                            System.out.println("Opción no válida. Intente de nuevo (1 o 2).");
                            break;
                    }
                } else {
                    System.out.println("Entrada no válida. Por favor, ingrese un número (1 o 2).");
                    scanner.nextLine();
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Error de entrada. Por favor, ingrese un número (1 o 2).");
                scanner.nextLine();
            }
        } while (!entradaValida);
    }

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

    public String pedirLineaBus() {
        String lineaBus;
        do {
            System.out.print("Ingrese la linea de bus (ej. Linea 01): "); // 'línea' -> 'linea'
            lineaBus = scanner.nextLine().trim();
            if (lineaBus.isEmpty()) {
                System.out.println("La linea de bus no puede estar vacía. Intente de nuevo.");
            }
        } while (lineaBus.isEmpty());
        return lineaBus;
    }

    private void buscarPorLinea(Buscadores buscadores) {
        String lineaBus = pedirLineaBus();
        List<Horario> horarios = buscadores.buscarHorariosPorLinea(lineaBus);
        mostrarHorariosConDetallesDeLinea(horarios, lineaBus, buscadores);
    }

    private void buscarPorHora(Buscadores buscadores) {
        String horaInput = pedirHora();
        List<Horario> horarios = buscadores.buscarHorariosDisponibles(horaInput);

        if (Buscadores.parsearHora(horaInput) == null && horarios.isEmpty()){
            System.out.println("Formato de hora ingresado no es válido. Por favor, use el formato 'h:mm AM/PM' o 'HH:mm'.");
            return;
        }

        if (horarios.isEmpty()) {
            System.out.println("No se encontraron horarios disponibles para la hora ingresada o próximos según los criterios.");
        } else {
            mostrarHorariosConSusLineasYParadas(horarios, buscadores);
        }
    }

    public void mostrarHorariosConDetallesDeLinea(List<Horario> horarios, String lineaBuscada, Buscadores buscadores) {
        if (horarios == null || horarios.isEmpty()) {
            System.out.println("No se encontraron horarios para la linea: " + lineaBuscada); // 'línea' -> 'linea'
            return;
        }
        System.out.println("\nHorarios para la linea " + lineaBuscada + ":"); // 'línea' -> 'linea'

        Ruta rutaDeLaLinea = buscadores.obtenerRutaPorNombre(lineaBuscada);
        for (Horario h : horarios) {
            if (h.getLineas().stream().anyMatch(l -> l.equalsIgnoreCase(lineaBuscada))) {
                System.out.println("------------------------------------");
                System.out.printf("Hora de Salida: %s\n", h.getHoraTexto());
                if (rutaDeLaLinea != null) {
                    System.out.println(rutaDeLaLinea.toString());
                } else {
                    System.out.println("   (No se encontró información detallada de la ruta para esta linea)"); // 'línea' -> 'linea'
                }
            }
        }
        System.out.println("------------------------------------");
        System.out.println("Nota: Los buses pueden tener un tiempo de espera adicional en paradas.");
    }

    private void mostrarHorariosConSusLineasYParadas(List<Horario> horarios, Buscadores buscadores) {
        if (horarios == null || horarios.isEmpty()) {
            System.out.println("No se encontraron horarios disponibles.");
            return;
        }
        System.out.println("\nHorarios disponibles (entre 0 y 30 minutos después de la hora ingresada):");

        for (Horario h : horarios) {
            System.out.println("-----------------------------");
            System.out.println("Hora de Salida Programada: " + h.getHoraTexto());
            if (h.getLineas().isEmpty()){
                System.out.println("  (Este horario no tiene lineas de bus asignadas)"); // 'líneas' -> 'lineas'
            } else {
                for (String linea : h.getLineas()) {
                    System.out.println("  - Linea: " + linea); // 'Línea' -> 'Linea'
                    List<String> paradas = buscadores.buscarParadasPorLinea(linea);
                    if (!paradas.isEmpty()) {
                        System.out.println("    Paradas:");
                        for (String parada : paradas) {
                            System.out.println("      -> " + parada); // Usando '->' para evitar '?'
                        }
                    } else {
                        System.out.println("    (No se encontraron paradas para esta linea)"); // 'línea' -> 'linea'
                    }
                }
            }
        }
        System.out.println("-----------------------------");
        System.out.println("Nota: Los buses pueden tener un tiempo de espera adicional en paradas.");
    }

   public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        String rutaHorariosCSV = "C:\\Users\\Lenin\\Desktop\\aab1-proyecto-g_7\\Solucion_Codigo\\Sistema_Bus\\src\\Datos\\Horarios.csv";
        String rutaRutasCSV = "C:\\Users\\Lenin\\Desktop\\aab1-proyecto-g_7\\Solucion_Codigo\\Sistema_Bus\\src\\Datos\\Lineasbu.csv";

        String rutaHorariosSer = "C:\\Users\\Lenin\\Desktop\\aab1-proyecto-g_7\\Solucion_Codigo\\Sistema_Bus\\src\\Datos\\horarios.ser";
        String rutaRutasSer = "C:\\Users\\Lenin\\Desktop\\aab1-proyecto-g_7\\Solucion_Codigo\\Sistema_Bus\\src\\Datos\\rutas.ser";
        String rutaBusesSer = "C:\\Users\\Lenin\\Desktop\\aab1-proyecto-g_7\\Solucion_Codigo\\Sistema_Bus\\src\\Datos\\buses.ser";

        List<Horario> horarios;
        List<Ruta> rutas;
        List<Bus> buses;

        List[] datosCargados = cargarDatos(rutaHorariosSer, rutaRutasSer, rutaBusesSer);

        if (datosCargados != null && datosCargados[0] != null && datosCargados[1] != null && datosCargados[2] != null) {
            horarios = (List<Horario>) datosCargados[0];
            rutas = (List<Ruta>) datosCargados[1];
            buses = (List<Bus>) datosCargados[2];
            System.out.println("Datos cargados desde archivos serializados.");
        } else {
            System.out.println("Cargando datos desde archivos CSV/TXT iniciales...");
            try { // Agregamos un bloque try-catch para manejar IOException de los métodos de Lector
                // CORRECCIÓN CLAVE: Cambiar el tipo de las variables para que coincida con el retorno de Lector
                List<Horario> datosHorariosLeidos = Lector.leerHorariosDesdeCSV(rutaHorariosCSV);
                List<Ruta> datosRutasLeidas = Lector.leerRutasDesdeCSV(rutaRutasCSV);

                if (datosHorariosLeidos.isEmpty()) {
                    System.out.println("Advertencia: No se cargaron datos de horarios. Verifique el archivo: " + rutaHorariosCSV);
                }
                if (datosRutasLeidas.isEmpty()) {
                    System.out.println("Advertencia: No se cargaron datos de rutas. Verifique el archivo: " + rutaRutasCSV);
                }
                horarios = GenerarObjetos.generarHorarios(datosHorariosLeidos);
                rutas = GenerarObjetos.generarRutas(datosRutasLeidas);
                buses = GenerarObjetos.crearBuses(5);

                if (!horarios.isEmpty() && !buses.isEmpty()) {
                    GenerarObjetos.asignarHorariosABuses(buses, horarios);
                } else {
                    System.out.println("No se pudieron asignar horarios a los buses debido a falta de horarios o buses.");
                }

                guardarDatos(horarios, rutas, buses, rutaHorariosSer, rutaRutasSer, rutaBusesSer);
            } catch (IOException e) {
                System.err.println("Error al cargar datos desde archivos CSV/TXT: " + e.getMessage());
                e.printStackTrace();
                // Si ocurre un error aquí, es posible que 'horarios', 'rutas' o 'buses' sean nulos
                // Es crucial que tu programa pueda manejar esto o salir.
                // Para este ejemplo, los inicializamos a listas vacías para evitar NullPointerException.
                horarios = new java.util.ArrayList<>();
                rutas = new java.util.ArrayList<>();
                buses = new java.util.ArrayList<>();
            }
        }

        Buscadores buscadores = new Buscadores(horarios, rutas);
        Vista vista = new Vista();

        do {
            vista.solicitarOpcion(buscadores);

            System.out.println("\n¿Desea realizar otra búsqueda? (1: Sí, cualquier otro número o entrada para salir)");
            if (scanner.hasNextInt()){
                opcion = scanner.nextInt();
            } else {
                opcion = 0;
            }
            scanner.nextLine();
        } while (opcion == 1);
        System.out.println("Gracias por usar el Sistema de Información de Buses. ¡Hasta pronto!");
        scanner.close();
        vista.scanner.close();
    }
}