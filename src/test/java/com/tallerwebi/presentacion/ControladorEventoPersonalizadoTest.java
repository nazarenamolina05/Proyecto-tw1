package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.FechaInvalida;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorEventoPersonalizadoTest {

  private static final String EVENTO = "eventoPersonalizado";
  private static final String USUARIO = "USUARIO";

  private ServicioEventoPersonalizado servicio;
  private ControladorEventoPersonalizado controlador;
  private HttpSession sesion;
  private EventoPersonalizado evento;
  private Usuario user;
  private ServicioSugerenciaServicios servicioSugerenciaServicios;
  private ServicioConsultarPaquetesPredeterminados servicioPaquetes;
  private ServicioDisponibilidadSalon servicioDisponibilidadSalon;

  @BeforeEach
  public void init() {
    servicio = mock(ServicioEventoPersonalizado.class);
    servicioSugerenciaServicios = mock(ServicioSugerenciaServicios.class);
    servicioPaquetes = mock(ServicioConsultarPaquetesPredeterminados.class);
    servicioDisponibilidadSalon = mock(ServicioDisponibilidadSalon.class);
    controlador =
      new ControladorEventoPersonalizado(
        servicio,
        servicioSugerenciaServicios,
        servicioPaquetes,
        servicioDisponibilidadSalon
      );
    sesion = mock(HttpSession.class);
    evento = mock(EventoPersonalizado.class);
    user = mock(Usuario.class);
  }

  // ---------- GET /evento-personalizado ----------

  @Test
  public void sinEventoEnSesionMuestraLaVistaSinDatos() {
    when(sesion.getAttribute(EVENTO)).thenReturn(null);

    ModelAndView mav = controlador.verEventoPersonalizado("predefinidos", sesion);

    assertThat(mav.getViewName(), equalTo("evento-personalizado"));
    assertThat(mav.getModel().get("evento"), nullValue());
  }

  @Test
  public void conEventoSinSeleccionesCargaListasYPresupuesto() {
    LocalDate fecha = LocalDate.now().plusDays(10);
    when(sesion.getAttribute(EVENTO)).thenReturn(evento);
    when(evento.getFecha()).thenReturn(fecha);
    when(evento.getSalon()).thenReturn(null);
    when(evento.getCatering()).thenReturn(null);
    when(evento.getServiciosAdicionales()).thenReturn(new ArrayList<>());

    ServiciosClasificados clasificadosMock = mock(ServiciosClasificados.class);
    when(servicioSugerenciaServicios.clasificar(evento)).thenReturn(clasificadosMock);
    when(clasificadosMock.getSugeridos()).thenReturn(new ArrayList<>());
    when(clasificadosMock.getRestantes()).thenReturn(new ArrayList<>());

    ModelAndView mav = controlador.verEventoPersonalizado("predefinidos", sesion);

    assertThat(mav.getModel().get("evento"), sameInstance(evento));
    assertThat(mav.getModel().get("salonElegidoId"), nullValue());
    assertThat(mav.getModel().get("cateringElegidoId"), nullValue());
    verify(servicioDisponibilidadSalon).obtenerSalonesDisponibles(fecha, fecha);
    verify(servicio).obtenerCaterings();
    //    verify(servicio).obtenerServicioAdicionales();
  }

  @Test
  public void conEventoConFechaCargaSoloLosSalonesDisponiblesDeEsaFecha() {
    LocalDate fecha = LocalDate.now().plusDays(10);
    Salon salonDisponible = mock(Salon.class);
    ServiciosClasificados clasificadosMock = mock(ServiciosClasificados.class);
    when(sesion.getAttribute(EVENTO)).thenReturn(evento);
    when(evento.getFecha()).thenReturn(fecha);
    when(evento.getServiciosAdicionales()).thenReturn(new ArrayList<>());
    when(servicioDisponibilidadSalon.obtenerSalonesDisponibles(fecha, fecha))
      .thenReturn(List.of(salonDisponible));

    when(servicioSugerenciaServicios.clasificar(evento)).thenReturn(clasificadosMock);
    when(clasificadosMock.getSugeridos()).thenReturn(new ArrayList<>());
    when(clasificadosMock.getRestantes()).thenReturn(new ArrayList<>());

    ModelAndView mav = controlador.verEventoPersonalizado("predefinidos", sesion);

    assertThat(mav.getModel().get("salones"), equalTo(List.of(salonDisponible)));
    verify(servicio, never()).obtenerSalones();
  }

  @Test
  public void conFechaInvalidaMuestraUnaListaVaciaYElMensajeDeError() {
    LocalDate ayer = LocalDate.now().minusDays(1);
    ServiciosClasificados clasificadosMock = mock(ServiciosClasificados.class);

    when(sesion.getAttribute(EVENTO)).thenReturn(evento);
    when(evento.getFecha()).thenReturn(ayer);
    when(evento.getServiciosAdicionales()).thenReturn(new ArrayList<>());
    when(servicioDisponibilidadSalon.obtenerSalonesDisponibles(ayer, ayer))
      .thenThrow(new FechaInvalida());

    when(servicioSugerenciaServicios.clasificar(evento)).thenReturn(clasificadosMock);
    when(clasificadosMock.getSugeridos()).thenReturn(new ArrayList<>());
    when(clasificadosMock.getRestantes()).thenReturn(new ArrayList<>());

    ModelAndView mav = controlador.verEventoPersonalizado("predefinidos", sesion);

    assertThat((List<?>) mav.getModel().get("salones"), is(empty()));
    assertThat(mav.getModel().get("errorFecha"), notNullValue());
  }

  @Test
  public void conSalonYCateringElegidosPasaSusIdsALaVista() {
    Salon salon = mock(Salon.class); // ajustá el tipo si tu clase se llama distinto
    Catering catering = mock(Catering.class);
    when(salon.getId()).thenReturn(1L);
    when(catering.getId()).thenReturn(2L);
    when(sesion.getAttribute(EVENTO)).thenReturn(evento);
    when(evento.getSalon()).thenReturn(salon);
    when(evento.getCatering()).thenReturn(catering);
    when(evento.getServiciosAdicionales()).thenReturn(new ArrayList<>());

    ServiciosClasificados clasificadosMock = mock(ServiciosClasificados.class);
    when(servicioSugerenciaServicios.clasificar(evento)).thenReturn(clasificadosMock);
    when(clasificadosMock.getSugeridos()).thenReturn(new ArrayList<>());
    when(clasificadosMock.getRestantes()).thenReturn(new ArrayList<>());

    ModelAndView mav = controlador.verEventoPersonalizado("predefinidos", sesion);

    assertThat(mav.getModel().get("salonElegidoId"), equalTo(1L));
    assertThat(mav.getModel().get("cateringElegidoId"), equalTo(2L));
  }

  // ---------- POST /evento-personalizado ----------

  @Test
  public void sinEventoPrevioCreaUnoNuevoYLoGuardaEnSesion() {
    when(sesion.getAttribute(EVENTO)).thenReturn(null);
    LocalDate fecha = LocalDate.of(2026, 10, 10);
    when(servicio.crearEvento(TipoPaquete.CASAMIENTO, fecha, 30)).thenReturn(evento);

    ModelAndView mav = controlador.guardarDatosDelEvento(TipoPaquete.CASAMIENTO, fecha, 30, sesion);

    verify(sesion).setAttribute(EVENTO, evento);
    assertThat(mav.getViewName(), equalTo("redirect:/evento-personalizado"));
  }

  @Test
  public void conEventoPrevioActualizaSusDatos() {
    when(sesion.getAttribute(EVENTO)).thenReturn(evento);
    LocalDate fecha = LocalDate.of(2026, 10, 10);

    controlador.guardarDatosDelEvento(TipoPaquete.CASAMIENTO, fecha, 50, sesion);

    verify(evento).setTipo(TipoPaquete.CASAMIENTO);
    verify(evento).setFecha(fecha);
    verify(evento).setCantidadInvitados(50);
    verify(sesion, never()).setAttribute(eq(EVENTO), any());
  }

  // ---------- Empezar de cero ----------

  @Test
  public void empezarDeCeroBorraElEventoDeLaSesion() {
    ModelAndView mav = controlador.volverAEmpezar("desde-cero", sesion);

    verify(sesion).removeAttribute(EVENTO);
    assertThat(mav.getViewName(), equalTo("redirect:/evento-personalizado"));
  }

  // ---------- Salón ----------

  @Test
  public void alElegirSalonDelegaAlServicio() {
    when(sesion.getAttribute(EVENTO)).thenReturn(evento);

    ModelAndView mav = controlador.elegirSalon(1L, sesion);

    verify(servicio).elegirSalon(evento, 1L);
    assertThat(mav.getViewName(), startsWith("redirect:/evento-personalizado"));
  }

  @Test
  public void alElegirSalonSinEventoNoLlamaAlServicio() {
    when(sesion.getAttribute(EVENTO)).thenReturn(null);

    controlador.elegirSalon(1L, sesion);

    verify(servicio, never()).elegirSalon(any(), anyLong());
  }

  // ---------- Catering ----------

  @Test
  public void alElegirCateringDelegaAlServicio() {
    when(sesion.getAttribute(EVENTO)).thenReturn(evento);

    controlador.elegirCatering(2L, sesion);

    verify(servicio).elegirCatering(evento, 2L);
  }

  @Test
  public void alElegirCateringSinEventoNoLlamaAlServicio() {
    when(sesion.getAttribute(EVENTO)).thenReturn(null);

    controlador.elegirCatering(2L, sesion);

    verify(servicio, never()).elegirCatering(any(), anyLong());
  }

  // ---------- Servicios adicionales ----------

  @Test
  public void alAgregarServicioDelegaAlServicio() {
    when(sesion.getAttribute(EVENTO)).thenReturn(evento);

    controlador.agregarServicio(3L, sesion);

    verify(servicio).agregarServicio(evento, 3L);
  }

  @Test
  public void alAgregarServicioSinEventoNoLlamaAlServicio() {
    when(sesion.getAttribute(EVENTO)).thenReturn(null);

    controlador.agregarServicio(3L, sesion);

    verify(servicio, never()).agregarServicio(any(), anyLong());
  }

  @Test
  public void alQuitarServicioDelegaAlServicio() {
    when(sesion.getAttribute(EVENTO)).thenReturn(evento);

    controlador.quitarServicio(3L, sesion);

    verify(servicio).quitarServicio(evento, 3L);
  }

  @Test
  public void alQuitarServicioSinEventoNoLlamaAlServicio() {
    when(sesion.getAttribute(EVENTO)).thenReturn(null);

    controlador.quitarServicio(3L, sesion);

    verify(servicio, never()).quitarServicio(any(), anyLong());
  }

  // ---------- Reservar ----------

  @Test
  public void reservarSinEventoVuelveAlEvento() {
    when(sesion.getAttribute(EVENTO)).thenReturn(null);

    ModelAndView mav = controlador.reservarEvento(sesion);

    assertThat(mav.getViewName(), equalTo("redirect:/evento-personalizado"));
  }

  @Test
  public void reservarSinSalonRedirigeConErrorIncompleto() {
    when(sesion.getAttribute(EVENTO)).thenReturn(evento);
    when(evento.getSalon()).thenReturn(null);
    when(evento.getCatering()).thenReturn(mock(Catering.class));
    when(sesion.getAttribute(USUARIO)).thenReturn(user);

    ModelAndView mav = controlador.reservarEvento(sesion);

    assertThat(mav.getViewName(), equalTo("redirect:/evento-personalizado?error=incompleto"));
  }

  @Test
  public void reservarSinCateringRedirigeConErrorIncompleto() {
    when(sesion.getAttribute(EVENTO)).thenReturn(evento);
    when(evento.getSalon()).thenReturn(mock(Salon.class));
    when(evento.getCatering()).thenReturn(null);
    when(sesion.getAttribute(USUARIO)).thenReturn(user);

    ModelAndView mav = controlador.reservarEvento(sesion);

    assertThat(mav.getViewName(), equalTo("redirect:/evento-personalizado?error=incompleto"));
  }

  @Test
  public void alQuererReservarNoDejaPorQueNoExisteUnUsuario() {
    when(sesion.getAttribute(EVENTO)).thenReturn(evento);

    ModelAndView mav = controlador.reservarEvento(sesion);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void reservarConSalonYCateringVuelveAlEvento() {
    when(sesion.getAttribute(EVENTO)).thenReturn(evento);
    when(evento.getSalon()).thenReturn(mock(Salon.class));
    when(evento.getCatering()).thenReturn(mock(Catering.class));
    when(sesion.getAttribute(USUARIO)).thenReturn(user);

    ModelAndView mav = controlador.reservarEvento(sesion);

    assertThat(mav.getViewName(), equalTo("redirect:/evento-personalizado"));
  }
}
