package Model;

import java.util.ArrayList;
import java.util.List;

public class Ruta {
    private int id; // Nuevo: ID para la base de datos
    private String nombreruta;
    private List<Parada> paradas;

    /**
     * Constructor para crear una Ruta sin un ID de base de datos.
     * 
     * @param nombreRuta El nombre de la ruta.
     */
    public Ruta(String nombreRuta) {
        this.nombreruta = nombreRuta;
        this.paradas = new ArrayList<>();
    }

    /**
     * Constructor para crear una Ruta con un ID de base de datos existente.
     * para id El ID de la ruta en la base de datos.
     * para nombreRuta El nombre de la ruta.
     */
    public Ruta(int id, String nombreRuta) {
        this.id = id;
        this.nombreruta = nombreRuta;
        this.paradas = new ArrayList<>();
    }

    /**
     * Agrega una parada a la ruta.
     * para parada La parada a agregar.
     */
    public void agregarParada(Parada parada) {
        paradas.add(parada);
    }

    public int getId() {
        return id;
    }

    public String getNombreruta() {
        return this.nombreruta;
    }

    public List<Parada> getParadas() {
        return this.paradas;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Ruta: " + this.nombreruta + ": ");
        if (paradas.isEmpty()) {
            sb.append(" (Sin paradas definidas)");
        } else {
            for (int i = 0; i < paradas.size(); i++) {
                sb.append(paradas.get(i).getNombreparada());
                if (i < paradas.size() - 1) {
                    sb.append(" -> ");
                }
            }
        }
        return sb.toString();
    }
}