package Datos;

import Model.Bus;
import Model.Horario;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase de utilidad para generar datos de prueba o aleatorios.
 * Responsabilidad Única: Generación de datos, sin interacción con la base de datos o CSV.
 */
public class DataGenerator {

    private final Random random = new Random();

    public DataGenerator() {
    }

    /**
     * Crea un número de objetos Bus con identificadores simples.
     *
     * @param cantidad La cantidad de buses a crear.
     * @return una lista de objetos Bus.
     */
    public List<Bus> crearBuses(int cantidad) {
        List<Bus> buses = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            buses.add(new Bus("Bus-" + String.format("%02d", i)));
        }
        System.out.println("Buses creados: " + buses.size());
        return buses;
    }

    /**
     * Asigna horarios aleatorios a los buses.
     *
     * @param buses La lista de buses.
     * @param horarios La lista de horarios disponibles.
     */
    public void asignarHorariosABuses(List<Bus> buses, List<Horario> horarios) {
        if (buses.isEmpty() || horarios.isEmpty()) {
            System.out.println("No hay buses u horarios para asignar.");
            return;
        }
        for (Bus bus : buses) {
            // Asignar 1 a 3 horarios por bus
            int numAsignaciones = random.nextInt(3) + 1; // 1, 2 o 3 asignaciones
            for (int i = 0; i < numAsignaciones; i++) {
                Horario horarioAleatorio = horarios.get(random.nextInt(horarios.size()));
                // Asegurarse de que el horario tenga al menos una línea para la asignación
                if (!horarioAleatorio.getLineas().isEmpty()) {
                    String lineaAsignada = horarioAleatorio.getLineas()
                            .get(random.nextInt(horarioAleatorio.getLineas().size()));
                    bus.agregarAsignacion(horarioAleatorio.getHoraTexto(), lineaAsignada);
                }
            }
        }
        System.out.println("Horarios asignados a buses.");
    }
}