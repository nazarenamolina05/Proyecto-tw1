package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.FechaInvalida;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ServicioSalonReservaImpl implements ServicioSalonReserva {

    private final RepositorioSalon repositorioSalon;
    private final RepositorioSalonReserva repositorioSalonReserva;

    public ServicioSalonReservaImpl(
            RepositorioSalon repositorioSalon, RepositorioSalonReserva repositorioSalonReserva) {
        this.repositorioSalon = repositorioSalon;
        this.repositorioSalonReserva = repositorioSalonReserva;
    }

    @Override
    public List<Salon> obtenerSalonesDisponibles(LocalDate desde, LocalDate hasta) {
        if (desde.isBefore(LocalDate.now())||desde.isEqual(LocalDate.now())) {
            throw new FechaInvalida();
        }

        List<Salon> todosLosSalones = repositorioSalon.obtenerTodos();

        Set<Long> idsOcupados = new HashSet<>();
        for (SalonReserva ocupacion : repositorioSalonReserva.obtenerEntreFechas(desde, hasta)) {
            if (bloqueaLaFecha(ocupacion)) {
                idsOcupados.add(ocupacion.getSalon().getId());
            }
        }

        List<Salon> disponibles = new ArrayList<>();
        for (Salon salon : todosLosSalones) {
            if (!idsOcupados.contains(salon.getId())) {
                disponibles.add(salon);
            }
        }
        return disponibles;
    }

    @Override
    public Map<LocalDate, Boolean> obtenerDisponibilidadDelMes(YearMonth mes) {
        LocalDate hoy = LocalDate.now();
        int totalSalones = repositorioSalon.obtenerTodos().size();

        Map<LocalDate, Set<Long>> salonesOcupadosPorDia = new HashMap<>();
        List<SalonReserva> ocupaciones =
                repositorioSalonReserva.obtenerEntreFechas(mes.atDay(1), mes.atEndOfMonth());
        for (SalonReserva ocupacion : ocupaciones) {
            if (bloqueaLaFecha(ocupacion)) {
                salonesOcupadosPorDia
                        .computeIfAbsent(ocupacion.getFecha(), fecha -> new HashSet<>())
                        .add(ocupacion.getSalon().getId());
            }
        }

        Map<LocalDate, Boolean> calendario = new LinkedHashMap<>();
        for (int dia = 1; dia <= mes.lengthOfMonth(); dia++) {
            LocalDate fecha = mes.atDay(dia);
            int ocupados = salonesOcupadosPorDia.getOrDefault(fecha, Set.of()).size();
            calendario.put(fecha, fecha.isAfter(hoy) && ocupados < totalSalones);
        }
        return calendario;
    }

    private boolean bloqueaLaFecha(SalonReserva ocupacion) {
        return ocupacion.getEstado() == EstadoReserva.SENIADA
                || ocupacion.getEstado() == EstadoReserva.CONFIRMADA;
    }


}