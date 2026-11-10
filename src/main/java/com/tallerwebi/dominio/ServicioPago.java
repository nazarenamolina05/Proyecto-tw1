package com.tallerwebi.dominio;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface ServicioPago {
  String iniciarPago(Long reservaId, Usuario usuario);
}
