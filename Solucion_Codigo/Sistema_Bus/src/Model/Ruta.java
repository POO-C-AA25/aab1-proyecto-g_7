package Model;
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
        if (paradas.isEmpty()) {
            sb.append(" (Sin paradas definidas)");
        } else {
            for (int i = 0; i < paradas.size(); i++) {
                sb.append(paradas.get(i).getNombreparada());
                if (i < paradas.size() - 1) {
                    sb.append(" → ");
                }
            }
        }
        return sb.toString();
    }
}