package com.tallerwebi.dominio;

public class DatosPagoExterno {

  private final EstadoPago estado;
  private final String referencia;

  public DatosPagoExterno(EstadoPago estado, String referencia) {
    this.estado = estado;
    this.referencia = referencia;
  }

  public EstadoPago getEstado() {
    return estado;
  }

  public String getReferencia() {
    return referencia;
  }
}
