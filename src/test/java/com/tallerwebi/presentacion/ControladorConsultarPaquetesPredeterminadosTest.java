package com.tallerwebi.presentacion;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
    Salon salon = new Salon("Salón Los Álamos", 50000.0);
    Paquete paqueteBasico = crearPaqueteBasico(salon);

    when(servicio.consultarPorId(1L)).thenReturn(paqueteBasico);

    ModelAndView modelAndView = controlador.mostrarDetalle(1L);

    assertEquals("paquete-detalle", modelAndView.getViewName());
    assertEquals(paqueteBasico, modelAndView.getModel().get("paquete"));
  }

  // [REFACTOR PAQUETE] paquete-detalle.html recibe los servicios aparte, en "servicios"
  @Test
  public void elDetalleIncluyeLosServiciosDelPaquete() {
    Salon salon = new Salon("Salón Los Álamos", 50000.0);
    Paquete paqueteBasico = crearPaqueteBasico(salon);
    List<ServicioAdicional> servicios = List.of(new ServicioAdicional("DJ", 40000.0, false));

    when(servicio.consultarPorId(1L)).thenReturn(paqueteBasico);
    when(servicio.consultarServiciosDe(paqueteBasico)).thenReturn(servicios);

    ModelAndView modelAndView = controlador.mostrarDetalle(1L);

    assertEquals(servicios, modelAndView.getModel().get("servicios"));
  }

  @Test
  public void muestraLaVistaSinPaqueteCuandoElIdNoExiste() {
    when(servicio.consultarPorId(99L)).thenThrow(new PaqueteNoEncontrado());

    ModelAndView modelAndView = controlador.mostrarDetalle(99L);

    assertEquals("paquete-detalle", modelAndView.getViewName());
    assertNull(modelAndView.getModel().get("paquete"));
  }

  @Test
  public void muestraElDetalleConFechasOcupadasCapacidadYTiposDeEvento() {
    Salon salon = new Salon("Salón Los Álamos", 50000.0);
    salon.setCapacidad(50);

    Paquete paqueteBasico = crearPaqueteBasico(salon);
    LocalDate fechaOcupada = LocalDate.now().plusDays(10);

    when(servicio.consultarPorId(1L)).thenReturn(paqueteBasico);
    when(servicioValidacion.obtenerFechasOcupadas(1L)).thenReturn(List.of(fechaOcupada));

    ModelAndView modelAndView = controlador.mostrarDetalle(1L);

    assertEquals(50, modelAndView.getModel().get("capacidad"));
    assertEquals(List.of(fechaOcupada.toString()), modelAndView.getModel().get("fechasOcupadas"));
    assertArrayEquals(
      TipoPaquete.values(),
      (TipoPaquete[]) modelAndView.getModel().get("tiposEvento")
    );
  }

  @Test
  public void muestraElErrorYConservaLaSeleccionCuandoLaValidacionFalla() {
    Salon salon = new Salon("Salón Los Álamos", 50000.0);
    salon.setCapacidad(50);
    Paquete paqueteBasico = crearPaqueteBasico(salon);
    LocalDate fecha = LocalDate.now().plusDays(10);

    when(servicio.consultarPorId(1L)).thenReturn(paqueteBasico);
    doThrow(new SeleccionInvalida("Esa fecha está ocupada, seleccioná otra."))
      .when(servicioValidacion)
      .validar(1L, TipoPaquete.CUMPLEANIOS, fecha, 20);

    DatosReservaPaquete datos = new DatosReservaPaquete();
    datos.setTipoEvento(TipoPaquete.CUMPLEANIOS);
    datos.setFecha(fecha);
    datos.setCantidadInvitados(20);

    ModelAndView modelAndView = controlador.reservar(1L, datos, session);

    assertEquals("paquete-detalle", modelAndView.getViewName());
    assertEquals("Esa fecha está ocupada, seleccioná otra.", modelAndView.getModel().get("error"));
    assertEquals(paqueteBasico, modelAndView.getModel().get("paquete"));
    assertEquals(TipoPaquete.CUMPLEANIOS, modelAndView.getModel().get("tipoSeleccionado"));
    assertEquals(fecha.toString(), modelAndView.getModel().get("fechaSeleccionada"));
    assertEquals(20, modelAndView.getModel().get("invitadosSeleccionados"));
    assertNull(session.getAttribute(ControladorReservas.EVENTO));
  }

  @Test
  public void guardaElEventoEnLaSesionYRedirigeAReservarCuandoLaSeleccionEsValida() {
    Salon salon = new Salon("Salón Los Álamos", 50000.0);
    salon.setCapacidad(50);
    Paquete paqueteBasico = crearPaqueteBasico(salon);
    LocalDate fecha = LocalDate.now().plusDays(10);
    Evento eventoCreado = paqueteBasico.crearEvento(TipoPaquete.CUMPLEANIOS, fecha, 20);

    session.setAttribute(ControladorReservas.USUARIO, new Usuario());

    when(servicio.consultarPorId(1L)).thenReturn(paqueteBasico);
    // [REFACTOR EVENTO] El servicio crea el evento desde el paquete y copia sus servicios
    // (eso se prueba en ServicioEventoTest).
    when(servicioEvento.crearEventoDesdePaquete(paqueteBasico, TipoPaquete.CUMPLEANIOS, fecha, 20))
      .thenReturn(eventoCreado);

    DatosReservaPaquete datos = new DatosReservaPaquete();
    datos.setTipoEvento(TipoPaquete.CUMPLEANIOS);
    datos.setFecha(fecha);
    datos.setCantidadInvitados(20);

    ModelAndView modelAndView = controlador.reservar(1L, datos, session);

    assertEquals("redirect:/reservar", modelAndView.getViewName());

    verify(servicioEvento)
      .crearEventoDesdePaquete(paqueteBasico, TipoPaquete.CUMPLEANIOS, fecha, 20);
    Evento eventoEnSesion = (Evento) session.getAttribute(ControladorReservas.EVENTO);
    assertSame(eventoCreado, eventoEnSesion);
    assertEquals(salon, eventoEnSesion.getSalon());
    assertEquals(paqueteBasico.getCatering(), eventoEnSesion.getCatering());
  }

  @Test
  public void mandaAlLoginYDejaReservarComoDestinoCuandoNoHayUsuarioLogueado() {
    Salon salon = new Salon("Salón Los Álamos", 50000.0);
    salon.setCapacidad(50);
    Paquete paqueteBasico = crearPaqueteBasico(salon);
    LocalDate fecha = LocalDate.now().plusDays(10);
    Evento eventoCreado = paqueteBasico.crearEvento(TipoPaquete.CUMPLEANIOS, fecha, 20);

    when(servicio.consultarPorId(1L)).thenReturn(paqueteBasico);
    when(servicioEvento.crearEventoDesdePaquete(paqueteBasico, TipoPaquete.CUMPLEANIOS, fecha, 20))
      .thenReturn(eventoCreado);

    DatosReservaPaquete datos = new DatosReservaPaquete();
    datos.setTipoEvento(TipoPaquete.CUMPLEANIOS);
    datos.setFecha(fecha);
    datos.setCantidadInvitados(20);

    ModelAndView modelAndView = controlador.reservar(1L, datos, session);

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
      80000.0
    );
  }
}
