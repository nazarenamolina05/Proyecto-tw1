package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.FechaInvalida;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
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
    validarFecha(desde);

    Set<Long> idsOcupados = obtenerIdsSalonesOcupados(desde, hasta);

    return repositorioSalon
      .obtenerTodos()
      .stream()
      .filter(salon -> !idsOcupados.contains(salon.getId()))
      .collect(Collectors.toList());
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

  private void validarFecha(LocalDate desde) {
    if (desde == null || !desde.isAfter(LocalDate.now())) {
      throw new FechaInvalida();
    }
  }

  private Set<Long> obtenerIdsSalonesOcupados(LocalDate desde, LocalDate hasta) {
    return repositorioReserva
      .buscarEntreFechas(desde, hasta)
      .stream()
      .filter(this::bloqueaLaFecha)
      .map(reserva -> salonDe(reserva).getId())
      .collect(Collectors.toSet());
  }

  private Salon salonDe(Reserva reserva) {
    return reserva.getEvento() == null ? null : reserva.getEvento().getSalon();
  }

  private boolean bloqueaLaFecha(Reserva reserva) {
    return (
      salonDe(reserva) != null &&
      (reserva.getEstado() == EstadoReserva.SENIADA ||
        reserva.getEstado() == EstadoReserva.CONFIRMADA)
    );
  }
}
