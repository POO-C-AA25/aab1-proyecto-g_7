package Solucion_Codigo.SistemaBus.src.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Ruta implements Serializable{
    public String nombreruta;
    public List<Parada> paradas;
    public Ruta(String nombreRuta) {
        this.nombreruta = nombreRuta;
        this.paradas = new ArrayList<>();
    }
    public void agregarParada(Parada parada) {
        paradas.add(parada);
    }
    public String getNombreruta() {
        return this.nombreruta;
    }
    public List<Parada> getParadas() {
        return this.paradas;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Ruta: " + this.nombreruta + ": ");
        for (Parada p : paradas) {
            sb.append(p.getNombreparada()).append(" → ");
        }
        sb.setLength(sb.length() - 3); // eliminar la última flecha
        return sb.toString();
    }
}