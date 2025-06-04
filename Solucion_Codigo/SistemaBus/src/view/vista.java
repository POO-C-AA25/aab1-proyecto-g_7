// vista.java
package Solucion_Codigo.SistemaBus.src.view;
import Solucion_Codigo.SistemaBus.src.model.Horario;
import Solucion_Codigo.SistemaBus.src.controller.Buscadores;
import Solucion_Codigo.SistemaBus.src.model.Ruta;
import java.util.List;
import java.util.Scanner;
public class vista {
    public Scanner scanner;
    public vista() {
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
                            // entradaValida sigue siendo false, el bucle se repetirá
                            break;
                    }
                } else {
                    System.out.println("Entrada no válida. Por favor, ingrese un número (1 o 2).");
                    scanner.nextLine();
                }
            } catch (java.util.InputMismatchException e) { 
                System.out.println("Error de entrada. Por favor, ingrese un número (1 o 2).");
                scanner.nextLine(); // Consumir la entrada inválida
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
        mostrarHorariosConDetallesDeLinea(horarios, lineaBus, buscadores); // Renombrado para claridad
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
            mostrarHorariosConSusLineasYParadas(horarios, buscadores); // Renombrado para claridad
        }
    }
    public void mostrarHorariosConDetallesDeLinea(List<Horario> horarios, String lineaBuscada, Buscadores buscadores) {
        if (horarios == null || horarios.isEmpty()) {
            System.out.println("No se encontraron horarios para la línea: " + lineaBuscada);
            return;
        }
        System.out.println("\nHorarios para la línea " + lineaBuscada + ":");
        
        Ruta rutaDeLaLinea = buscadores.obtenerRutaPorNombre(lineaBuscada);
        for (Horario h : horarios) {
             // buscarHorariosPorLinea ya filtra, pero por si acaso la lista viniera de otro lado o para ser explícito.
            if (h.getLineas().stream().anyMatch(l -> l.equalsIgnoreCase(lineaBuscada))) {
                System.out.println("------------------------------------");
                System.out.printf("Hora de Salida: %s\n", h.getHoraTexto()); 
                if (rutaDeLaLinea != null) {
                    System.out.println(rutaDeLaLinea.toString()); // Muestra la ruta completa
                } else {
                    System.out.println("   (No se encontró información detallada de la ruta para esta línea)");
                }
            }
        }
        System.out.println("------------------------------------");
        System.out.println("Nota: Los buses pueden tener un tiempo de espera adicional en paradas.");
    }
    private void mostrarHorariosConSusLineasYParadas(List<Horario> horarios, Buscadores buscadores) {
        if (horarios == null || horarios.isEmpty()) {
            // Este caso ya se maneja en buscarPorHora, pero es una salvaguarda.
            System.out.println("No se encontraron horarios disponibles.");
            return;
        }
        if (horarios.size() == 1 && buscadores.buscarHorariosDisponibles(horarios.get(0).getHoraTexto()).size() == 1) { // Una heurística para saber si es el "más próximo"
            System.out.println("\nHorario más próximo encontrado (si no hay en la ventana de 15-30 min):");
        } else {
            System.out.println("\nHorarios disponibles (entre 15 y 30 minutos después de la hora ingresada, o el más próximo):");
        }
        for (Horario h : horarios) {
            System.out.println("-----------------------------");
            System.out.println("Hora de Salida Programada: " + h.getHoraTexto()); 
            if (h.getLineas().isEmpty()){
                System.out.println("  (Este horario no tiene líneas de bus asignadas)");
            } else {
                for (String linea : h.getLineas()) {
                    System.out.println("  - Línea: " + linea);
                    List<String> paradas = buscadores.buscarParadasPorLinea(linea);
                    if (!paradas.isEmpty()) {
                        System.out.println("    Paradas:");
                        for (String parada : paradas) {
                            System.out.println("      → " + parada);
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
}