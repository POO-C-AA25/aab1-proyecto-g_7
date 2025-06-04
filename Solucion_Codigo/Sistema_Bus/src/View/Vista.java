package View;
import Model.*;
import Controller.Buscadores;
import java.util.List;
import java.util.Scanner;
public class Vista {
    public Scanner scanner;
    public Vista() { 
        scanner = new Scanner(System.in); 
    }
    public void solicitarOpcion(Buscadores buscadores) { 
        int opcion = -1; 
        boolean entradaValida = false; 
        do { 
            System.out.println("\n¿Qué desea buscar?"); 
            System.out.println("1. Información de una Línea de bus (horarios y paradas)"); 
            System.out.println("2. Horarios disponibles por Hora"); 
            System.out.print("Seleccione una opción (1 o 2): "); 
            try { 
                if (scanner.hasNextInt()) { 
                    opcion = scanner.nextInt(); 
                    scanner.nextLine(); 
                    switch (opcion) { 
                        case 1 -> { 
                            buscarPorLinea(buscadores);
                            entradaValida = true;
                        }
                        case 2 -> {
                            buscarPorHora(buscadores);
                            entradaValida = true; 
                        }
                        default -> System.out.println("Opción no válida. Intente de nuevo (1 o 2).");
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
            System.out.print("Ingrese la línea de bus (ej. Linea 01): "); 
            lineaBus = scanner.nextLine().trim(); 
            if (lineaBus.isEmpty()) { 
                System.out.println("La línea de bus no puede estar vacía. Intente de nuevo.");
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
        if (Buscadores.parsearHora(horaInput) == null && !horarios.isEmpty()){ 
            System.out.println("Formato de hora no válido. Por favor, use el formato 'h:mm AM/PM' o 'HH:mm'.");
            return;
        }
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
            System.out.println("No se encontraron horarios para la linea: " + lineaBuscada);
            return;
        }
        System.out.println("\nHorarios para la linea " + lineaBuscada + ":");

        Ruta rutaDeLaLinea = buscadores.obtenerRutaPorNombre(lineaBuscada);
        for (Horario h : horarios) {
            if (h.getLineas().stream().anyMatch(l -> l.equalsIgnoreCase(lineaBuscada))) {
                System.out.println("------------------------------------");
                System.out.printf("Hora de Salida: %s\n", h.getHoraTexto());
                if (rutaDeLaLinea != null) {
                    System.out.println(rutaDeLaLinea.toString());
                } else {
                    System.out.println("   (No se encontró información detallada de la ruta para esta linea)"); 
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
        // Mensaje ajustado para el rango de 0 a 30 minutos
        System.out.println("\nHorarios disponibles (entre 0 y 30 minutos después de la hora ingresada):");
        
        for (Horario h : horarios) { 
            System.out.println("-----------------------------");
            System.out.println("Hora de Salida Programada: " + h.getHoraTexto());
            if (h.getLineas().isEmpty()){
                System.out.println("  (Este horario no tiene líneas de bus asignadas)");
            } else { //
                for (String linea : h.getLineas()) {
                    System.out.println("  - Linea: " + linea);
                    List<String> paradas = buscadores.buscarParadasPorLinea(linea); 
                    if (!paradas.isEmpty()) {
                        System.out.println("    Paradas:"); 
                        for (String parada : paradas) { 
                            System.out.println("      - > " + parada); 
                        }
                    } else { //
                        System.out.println("    (No se encontraron paradas para esta línea)"); 
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

        String rutaHorarios = "C:\\Users\\Lenin\\Desktop\\aab1-proyecto-g_7\\Solucion_Codigo\\Sistema_Bus\\src\\Datos\\Horarios.txt";
        String rutaRutas = "C:\\Users\\Lenin\\Desktop\\aab1-proyecto-g_7\\Solucion_Codigo\\Sistema_Bus\\src\\Datos\\Lineasbu.csv";

        List<String[]> datosHorarios = Lector.leerHorarios(rutaHorarios); 
        List<String[]> datosRutas = Lector.leerRutas(rutaRutas); 

        if (datosHorarios.isEmpty()) { 
            System.out.println("Advertencia: No se cargaron datos de horarios. Verifique el archivo: " + rutaHorarios); 
        }
        if (datosRutas.isEmpty()) { 
            System.out.println("Advertencia: No se cargaron datos de rutas. Verifique el archivo: " + rutaRutas); 
        }

        List<Horario> horarios = GenerarObjetos.generarHorarios(datosHorarios); 
        List<Ruta> rutas = GenerarObjetos.generarRutas(datosRutas); 
        List<Bus> buses = GenerarObjetos.crearBuses(5); 

        if (!horarios.isEmpty() && !buses.isEmpty()) { 
            GenerarObjetos.asignarHorariosABuses(buses, horarios); 
        } else { 
            System.out.println("No se pudieron asignar horarios a los buses debido a falta de horarios o buses."); 
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
    }
}