package com.tallerwebi.dominio.excepcion;

public class PagoNoCoincide extends RuntimeException {

  public PagoNoCoincide() {
    super("El pago de Mercado Pago no corresponde a este pago");
  }

  private static final long serialVersionUID = 1L;
}
