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

  @Autowired
  public ServicioEventoImpl(
    RepositorioSalon repositorioSalon,
    RepositorioCatering repositorioCatering,
    RepositorioServicioAdicional repositorioServicioAdicional
  ) {
    this.repositorioSalon = repositorioSalon;
    this.repositorioCatering = repositorioCatering;
    this.repositorioServicioAdicional = repositorioServicioAdicional;
  }

  @Override
  public Evento crearEvento(TipoPaquete tipoPaquete, LocalDate fecha, Integer cantidadInvitados) {
    return new Evento(tipoPaquete, fecha, cantidadInvitados);
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
  public void elegirSalon(Evento evento, Long idSalon) {
    Salon salon = repositorioSalon.buscarPorId(idSalon);
    if (salon == null) {
      throw new ElementoInexistenteException("No existe el salon con id " + idSalon);
    }
    evento.setSalon(salon);
  }

  @Override
  public void elegirCatering(Evento evento, Long idCatering) {
    Catering catering = repositorioCatering.buscarPorId(idCatering);
    if (catering == null) {
      throw new ElementoInexistenteException("No existe el Catering con id " + idCatering);
    }
    evento.setCatering(catering);
  }

  @Override
  public void agregarServicio(Evento evento, Long idServicio) {
    ServicioAdicional servicio = repositorioServicioAdicional.buscarPorId(idServicio);
    if (servicio == null) {
      throw new ElementoInexistenteException("No existe el servicio con id " + idServicio);
    }
    evento.agregarServicio(servicio);
  }

  @Override
  public void quitarServicio(Evento evento, Long idServicio) {
    ServicioAdicional aQuitar = null;
    for (ServicioAdicional servicio : evento.getServicioAdicionalsList()) {
      if (servicio.getId().equals(idServicio)) {
        aQuitar = servicio;
      }
    }
    if (aQuitar == null) {
      throw new ElementoInexistenteException("El evento no tiene el servicio con id " + idServicio);
    }
    evento.quitarServicio(aQuitar);
  }
}
