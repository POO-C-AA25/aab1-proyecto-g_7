package Datos;

import Model.Horario;
import Model.Parada;
import Model.Ruta;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Clase de utilidad para cargar datos desde archivos CSV.
 * Responsabilidad Única: Leer y parsear CSV, sin interacción con la base de
 * datos.
 */
public class CSVLoader {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.US);

    public CSVLoader() {
    }

    /**
     * Lee horarios desde un archivo CSV.
     *
     * @param rutaArchivoCSV La ruta al archivo CSV de horarios.
     * @return Una lista de objetos Horario.
     * @throws IOException Si ocurre un error de Input / Output al leer el archivo.
     */
    public List<Horario> leerHorariosDesdeCSV(String rutaArchivoCSV) throws IOException {
        List<Horario> horarios = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivoCSV))) {
            String line;
            br.readLine(); // Saltar la primera línea (encabezados)
            while ((line = br.readLine()) != null) {
                // Asumimos que el delimitador es punto y coma (;)
                String[] parts = line.split(";");
                if (parts.length >= 2) {
                    String horaOriginal = parts[0].trim();
                    LocalTime hora = parsearHora(horaOriginal);
                    List<String> lineas = new ArrayList<>();
                    if (parts.length > 1) { // Las líneas son las partes restantes después de la hora original
                        for (int i = 1; i < parts.length; i++) {
                            lineas.add(parts[i].trim());
                        }
                    }
                    if (hora != null) {
                        horarios.add(new Horario(horaOriginal, hora, lineas));
                    } else {
                        System.err.println("Advertencia: No se pudo parsear la hora del CSV: " + horaOriginal + " en "
                                + rutaArchivoCSV);
                    }
                }
            }
        }
        System.out.println("Horarios leídos desde CSV: " + horarios.size());
        return horarios;
    }

    /**
     * Lee rutas y sus paradas desde un archivo CSV.
     * Asume que cada línea del CSV es: "NombreRuta;Parada1;Parada2;..."
     *
     * @param rutaArchivoCSV La ruta al archivo CSV de rutas.
     * @return Una lista de objetos Ruta.
     * @throws IOException Si ocurre un error de I/O al leer el archivo.
     */
    public List<Ruta> leerRutasDesdeCSV(String rutaArchivoCSV) throws IOException {
        List<Ruta> rutas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new BufferedReader(new FileReader(rutaArchivoCSV)))) {
            String line;
            br.readLine(); // Saltar la primera línea (encabezados)
            while ((line = br.readLine()) != null) {
                // Asumimos que el delimitador es punto y coma (;)
                String[] parts = line.split(";");
                if (parts.length > 0) {
                    String nombreRuta = parts[0].trim();
                    // Eliminar BOM si existe en el primer carácter del nombre de la ruta
                    if (nombreRuta.length() > 0 && nombreRuta.charAt(0) == '\uFEFF') {
                        nombreRuta = nombreRuta.substring(1);
                    }

                    Ruta ruta = new Ruta(nombreRuta);
                    for (int i = 1; i < parts.length; i++) {
                        ruta.agregarParada(new Parada(parts[i].trim()));
                    }
                    rutas.add(ruta);
                }
            }
        }
        System.out.println("Rutas leídas desde CSV: " + rutas.size());
        return rutas;
    }

    /**
     * Parsea una cadena de texto que representa una hora a un objeto LocalTime.
     *
     * @param horaTexto La cadena de la hora a parsear.
     * @return Un objeto LocalTime si el parseo es exitoso, o null si hay un error.
     */
    private LocalTime parsearHora(String horaTexto) {
        try {
            String estandarizada = horaTexto.replaceAll("\\s+", " ").trim();
            estandarizada = estandarizada.toUpperCase().replace(".", "");
            int amPmIndex = estandarizada.indexOf("AM");
            if (amPmIndex == -1) {
                amPmIndex = estandarizada.indexOf("PM");
            }
            if (amPmIndex != -1 && estandarizada.charAt(amPmIndex - 1) != ' ') {
                estandarizada = estandarizada.substring(0, amPmIndex) + " " + estandarizada.substring(amPmIndex);
            }
            return LocalTime.parse(estandarizada, FORMATTER);
        } catch (DateTimeParseException e) {
            System.err.println(
                    "Error de parseo en CSVLoader.parsearHora: " + horaTexto + " (Causa: " + e.getMessage() + ")");
            return null;
        }
    }
}