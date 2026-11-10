package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.EventoNoFinalizado;
import com.tallerwebi.dominio.excepcion.NombrePaqueteInvalido;
import com.tallerwebi.dominio.excepcion.ReservaNoEncontrada;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioPublicarPaqueteImpl implements ServicioPublicarPaquete {

  private RepositorioReserva repositorioReserva;
  private RepositorioPaquete repositorioPaquete;
  private RepositorioEvento repositorioEvento;

  @Autowired
  public ServicioPublicarPaqueteImpl(
    RepositorioReserva repositorioReserva,
    RepositorioPaquete repositorioPaquete,
    RepositorioEvento repositorioEvento
  ) {
    this.repositorioReserva = repositorioReserva;
    this.repositorioPaquete = repositorioPaquete;
    this.repositorioEvento = repositorioEvento;
  }

  @Override
  public Paquete publicar(Long idReserva, Usuario usuario, String nombre) {
    Reserva reserva = repositorioReserva.buscarPorId(idReserva);

    validarQuePertenece(reserva, usuario);
    validarQueYaOcurrio(reserva.getEvento());
    validarNombre(nombre);

    // [REFACTOR EVENTO] Los servicios del evento se consultan en EventoServicio.
    List<ServicioAdicional> serviciosDelEvento = repositorioEvento.buscarServicio(
      reserva.getEvento().getId()
    );
    Paquete paquete = Paquete.desdeEvento(reserva.getEvento(), nombre, usuario, serviciosDelEvento);
    repositorioPaquete.guardar(paquete);
    guardarServiciosDelEvento(paquete, serviciosDelEvento);
    return paquete;
  }

  // [REFACTOR PAQUETE] Antes los servicios viajaban dentro del Paquete (@ManyToMany).
  // Ahora se guarda una fila de PaqueteServicio por cada servicio del evento.
  private void guardarServiciosDelEvento(
    Paquete paquete,
    List<ServicioAdicional> serviciosDelEvento
  ) {
    for (ServicioAdicional servicio : serviciosDelEvento) {
      repositorioPaquete.guardarServicio(new PaqueteServicio(paquete, servicio));
    }
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
