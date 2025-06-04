package Model;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
public class Lector {
    public static List<String[]> leerHorarios(String ruta) {
        List<String[]> datos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    datos.add(linea.split(","));
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo archivo CSV de horarios: " + ruta + " (" + e.getMessage() + ")");
        }
        return datos;
    }
    public static List<String[]> leerRutas(String ruta) {
        List<String[]> datos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) { // Ignorar líneas vacías
                    datos.add(linea.split(";")); // Asume delimitador punto y coma para rutas
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo archivo CSV de rutas: " + ruta + " (" + e.getMessage() + ")");
        }
        return datos;
    }
}
