package View;

import Model.Horario;
import Model.Ruta;
import Model.Parada; // Importar Parada
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Clase que gestiona la interacción con el usuario a través de la consola.
 * Es responsable de mostrar información y leer la entrada del usuario.
 * Adaptada específicamente para las funcionalidades de búsqueda y optimización.
 */
public class Vista {

    private final Scanner scanner;
    // Formateador para mostrar las horas al usuario
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.US);

    public Vista() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Muestra el menú principal de la aplicación al usuario, enfocado en búsqueda y
     * optimización.
     */
    public void mostrarMenuPrincipal() {
        System.out.println("\n--- Menú de Buscador de Rutas ---");
        System.out.println("1. Buscar Horarios por Línea");
        System.out.println("2. Buscar Horarios por Hora");
        System.out.println("3. Optimizar Rutas por Ocupación");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    /**
     * Lee la opción seleccionada por el usuario desde la consola.
     *
     * @return La opción numérica seleccionada.
     */
    public int leerOpcion() {
        while (true) {
            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();
                return opcion;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                scanner.nextLine();
                System.out.print("Seleccione una opción: ");
            }
        }
    }

    /**
     * Solicita al usuario que ingrese una línea para buscar.
     * * @return La línea ingresada por el usuario.
     */
    public String pedirLineaBusqueda() {
        System.out.print("Ingrese la línea de bus a buscar (ej. Linea01): ");
        return scanner.nextLine().trim();
    }

    /**
     * Solicita al usuario que ingrese una hora para buscar.
     * * @return La hora ingresada por el usuario (ej. "6:00 AM", "3:30 PM").
     */
    public String pedirHoraBusqueda() {
        System.out.print("Ingrese la hora a buscar (ej. 6:00 AM, 3:30 PM): ");
        return scanner.nextLine().trim();
    }

    /**
     * Solicita al usuario un número para filtrar buses por ocupación.
     * 
     * @return El número de personas ingresado.
     */
    public int pedirPromedioPersonas() {
        System.out.print("Ingrese el número mínimo de personas en el bus para mostrar la ruta: ");
        while (true) {
            try {
                int promedio = scanner.nextInt();
                scanner.nextLine(); // Consumir el salto de línea
                return promedio;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número entero.");
                scanner.nextLine(); // Consumir la entrada incorrecta
                System.out.print("Ingrese el número mínimo de personas: ");
            }
        }
    }

    /**
     * Muestra un mensaje genérico al usuario.
     * * @param mensaje El mensaje a mostrar.
     */
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    /**
     * Muestra un mensaje de error al usuario.
     * * @param error El mensaje de error a mostrar.
     */
    public void mostrarError(String error) {
        System.err.println("ERROR: " + error);
    }

    /**
     * Muestra la lista de horarios formateados.
     * * @param horarios La lista de objetos Horario a mostrar.
     */
    public void mostrarHorarios(List<Horario> horarios) {
        if (horarios.isEmpty()) {
            System.out.println("No se encontraron horarios para su criterio de búsqueda.");
            return;
        }
        System.out.println("\n--- Horarios Encontrados ---");
        for (Horario h : horarios) {
            System.out.println("ID: " + h.getId() +
                    ", Original: " + h.getHoraTexto() +
                    // Utiliza el DISPLAY_FORMATTER de la propia Vista
                    ", Local: " + (h.getHora() != null ? h.getHora().format(DISPLAY_FORMATTER) : "N/A") +
                    ", Líneas: \n" + String.join(", ", h.getLineas()));
        }
    }

    /**
     * Muestra la lista de rutas y sus paradas.
     * NOTA: Este método no es utilizado por la opción de optimización actual,
     * ya que la clase OptimizadorRutas imprime su propio formato. Se mantiene
     * por si se necesita en el futuro.
     * * @param rutas La lista de objetos Ruta a mostrar.
     */
    public void mostrarRutas(List<Ruta> rutas) {
        if (rutas.isEmpty()) {
            System.out.println("No hay rutas para mostrar.");
            return;
        }
        System.out.println("\n--- Rutas ---");
        for (Ruta r : rutas) {
            System.out.println("ID: " + r.getId() + ", Ruta: " + r.getNombreruta());
            if (!r.getParadas().isEmpty()) {
                System.out.print("    Paradas: ");
                for (int i = 0; i < r.getParadas().size(); i++) {
                    Parada p = r.getParadas().get(i);
                    System.out.print(p.getNombreparada() + (i < r.getParadas().size() - 1 ? " -> " : ""));
                }
                System.out.println();
            } else {
                System.out.println("    (Sin paradas asignadas)");
            }
        }
    }

    /**
     * Muestra el nombre y las paradas de una ruta específica.
     * 
     * @param ruta La ruta a mostrar.
     */
    public void mostrarDetalleRuta(Ruta ruta) {
        System.out.println("\n--- Detalles de la Ruta ---");
        System.out.println("Línea: " + ruta.getNombreruta());

        if (ruta.getParadas() == null || ruta.getParadas().isEmpty()) {
            System.out.println("    (Esta ruta no tiene paradas definidas)");
            return;
        }

        System.out.print("    Paradas: ");
        List<Parada> paradas = ruta.getParadas();
        for (int i = 0; i < paradas.size(); i++) {
            System.out.print(paradas.get(i).getNombreparada());
            if (i < paradas.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println(); // Salto de línea final
    }

    /**
     * Muestra una lista de horarios de forma simplificada para una línea
     * específica.
     * 
     * @param horarios     La lista de horarios encontrados.
     * @param lineaBuscada El nombre de la línea que se buscó.
     */
    public void mostrarHorariosParaLinea(List<Horario> horarios, String lineaBuscada) {
        if (horarios.isEmpty()) {
            System.out.println("No se encontraron horarios para la línea '" + lineaBuscada + "'.");
            return;
        }
        System.out.println("\n--- Horarios Encontrados para " + lineaBuscada + " ---");
        for (Horario h : horarios) {
            // Muestra solo la hora, sin la lista completa de líneas.
            System.out
                    .println(" -> " + (h.getHora() != null ? h.getHora().format(DISPLAY_FORMATTER) : h.getHoraTexto()));
        }
    }

    /**
     * Cierra el scanner de entrada. Debería llamarse al finalizar la aplicación.
     */
    public void cerrarScanner() {
        if (scanner != null) {
            scanner.close();
            System.out.println("Scanner de entrada cerrado.");
        }
    }
}