package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un Bus en el sistema.
 * Contiene un identificador único y una lista de asignaciones de horarios y rutas.
 * Adherencia a Encapsulamiento: Los atributos son privados y se accede a ellos mediante getters y setters.
 */
public class Bus {
    private int id; // Nuevo: ID para la base de datos
    private String identificador; // Renombrado de 'id' a 'identificador' para evitar conflicto con el ID de la DB
    private List<String> asignacion; // Mantiene la representación de las asignaciones para la lógica de la aplicación

    /**
     * Constructor para crear un Bus sin un ID de base de datos inicial (útil para nuevas creaciones).
     * @param identificador El nombre o identificador del bus (e.g., "Bus-1").
     */
    public Bus(String identificador) {
        this.identificador = identificador;
        this.asignacion = new ArrayList<>();
    }

    /**
     * Constructor para crear un Bus con un ID de base de datos existente.
     * @param id El ID del bus en la base de datos.
     * @param identificador El nombre o identificador del bus.
     */
    public Bus(int id, String identificador) {
        this.id = id;
        this.identificador = identificador;
        this.asignacion = new ArrayList<>();
    }

    /**
     * Agrega una asignación de horario y ruta a este bus.
     * @param hora La hora programada para la asignación.
     * @param ruta La ruta asignada.
     */
    public void agregarAsignacion(String hora, String ruta) {
        asignacion.add(hora + "\t->\t" + ruta);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public List<String> getAsignacion() {
        return asignacion;
    }

    // Setters (si son necesarios, por ahora solo para el ID de la DB)
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