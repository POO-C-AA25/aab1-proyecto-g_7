package Model;
import Controller.Buscadores;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class GenerarObjetos {

    public static List<Horario> generarHorarios(List<Horario> horariosYaParseados) {
        return new ArrayList<>(horariosYaParseados);
    }

    public static List<Ruta> generarRutas(List<Ruta> rutasYaParseadas) {
        return new ArrayList<>(rutasYaParseadas);
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