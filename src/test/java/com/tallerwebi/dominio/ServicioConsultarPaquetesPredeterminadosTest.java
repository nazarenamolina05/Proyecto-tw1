package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioConsultarPaquetesPredeterminadosTest {

  private RepositorioPaquete repositorioPaquete;
  private ServicioConsultarPaquetesPredeterminados servicio;

  @BeforeEach
  public void init() {
    repositorioPaquete = mock(RepositorioPaquete.class);
    servicio = new ServicioConsultarPaquetesPredeterminadosImpl(repositorioPaquete);
  }

  @Test
  public void devuelveTodosLosPaquetesQueTraeElRepositorio() {
    //preparacion
    Paquete paqueteBasico = new Paquete(
      1L,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      "Salón Los Álamos",
      "Catering Don José",
      List.of("DJ"),
      150000.0
    );
    when(repositorioPaquete.obtenerPredeterminados()).thenReturn(List.of(paqueteBasico));

    //ejecucion
    List<Paquete> paquetes = servicio.consultarTodos();

    //validacion
    assertEquals(1, paquetes.size());
    assertEquals(paqueteBasico, paquetes.get(0));
  }

  @Test
  public void devuelveElPaqueteQueCoincideConElIdBuscado() {
    //preparacion
    Paquete paqueteBasico = new Paquete(
      1L,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      "Salón Los Álamos",
      "Catering Don José",
      List.of("DJ"),
      80000.0
    );
    Paquete paqueteEstandar = new Paquete(
      2L,
      "Estándar",
      TipoPaquete.FIESTA_DE_15,
      "Salón Jardín del Sol",
      "Catering Fiesta Plena",
      List.of("DJ", "Fotografía"),
      150000.0
    );
    when(repositorioPaquete.obtenerPredeterminados())
      .thenReturn(List.of(paqueteBasico, paqueteEstandar));

    //ejecucion
    Paquete paqueteEncontrado = servicio.consultarPorId(2L);

    //validacion
    assertEquals(paqueteEstandar, paqueteEncontrado);
  }

  @Test
  public void devuelveNullSiNoExisteUnPaqueteConEseId() {
    //preparacion
    Paquete paqueteBasico = new Paquete(
      1L,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      "Salón Los Álamos",
      "Catering Don José",
      List.of("DJ"),
      80000.0
    );
    when(repositorioPaquete.obtenerPredeterminados()).thenReturn(List.of(paqueteBasico));

    //ejecucion
    Paquete paqueteEncontrado = servicio.consultarPorId(99L);

    //validacion
    assertNull(paqueteEncontrado);
  }
}
