package com.tallerwebi.dominio.excepcion;

public class ReservaCancelada extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ReservaCancelada() {
    super("La reserva está cancelada");
  }
}
