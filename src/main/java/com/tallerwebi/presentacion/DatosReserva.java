package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ContactoReserva;
import com.tallerwebi.dominio.TipoPago;

// Datos del formulario de reservar.html.
// Los inputs de contacto se llaman "contacto.nombre", "contacto.dni", etc.:
// Spring completa el ContactoReserva de adentro siguiendo ese punto.
public class DatosReserva {

  private TipoPago tipoPago;
  private ContactoReserva contacto = new ContactoReserva();

  public DatosReserva() {}

  public DatosReserva(TipoPago tipoPago, ContactoReserva contacto) {
    this.tipoPago = tipoPago;
    this.contacto = contacto;
  }

  public TipoPago getTipoPago() {
    return tipoPago;
  }

  public void setTipoPago(TipoPago tipoPago) {
    this.tipoPago = tipoPago;
  }

  public ContactoReserva getContacto() {
    return contacto;
  }

  public void setContacto(ContactoReserva contacto) {
    this.contacto = contacto;
  }
}
