package com.tallerwebi.dominio;

import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioReserva")
@Transactional
public class ServicioReservaImpl implements ServicioReserva {

  private RepositorioReserva repositorioReserva;

  @Autowired
  public ServicioReservaImpl(RepositorioReserva repositorioReserva) {
    this.repositorioReserva = repositorioReserva;
  }

  @Override
  public List<Reserva> obtenerReservasDe(Usuario usuario) {
    return repositorioReserva.buscarPorUsuario(usuario);
  }
}
