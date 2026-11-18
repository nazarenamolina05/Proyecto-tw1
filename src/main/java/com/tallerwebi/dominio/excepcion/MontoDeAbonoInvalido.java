package com.tallerwebi.dominio.excepcion;

public class MontoDeAbonoInvalido extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public MontoDeAbonoInvalido() {
    super("El monto del abono no es válido");
  }
}
