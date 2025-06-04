package Solucion_Codigo.SistemaBus.src.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Horario implements Serializable{
    private String hora;
    private List<String> lineas;
    public Horario(){}
    public Horario(String hora) {
        this.hora = hora;
        this.lineas = new ArrayList<>();
    }
    public void agregarLinea(String linea) {
        lineas.add(linea);
    }
    public String getHora() {
        return hora;
    }
    public List<String> getLineas() {
        return lineas;
    }
    @Override
    public String toString() {
        return hora + " -> " + lineas;
    }
}