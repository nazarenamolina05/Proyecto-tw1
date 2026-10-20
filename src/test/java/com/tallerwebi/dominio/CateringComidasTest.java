package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CateringComidasTest {

  @Test
  public void deberiaAgregarComidaAlCateringSinCambiarSuPrecio() {
    Catering catering = new Catering("Catering completo", 5000.0);
    Comida comida = new Comida("Milanesa", TipoComida.PRINCIPAL);

    catering.agregarComida(comida);
    catering.agregarComida(comida);

    assertEquals(1, catering.getComidas().size());
    assertTrue(catering.getComidas().contains(comida));
    assertEquals(5000.0, catering.getPrecioPorPersona());
  }
}
