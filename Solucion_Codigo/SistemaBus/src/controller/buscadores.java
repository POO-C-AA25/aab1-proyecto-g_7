// Buscadores.java
package Solucion_Codigo.SistemaBus.src.controller;

import Solucion_Codigo.SistemaBus.src.model.Horario;
import Solucion_Codigo.SistemaBus.src.model.Parada;
import Solucion_Codigo.SistemaBus.src.model.Ruta;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale; // Asegúrate de importar Locale

public class Buscadores {
    // FORMATTER es correcto para "h:mm a"
    // ***** CAMBIO CLAVE AQUÍ: Se añade Locale.US *****
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
    private List<Horario> horarios;
    private List<Ruta> rutas;

    public Buscadores(List<Horario> horarios, List<Ruta> rutas) {
        this.horarios = horarios;
        this.rutas = rutas;
    }

    public List<Horario> buscarHorariosDisponibles(String horaEntrada) {
        List<Horario> resultados = new ArrayList<>();
        Horario ultimoHorario = null;
        int menorDiferencia = Integer.MAX_VALUE;

        LocalTime entrada = Buscadores.parsearHora(horaEntrada);
        if (entrada == null) {
            return resultados; // Retorna lista vacía si la hora de entrada no es válida
        }

        for (Horario h : horarios) {
            LocalTime horaHorario = h.getHora();
            if (horaHorario == null) {
                continue; // Ignorar horarios sin hora parseada correctamente
            }

            long diferenciaMinutos = java.time.Duration.between(entrada, horaHorario).toMinutes();

            if (diferenciaMinutos >= 15 && diferenciaMinutos <= 30) {
                resultados.add(h);
            } else if (diferenciaMinutos > 0 && diferenciaMinutos < menorDiferencia) {
                // Este es el horario más próximo en el futuro, si no hay nada en la ventana de 15-30 min
                menorDiferencia = (int) diferenciaMinutos;
                ultimoHorario = h;
            }
        }

        // Si no se encontraron horarios en la ventana de 15-30 min, pero hay un horario próximo
        if (resultados.isEmpty() && ultimoHorario != null) {
            resultados.add(ultimoHorario);
        }

        return resultados;
    }

    public static LocalTime parsearHora(String horaTexto) {
        try {
            // Limpieza de espacios más robusta
            String estandarizada = horaTexto.replaceAll("\\s+", " ").trim(); // Reemplaza múltiples espacios por uno solo, y luego trim
            estandarizada = estandarizada.toUpperCase().replace(".", ""); // Convertir a mayúsculas y quitar puntos (ej. A.M. -> AM)

            // Aseguramos que solo haya un espacio entre la hora y AM/PM
            // Por ejemplo, "6:20AM" -> "6:20 AM"
            // "6:20 AM" -> "6:20 AM" (sin cambios)
            int amPmIndex = estandarizada.indexOf("AM");
            if (amPmIndex == -1) {
                amPmIndex = estandarizada.indexOf("PM");
            }

            // Si se encontró AM/PM y no hay un espacio antes (y no está al inicio de la cadena)
            if (amPmIndex != -1 && amPmIndex > 0 && estandarizada.charAt(amPmIndex - 1) != ' ') {
                estandarizada = estandarizada.substring(0, amPmIndex) + " " + estandarizada.substring(amPmIndex);
            }
            
            return LocalTime.parse(estandarizada, FORMATTER);
        } catch (DateTimeParseException e) {
            // Se mantiene el mensaje de error original que incluye la causa específica.
            System.err.println("Error de parseo en Buscadores.parsearHora: " + horaTexto + " (Causa: " + e.getMessage() + ")");
            return null; // Retorna null si el formato es incorrecto
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