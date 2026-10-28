package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.EventoIncompleto;
import com.tallerwebi.dominio.excepcion.FechaInvalida;
import com.tallerwebi.dominio.excepcion.ReservaNoEncontrada;
import com.tallerwebi.dominio.excepcion.SalonNoDisponible;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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

  @Override
  public Reserva reservar(Usuario usuario, Evento evento, TipoPago tipoPago) {
    validarFecha(evento);
    validarSalonLibre(evento);

    Reserva reserva = new Reserva(usuario, evento, tipoPago);
    repositorioReserva.guardar(reserva);
    return reserva;
  }

  private void validarFecha(Evento evento) {
    if (evento.getFecha() == null || !evento.getFecha().isAfter(LocalDate.now())) {
      throw new FechaInvalida();
    }
  }

  private void validarSalonLibre(Evento evento) {
    if (evento.getSalon() == null) {
      throw new EventoIncompleto();
    }
    List<LocalDate> fechasOcupadas = repositorioReserva.obtenerFechasOcupadasPorSalon(
      evento.getSalon().getId()
    );
    if (fechasOcupadas.contains(evento.getFecha())) {
      throw new SalonNoDisponible();
    }
  }

  @Override
  public Reserva obtenerReservaDe(Long idReserva, Usuario usuario) {
    Reserva reserva = repositorioReserva.buscarPorId(idReserva);

    if (reserva == null || !perteneceA(reserva, usuario)) {
      throw new ReservaNoEncontrada();
    }
    return reserva;
  }

  private boolean perteneceA(Reserva reserva, Usuario usuario) {
    return (
      reserva.getUsuario() != null &&
      reserva.getUsuario().getId() != null &&
      Objects.equals(reserva.getUsuario().getId(), usuario.getId())
    );
  }
}
