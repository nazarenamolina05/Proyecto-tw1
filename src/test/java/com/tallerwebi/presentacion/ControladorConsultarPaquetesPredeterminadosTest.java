package com.tallerwebi.presentacion;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Paquete;
import com.tallerwebi.dominio.Salon;
import com.tallerwebi.dominio.ServicioConsultarPaquetesPredeterminados;
import com.tallerwebi.dominio.ServicioValidarSeleccionPaquete;
import com.tallerwebi.dominio.TipoPaquete;
import com.tallerwebi.dominio.excepcion.PaqueteNoEncontrado;
import com.tallerwebi.dominio.excepcion.SeleccionInvalida;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorConsultarPaquetesPredeterminadosTest {

  private ServicioConsultarPaquetesPredeterminados servicio;
  private ServicioValidarSeleccionPaquete servicioValidacion;
  private ControladorConsultarPaquetesPredeterminados controlador;

  @BeforeEach
  public void init() {
    servicio = mock(ServicioConsultarPaquetesPredeterminados.class);
    servicioValidacion = mock(ServicioValidarSeleccionPaquete.class);
    controlador = new ControladorConsultarPaquetesPredeterminados(servicio, servicioValidacion);
  }

  @Test
  public void muestraElDetalleDeUnPaqueteSegunSuId() {
    // Preparación
    Salon salon = new Salon("Salón Los Álamos", 50000.0);

    Paquete paqueteBasico = new Paquete(
      1L,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      salon,
      "Catering Don José",
      List.of("DJ"),
      80000.0
    );

    when(servicio.consultarPorId(1L)).thenReturn(paqueteBasico);

    // Ejecución
    ModelAndView modelAndView = controlador.mostrarDetalle(1L);

    // Validación
    assertEquals("paquete-detalle", modelAndView.getViewName());
    assertEquals(paqueteBasico, modelAndView.getModel().get("paquete"));
  }

  @Test
  public void muestraLaVistaSinPaqueteCuandoElIdNoExiste() {
    // Preparación
    when(servicio.consultarPorId(99L)).thenThrow(new PaqueteNoEncontrado());

    // Ejecución
    ModelAndView modelAndView = controlador.mostrarDetalle(99L);

    // Validación
    assertEquals("paquete-detalle", modelAndView.getViewName());
    assertNull(modelAndView.getModel().get("paquete"));
  }

  @Test
  public void muestraElDetalleConFechasOcupadasCapacidadYTiposDeEvento() {
    // Preparación
    Salon salon = new Salon("Salón Los Álamos", 50000.0);
    salon.setCapacidad(50);

    Paquete paqueteBasico = new Paquete(
      1L,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      salon,
      "Catering Don José",
      List.of("DJ"),
      80000.0
    );
    LocalDate fechaOcupada = LocalDate.now().plusDays(10);

    when(servicio.consultarPorId(1L)).thenReturn(paqueteBasico);
    when(servicioValidacion.obtenerFechasOcupadas(1L)).thenReturn(List.of(fechaOcupada));

    // Ejecución
    ModelAndView modelAndView = controlador.mostrarDetalle(1L);

    // Validación
    assertEquals(50, modelAndView.getModel().get("capacidad"));
    assertEquals(List.of(fechaOcupada.toString()), modelAndView.getModel().get("fechasOcupadas"));
    assertArrayEquals(
      TipoPaquete.values(),
      (TipoPaquete[]) modelAndView.getModel().get("tiposEvento")
    );
  }

  @Test
  public void muestraElErrorYConservaLaSeleccionCuandoLaValidacionFalla() {
    // Preparación
    Salon salon = new Salon("Salón Los Álamos", 50000.0);
    salon.setCapacidad(50);
    Paquete paquete = new Paquete(
      1L,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      salon,
      "Catering Don José",
      List.of("DJ"),
      80000.0
    );
    LocalDate fecha = LocalDate.now().plusDays(10);

    when(servicio.consultarPorId(1L)).thenReturn(paquete);
    doThrow(new SeleccionInvalida("Esa fecha está ocupada, seleccioná otra."))
      .when(servicioValidacion)
      .validar(1L, TipoPaquete.CUMPLEANIOS, fecha, 20);

    // Ejecución
    ModelAndView modelAndView = controlador.reservar(1L, TipoPaquete.CUMPLEANIOS, fecha, 20);

    // Validación
    assertEquals("paquete-detalle", modelAndView.getViewName());
    assertEquals("Esa fecha está ocupada, seleccioná otra.", modelAndView.getModel().get("error"));
    assertEquals(paquete, modelAndView.getModel().get("paquete"));
    assertEquals(TipoPaquete.CUMPLEANIOS, modelAndView.getModel().get("tipoSeleccionado"));
    assertEquals(fecha.toString(), modelAndView.getModel().get("fechaSeleccionada"));
    assertEquals(20, modelAndView.getModel().get("invitadosSeleccionados"));
  }

  @Test
  public void muestraMensajeDeExitoCuandoLaSeleccionEsValida() {
    // Preparación
    Salon salon = new Salon("Salón Los Álamos", 50000.0);
    salon.setCapacidad(50);
    Paquete paquete = new Paquete(
      1L,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      salon,
      "Catering Don José",
      List.of("DJ"),
      80000.0
    );
    LocalDate fecha = LocalDate.now().plusDays(10);

    when(servicio.consultarPorId(1L)).thenReturn(paquete);

    // Ejecución
    ModelAndView modelAndView = controlador.reservar(1L, TipoPaquete.CUMPLEANIOS, fecha, 20);

    // Validación
    assertEquals("paquete-detalle", modelAndView.getViewName());
    assertNull(modelAndView.getModel().get("error"));
    assertNotNull(modelAndView.getModel().get("mensajeExito"));
  }
}
