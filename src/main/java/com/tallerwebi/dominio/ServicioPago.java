package com.tallerwebi.dominio;

public interface ServicioPago {
  String iniciarPago(Long reservaId, Usuario usuario);

  EstadoPago confirmarPago(Long pagoId, Long mpPaymentId);
}
