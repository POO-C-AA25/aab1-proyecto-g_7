package Model;

/**
 * Clase que representa una Parada individual en una ruta de bus.
 * Adherencia a Encapsulamiento: Los atributos son privados y se accede a ellos mediante getters y setters.
 */
public class Parada {
    private int id; // Nuevo: ID para la base de datos
    private int rutaId; // Nuevo: Clave foránea para la ruta a la que pertenece
    private String nombreparada;

    /**
     * Constructor para crear una Parada sin un ID de base de datos.
     * @param nombreparada El nombre de la parada.
     */
    public Parada(String nombreparada) {
        this.nombreparada = nombreparada;
    }

    /**
     * Constructor para crear una Parada con un ID de base de datos y un ID de ruta.
     * @param id El ID de la parada en la base de datos.
     * @param rutaId El ID de la ruta a la que pertenece la parada.
     * @param nombreparada El nombre de la parada.
     */
    public Parada(int id, int rutaId, String nombreparada) {
        this.id = id;
        this.rutaId = rutaId;
        this.nombreparada = nombreparada;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getRutaId() {
        return rutaId;
    }

    public String getNombreparada() {
        return this.nombreparada;
    }

    // Setters (para cuando se inserta y se obtiene el ID generado)
    public void setId(int id) {
        this.id = id;
    }

    public void setRutaId(int rutaId) {
        this.rutaId = rutaId;
    }

    @Override
    public String toString() {
        return this.nombreparada;
    }
}