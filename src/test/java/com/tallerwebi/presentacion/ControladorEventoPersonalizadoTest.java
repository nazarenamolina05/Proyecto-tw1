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
import com.tallerwebi.dominio.excepcion.PaqueteNoEncontrado;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.servlet.ModelAndView;

public class ControladorEventoPersonalizadoTest {

  private static final String EVENTO = "eventoPersonalizado";
  private static final String USUARIO = "USUARIO";

  private ServicioEvento servicio;
  private ControladorEventoPersonalizado controlador;
  private HttpSession sesion;
  private Evento evento;
  private Usuario user;
  private ServicioSugerenciaServicios servicioSugerenciaServicios;
  private ServicioConsultarPaquetesPredeterminados servicioPaquetes;
  private ServicioDisponibilidadSalon servicioDisponibilidadSalon;

  @BeforeEach
  public void init() {
    servicio = mock(ServicioEvento.class);
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
    evento = mock(Evento.class);
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
  public void conFechaInvalidaPeroConSalonDelPaqueteMuestraSoloEseSalon() {
    Salon salonDelPaquete = mock(Salon.class);
    ServiciosClasificados clasificadosMock = mock(ServiciosClasificados.class);

    when(sesion.getAttribute(EVENTO)).thenReturn(evento);
    when(evento.getFecha()).thenReturn(null);
    when(evento.getSalon()).thenReturn(salonDelPaquete);
    when(evento.getServiciosAdicionales()).thenReturn(new ArrayList<>());
    when(servicioDisponibilidadSalon.obtenerSalonesDisponibles(null, null))
      .thenThrow(new FechaInvalida());

    when(servicioSugerenciaServicios.clasificar(evento)).thenReturn(clasificadosMock);
    when(clasificadosMock.getSugeridos()).thenReturn(new ArrayList<>());
    when(clasificadosMock.getRestantes()).thenReturn(new ArrayList<>());

    ModelAndView mav = controlador.verEventoPersonalizado("predefinidos", sesion);

    assertThat(mav.getModel().get("salones"), equalTo(List.of(salonDelPaquete)));
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

  @Test
  public void conEventoDePaqueteCargaPrecioBaseYAdicionalEnElModelo() {
    ServiciosClasificados clasificadosMock = mock(ServiciosClasificados.class);
    when(sesion.getAttribute(EVENTO)).thenReturn(evento);
    when(evento.getFecha()).thenReturn(LocalDate.now().plusDays(10));
    when(evento.getServiciosAdicionales()).thenReturn(new ArrayList<>());
    when(evento.calcularPrecioBase()).thenReturn(165000.0);
    when(evento.calcularAdicional()).thenReturn(30000.0);

    when(servicioSugerenciaServicios.clasificar(evento)).thenReturn(clasificadosMock);
    when(clasificadosMock.getSugeridos()).thenReturn(new ArrayList<>());
    when(clasificadosMock.getRestantes()).thenReturn(new ArrayList<>());

    ModelAndView mav = controlador.verEventoPersonalizado("predefinidos", sesion);

    assertThat(mav.getModel().get("precioBase"), equalTo(165000.0));
    assertThat(mav.getModel().get("adicional"), equalTo(30000.0));
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

    verify(evento).setTipoPaquete(TipoPaquete.CASAMIENTO);
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

  @Test
  public void modificarUnPaqueteGuardaEnSesionUnEventoConLoQueIncluyeElPaquete() {
    Salon salon = new Salon("Salon Los Alamos", 50000.0);
    salon.setCapacidad(50);
    Catering catering = new Catering("Catering Basico", 1500.0);
    Paquete paquete = new Paquete(
      1L,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      salon,
      catering,
      new ArrayList<>(),
      165000.0
    );
    when(servicioPaquetes.consultarPorId(1L)).thenReturn(paquete);
    when(sesion.getAttribute(EVENTO)).thenReturn(null);

    ModelAndView mav = controlador.modificarPaquete(1L, sesion);

    ArgumentCaptor<Evento> captor = ArgumentCaptor.forClass(Evento.class);
    verify(sesion).setAttribute(eq(EVENTO), captor.capture());
    Evento cargado = captor.getValue();
    assertThat(cargado.getPaqueteOrigen(), sameInstance(paquete));
    assertThat(cargado.getSalon(), sameInstance(salon));
    assertThat(cargado.getCatering(), sameInstance(catering));
    assertThat(cargado.getCantidadInvitados(), equalTo(50));
    assertThat(mav.getViewName(), startsWith("redirect:/evento-personalizado"));
  }

  @Test
  public void modificarUnPaqueteInexistenteVuelveAlHomeSinTocarLaSesion() {
    when(servicioPaquetes.consultarPorId(99L)).thenThrow(new PaqueteNoEncontrado());

    ModelAndView mav = controlador.modificarPaquete(99L, sesion);

    verify(sesion, never()).setAttribute(eq(EVENTO), any());
    assertThat(mav.getViewName(), equalTo("redirect:/home-eventos"));
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
}
