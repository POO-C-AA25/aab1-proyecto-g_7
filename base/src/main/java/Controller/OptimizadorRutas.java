package Controller;

import Model.Bus;
import Model.Horario;
import Model.Ruta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que extiende Buscadores para añadir funcionalidades de optimización de rutas.
 * Adherencia a Polimorfismo y LSP: Es una especialización de Buscadores.
 * Adherencia a SRP: Se enfoca en la lógica de optimización.
 */
public class OptimizadorRutas extends Buscadores {

    private List<Bus> buses;

    /**
     * Constructor de OptimizadorRutas.
     * para horarios Lista de horarios.
     * para rutas Lista de rutas.
     * para buses Lista de buses.
     */
    public OptimizadorRutas(List<Horario> horarios, List<Ruta> rutas, List<Bus> buses) {
        super(horarios, rutas); // Llama al constructor de la clase padre
        this.buses = buses;
    }

    /**
     * Optimiza y muestra las rutas de bus basándose en un promedio de personas ingresado.
     * Solo muestra las líneas, rutas y paradas de los buses que superan este promedio.
     * Se simula el promedio de personas por bus para el propósito de esta optimización.
     * para promedioPersonas El promedio de personas mínimo para mostrar la ruta.
     */
    public void optimizarRutasPorPromedioPersonas(int promedioPersonas) {
        System.out.println("\n--- Optimización de Rutas por Promedio de Personas ---");
        // Simulación: Asignar un número aleatorio de personas a cada bus para el ejemplo
        Map<String, Integer> personasPorBus = new HashMap<>();
        for (Bus bus : buses) {
            // Simula un número de personas entre 10 y 60
            personasPorBus.put(bus.getIdentificador(), (int) (Math.random() * 51) + 10);
        }

        System.out.println("Promedio de personas ingresado para filtrar: " + promedioPersonas);

        boolean rutasEncontradas = false;
        for (Bus bus : buses) {
            int currentPersonas = personasPorBus.getOrDefault(bus.getIdentificador(), 0);
            if (currentPersonas > promedioPersonas) {
                rutasEncontradas = true;
                System.out.println("\n----------------------------------------------------");
                System.out.printf("Bus: %s (Personas a bordo: %d)\n", bus.getIdentificador(), currentPersonas);
                System.out.println("Asignaciones y Rutas:");

                // Recorre las asignaciones del bus para mostrar las rutas y paradas
                for (String asignacion : bus.getAsignacion()) {
                    // La asignación es "hora -> linea", necesitamos extraer la línea
                    String[] partesAsignacion = asignacion.split("\t->\t");
                    if (partesAsignacion.length == 2) {
                        String horaAsignacion = partesAsignacion[0].trim();
                        String lineaAsignacion = partesAsignacion[1].trim();

                        System.out.println("  - Hora Programada: " + horaAsignacion);
                        System.out.println("  - Línea: " + lineaAsignacion);

                        // Usa el método de la clase padre para obtener la ruta
                        Ruta rutaAsignada = obtenerRutaPorNombre(lineaAsignacion);
                        if (rutaAsignada != null) {
                            System.out.println("    " + rutaAsignada.toString());
                        } else {
                            System.out.println("    (No se encontró información detallada de la ruta para esta línea)");
                        }
                    }
                }
                System.out.println("----------------------------------------------------");
            }
        }

        if (!rutasEncontradas) {
            System.out.println("No se encontraron buses o rutas que superen el promedio de " + promedioPersonas + " personas.");
            System.out.println("Considere ajustar el promedio o verificar la ocupación de los buses.");
        }
    }
}