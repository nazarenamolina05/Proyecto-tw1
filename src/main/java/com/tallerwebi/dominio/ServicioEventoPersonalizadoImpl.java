package com.tallerwebi.dominio;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioEventoPersonalizado")
@Transactional
public class ServicioEventoPersonalizadoImpl implements ServicioEventoPersonalizado {

  private RepositorioSalon repositorioSalon;
  private RepositorioCatering repositorioCatering;
  private RepositorioServicioAdicional repositorioServicioAdicional;

  @Autowired
  public ServicioEventoPersonalizadoImpl(
    RepositorioSalon repositorioSalon,
    RepositorioCatering repositorioCatering,
    RepositorioServicioAdicional repositorioServicioAdicional
  ) {
    this.repositorioSalon = repositorioSalon;
    this.repositorioCatering = repositorioCatering;
    this.repositorioServicioAdicional = repositorioServicioAdicional;
  }

  @Override
  public EventoPersonalizado crearEvento(
    TipoPaquete tipo,
    LocalDate fecha,
    Integer cantidadInvitados
  ) {
    return new EventoPersonalizado(tipo, fecha, cantidadInvitados);
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
  public List<ServicioAdicional> obtenerServicioAdicionales() {
    return repositorioServicioAdicional.obtenerTodos();
  }

  @Override
  public void elegirSalon(EventoPersonalizado evento, Long idSalon) {
    evento.setSalon(repositorioSalon.buscarPorId(idSalon));
  }

  @Override
  public void elegirCatering(EventoPersonalizado evento, Long idCatering) {
    evento.setCatering(repositorioCatering.buscarPorId(idCatering));
  }

  @Override
  public void agregarServicio(EventoPersonalizado evento, Long idServicio) {
    evento.agregarServicio(repositorioServicioAdicional.buscarPorId(idServicio));
  }

  @Override
  public void quitarServicio(EventoPersonalizado evento, Long idServicio) {
    ServicioAdicional aQuitar = null;
    for (ServicioAdicional servicio : evento.getServiciosAdicionales()) {
      if (servicio.getId().equals(idServicio)) {
        aQuitar = servicio;
      }
    }
    evento.quitarServicio(aQuitar);
  }
}
