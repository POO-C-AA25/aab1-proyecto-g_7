package View;

import Model.*;
import Controller.Buscadores;
import Controller.OptimizadorRutas;
import Datos.DBConnection;
import java.util.List;
import java.util.Scanner;

public class Vista {
    private Scanner scanner;
    private Buscadores buscadores;
    private OptimizadorRutas optimizador;

    public Vista(Buscadores buscadores, OptimizadorRutas optimizador) {
        this.scanner = new Scanner(System.in);
        this.buscadores = buscadores;
        this.optimizador = optimizador;
    }

    public void mostrarMenu() {
        int opcion = -1;
        boolean entradaValida = false;
        do {
            System.out.println("\n¿Qué desea buscar o hacer?");
            System.out.println("1. Información de una Línea de bus (horarios y paradas)");
            System.out.println("2. Horarios disponibles por Hora");
            System.out.println("3. Optimizar Rutas por Promedio de Personas");
            System.out.print("Seleccione una opción (1, 2 o 3): ");
            try {
                if (scanner.hasNextInt()) {
                    opcion = scanner.nextInt();
                    scanner.nextLine(); 
                    entradaValida = true;
                    procesarOpcion(opcion);
                } else {
                    System.out.println("Entrada no válida. Por favor, ingrese un número (1, 2 o 3).");
                    scanner.nextLine(); 
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Error de entrada. Por favor, ingrese un número (1, 2 o 3).");
                scanner.nextLine(); 
            }
        } while (!entradaValida);
    }

    private void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1:
                buscarPorLinea();
                break;
            case 2:
                buscarPorHora();
                break;
            case 3:
                optimizarRutas();
                break;
            default:
                System.out.println("Opción no válida. Intente de nuevo (1, 2 o 3).");
                break;
        }
    }

    private void buscarPorLinea() {
        String lineaBus = pedirLineaBus();
        List<Horario> horarios = buscadores.buscarHorariosPorLinea(lineaBus);
        mostrarHorariosConDetallesDeLinea(horarios, lineaBus);
    }

    private void buscarPorHora() {
        String horaInput = pedirHora();
        List<Horario> horarios = buscadores.buscarHorariosDisponibles(horaInput);
        if (horarios.isEmpty()) {
            System.out.println("No se encontraron horarios disponibles para la hora ingresada.");
        } else {
            mostrarHorariosConSusLineasYParadas(horarios);
        }
    }

    private void optimizarRutas() {
        int promedio = pedirPromedio();
        optimizador.optimizarRutasPorPromedioPersonas(promedio);
    }

    private int pedirPromedio() {
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
        return promedio;
    }

    private String pedirHora() {
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

    private String pedirLineaBus() {
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

    private void mostrarHorariosConDetallesDeLinea(List<Horario> horarios, String lineaBus) {
        if (horarios.isEmpty()) {
            System.out.println("No se encontraron horarios para la línea: " + lineaBus);
            return;
        }
        System.out.println("\nHorarios para la línea " + lineaBus + ":");
        for (Horario h : horarios) {
            System.out.println("------------------------------------");
            System.out.printf("Hora de Salida: %s\n", h.getHoraTexto());
        }
        System.out.println("------------------------------------");
    }

    private void mostrarHorariosConSusLineasYParadas(List<Horario> horarios) {
        if (horarios.isEmpty()) {
            System.out.println("No se encontraron horarios disponibles.");
            return;
        }
        System.out.println("\nHorarios disponibles:");
        for (Horario h : horarios) {
            System.out.println("-----------------------------");
            System.out.println("Hora de Salida Programada: " + h.getHoraTexto());
            for (String linea : h.getLineas()) {
                System.out.println("  - Línea: " + linea);
            }
        }
        System.out.println("-----------------------------");
    }

    public static void main(String[] args) {
        Scanner mainScanner = null;
        Vista vista = null;

        try {
            DataManager dataManager = new DataManager();
            List<Horario> horarios = dataManager.cargarHorarios();
            List<Ruta> rutas = dataManager.cargarRutas();
            List<Bus> buses = dataManager.cargarBuses();
            dataManager.asignarHorariosABuses(buses, horarios);

            if (horarios.isEmpty() || rutas.isEmpty() || buses.isEmpty()) {
                System.out.println("No se encontraron datos suficientes para iniciar el sistema.");
                return;
            }

            Buscadores buscadores = new Buscadores(horarios, rutas);
            OptimizadorRutas optimizador = new OptimizadorRutas(horarios, rutas, buses);

            mainScanner = new Scanner(System.in);
            vista = new Vista(buscadores, optimizador);

            boolean continuar = true;
            while (continuar) {
                vista.mostrarMenu();

                System.out.println(
                        "\n¿Desea realizar otra operación? (1: Sí, cualquier otro número o entrada para salir)");
                if (mainScanner.hasNextInt()) {
                    int opcion = mainScanner.nextInt();
                    mainScanner.nextLine();
                    continuar = (opcion == 1);
                } else {
                    continuar = false;
                }
            }

        } catch (Exception e) {
            System.err.println("Error en la ejecución: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (mainScanner != null) {
                    mainScanner.close();
                }
            } catch (Exception e) {
                System.err.println("Error al cerrar scanner: " + e.getMessage());
            }

            try {
                if (vista != null) {
                    vista.cerrarRecursos(); // Añadir este método en Vista
                }
            } catch (Exception e) {
                System.err.println("Error al cerrar vista: " + e.getMessage());
            }

            DBConnection.closeConnection(); // Ahora es más seguro
        }

        System.out.println("Gracias por usar el Sistema de Información de Buses. ¡Hasta pronto!");
    }

    public void cerrarRecursos() {
        if (this.scanner != null) {
            this.scanner.close();
        }
    }
}
