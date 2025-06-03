package Solucion_Codigo.SistemaBus.src;
import java.util.Scanner;
import Solucion_Codigo.SistemaBus.src.controller.buscadores;
import Solucion_Codigo.SistemaBus.src.view.vista;
import java.util.List;
import Solucion_Codigo.SistemaBus.src.model.*;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;
        String rutaHorarios = "C:\\Users\\Lenin\\Desktop\\aab1-proyecto-g_7\\Solucion_Codigo\\SistemaBus\\src\\Datos\\Horarios.csv";
        String rutaRutas = "C:\\Users\\Lenin\\Desktop\\aab1-proyecto-g_7\\Solucion_Codigo\\SistemaBus\\src\\Datos\\Lineasbu.csv";
        List<String[]> datosHorarios = lector.leerHorarios(rutaHorarios); // Leer horarios
        List<String[]> datosRutas = lector.leerRutas(rutaRutas); // Leer rutas
        List<Horario> horarios = generarObjetos.generarHorarios(datosHorarios);
        List<ruta> rutas = generarObjetos.generarRutas(datosRutas);
        List<bus> buses = generarObjetos.crearBuses(5); // ejemplo: 5 buses
        // Asignar horarios a buses
        generarObjetos.asignarHorariosABuses(buses, horarios);
        generarObjetos.asignarHorariosABuses(buses, horarios);
        // Instanciar controlador y vista
        buscadores buscadores = new buscadores(horarios, rutas); // Pasar rutas a buscadores
        vista vista = new vista();
        do {
            // Solicitar opción al usuario
            vista.solicitarOpcion(buscadores);
            System.out.println("¿Desea realizar otra búsqueda? (1: Sí, 0: No)");
            opcion = scanner.nextInt();
        } while (opcion == 1);
        scanner.close();
    }
}    