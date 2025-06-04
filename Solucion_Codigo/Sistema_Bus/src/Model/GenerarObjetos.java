package Model;

import Controller.Buscadores;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays; // Importar java.util.Arrays
import java.util.List;

public class GenerarObjetos {

    public static List<Horario> generarHorarios(List<String[]> datos) {
        List<Horario> horarios = new ArrayList<>();
        for (String[] fila : datos) {
            if (fila.length < 1) {
                continue;
            }
            String horaTextoOriginal = fila[0].trim();
            LocalTime horaParseada = Buscadores.parsearHora(horaTextoOriginal);
            if (horaParseada == null) {
                System.err.println("Advertencia: No se pudo parsear la hora del CSV: '" + horaTextoOriginal + "'. Se ignorará este horario.");
                continue;
            }

            // --- INICIO DE LA CORRECCIÓN ---
            // 1. Crear una lista mutable para las líneas
            List<String> lineasDelHorario = new ArrayList<>();
            // 2. Iterar desde el segundo elemento de la fila (índice 1) para obtener las líneas
            for (int i = 1; i < fila.length; i++) {
                String linea = fila[i].trim();
                if (!linea.isEmpty()) {
                    lineasDelHorario.add(linea);
                }
            }
            // 3. Pasar la lista de líneas al constructor de Horario
            Horario horario = new Horario(horaTextoOriginal, horaParseada, lineasDelHorario);
            // --- FIN DE LA CORRECCIÓN ---

            horarios.add(horario);
        }
        return horarios;
    }

    // ... (El resto de tu clase GenerarObjetos sigue igual) ...
    public static List<Ruta> generarRutas(List<String[]> datos) {
        List<Ruta> rutas = new ArrayList<>();
        for (String[] fila : datos) {
            if (fila.length < 2) continue;
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
            if (horario.getHora() != null) {
                for (String linea : horario.getLineas()) {
                    buses.get(busIndex).agregarAsignacion(horario.getHoraTexto(), linea);
                }
                busIndex = (busIndex + 1) % buses.size();
            }
        }
    }
}