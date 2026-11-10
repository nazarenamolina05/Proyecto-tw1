package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.ElementoInexistenteException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioEvento")
@Transactional
public class ServicioEventoImpl implements ServicioEvento {

  private RepositorioSalon repositorioSalon;
  private RepositorioCatering repositorioCatering;
  private RepositorioServicioAdicional repositorioServicioAdicional;
  private RepositorioEvento repositorioEvento;
  private RepositorioPaquete repositorioPaquete;

  @Autowired
  public ServicioEventoImpl(
    RepositorioSalon repositorioSalon,
    RepositorioCatering repositorioCatering,
    RepositorioServicioAdicional repositorioServicioAdicional,
    RepositorioEvento repositorioEvento,
    RepositorioPaquete repositorioPaquete
  ) {
    this.repositorioSalon = repositorioSalon;
    this.repositorioCatering = repositorioCatering;
    this.repositorioServicioAdicional = repositorioServicioAdicional;
    this.repositorioEvento = repositorioEvento;
    this.repositorioPaquete = repositorioPaquete;
  }

  // [REFACTOR EVENTO] El evento se guarda apenas se crea, porque sus servicios viven
  // en EventoServicio y esa tabla necesita el id del evento.
  @Override
  public Evento crearEvento(TipoPaquete tipoPaquete, LocalDate fecha, Integer cantidadInvitados) {
    Evento evento = new Evento(tipoPaquete, fecha, cantidadInvitados);
    repositorioEvento.guardar(evento);
    return evento;
  }

  // [REFACTOR EVENTO] Antes Paquete.crearEvento copiaba los servicios a la lista del
  // evento. Ahora se guarda una fila de EventoServicio por cada servicio del paquete.
  @Override
  public Evento crearEventoDesdePaquete(
    Paquete paquete,
    TipoPaquete tipoPaquete,
    LocalDate fecha,
    Integer cantidadInvitados
  ) {
    Evento evento = paquete.crearEvento(tipoPaquete, fecha, cantidadInvitados);
    repositorioEvento.guardar(evento);
    for (ServicioAdicional servicio : repositorioPaquete.obtenerServiciosDe(paquete.getId())) {
      repositorioEvento.agregarServicio(evento, servicio);
    }
    return evento;
  }

  @Override
  public void actualizarDatos(
    Evento evento,
    TipoPaquete tipoPaquete,
    LocalDate fecha,
    Integer cantidadInvitados
  ) {
    evento.setTipoPaquete(tipoPaquete);
    evento.setFecha(fecha);
    evento.setCantidadInvitados(cantidadInvitados);
    repositorioEvento.actualizar(evento);
  }

  @Override
  public List<Salon> obtenerSalones() {
    return repositorioSalon.obtenerTodos();
  }

  @Override
  public List<Catering> obtenerCaterings() {
    return repositorioCatering.obtenerTodos();
  }

  @Override
  public List<ServicioAdicional> obtenerServiciosAdicionales() {
    return repositorioServicioAdicional.obtenerTodos();
  }

  @Override
  public List<ServicioAdicional> obtenerServiciosDe(Evento evento) {
    return repositorioEvento.buscarServicio(evento.getId());
  }

  @Override
  public void elegirSalon(Evento evento, Long idSalon) {
    Salon salon = repositorioSalon.buscarPorId(idSalon);
    if (salon == null) {
      throw new ElementoInexistenteException("No existe el salon con id " + idSalon);
    }
    evento.setSalon(salon);
    repositorioEvento.actualizar(evento);
  }

  @Override
  public void elegirCatering(Evento evento, Long idCatering) {
    Catering catering = repositorioCatering.buscarPorId(idCatering);
    if (catering == null) {
      throw new ElementoInexistenteException("No existe el Catering con id " + idCatering);
    }
    evento.setCatering(catering);
    repositorioEvento.actualizar(evento);
  }

  // [REFACTOR EVENTO] Si el evento ya tiene el servicio no se agrega de nuevo
  // (antes lo evitaba Evento.agregarServicio con contains).
  @Override
  public void agregarServicio(Evento evento, Long idServicio) {
    ServicioAdicional servicio = repositorioServicioAdicional.buscarPorId(idServicio);
    if (servicio == null) {
      throw new ElementoInexistenteException("No existe el servicio con id " + idServicio);
    }
    if (!repositorioEvento.tieneServicio(evento.getId(), idServicio)) {
      repositorioEvento.agregarServicio(evento, servicio);
    }
  }

  @Override
  public void quitarServicio(Evento evento, Long idServicio) {
    if (!repositorioEvento.tieneServicio(evento.getId(), idServicio)) {
      throw new ElementoInexistenteException("El evento no tiene el servicio con id " + idServicio);
    }
    repositorioEvento.quitarServicio(evento.getId(), idServicio);
  }
}
