package Model;

import java.util.ArrayList;
import java.util.List;

public class Bus {
    private int id;
    private String identificador;
    private List<String> asignacion;

    /**
     * Constructor para crear un Bus sin un ID de base de datos inicial (útil para
     * nuevas creaciones).
     * 
     * @param identificador El nombre o identificador del bus (e.g., "Bus-1").
     */
    public Bus(String identificador) {
        this.identificador = identificador;
        this.asignacion = new ArrayList<>();
    }

    /**
     * Constructor para crear un Bus con un ID de base de datos existente.
     * 
     * @param id            El ID del bus en la base de datos.
     * @param identificador El nombre o identificador del bus.
     */
    public Bus(int id, String identificador) {
        this.id = id;
        this.identificador = identificador;
        this.asignacion = new ArrayList<>();
    }

    public void agregarAsignacion(String hora, String ruta) {
        asignacion.add(hora + "\t->\t" + ruta);
    }

    public int getId() {
        return id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public List<String> getAsignacion() {
        return asignacion;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Bus " + identificador + "\n");
        for (String asigna : asignacion) {
            sb.append("   ").append(asigna).append("\n");
        }
        return sb.toString();
    }
}