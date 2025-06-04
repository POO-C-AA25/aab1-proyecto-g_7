package Solucion_Codigo.SistemaBus.src.model;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class lector {
    public static List<String[]> leerHorarios(String ruta) {
        List<String[]> datos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) { // Ignorar líneas vacías
                    datos.add(linea.split(",")); // Asume delimitador coma para horarios
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo archivo CSV de horarios: " + ruta + " (" + e.getMessage() + ")");
            // Considerar lanzar una excepción personalizada o manejar de forma más robusta
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
            // Considerar lanzar una excepción personalizada o manejar de forma más robusta
        }
        return datos;
    }
}