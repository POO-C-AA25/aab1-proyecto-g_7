package Model;

import java.time.LocalTime;
import java.util.List;

/**
 * Clase que representa un Horario de salida para una o varias líneas de bus.
 * Adherencia a Encapsulamiento: Los atributos son privados y se accede a ellos mediante getters y setters.
 */
public class Horario {
    private int id; // Nuevo: ID para la base de datos
    private String horaOriginal;
    private LocalTime hora;
    private List<String> lineas; // Agregación: Horario tiene una lista de líneas

    /**
     * Constructor para crear un Horario sin un ID de base de datos inicial.
     * para horaOriginal La representación original de la hora (ej. "4:15 PM").
     * para hora El objeto LocalTime parseado de la hora original.
     * paralineas La lista de líneas de bus asociadas a este horario.
     */
    public Horario(String horaOriginal, LocalTime hora, List<String> lineas) {
        this.horaOriginal = horaOriginal;
        this.hora = hora;
        this.lineas = lineas;
    }

    /**
     * Constructor para crear un Horario con un ID de base de datos existente.
     * para id El ID del horario en la base de datos.
     * para horaOriginal La representación original de la hora.
     * param hora El objeto LocalTime parseado.
     * param lineas La lista de líneas de bus.
     */
    public Horario(int id, String horaOriginal, LocalTime hora, List<String> lineas) {
        this.id = id;
        this.horaOriginal = horaOriginal;
        this.hora = hora;
        this.lineas = lineas;
    }

    /**
     * Agrega una línea de bus a este horario.
     * param linea La línea de bus a agregar.
     */
    public void agregarLinea(String linea) {
        this.lineas.add(linea);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getHoraTexto() {
        return horaOriginal;
    }

    public LocalTime getHora() {
        return hora;
    }

    public List<String> getLineas() {
        return lineas;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return horaOriginal + " → " + lineas;
    }
}