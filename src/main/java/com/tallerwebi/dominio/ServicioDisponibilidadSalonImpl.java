package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.FechaInvalida;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ServicioDisponibilidadSalonImpl implements ServicioDisponibilidadSalon {

  private final RepositorioSalon repositorioSalon;
  private final RepositorioReserva repositorioReserva;

  public ServicioDisponibilidadSalonImpl(
    RepositorioSalon repositorioSalon,
    RepositorioReserva repositorioReserva
  ) {
    this.repositorioSalon = repositorioSalon;
    this.repositorioReserva = repositorioReserva;
  }

  @Override
  public List<Salon> obtenerSalonesDisponibles(LocalDate desde, LocalDate hasta) {
    if (desde == null || !desde.isAfter(LocalDate.now())) {
      throw new FechaInvalida();
    }

    List<Salon> todosLosSalones = repositorioSalon.obtenerTodos();

    Set<Long> idsOcupados = new HashSet<>();
    for (Reserva reserva : repositorioReserva.buscarEntreFechas(desde, hasta)) {
      if (bloqueaLaFecha(reserva)) {
        idsOcupados.add(salonDe(reserva).getId());
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
    List<Reserva> reservas = repositorioReserva.buscarEntreFechas(mes.atDay(1), mes.atEndOfMonth());
    for (Reserva reserva : reservas) {
      if (bloqueaLaFecha(reserva)) {
        salonesOcupadosPorDia
          .computeIfAbsent(reserva.getEvento().getFecha(), fecha -> new HashSet<>())
          .add(salonDe(reserva).getId());
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

  // Único lugar que sabe cómo llegar al salón de una reserva: a través de su evento.
  private Salon salonDe(Reserva reserva) {
    return reserva.getEvento() == null ? null : reserva.getEvento().getSalon();
  }

  // Una reserva bloquea su salón si está señada o confirmada. Las reservas sin salón
  // asociado no se pueden atribuir a ningún salón, así que no bloquean.
  private boolean bloqueaLaFecha(Reserva reserva) {
    return (
      salonDe(reserva) != null &&
      (reserva.getEstado() == EstadoReserva.SENIADA ||
        reserva.getEstado() == EstadoReserva.CONFIRMADA)
    );
  }
}
