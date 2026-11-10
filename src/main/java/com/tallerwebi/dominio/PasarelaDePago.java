package com.tallerwebi.dominio;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface PasarelaDePago {
  String crearPago(Long pagoId, String descripcion, Double monto);
}
