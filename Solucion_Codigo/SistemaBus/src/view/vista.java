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
    /**
     * Solicita al usuario una opción y realiza la búsqueda correspondiente.
     *
     * @param buscadores Controlador para buscar horarios
     */
    public void solicitarOpcion(Buscadores buscadores) {
        int opcion = 0; // Inicializar la opción para evitar errores de compilación
        System.out.println("¿Qué desea buscar?");
        System.out.println("1. Línea de bus");
        System.out.println("2. Horario");
        System.out.print("Seleccione una opción (1 o 2): ");
        opcion = scanner.nextInt();
        scanner.nextLine(); // <--- AÑADE ESTA LÍNEA
        try {
            switch (opcion) {
                case 1 -> buscarPorLinea(buscadores);
                case 2 -> buscarPorHora(buscadores);
                default ->{
                    System.out.print("Opción no válida. Intente de nuevo (1 o 2): ");
                }
            }
        } catch (NumberFormatException e) {
                System.out.print("Entrada no válida. Intente de nuevo (1 o 2): ");
        }
    }
    /**
     * Solicita al usuario ingresar una hora en formato "h:mm a.m." / "h:mm p.m.".
     *
     * @return String con la hora ingresada por el usuario
     */
    public String pedirHora() {
        String hora;
        do {
            System.out.print("Ingrese la hora (ejemplo: 4:15 p.m): ");
            hora = scanner.nextLine();
        } while (hora == null || hora.trim().isEmpty());
        return hora;
    }
    /**
     * Solicita al usuario ingresar la línea del bus.
     *
     * @return String con la línea del bus ingresada por el usuario
     */
    public String pedirLineaBus() {
        String lineaBus;
        do {
            System.out.print("Ingrese la línea del bus: ");
            lineaBus = scanner.nextLine();
        } while (lineaBus == null || lineaBus.trim().isEmpty());
        return lineaBus;
    }
    private void buscarPorLinea(Buscadores buscadores) {
        String lineaBus = pedirLineaBus();
        List<Horario> horarios = buscadores.buscarHorariosPorLinea(lineaBus);
        mostrarHorariosConDetalles(horarios, lineaBus, buscadores); // Mostrar detalles de ruta
    }
    private void buscarPorHora(Buscadores buscadores) {
        String hora = pedirHora();
        if (hora != null) {
            List<Horario> horarios = buscadores.buscarHorariosDisponibles(hora);
            mostrarHorarios(horarios);
        }
    }
    /**
     * Muestra la lista de horarios con detalles de la línea de bus.
     *
     * @param horarios   Lista de horarios para mostrar
     * @param lineaBus   Línea de bus consultada
     * @param buscadores Controlador para buscar información adicional (ruta)
     */
    private void mostrarHorariosConDetalles(List<Horario> horarios, String lineaBus, Buscadores buscadores) {
        if (horarios == null || horarios.isEmpty()) {
            System.out.println("No se encontraron horarios para la línea: " + lineaBus);
            return;
        }
        System.out.println("\nHorarios para la línea " + lineaBus + ":");
        System.out.printf("%-10s\n", "Hora");
        System.out.println("----------");
        for (Horario h : horarios) {
            if (h.getLineas().contains(lineaBus)) { // Solo mostrar si contiene la línea buscada
                System.out.printf("%-10s\n", h.getHora());
                // Mostrar la ruta para la línea
                Ruta rutaBus = buscadores.obtenerRutaPorNombre(lineaBus); // Usar el método del buscador
                if (rutaBus != null) {
                    System.out.println(rutaBus.toString());
                } else {
                    System.out.println("No se encontró la ruta para esta línea.");
                }
                System.out.println("----------");
            }
        }
    }
    /**
     * Muestra la lista de horarios disponibles al usuario.
     *
     * @param horarios Lista de horarios para mostrar
     */
    public void mostrarHorarios(List<Horario> horarios) {
        if (horarios == null || horarios.isEmpty()) {
            System.out.println("No se encontraron horarios disponibles.");
            return;
        }
        System.out.println("\nHorarios disponibles:");
        System.out.printf("%-10s %-20s\n", "Hora", "Líneas"); // Encabezados formateados
        System.out.println("-------------------------");
        for (Horario h : horarios) {
            System.out.printf("%-10s %-20s\n", h.getHora(), String.join(", ", h.getLineas()));
        }
    }
}