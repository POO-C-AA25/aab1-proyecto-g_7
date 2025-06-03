package Solucion_Codigo.SistemaBus.src.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class ruta implements Serializable{
    public String nombre;
    public List<parada> paradas;
    public ruta(String nombre) {
        this.nombre = nombre;
        this.paradas = new ArrayList<>();
    }
    public void agregarParada(parada parada) {
        paradas.add(parada);
    }
    public String getnombre() {
        return nombre;
    }
    public List<parada> getParadas() {
        return paradas;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Ruta: " + nombre + ": ");
        for (parada p : paradas) {
            sb.append(p.getNombre()).append(" → ");
        }
        sb.setLength(sb.length() - 3); // eliminar la última flecha
        return sb.toString();
    }
}