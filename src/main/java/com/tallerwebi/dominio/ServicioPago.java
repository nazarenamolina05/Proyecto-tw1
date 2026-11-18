package com.tallerwebi.dominio;

public interface ServicioPago {
  String iniciarPago(Long reservaId, Usuario usuario);
  String iniciarAbono(Long reservaId, Usuario usuario, Double monto);
  EstadoPago confirmarPago(Long pagoId, Long mpPaymentId);
}
