package com.tallerwebi.dominio.excepcion;

public class DatosDePerfilInvalidos extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DatosDePerfilInvalidos(String mensaje) {
    super(mensaje);
  }
}
