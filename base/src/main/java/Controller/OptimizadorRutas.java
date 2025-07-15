package Controller;

import Model.Bus;
import Model.Horario;
import Model.Parada;
import Model.Ruta;
import java.util.List;

/**
 * Clase que extiende Buscadores para añadir funcionalidades de optimización de
 * rutas.
 * Adherencia a Polimorfismo y LSP: Es una especialización de Buscadores.
 * Adherencia a SRP: Se enfoca en la lógica de optimización.
 */
public class OptimizadorRutas extends Buscadores {

    private List<Bus> buses;
    public OptimizadorRutas(List<Horario> horarios, List<Ruta> rutas, List<Bus> buses) {
        super(horarios, rutas); // Llama al constructor de la clase padre
        this.buses = buses;
    }

    /**
     * Optimiza y muestra las rutas de bus basándose en un promedio de personas
     * ingresado.
     * Solo muestra las líneas, rutas y paradas de los buses que superan este
     * promedio.
     * 
     * @param promedioPersonas El promedio de personas mínimo para mostrar la ruta.
     */
    public void optimizarRutasPorPromedioPersonas(int promedioPersonas) {
        System.out.println("\n--- Optimización de Rutas por Promedio de Personas ---");
        System.out.println("Promedio de personas ingresado para filtrar: " + promedioPersonas);

        boolean rutasEncontradas = false;

        for (Bus bus : buses) {
            // Simular ocupación
            int ocupacion = (int) (Math.random() * 51) + 10; // Entre 10 y 60 personas

            if (ocupacion > promedioPersonas) {
                rutasEncontradas = true;
                System.out.println("\n----------------------------------------");
                System.out.printf(" Bus: %s (Ocupación: %d personas)\n",
                        bus.getIdentificador(), ocupacion);

                System.out.println("Rutas asignadas:");

                for (String asignacion : bus.getAsignacion()) {
                    // Parsear correctamente la asignación (formato: "hora -> línea")
                    String[] partes = asignacion.split("->");
                    if (partes.length == 2) {
                        String hora = partes[0].trim();
                        String linea = partes[1].trim();

                        System.out.println("Hora: " + hora);
                        System.out.println("Línea: " + linea);

                        // Obtener y mostrar detalles completos de la ruta
                        Ruta ruta = obtenerRutaPorNombre(linea);
                        if (ruta != null) {
                            System.out.println("Ruta completa:");
                            System.out.println("   " + formatParadas(ruta.getParadas()));
                        } else {
                            System.out.println("    (No se encontró información detallada de la ruta para esta línea)");
                        }
                    } else {
                        System.out.println("    (Formato de asignación no válido: " + asignacion + ")");
                    }
                }
            }
        }

        if (!rutasEncontradas) {
            System.out.println("No hay buses que superen el promedio de " + promedioPersonas + " personas");
        }
    }

    // metodo auxiliar para formatear paradas
    private String formatParadas(List<Parada> paradas) {
        if (paradas == null || paradas.isEmpty()) {
            return "Sin paradas definidas";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paradas.size(); i++) {
            sb.append(paradas.get(i).getNombreparada());
            if (i < paradas.size() - 1) {
                sb.append("->");
            }
        }
        return sb.toString();
    }
}
