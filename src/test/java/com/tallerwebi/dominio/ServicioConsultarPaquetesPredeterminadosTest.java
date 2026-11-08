package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.PaqueteNoEncontrado;
import java.util.List;
import java.util.Map;
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
    // Preparación
    Paquete paqueteBasico = crearPaquete(1L, "Básico", TipoPaquete.CUMPLEANIOS, 80000.0);

    when(repositorioPaquete.obtenerPredeterminados()).thenReturn(List.of(paqueteBasico));

    // Ejecución
    List<Paquete> paquetes = servicio.consultarTodos();

    // Validación
    assertEquals(1, paquetes.size());
    assertEquals(paqueteBasico, paquetes.get(0));
  }

  @Test
  public void devuelveElPaquetePredeterminadoQueCoincideConElId() {
    // Preparación
    Paquete paqueteEstandar = crearPaquete(2L, "Estándar", TipoPaquete.FIESTA_DE_15, 150000.0);

    when(repositorioPaquete.obtenerPredeterminadoPorId(2L)).thenReturn(paqueteEstandar);

    // Ejecución
    Paquete paqueteEncontrado = servicio.consultarPorId(2L);

    // Validación
    assertEquals(paqueteEstandar, paqueteEncontrado);
  }

  @Test
  public void lanzaPaqueteNoEncontradoSiNoExisteElPaquete() {
    // Preparación
    when(repositorioPaquete.obtenerPredeterminadoPorId(99L)).thenReturn(null);

    // Ejecución y validación
    assertThrows(PaqueteNoEncontrado.class, () -> servicio.consultarPorId(99L));
  }

  // [REFACTOR PAQUETE] Los servicios del paquete se consultan desde el repositorio

  @Test
  public void devuelveLosServiciosDelPaqueteQueTraeElRepositorio() {
    // Preparación
    Paquete paqueteBasico = crearPaquete(1L, "Básico", TipoPaquete.CUMPLEANIOS, 80000.0);
    List<ServicioAdicional> servicios = List.of(new ServicioAdicional("DJ", 40000.0, false));
    when(repositorioPaquete.obtenerServiciosDe(1L)).thenReturn(servicios);

    // Ejecución
    List<ServicioAdicional> obtenidos = servicio.consultarServiciosDe(paqueteBasico);

    // Validación
    assertEquals(servicios, obtenidos);
  }

  @Test
  public void sinPaqueteDeOrigenDevuelveUnaListaDeServiciosVacia() {
    // Ejecución
    List<ServicioAdicional> obtenidos = servicio.consultarServiciosDe(null);

    // Validación
    assertTrue(obtenidos.isEmpty());
  }

  @Test
  public void armaUnMapaConLosServiciosDeCadaPaquete() {
    // Preparación
    Paquete basico = crearPaquete(1L, "Básico", TipoPaquete.CUMPLEANIOS, 80000.0);
    Paquete premium = crearPaquete(3L, "Premium", TipoPaquete.CASAMIENTO, 890000.0);
    List<ServicioAdicional> serviciosBasico = List.of(new ServicioAdicional("DJ", 40000.0, false));
    List<ServicioAdicional> serviciosPremium = List.of(
      new ServicioAdicional("DJ", 40000.0, false),
      new ServicioAdicional("Video", 45000.0, false)
    );
    when(repositorioPaquete.obtenerServiciosDe(1L)).thenReturn(serviciosBasico);
    when(repositorioPaquete.obtenerServiciosDe(3L)).thenReturn(serviciosPremium);

    // Ejecución
    Map<Long, List<ServicioAdicional>> serviciosPorPaquete = servicio.consultarServiciosPorPaquete(
      List.of(basico, premium)
    );

    // Validación
    assertEquals(serviciosBasico, serviciosPorPaquete.get(1L));
    assertEquals(serviciosPremium, serviciosPorPaquete.get(3L));
  }

  private Paquete crearPaquete(Long id, String nombre, TipoPaquete tipo, Double precioEstimado) {
    Paquete paquete = new Paquete(
      id,
      nombre,
      tipo,
      new Salon("Salon Los Alamos", 50000.0),
      new Catering("Catering Basico", 1500.0),
      precioEstimado
    );

    paquete.setPredeterminado(true);

    return paquete;
  }
}
