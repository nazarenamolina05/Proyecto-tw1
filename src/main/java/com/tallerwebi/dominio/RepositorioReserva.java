package com.tallerwebi.dominio;

import java.time.LocalDate;
import java.util.List;

public interface RepositorioReserva {
  List<Reserva> buscarPorUsuario(Usuario usuario);

  List<LocalDate> obtenerFechasOcupadasPorSalon(Long salonId);

  List<Reserva> buscarEntreFechas(LocalDate desde, LocalDate hasta);

  void guardar(Reserva reserva);

  Reserva buscarPorId(Long id);
}
