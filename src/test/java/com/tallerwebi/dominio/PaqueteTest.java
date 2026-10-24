package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class PaqueteTest {

  //este test prueba que la clase Paquete en sí misma guarda y devuelve bien sus propios datos
  @Test
  public void seCreaUnPaqueteConTodosSusDatos() {
    // given
    Long idEsperado = 1L;
    String nombreEsperado = "Básico";
    TipoPaquete tipoEsperado = TipoPaquete.CUMPLEANIOS;
    Salon salonEsperado = new Salon("Salón los Alamos", 50000.0);
    Catering cateringEsperado = new Catering("Catering Basico", 1500.0);
    ServicioAdicional dj = new ServicioAdicional("DJ", 40000.0, false);
    ServicioAdicional fotografia = new ServicioAdicional("Fotografia", 30000.0, false);
    List<ServicioAdicional> serviciosEsperados = List.of(dj, fotografia);
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

  //el paquete guarda una copia de la lista: cambiar la lista original no cambia el paquete
  @Test
  public void modificarLaListaOriginalNoCambiaLosServiciosDelPaquete() {
    // given
    ServicioAdicional dj = new ServicioAdicional("DJ", 40000.0, false);
    List<ServicioAdicional> servicios = new ArrayList<>(List.of(dj));
    Paquete paquete = new Paquete(
      null,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      null,
      null,
      servicios,
      80000.0
    );

    // when
    servicios.clear();

    // then
    assertEquals(List.of(dj), paquete.getServiciosAdicionales());
  }

  //los campos nuevos arrancan vacíos: los predeterminados no tienen publicador
  @Test
  public void unPaqueteNuevoNoTienePublicadorNiDescripcion() {
    Paquete paquete = new Paquete();

    assertNull(paquete.getPublicador());
    assertNull(paquete.getDescripcion());
  }

  //el menú agrupa las comidas del catering por tipo y respeta el orden ENTRADA, PRINCIPAL, ..., BEBIDA
  @Test
  public void elMenuAgrupaLasComidasDelCateringPorTipoEnOrden() {
    // given
    Comida gaseosas = new Comida("Gaseosas", TipoComida.BEBIDA);
    Comida empanadas = new Comida("Empanadas", TipoComida.ENTRADA);
    Comida asado = new Comida("Asado con ensalada", TipoComida.PRINCIPAL);
    Catering catering = new Catering("Catering Medio", 3000.0);
    catering.agregarComida(gaseosas);
    catering.agregarComida(empanadas);
    catering.agregarComida(asado);
    Paquete paquete = new Paquete(
      null,
      "Estándar",
      TipoPaquete.FIESTA_DE_15,
      null,
      catering,
      List.of(),
      150000.0
    );

    // when
    Map<TipoComida, List<Comida>> menu = paquete.getMenu();

    // then
    assertEquals(
      List.of(TipoComida.ENTRADA, TipoComida.PRINCIPAL, TipoComida.BEBIDA),
      List.copyOf(menu.keySet())
    );
    assertEquals(List.of(empanadas), menu.get(TipoComida.ENTRADA));
    assertEquals(List.of(asado), menu.get(TipoComida.PRINCIPAL));
    assertEquals(List.of(gaseosas), menu.get(TipoComida.BEBIDA));
  }

  @Test
  public void unPaqueteSinCateringTieneElMenuVacio() {
    Paquete paquete = new Paquete();

    assertTrue(paquete.getMenu().isEmpty());
  }
}
