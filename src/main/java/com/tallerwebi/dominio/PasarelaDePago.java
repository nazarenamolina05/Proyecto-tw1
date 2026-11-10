package com.tallerwebi.dominio;

public interface PasarelaDePago {
  String crearPago(Long pagoId, String descripcion, Double monto);

  DatosPagoExterno consultarPago(Long mpPaymentId);
}
