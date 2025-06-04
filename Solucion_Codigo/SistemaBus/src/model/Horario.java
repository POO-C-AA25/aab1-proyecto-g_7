package Solucion_Codigo.SistemaBus.src.model;

import java.io.Serializable; // Importar Serializable
import java.time.LocalTime;
import java.util.List;

public class Horario implements Serializable { // Añadido implements Serializable
    private String horaOriginal; // El texto original de la hora, ej "6:00 AM"
    private LocalTime hora;      // La hora parseada como LocalTime para comparaciones
    private List<String> lineas;

    public Horario(String horaOriginal, LocalTime hora, List<String> lineas) {
        this.horaOriginal = horaOriginal;
        this.hora = hora;
        this.lineas = lineas;
    }

    public void agregarLinea(String linea) {
        this.lineas.add(linea);
    }

    // Devuelve el texto original de la hora leída del CSV o ingresada
    public String getHoraTexto() {
        return horaOriginal;
    }

    // Devuelve el objeto LocalTime para cálculos y comparaciones
    public LocalTime getHora() {
        return hora;
    }

    public List<String> getLineas() {
        return lineas;
    }

    @Override
    public String toString() {
        // Usa horaOriginal para mostrar la hora tal como se leyó
        return horaOriginal + " → " +  lineas;
    }
}