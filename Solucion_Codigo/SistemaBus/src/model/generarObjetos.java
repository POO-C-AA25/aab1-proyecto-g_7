package Solucion_Codigo.SistemaBus.src.model;
import java.util.ArrayList;
import java.util.List;
public class generarObjetos {
    public static List<Horario> generarHorarios(List<String[]> datos) {
        List<Horario> horarios = new ArrayList<>();
        
        for (String[] fila : datos) {
            if (fila.length < 1) continue;
            String hora = fila[0].trim();
            Horario horario = new Horario(hora);
            for (int i = 1; i < fila.length; i++) {
                String linea = fila[i].trim().replace(";", "");
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
        int busIndex = 0;
        for (Horario horario : horarios) {
            for (String linea : horario.getLineas()) {
                buses.get(busIndex).agregarAsignacion(horario.getHora(), linea);
                busIndex = (busIndex + 1) % buses.size();
            }
        }
    }
}
