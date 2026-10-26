package com.tallerwebi.dominio.excepcion;

public class ElementoInexistenteException extends RuntimeException {

  /* Identificador para la serialización de la clase, requerido por PMD en excepciones */
  private static final long serialVersionUID = 1L;

  public ElementoInexistenteException(String message) {
    super(message);
  }
}
