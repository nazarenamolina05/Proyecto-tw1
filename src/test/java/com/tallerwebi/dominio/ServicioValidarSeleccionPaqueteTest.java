package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.PaqueteNoEncontrado;
import com.tallerwebi.dominio.excepcion.SeleccionInvalida;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioValidarSeleccionPaqueteTest {

  private RepositorioPaquete repositorioPaquete;
  private RepositorioReserva repositorioReserva;
  private ServicioValidarSeleccionPaquete servicio;
  private LocalDate fechaFutura;

  @BeforeEach
  public void init() {
    repositorioPaquete = mock(RepositorioPaquete.class);
    repositorioReserva = mock(RepositorioReserva.class);
    servicio = new ServicioValidarSeleccionPaqueteImpl(repositorioPaquete, repositorioReserva);
    fechaFutura = LocalDate.now().plusDays(30);

    Salon salon = new Salon("Salón de prueba", 50000.0);
    salon.setId(1L);
    salon.setCapacidad(50);

    Paquete paquete = new Paquete(
      1L,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      salon,
      new Catering("Catering Basico", 1500.0),
      List.of(new ServicioAdicional("DJ", 40000.0, false)),
      80000.0
    );
    paquete.setPredeterminado(true);

    when(repositorioPaquete.obtenerPredeterminadoPorId(1L)).thenReturn(paquete);
    when(repositorioReserva.obtenerFechasOcupadasPorSalon(1L)).thenReturn(List.of());
  }

  @Test
  public void noLanzaErrorSiLaSeleccionEsValida() {
    assertDoesNotThrow(() -> servicio.validar(1L, TipoPaquete.CUMPLEANIOS, fechaFutura, 50));
  }

  @Test
  public void lanzaErrorSiLaFechaEstaOcupada() {
    // Preparación
    when(repositorioReserva.obtenerFechasOcupadasPorSalon(1L)).thenReturn(List.of(fechaFutura));

    // Ejecución
    SeleccionInvalida error = assertThrows(
      SeleccionInvalida.class,
      () -> servicio.validar(1L, TipoPaquete.CUMPLEANIOS, fechaFutura, 20)
    );

    // Validación
    assertEquals("Esa fecha está ocupada, seleccioná otra.", error.getMessage());
  }

  @Test
  public void lanzaErrorSiSuperaLaCapacidadDelSalon() {
    // Ejecución
    SeleccionInvalida error = assertThrows(
      SeleccionInvalida.class,
      () -> servicio.validar(1L, TipoPaquete.CUMPLEANIOS, fechaFutura, 51)
    );

    // Validación
    assertEquals("Este paquete permite hasta 50 invitados.", error.getMessage());
  }

  @Test
  public void lanzaErrorSiLaCantidadDeInvitadosNoEsValida() {
    assertThrows(
      SeleccionInvalida.class,
      () -> servicio.validar(1L, TipoPaquete.CUMPLEANIOS, fechaFutura, 0)
    );
    assertThrows(
      SeleccionInvalida.class,
      () -> servicio.validar(1L, TipoPaquete.CUMPLEANIOS, fechaFutura, null)
    );
  }

  @Test
  public void lanzaErrorSiNoSeleccionaTipoDeEvento() {
    SeleccionInvalida error = assertThrows(
      SeleccionInvalida.class,
      () -> servicio.validar(1L, null, fechaFutura, 20)
    );

    assertEquals("Seleccioná el tipo de evento.", error.getMessage());
  }

  @Test
  public void lanzaErrorSiNoSeleccionaFecha() {
    SeleccionInvalida error = assertThrows(
      SeleccionInvalida.class,
      () -> servicio.validar(1L, TipoPaquete.CUMPLEANIOS, null, 20)
    );

    assertEquals("Seleccioná una fecha para el evento.", error.getMessage());
  }

  @Test
  public void lanzaErrorSiLaFechaNoEsPosteriorAHoy() {
    SeleccionInvalida errorPasada = assertThrows(
      SeleccionInvalida.class,
      () -> servicio.validar(1L, TipoPaquete.CUMPLEANIOS, LocalDate.now().minusDays(1), 20)
    );
    SeleccionInvalida errorHoy = assertThrows(
      SeleccionInvalida.class,
      () -> servicio.validar(1L, TipoPaquete.CUMPLEANIOS, LocalDate.now(), 20)
    );

    assertEquals("La fecha debe ser posterior a hoy.", errorPasada.getMessage());
    assertEquals("La fecha debe ser posterior a hoy.", errorHoy.getMessage());
  }

  @Test
  public void lanzaPaqueteNoEncontradoSiElPaqueteNoExiste() {
    when(repositorioPaquete.obtenerPredeterminadoPorId(99L)).thenReturn(null);

    assertThrows(
      PaqueteNoEncontrado.class,
      () -> servicio.validar(99L, TipoPaquete.CUMPLEANIOS, fechaFutura, 20)
    );
  }
}
