package controller;

import model.Horario;
import java.util.ArrayList;
import java.util.List;

public class buscadores {
    private List<Horario> horarios;

    public buscadores(List<Horario> horarios) {
        this.horarios = horarios;
    }

    /**
     * Busca los horarios disponibles dentro de un rango de 15 a 30 minutos después de la hora dada en formato "h:mm a.m." o "h:mm p.m."
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
}


