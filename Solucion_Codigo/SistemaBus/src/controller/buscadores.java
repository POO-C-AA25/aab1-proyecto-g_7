package Solucion_Codigo.SistemaBus.src.controller;
import Solucion_Codigo.SistemaBus.src.model.Horario;
import Solucion_Codigo.SistemaBus.src.model.parada;
import Solucion_Codigo.SistemaBus.src.model.ruta;
import java.util.ArrayList;
import java.util.List;
public class buscadores {
    private List<Horario> horarios;
    private List<ruta> rutas;
    public buscadores(List<Horario> horarios, List<ruta> rutas) {
        this.horarios = horarios;
        this.rutas = rutas;
    }
        /**
     * Busca los horarios disponibles dentro de un rango de 15 a 30 minutos
     * después de la hora dada en formato "h:mm a.m." o "h:mm p.m."
     *
     * @param horaEntrada Hora en formato "4:15 p.m."
     * @return lista de Horarios que cumplen el criterio
     */
    public List<Horario> buscarHorariosDisponibles(String horaEntrada) {
        List<Horario> resultados = new ArrayList<>();
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
                if (diferencia >= 15 && diferencia <= 30) {
                    resultados.add(h);
                }
            }
        } catch (NumberFormatException e) {
            // Retornar lista vacía si error al parsear
        }
        return resultados;
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
    /**
     * Busca las paradas disponibles para una línea de bus específica.
     *
     * @param lineaBus Línea de bus a buscar
     * @return lista de paradas que pertenecen a la línea especificada
     */
    public List<String> buscarParadasPorLinea(String lineaBus) {
        for (ruta r : rutas) {
            if (r.getnombre().equalsIgnoreCase(lineaBus)) {
                List<String> nombresParadas = new ArrayList<>();
                for (parada p : r.getParadas()) {
                    nombresParadas.add(p.getNombre());
                }
                return nombresParadas;
            }
        }
        return new ArrayList<>();
    }
    /**
     * Obtiene la ruta dado su nombre.
     *
     * @param nombreRuta El nombre de la ruta a buscar.
     * @return El objeto ruta si se encuentra, null en caso contrario.
     */
    public ruta obtenerRutaPorNombre(String nombreRuta) {
        for (ruta r : rutas) {
            if (r.getnombre().equalsIgnoreCase(nombreRuta)) {
                return r;
            }
        }
        return null;
    }
    public List<ruta> obtenerTodasLasRutas() {
        return rutas;
    }
}