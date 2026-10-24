package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.PaqueteNoEncontrado;
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

  // Método auxiliar que utiliza el constructor completo
  private Paquete crearPaquete(Long id, String nombre, TipoPaquete tipo, Double precioEstimado) {
    Paquete paquete = new Paquete(
      id,
      nombre,
      tipo,
      new Salon("Salón de prueba", 50000.0),
      new Catering("Catering de prueba", 1500.0),
      List.of(
        new ServicioAdicional("DJ", 40000.0, false),
        new ServicioAdicional("Fotografia", 30000.0, false)
      ),
      precioEstimado
    );

    paquete.setPredeterminado(true);

    return paquete;
  }
}
