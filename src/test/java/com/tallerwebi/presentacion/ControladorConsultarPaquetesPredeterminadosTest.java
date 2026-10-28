package com.tallerwebi.presentacion;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.PaqueteNoEncontrado;
import com.tallerwebi.dominio.excepcion.SeleccionInvalida;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

public class ControladorConsultarPaquetesPredeterminadosTest {

  private ServicioConsultarPaquetesPredeterminados servicio;
  private ServicioValidarSeleccionPaquete servicioValidacion;
  private ControladorConsultarPaquetesPredeterminados controlador;
  private ServicioEvento servicioEvento;
  private MockHttpSession session;

  @BeforeEach
  public void init() {
    servicio = mock(ServicioConsultarPaquetesPredeterminados.class);
    servicioValidacion = mock(ServicioValidarSeleccionPaquete.class);
    servicioEvento = mock(ServicioEvento.class);
    session = new MockHttpSession();
    controlador =
      new ControladorConsultarPaquetesPredeterminados(servicio, servicioValidacion, servicioEvento);
  }

  @Test
  public void muestraElDetalleDeUnPaqueteSegunSuId() {
    // Preparación
    Salon salon = new Salon("Salón Los Álamos", 50000.0);

    Paquete paqueteBasico = crearPaqueteBasico(salon);

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

    Paquete paqueteBasico = crearPaqueteBasico(salon);
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
    Paquete paqueteBasico = crearPaqueteBasico(salon);
    LocalDate fecha = LocalDate.now().plusDays(10);

    when(servicio.consultarPorId(1L)).thenReturn(paqueteBasico);
    doThrow(new SeleccionInvalida("Esa fecha está ocupada, seleccioná otra."))
      .when(servicioValidacion)
      .validar(1L, TipoPaquete.CUMPLEANIOS, fecha, 20);

    // Ejecución
    ModelAndView modelAndView = controlador.reservar(
      1L,
      TipoPaquete.CUMPLEANIOS,
      fecha,
      20,
      session
    );

    // Validación
    assertEquals("paquete-detalle", modelAndView.getViewName());
    assertEquals("Esa fecha está ocupada, seleccioná otra.", modelAndView.getModel().get("error"));
    assertEquals(paqueteBasico, modelAndView.getModel().get("paquete"));
    assertEquals(TipoPaquete.CUMPLEANIOS, modelAndView.getModel().get("tipoSeleccionado"));
    assertEquals(fecha.toString(), modelAndView.getModel().get("fechaSeleccionada"));
    assertEquals(20, modelAndView.getModel().get("invitadosSeleccionados"));
    assertNull(session.getAttribute(ControladorReservas.EVENTO));
  }

  //  @Test
  //  public void muestraMensajeDeExitoCuandoLaSeleccionEsValida() {
  //    // Preparación
  //    Salon salon = new Salon("Salón Los Álamos", 50000.0);
  //    salon.setCapacidad(50);
  //    Paquete paqueteBasico = crearPaqueteBasico(salon);
  //    LocalDate fecha = LocalDate.now().plusDays(10);
  //
  //    when(servicio.consultarPorId(1L)).thenReturn(paqueteBasico);
  //
  //    // Ejecución
  //    ModelAndView modelAndView = controlador.reservar(1L, TipoPaquete.CUMPLEANIOS, fecha, 20, session);
  //
  //    // Validación
  //    assertEquals("paquete-detalle", modelAndView.getViewName());
  //    assertNull(modelAndView.getModel().get("error"));
  //    assertNotNull(modelAndView.getModel().get("mensajeExito"));
  //  }

  @Test
  public void guardaElEventoEnLaSesionYRedirigeAReservarCuandoLaSeleccionEsValida() {
    // Preparación
    Salon salon = new Salon("Salón Los Álamos", 50000.0);
    salon.setCapacidad(50);
    Paquete paqueteBasico = crearPaqueteBasico(salon);
    LocalDate fecha = LocalDate.now().plusDays(10);
    Evento eventoCreado = new Evento(TipoPaquete.CUMPLEANIOS, fecha, 20);

    session.setAttribute(ControladorReservas.USUARIO, new Usuario());

    when(servicio.consultarPorId(1L)).thenReturn(paqueteBasico);
    when(servicioEvento.crearEvento(TipoPaquete.CUMPLEANIOS, fecha, 20)).thenReturn(eventoCreado);

    // Ejecución
    ModelAndView modelAndView = controlador.reservar(
      1L,
      TipoPaquete.CUMPLEANIOS,
      fecha,
      20,
      session
    );

    // Validación
    assertEquals("redirect:/reservar", modelAndView.getViewName());

    Evento eventoEnSesion = (Evento) session.getAttribute(ControladorReservas.EVENTO);
    assertEquals(salon, eventoEnSesion.getSalon());
    assertEquals(paqueteBasico.getCatering(), eventoEnSesion.getCatering());
    assertEquals(1, eventoEnSesion.getServiciosAdicionales().size());
    assertEquals("redirect:/reservar", modelAndView.getViewName());
  }

  @Test
  public void mandaAlLoginYDejaReservarComoDestinoCuandoNoHayUsuarioLogueado() {
    Salon salon = new Salon("Salón Los Álamos", 50000.0);
    salon.setCapacidad(50);
    Paquete paqueteBasico = crearPaqueteBasico(salon);
    LocalDate fecha = LocalDate.now().plusDays(10);
    Evento eventoCreado = new Evento(TipoPaquete.CUMPLEANIOS, fecha, 20);

    when(servicio.consultarPorId(1L)).thenReturn(paqueteBasico);
    when(servicioEvento.crearEvento(TipoPaquete.CUMPLEANIOS, fecha, 20)).thenReturn(eventoCreado);

    ModelAndView modelAndView = controlador.reservar(
      1L,
      TipoPaquete.CUMPLEANIOS,
      fecha,
      20,
      session
    );

    assertEquals("redirect:/login", modelAndView.getViewName());
    assertEquals("/reservar", session.getAttribute(ControladorLogin.DESTINO_POST_LOGIN));
    assertNotNull(session.getAttribute(ControladorReservas.EVENTO));
  }

  private Paquete crearPaqueteBasico(Salon salon) {
    return new Paquete(
      1L,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      salon,
      new Catering("Catering Basico", 1500.0),
      List.of(new ServicioAdicional("DJ", 40000.0, false)),
      80000.0
    );
  }
}
