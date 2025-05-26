package model;

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
                if (!linea.trim().isEmpty()) {
                    datos.add(linea.split(","));
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo archivo CSV: " + ruta);
        }
        return datos;
    }

    public static List<String[]> leerRutas(String ruta) {
        List<String[]> datos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    datos.add(linea.split(";"));
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo archivo CSV: " + ruta);
        }
        return datos;
    }
    
}
