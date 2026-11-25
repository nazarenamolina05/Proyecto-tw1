package com.tallerwebi.presentacion;

// Datos del formulario para abonar parte del saldo de una reserva señada
// (fragments/pago-reserva.html, input name="monto").
public class DatosAbono {

  private Double monto;

  public DatosAbono() {}

  public DatosAbono(Double monto) {
    this.monto = monto;
  }

  public Double getMonto() {
    return monto;
  }

  public void setMonto(Double monto) {
    this.monto = monto;
  }
}
