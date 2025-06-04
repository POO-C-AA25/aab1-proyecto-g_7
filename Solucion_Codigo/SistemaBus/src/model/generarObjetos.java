package Solucion_Codigo.SistemaBus.src.model;
import Solucion_Codigo.SistemaBus.src.controller.Buscadores; // Importar Buscadores para usar su parsearHora
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
public class generarObjetos {
    public static List<Horario> generarHorarios(List<String[]> datos) {
        List<Horario> horarios = new ArrayList<>();
        for (String[] fila : datos) {
            if (fila.length < 1) { // Si la fila está vacía o no tiene al menos la hora
                // System.err.println("Advertencia: Fila incompleta en CSV de horarios. Se ignorará.");
                continue;
            }
            String horaTextoOriginal = fila[0].trim(); // Guardar el texto original de la hora
            
            // Usar el método centralizado de Buscadores para parsear la hora
            LocalTime horaParseada = Buscadores.parsearHora(horaTextoOriginal); 
            
            if (horaParseada == null) {
                // Mensaje de advertencia claro si la hora del CSV no se pudo parsear
                System.err.println("Advertencia: No se pudo parsear la hora del CSV: '" + horaTextoOriginal + "'. Se ignorará este horario.");
                continue; // Si no se puede parsear, saltar este horario
            }
            // Usar horaTextoOriginal para el constructor de Horario, para mantener el formato original para visualización
            Horario horario = new Horario(horaTextoOriginal, horaParseada, new ArrayList<>());
            for (int i = 1; i < fila.length; i++) {
                String linea = fila[i].trim(); 
                if (!linea.isEmpty()) {
                    horario.agregarLinea(linea);
                }
            }
            horarios.add(horario);
        }
        return horarios;
    }
    public static List<Ruta> generarRutas(List<String[]> datos) {
        List<Ruta> rutas = new ArrayList<>();
        for (String[] fila : datos) {
            if (fila.length < 2) continue; // Se necesita al menos nombre de ruta y una parada
            String nombreLinea = fila[0].trim();
            Ruta rut = new Ruta(nombreLinea);
            for (int i = 1; i < fila.length; i++) {
                String parada = fila[i].trim();
                if (!parada.isEmpty()) {
                    rut.agregarParada(new Parada(parada));
                }
            }
            rutas.add(rut);
        }
        return rutas;
    }
    public static List<Bus> crearBuses(int cantidad) {
        List<Bus> buses = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            buses.add(new Bus("Bus-" + i));
        }
        return buses;
    }
    
    public static void asignarHorariosABuses(List<Bus> buses, List<Horario> horarios) {
        if (buses == null || buses.isEmpty()) {
            System.err.println("No hay buses para asignar horarios.");
            return;
        }
        int busIndex = 0;
        for (Horario horario : horarios) {
            // Asegurarse de que la hora parseada (LocalTime) no sea null
            if (horario.getHora() != null) { 
                for (String linea : horario.getLineas()) {
                    // Usar getHoraTexto() para obtener el string original de la hora para la asignación
                    buses.get(busIndex).agregarAsignacion(horario.getHoraTexto(), linea); 
                    busIndex = (busIndex + 1) % buses.size(); // Avanza al siguiente bus de forma circular
                }
            }
        }
    }
}