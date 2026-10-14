package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.PaqueteNoEncontrado;
import com.tallerwebi.dominio.excepcion.SeleccionInvalida;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ServicioValidarSeleccionPaqueteImpl implements ServicioValidarSeleccionPaquete {

  private RepositorioPaquete repositorioPaquete;
  private RepositorioReserva repositorioReserva;

  public ServicioValidarSeleccionPaqueteImpl(
    RepositorioPaquete repositorioPaquete,
    RepositorioReserva repositorioReserva
  ) {
    this.repositorioPaquete = repositorioPaquete;
    this.repositorioReserva = repositorioReserva;
  }

  @Override
  public List<LocalDate> obtenerFechasOcupadas(Long paqueteId) {
    Paquete paquete = buscarPaquete(paqueteId);
    return repositorioReserva.obtenerFechasOcupadasPorSalon(paquete.getSalon().getId());
  }

  @Override
  public void validar(
    Long paqueteId,
    TipoPaquete tipoEvento,
    LocalDate fecha,
    Integer cantidadInvitados
  ) {
    Paquete paquete = buscarPaquete(paqueteId);

    validarTipoEvento(tipoEvento);
    validarFecha(paqueteId, fecha);
    validarInvitados(paquete, cantidadInvitados);
  }

  private void validarTipoEvento(TipoPaquete tipoEvento) {
    if (tipoEvento == null) {
      throw new SeleccionInvalida("Seleccioná el tipo de evento.");
    }
  }

  private void validarFecha(Long paqueteId, LocalDate fecha) {
    if (fecha == null) {
      throw new SeleccionInvalida("Seleccioná una fecha para el evento.");
    }
    if (!fecha.isAfter(LocalDate.now())) {
      throw new SeleccionInvalida("La fecha debe ser posterior a hoy.");
    }
    if (obtenerFechasOcupadas(paqueteId).contains(fecha)) {
      throw new SeleccionInvalida("Esa fecha está ocupada, seleccioná otra.");
    }
  }

  private void validarInvitados(Paquete paquete, Integer cantidadInvitados) {
    if (cantidadInvitados == null || cantidadInvitados < 1) {
      throw new SeleccionInvalida("Ingresá la cantidad de invitados.");
    }
    Integer capacidad = paquete.getSalon().getCapacidad();
    if (cantidadInvitados > capacidad) {
      throw new SeleccionInvalida("Este paquete permite hasta " + capacidad + " invitados.");
    }
  }

  private Paquete buscarPaquete(Long id) {
    Paquete paquete = repositorioPaquete.obtenerPredeterminadoPorId(id);
    if (paquete == null) {
      throw new PaqueteNoEncontrado();
    }
    return paquete;
  }
}
