package Solucion_Codigo.SistemaBus.src.model;
import java.io.Serializable;
public class Parada implements Serializable{
    private String nombreparada;
    public Parada(String nombreparada) {
        this.nombreparada = nombreparada;
    }
    public String getNombreparada() {
        return this.nombreparada;
    }
    @Override
    public String toString() {
        return this.nombreparada;
    }
}