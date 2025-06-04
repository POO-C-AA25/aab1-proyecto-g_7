package Solucion_Codigo.SistemaBus.src.model;
import java.io.*;

public class serializar {
    public static void guardar(String ruta, Object objeto) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(objeto);
            System.out.println("Objeto guardado correctamente en: " + ruta);
        } catch (IOException e) {
            System.err.println("Error al guardar el objeto: " + e.getMessage());
        }
    }

    public static Object cargar(String ruta) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ruta))) {
            Object objeto = ois.readObject();
            System.out.println("Objeto cargado correctamente desde: " + ruta);
            return objeto;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar el objeto: " + e.getMessage());
            return null;
        }
    }
}