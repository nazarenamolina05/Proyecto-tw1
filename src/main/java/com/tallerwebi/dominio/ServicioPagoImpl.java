package com.tallerwebi.dominio;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioPago")
@Transactional
public class ServicioPagoImpl implements ServicioPago {

  private RepositorioPago repositorioPago;
  private ServicioReserva servicioReserva;
  private PasarelaDePago pasarela;

  @Autowired
  public ServicioPagoImpl(
    RepositorioPago repositorioPago,
    ServicioReserva servicioReserva,
    PasarelaDePago pasarela
  ) {
    this.repositorioPago = repositorioPago;
    this.servicioReserva = servicioReserva;
    this.pasarela = pasarela;
  }

  @Override
  public String iniciarPago(Long reservaId, Usuario usuario) {
    Reserva reserva = servicioReserva.obtenerReservaDe(reservaId, usuario);

    Pago pago = new Pago();
    pago.setReserva(reserva);
    pago.setTipo(reserva.getTipoPago());
    pago.setMonto(reserva.calcularMontoAPagar());
    pago.setEstado(EstadoPago.PENDIENTE);
    pago.setFecha(LocalDateTime.now());
    repositorioPago.guardar(pago);

    return pasarela.crearPago(pago.getId(), this.describir(reserva), pago.getMonto());
  }

  private String describir(Reserva reserva) {
    String concepto = reserva.getTipoPago() == TipoPago.SENIA ? "Seña" : "Pago total";
    return concepto + " - Evento del " + reserva.getEvento().getFecha();
  }
}
