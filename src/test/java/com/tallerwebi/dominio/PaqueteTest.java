package com.tallerwebi.dominio;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;

public class PaqueteTest {

  //este test prueba que la clase Paquete en sí misma guarda y devuelve bien sus propios datos
  @Test
  public void seCreaUnPaqueteConTodosSusDatos() {
    // given
    Long idEsperado = 1L;
    String nombreEsperado = "Básico";
    TipoPaquete tipoEsperado = TipoPaquete.CUMPLEANIOS;
    String salonEsperado = "Salón Los Álamos";
    String cateringEsperado = "Catering Don José";
    List<String> serviciosEsperados = List.of("DJ", "Fotografía");
    Double precioEsperado = 150000.0;

    // when
    Paquete paquete = new Paquete(
      idEsperado,
      nombreEsperado,
      tipoEsperado,
      salonEsperado,
      cateringEsperado,
      serviciosEsperados,
      precioEsperado
    );

    // then
    assertEquals(idEsperado, paquete.getId());
    assertEquals(nombreEsperado, paquete.getNombre());
    assertEquals(tipoEsperado, paquete.getTipo());
    assertEquals(salonEsperado, paquete.getSalon());
    assertEquals(cateringEsperado, paquete.getCatering());
    assertEquals(serviciosEsperados, paquete.getServiciosAdicionales());
    assertEquals(precioEsperado, paquete.getPrecioEstimado());
  }
}
