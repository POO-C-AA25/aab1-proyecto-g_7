package Model;
import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException; // Importar para manejar excepciones de parseo de fecha/hora
import java.util.*;
public class Lector {
    // Usar Locale.US para asegurar la compatibilidad con "AM/PM" si tus CSV usan esos formatos
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.US); // Cambiado a Locale.US

    public static List<Horario> leerHorariosDesdeCSV(String ruta) throws IOException {
        List<Horario> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            br.readLine(); // Saltar cabecera si existe
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) { // Ignorar líneas vacías
                    continue;
                }
                System.out.println("Línea CSV Horario: " + linea); // Depuración

                // Asume que la hora está en la primera columna y las líneas en la segunda, separadas por ';'
                String[] partes = linea.split(";");

                if (partes.length >= 2) {
                    String horaTexto = normalizarHora(partes[0].trim());
                    try {
                        LocalTime horaParseada = LocalTime.parse(horaTexto, FORMATTER);
                        // Las líneas de bus están en partes[1], separadas por '|'
                        List<String> lineasBus = new ArrayList<>(Arrays.asList(partes[1].split("\\|")));

                        // Eliminar espacios en blanco de cada elemento de la lista de líneas
                        lineasBus.replaceAll(String::trim);

                        // Modificación clave: Pasar horaTextoOriginal, horaParseada y lineasBus al constructor de Horario
                        lista.add(new Horario(horaTexto, horaParseada, lineasBus));
                    } catch (DateTimeParseException e) { // Capturar excepción específica de parseo de fecha/hora
                        System.err.println("Advertencia: No se pudo parsear la hora '" + horaTexto + "' en la línea: " + linea + " (" + e.getMessage() + ")");
                    } catch (Exception e) {
                        System.err.println("Error al procesar línea de horario: " + linea + " (" + e.getMessage() + ")");
                        e.printStackTrace(); // Imprime el stack trace para depuración
                    }
                } else {
                    System.err.println("Advertencia: Línea de horario inválida (menos de 2 columnas esperadas 'hora;lineas'): " + linea);
                }
            }
        }
        System.out.println(">> Total horarios leídos: " + lista.size());
        return lista;
    }
    /*
     * Normaliza la cadena de la hora para que coincida con el formato esperado por DateTimeFormatter.
     * Convierte a mayúsculas y reemplaza variaciones comunes de AM/PM.
     */
    private static String normalizarHora(String entrada) {
        return entrada.toLowerCase()
                .replace("a. m.", "AM").replace("a.m.", "AM").replace("am", "AM")
                .replace("p. m.", "PM").replace("p.m.", "PM").replace("pm", "PM")
                .replace(".", "").trim().toUpperCase(); // Asegura que AM/PM esté en mayúsculas y sin puntos
    }
    public static List<Ruta> leerRutasDesdeCSV(String ruta) throws IOException {
        List<Ruta> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            br.readLine(); // Saltar cabecera si existe
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) { // Ignorar líneas vacías
                    continue;
                }
                System.out.println("Línea CSV Ruta: " + linea); // Depuración
                // Asume que el nombre de la ruta está en la primera columna y las paradas en la segunda, separadas por ';'
                String[] partes = linea.split(";");

                if (partes.length >= 2) {
                    String nombreRuta = partes[0].trim();
                    Ruta nuevaRuta = new Ruta(nombreRuta); // Crea la Ruta con su nombre

                    // Las paradas están en partes[1], separadas por '|'
                    String[] paradasArray = partes[1].split("\\|");
                    for (String paradaTexto : paradasArray) {
                        if (!paradaTexto.trim().isEmpty()) { // Asegurarse de que la parada no esté vacía
                            nuevaRuta.agregarParada(new Parada(paradaTexto.trim())); // Agrega cada Parada individualmente
                        }
                    }
                    lista.add(nuevaRuta);
                } else {
                    System.err.println("Advertencia: Línea de ruta inválida (menos de 2 columnas esperadas 'nombre_ruta;paradas'): " + linea);
                }
            }
        }
        System.out.println(">> Total rutas leídas: " + lista.size());
        return lista;
    }
}
