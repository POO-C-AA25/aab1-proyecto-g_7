package Solucion_Codigo.SistemaBus.src.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
    public class Bus implements Serializable{
    public String id;
    public List<String> asignacion;
    public Bus (String id){
        this.id=id;
        this.asignacion=new ArrayList<>();
    }
    public void agregarAsignacion(String hora, String ruta){
        asignacion.add(hora+"\t->\t"+ruta);
    }
    public String getId(){
        return id;
    }
    public List<String> getAsignacion(){
        return asignacion;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Bus " + id + "\n");
        for (String asigna : asignacion) {
            sb.append("   ").append(asigna).append("\n");
        }
        return sb.toString();
    }
}