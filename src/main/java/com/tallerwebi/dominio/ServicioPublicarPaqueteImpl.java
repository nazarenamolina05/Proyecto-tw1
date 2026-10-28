package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.EventoNoFinalizado;
import com.tallerwebi.dominio.excepcion.NombrePaqueteInvalido;
import com.tallerwebi.dominio.excepcion.ReservaNoEncontrada;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioPublicarPaqueteImpl implements ServicioPublicarPaquete {

  private RepositorioReserva repositorioReserva;
  private RepositorioPaquete repositorioPaquete;

  @Autowired
  public ServicioPublicarPaqueteImpl(
    RepositorioReserva repositorioReserva,
    RepositorioPaquete repositorioPaquete
  ) {
    this.repositorioReserva = repositorioReserva;
    this.repositorioPaquete = repositorioPaquete;
  }

  @Override
  public Paquete publicar(Long idReserva, Usuario usuario, String nombre) {
    Reserva reserva = repositorioReserva.buscarPorId(idReserva);

    validarQuePertenece(reserva, usuario);
    validarQueYaOcurrio(reserva.getEvento());
    validarNombre(nombre);

    Paquete paquete = Paquete.desdeEvento(reserva.getEvento(), nombre, usuario);
    repositorioPaquete.guardar(paquete);
    return paquete;
  }

  private void validarQuePertenece(Reserva reserva, Usuario usuario) {
    if (
      reserva == null ||
      reserva.getUsuario() == null ||
      !Objects.equals(reserva.getUsuario().getId(), usuario.getId())
    ) {
      throw new ReservaNoEncontrada();
    }
  }

  private void validarQueYaOcurrio(Evento evento) {
    if (!evento.yaOcurrio()) {
      throw new EventoNoFinalizado();
    }
  }

  private void validarNombre(String nombre) {
    if (nombre == null || nombre.isBlank()) {
      throw new NombrePaqueteInvalido();
    }
  }
}
