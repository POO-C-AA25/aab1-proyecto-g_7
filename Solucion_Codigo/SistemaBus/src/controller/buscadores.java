package Solucion_Codigo.SistemaBus.src.controller;
import Solucion_Codigo.SistemaBus.src.model.Horario;
import Solucion_Codigo.SistemaBus.src.model.Parada;
import Solucion_Codigo.SistemaBus.src.model.Ruta;
import java.util.ArrayList;
import java.util.List;
public class Buscadores {
    private List<Horario> horarios;
    private List<Ruta> rutas;
    public Buscadores(List<Horario> horarios, List<Ruta> rutas) {
        this.horarios = horarios;
        this.rutas = rutas;
    }
    /**
     * Busca los horarios disponibles dentro de un rango de 0 a 30 minutos después de la hora dada en formato "h:mm a.m." o "h:mm p.m."
     * @param horaEntrada Hora en formato "4:15 p.m."
     * @return lista de Horarios que cumplen el criterio
     */
    public List<Horario> buscarHorariosDisponibles(String horaEntrada) {
        List<Horario> resultados = new ArrayList<>();
        boolean esUltimoHorario = true; // Para verificar si se alcanzó el último horario
        try {
            String[] partesHora = horaEntrada.trim().split("[: ]+");
            if (partesHora.length < 3) {
                return resultados;
            }
            int hora = Integer.parseInt(partesHora[0]);
            int minutos = Integer.parseInt(partesHora[1]);
            String periodo = partesHora[2].toLowerCase();
            // Convertir a formato 24 horas
            if (periodo.equals("p.m.") && hora != 12) {
                hora += 12;
            } else if (periodo.equals("a.m.") && hora == 12) {
                hora = 0;
            }
            int minutosEntrada = hora * 60 + minutos;
            for (Horario h : horarios) {
                String[] partesHorario = h.getHora().trim().split("[: ]+");
                if (partesHorario.length < 3) continue;
                int horaHorario = Integer.parseInt(partesHorario[0]);
                int minutosHorario = Integer.parseInt(partesHorario[1]);
                String periodoHorario = partesHorario[2].toLowerCase();
                if (periodoHorario.equals("p.m.") && horaHorario != 12) {
                    horaHorario += 12;
                } else if (periodoHorario.equals("a.m.") && horaHorario == 12) {
                    horaHorario = 0;
                }
                int minutosHorarioTotales = horaHorario * 60 + minutosHorario;
                int diferencia = minutosHorarioTotales - minutosEntrada;
                // Incluir el horario exacto y buscar dentro del rango de 0 a 30 minutos
                if (diferencia >= 0 && diferencia <= 30) {
                    resultados.add(h);
                    esUltimoHorario = false; // Hay horarios disponibles después de la hora ingresada
                }
            }
            // Mensaje si se alcanzó el último horario
            if (esUltimoHorario) {
                System.out.println("Último horario alcanzado. No hay más buses después de " + horaEntrada + ".");
            }
        } catch (NumberFormatException e) {
            // Retornar lista vacía si error al parsear
        }
        return resultados;
    }
    /**
     * Busca las paradas disponibles para una línea de bus específica.
     *
     * @param lineaBus Línea de bus a buscar
     * @return lista de paradas que pertenecen a la línea especificada
     */
    public List<String> buscarParadasPorLinea(String lineaBus) {
        for (Ruta r : rutas) {
            if (r.getNombreruta().equalsIgnoreCase(lineaBus)) {
                List<String> nombresParadas = new ArrayList<>();
                for (Parada p : r.getParadas()) {
                    nombresParadas.add(p.getNombreparada());
                }
                return nombresParadas;
            }
        }
        return new ArrayList<>();
    }
    /**
     * Busca los horarios disponibles para una línea de bus específica.
     *
     * @param lineaBus Línea de bus a buscar
     * @return lista de Horarios que pertenecen a la línea especificada
     */
    public List<Horario> buscarHorariosPorLinea(String lineaBus) {
        List<Horario> resultados = new ArrayList<>();
        for (Horario h : horarios) {
            for (String linea : h.getLineas()) {
                if (linea.equalsIgnoreCase(lineaBus)) {
                    resultados.add(h);
                    break;
                }
            }
        }
        return resultados;
    }
    public Ruta obtenerRutaPorNombre(String nombreRuta) {
        for (Ruta r : rutas) {
            if (r.getNombreruta().equalsIgnoreCase(nombreRuta)) {
                return r;
            }
        }
        return null;
    }
    /**
     * Muestra los horarios encontrados y un mensaje sobre la espera de los buses.
     * @param horarios Lista de horarios encontrados
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
        System.out.println("Los buses esperan 5 minutos más para recoger a todos los pasajeros.");
    }
}