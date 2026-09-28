package com.tallerwebi.dominio;

import java.util.List;

@FunctionalInterface
public interface RepositorioReserva {
  List<Reserva> buscarPorUsuario(Usuario usuario);
}
