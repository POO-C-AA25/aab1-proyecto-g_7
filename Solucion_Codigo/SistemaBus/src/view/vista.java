package view;

import model.Horario;
import java.util.List;
import java.util.Scanner;

public class vista {
    private Scanner scanner;

    public vista() {
        scanner = new Scanner(System.in);
    }

    /**
     * Solicita al usuario ingresar una hora en formato "h:mm a.m." / "h:mm p.m.".
     * @return String con la hora ingresada por el usuario
     */
    public String pedirHora() {
        System.out.print("Ingrese la hora (ejemplo: 4:15 p.m.): ");
        return scanner.nextLine();
    }

    /**
     * Muestra la lista de horarios disponibles al usuario.
     * @param horarios Lista de horarios para mostrar
     */
    public void mostrarHorarios(List<Horario> horarios) {
        if (horarios == null || horarios.isEmpty()) {
            System.out.println("No se encontraron horarios disponibles.");
            return;
        }
        System.out.println("Horarios disponibles:");
        for (Horario h : horarios) {
            System.out.println(h);
        }
    }
}