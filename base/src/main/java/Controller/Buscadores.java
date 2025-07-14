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

/**
 * Clase base para la búsqueda de información en el sistema de buses.
 * Adherencia a Encapsulamiento: Los datos son privados y se accede a través del constructor.
 * Adherencia a OCP: Puede ser extendida para nuevas funcionalidades de búsqueda sin modificar el código existente.
 */
public class Buscadores {
    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.US); // Protected para que las subclases accedan
    protected List<Horario> horarios; // Protected para que las subclases accedan
    protected List<Ruta> rutas; // Protected para que las subclases accedan

    /**
     * Constructor de Buscadores.
     * @param horarios Lista de horarios disponibles.
     * @param rutas Lista de rutas disponibles.
     */
    public Buscadores(List<Horario> horarios, List<Ruta> rutas) {
        this.horarios = horarios;
        this.rutas = rutas;
    }

    /**
     * Busca horarios disponibles que estén dentro de los 30 minutos posteriores a la hora de entrada.
     * @param horaEntrada La hora ingresada por el usuario (ej. "4:15 PM").
     * @return Una lista de horarios que cumplen con el criterio.
     */
    public List<Horario> buscarHorariosDisponibles(String horaEntrada) {
        List<Horario> resultados = new ArrayList<>();
        LocalTime entrada = Buscadores.parsearHora(horaEntrada);
        if (entrada == null) {
            return resultados;
        }
        for (Horario h : horarios) {
            LocalTime horaHorario = h.getHora();
            if (horaHorario == null) {
                continue;
            }
            long diferenciaMinutos = Duration.between(entrada, horaHorario).toMinutes();
            if (diferenciaMinutos >= 0 && diferenciaMinutos <= 30) {
                resultados.add(h);
            }
        }
        return resultados;
    }

    /**
     * Parsea una cadena de texto que representa una hora a un objeto LocalTime.
     * @param horaTexto La cadena de la hora a parsear.
     * @return Un objeto LocalTime si el parseo es exitoso, o null si hay un error.
     */
    public static LocalTime parsearHora(String horaTexto) {
        try {
            String estandarizada = horaTexto.replaceAll("\\s+", " ").trim();
            estandarizada = estandarizada.toUpperCase().replace(".", "");
            int amPmIndex = estandarizada.indexOf("AM");
            if (amPmIndex == -1) {
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

    /**
     * Busca todos los horarios asociados a una línea de bus específica.
     * @param lineaBus El nombre de la línea de bus a buscar.
     * @return Una lista de horarios que incluyen la línea especificada.
     */
    public List<Horario> buscarHorariosPorLinea(String lineaBus) {
        List<Horario> resultados = new ArrayList<>();
        for (Horario h : horarios) {
            for (String linea : h.getLineas()) {
                if (linea != null && linea.trim().equalsIgnoreCase(lineaBus.trim())) {
                    resultados.add(h);
                    break;
                }
            }
        }
        return resultados;
    }

    /**
     * Busca las paradas asociadas a una línea de bus (ruta).
     * @param lineaBus El nombre de la línea de bus (ruta).
     * @return Una lista de nombres de paradas si la línea se encuentra, o una lista vacía.
     */
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
        return new ArrayList<>();
    }

    /**
     * Obtiene un objeto Ruta por su nombre.
     * @param nombreRuta El nombre de la ruta a buscar.
     * @return El objeto Ruta si se encuentra, o null.
     */
    public Ruta obtenerRutaPorNombre(String nombreRuta) {
        for (Ruta r : rutas) {
            if (r.getNombreruta().equalsIgnoreCase(nombreRuta)) {
                return r;
            }
        }
        return null;
    }
}