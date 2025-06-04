package Controller;
import Model.Horario;
import Model.Ruta;
import Model.Parada;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale; 
public class Buscadores {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
    private List<Horario> horarios;
    private List<Ruta> rutas;
    public Buscadores(List<Horario> horarios, List<Ruta> rutas) {
        this.horarios = horarios;
        this.rutas = rutas;
    }
    public List<Horario> buscarHorariosDisponibles(String horaEntrada) {
        List<Horario> resultados = new ArrayList<>();
        LocalTime entrada = Buscadores.parsearHora(horaEntrada); 
        if (entrada == null) { 
            return resultados; // Retorna lista vacía si la hora de entrada no es válida
        }
        for (Horario h : horarios) { 
            LocalTime horaHorario = h.getHora(); 
            if (horaHorario == null) { 
                continue; // Ignorar horarios sin hora parseada correctamente
            }
            long diferenciaMinutos = Duration.between(entrada, horaHorario).toMinutes(); 
            // Ahora busca horarios con diferencia entre 0 y 30 minutos (inclusive)
            // y que sean iguales o posteriores a la hora de entrada.
            if (diferenciaMinutos >= 0 && diferenciaMinutos <= 30) {
                resultados.add(h); 
            }
        }
        return resultados; 
    }
    public static LocalTime parsearHora(String horaTexto) { 
        try { //
            // Limpieza de espacios más robusta
            String estandarizada = horaTexto.replaceAll("\\s+", " ").trim(); 
            estandarizada = estandarizada.toUpperCase().replace(".", ""); 
            // Aseguramos que solo haya un espacio entre la hora y AM/PM
            int amPmIndex = estandarizada.indexOf("AM"); 
            if (amPmIndex == -1) { //
                amPmIndex = estandarizada.indexOf("PM"); 
            }
            if (amPmIndex != -1 && amPmIndex > 0 && estandarizada.charAt(amPmIndex - 1) != ' ') { 
                estandarizada = estandarizada.substring(0, amPmIndex) + " " + estandarizada.substring(amPmIndex); 
            }

            return LocalTime.parse(estandarizada, FORMATTER); 
        } catch (DateTimeParseException e) { 
            System.err.println("Error de parseo en Buscadores.parsearHora: " + horaTexto + " (Causa: " + e.getMessage() + ")"); 
            return null; 
        }
    }
    public List<Horario> buscarHorariosPorLinea(String lineaBus) {
        List<Horario> resultados = new ArrayList<>();
        for (Horario h : horarios) {
            for (String linea : h.getLineas()) {
                if (linea != null && linea.trim().equalsIgnoreCase(lineaBus.trim())) {
                    resultados.add(h);
                    break; // El horario ya se añadió, no es necesario seguir revisando otras líneas del mismo horario
                }
            }
        }
        return resultados;
    }
    public List<String> buscarParadasPorLinea(String lineaBus) {
        for (Ruta r : rutas) {
            if (r.getNombreruta().equalsIgnoreCase(lineaBus)) {
                List<String> nombresParadas = new ArrayList<>();
                for (Parada p : r.getParadas()) {
                    nombresParadas.add(p.getNombreparada());
                }
                return nombresParadas;
            }
        }
        return new ArrayList<>(); // Retorna lista vacía si no se encuentra la línea
    }
    public Ruta obtenerRutaPorNombre(String nombreRuta) {
        for (Ruta r : rutas) {
            if (r.getNombreruta().equalsIgnoreCase(nombreRuta)) {
                return r;
            }
        }
        return null; // Retorna null si no se encuentra la ruta
    }
}