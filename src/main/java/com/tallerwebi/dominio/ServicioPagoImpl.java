package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.PagoNoCoincide;
import com.tallerwebi.dominio.excepcion.PagoNoEncontrado;
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

  @Override
  public void confirmarPago(Long pagoId, Long mpPaymentId) {
    Pago pago = repositorioPago.buscarPorId(pagoId);
    if (pago == null) {
      throw new PagoNoEncontrado();
    }
    if (pago.getEstado() == EstadoPago.APROBADO) {
      return;
    }

    DatosPagoExterno datos = pasarela.consultarPago(mpPaymentId);
    if (!String.valueOf(pagoId).equals(datos.getReferencia())) {
      throw new PagoNoCoincide();
    }

    if (datos.getEstado() == EstadoPago.APROBADO) {
      pago.setEstado(EstadoPago.APROBADO);
      pago.setMpPaymentId(mpPaymentId);
      pago.getReserva().confirmarPago();
    } else if (datos.getEstado() == EstadoPago.RECHAZADO) {
      pago.setEstado(EstadoPago.RECHAZADO);
    }
  }

  private String describir(Reserva reserva) {
    String concepto = reserva.getTipoPago() == TipoPago.SENIA ? "Seña" : "Pago total";
    return concepto + " - Evento del " + reserva.getEvento().getFecha();
  }
}
