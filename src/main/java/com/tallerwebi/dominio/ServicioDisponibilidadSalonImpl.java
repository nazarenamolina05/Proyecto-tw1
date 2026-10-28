package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.FechaInvalida;
import java.time.LocalDate;
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