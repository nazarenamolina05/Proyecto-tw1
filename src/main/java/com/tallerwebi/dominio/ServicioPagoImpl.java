package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.AbonoNoPermitido;
import com.tallerwebi.dominio.excepcion.MontoDeAbonoInvalido;
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
    return this.crearPagoPendiente(reserva, reserva.getTipoPago(), reserva.calcularMontoAPagar());
  }

  @Override
  public String iniciarAbono(Long reservaId, Usuario usuario, Double monto) {
    Reserva reserva = servicioReserva.obtenerReservaDe(reservaId, usuario);
    if (!reserva.permiteAbonos()) {
      throw new AbonoNoPermitido();
    }
    if (!reserva.admiteAbonoDe(monto)) {
      throw new MontoDeAbonoInvalido();
    }
    return this.crearPagoPendiente(reserva, TipoPago.ABONO, monto);
  }

  @Override
  public EstadoPago confirmarPago(Long pagoId, Long mpPaymentId) {
    Pago pago = repositorioPago.buscarPorId(pagoId);
    if (pago == null) {
      throw new PagoNoEncontrado();
    }
    if (pago.getEstado() == EstadoPago.APROBADO) {
      return pago.getEstado();
    }

    DatosPagoExterno datos = pasarela.consultarPago(mpPaymentId);
    if (!String.valueOf(pagoId).equals(datos.getReferencia())) {
      throw new PagoNoCoincide();
    }

    if (datos.getEstado() == EstadoPago.APROBADO) {
      pago.setEstado(EstadoPago.APROBADO);
      pago.setMpPaymentId(mpPaymentId);
      pago.getReserva().registrarPago(pago.getMonto());
    } else if (datos.getEstado() == EstadoPago.RECHAZADO) {
      pago.setEstado(EstadoPago.RECHAZADO);
    }
    return pago.getEstado();
  }

  private String crearPagoPendiente(Reserva reserva, TipoPago tipo, Double monto) {
    Pago pago = new Pago();
    pago.setReserva(reserva);
    pago.setTipo(tipo);
    pago.setMonto(monto);
    pago.setEstado(EstadoPago.PENDIENTE);
    pago.setFecha(LocalDateTime.now());
    repositorioPago.guardar(pago);

    return pasarela.crearPago(pago.getId(), this.describir(reserva, tipo), monto);
  }

  private String describir(Reserva reserva, TipoPago tipo) {
    String concepto =
      switch (tipo) {
        case SENIA -> "Seña";
        case TOTAL -> "Pago total";
        case ABONO -> "Abono";
      };
    return concepto + " - Evento del " + reserva.getEvento().getFecha();
  }
}
