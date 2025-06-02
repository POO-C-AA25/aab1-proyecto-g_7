import model.*;
import controller.buscadores;
import view.vista;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String rutaHorarios = "C:\\Users\\User\\Desktop\\taller poo\\aab1-proyecto-g_7\\Solucion_Codigo\\SistemaBus\\src\\Datos\\Horarios.csv";
        String rutaRutas = "C:\\Users\\User\\Desktop\\taller poo\\aab1-proyecto-g_7\\Solucion_Codigo\\SistemaBus\\src\\Datos\\Lineasbu.csv";

        List<String[]> datosHorarios = lector.leerHorarios(rutaHorarios);
        List<String[]> datosRutas = lector.leerRutas(rutaRutas);

        List<Horario> horarios = generarObjetos.generarHorarios(datosHorarios);
        List<ruta> rutas = generarObjetos.generarRutas(datosRutas);
        List<bus> buses = generarObjetos.crearBuses(5); // ejemplo: 5 buses

        // Asignar horarios a buses
        generarObjetos.asignarHorariosABuses(buses, horarios);

        // Instanciar controlador y vista
        buscadores buscadores = new buscadores(horarios);
        vista vista = new vista();

        // Interacción MVC
        String horaSolicitada = vista.pedirHora();
        List<Horario> resultados = buscadores.buscarHorariosDisponibles(horaSolicitada);
        vista.mostrarHorarios(resultados);
    }
}

