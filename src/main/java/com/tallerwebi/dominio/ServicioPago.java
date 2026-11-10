package com.tallerwebi.dominio;

public interface ServicioPago {
  String iniciarPago(Long reservaId, Usuario usuario);

  void confirmarPago(Long pagoId, Long mpPaymentId);
}
