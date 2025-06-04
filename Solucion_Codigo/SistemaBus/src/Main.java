package Solucion_Codigo.SistemaBus.src;
import java.util.Scanner;
import Solucion_Codigo.SistemaBus.src.controller.Buscadores;
import Solucion_Codigo.SistemaBus.src.view.vista;
import java.util.List;
import Solucion_Codigo.SistemaBus.src.model.*;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;
        // para mejorar la portabilidad y flexibilidad de la aplicación.
        String rutaHorarios = "C:\\Users\\Lenin\\Desktop\\aab1-proyecto-g_7\\Solucion_Codigo\\SistemaBus\\src\\Datos\\Horarios.csv";
        String rutaRutas = "C:\\Users\\Lenin\\Desktop\\aab1-proyecto-g_7\\Solucion_Codigo\\SistemaBus\\src\\Datos\\Lineasbu.csv";
        // Carga de datos
        List<String[]> datosHorarios = Lector.leerHorarios(rutaHorarios);
        List<String[]> datosRutas = Lector.leerRutas(rutaRutas);
        if (datosHorarios.isEmpty()) {
            System.out.println("Advertencia: No se cargaron datos de horarios. Verifique el archivo: " + rutaHorarios);
            // Se podría decidir terminar la aplicación aquí si los horarios son esenciales.
        }
        if (datosRutas.isEmpty()) {
            System.out.println("Advertencia: No se cargaron datos de rutas. Verifique el archivo: " + rutaRutas);
        }
        // Generación de objetos del modelo
        List<Horario> horarios = GenerarObjetos.generarHorarios(datosHorarios);
        List<Ruta> rutas = GenerarObjetos.generarRutas(datosRutas);
        List<Bus> buses = GenerarObjetos.crearBuses(5); // Ejemplo: crear 5 buses
        // Asignar horarios a buses (si hay horarios y buses disponibles)
        if (!horarios.isEmpty() && !buses.isEmpty()) {
            GenerarObjetos.asignarHorariosABuses(buses, horarios);
        } else {
            System.out.println("No se pudieron asignar horarios a los buses debido a falta de horarios o buses.");
        }
        // Instanciar controlador y vista
        Buscadores buscadores = new Buscadores(horarios, rutas);
        vista vista = new vista(); // La vista crea su propio Scanner
        do {
            vista.solicitarOpcion(buscadores); // La vista maneja la interacción para la opción
            
            System.out.println("\n¿Desea realizar otra búsqueda? (1: Sí, cualquier otro número o entrada para salir)");
            if (scanner.hasNextInt()){
                opcion = scanner.nextInt();
            } else {
                opcion = 0; // Si no es un entero, se asume que no quiere continuar
            }
            scanner.nextLine(); // Consumir el salto de línea restante
        } while (opcion == 1);
        System.out.println("Gracias por usar el Sistema de Información de Buses. ¡Hasta pronto!");
        scanner.close(); // Cerrar el Scanner de Main al final
    }
}