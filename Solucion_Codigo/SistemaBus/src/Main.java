import java.util.List;
import model.*;
public class Main {
    public static void main(String[] args) {
        String rutaHorarios ="C:\\Users\\User\\Desktop\\taller poo\\aab1-proyecto-g_7\\Solucion_Codigo\\SistemaBus\\src\\Datos\\Horarios.csv";
        String rutaRutas ="C:\\Users\\User\\Desktop\\taller poo\\aab1-proyecto-g_7\\Solucion_Codigo\\SistemaBus\\src\\Datos\\Lineasbu.csv";
        
         List<String[]> datosHorarios = lector.leerHorarios(rutaHorarios);
        List<String[]> datosRutas = lector.leerRutas(rutaRutas);

        for (String[] fila : datosHorarios) {
            for (String valor : fila) {
                System.out.print(valor + " | ");
            }
            System.out.println();
        }
    }
}
