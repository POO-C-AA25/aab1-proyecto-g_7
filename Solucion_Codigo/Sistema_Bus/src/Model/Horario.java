package Model;
import java.io.Serializable; 
import java.time.LocalTime;
import java.util.List;
public class Horario implements Serializable { 
    private String horaOriginal; 
    private LocalTime hora;      
    private List<String> lineas;
    public Horario(String horaOriginal, LocalTime hora, List<String> lineas) {
        this.horaOriginal = horaOriginal;
        this.hora = hora;
        this.lineas = lineas;
    }
    public void agregarLinea(String linea) {
        this.lineas.add(linea);
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
    @Override
    public String toString() {
        return horaOriginal + " → " +  lineas;
    }
}