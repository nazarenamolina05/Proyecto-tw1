package com.tallerwebi.dominio;

public interface RepositorioPago {
  void guardar(Pago pago);

  Pago buscarPorId(Long id);
}
