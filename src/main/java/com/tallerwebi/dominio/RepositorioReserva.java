package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioReserva {
    List<Reserva> buscarPorUsuario(Usuario usuario);
}