package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.EventoIncompleto;
import com.tallerwebi.dominio.excepcion.FechaInvalida;
import com.tallerwebi.dominio.excepcion.SalonNoDisponible;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorReservasTest {

  private static final String EVENTO = "eventoPersonalizado";
  private static final String USUARIO = "USUARIO";

  private ServicioReserva servicioReservaMock;
  private ControladorReservas controlador;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  void init() {
    servicioReservaMock = mock(ServicioReserva.class);
    controlador = new ControladorReservas(servicioReservaMock);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
  }

  @Test
  void siNoHayUsuarioLogueadoDeberiaRedirigirAlLogin() {
    ModelAndView mav = controlador.reservar(TipoPago.SENIA, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    verify(servicioReservaMock, never()).reservar(any(), any(), any());
  }

  @Test
  void siNoHayEventoEnSesionDeberiaVolverAlArmadoDelEvento() {
    when(sessionMock.getAttribute(USUARIO)).thenReturn(new Usuario());

    ModelAndView mav = controlador.reservar(TipoPago.SENIA, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/evento-personalizado"));
    verify(servicioReservaMock, never()).reservar(any(), any(), any());
  }

  @Test
  void deberiaReservarYRedirigirAMisReservas() {
    Usuario usuario = new Usuario();
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().plusDays(30), 30);
    when(sessionMock.getAttribute(USUARIO)).thenReturn(usuario);
    when(sessionMock.getAttribute(EVENTO)).thenReturn(evento);

    ModelAndView mav = controlador.reservar(TipoPago.TOTAL, requestMock);

    verify(servicioReservaMock).reservar(usuario, evento, TipoPago.TOTAL);
    verify(sessionMock).removeAttribute(EVENTO);
    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas"));
  }

  @Test
  void siElSalonYaEstaOcupadoDeberiaVolverConElErrorCorrespondiente() {
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().plusDays(30), 30);
    when(sessionMock.getAttribute(USUARIO)).thenReturn(new Usuario());
    when(sessionMock.getAttribute(EVENTO)).thenReturn(evento);
    when(servicioReservaMock.reservar(any(), any(), any())).thenThrow(new SalonNoDisponible());

    ModelAndView mav = controlador.reservar(TipoPago.SENIA, requestMock);

    assertThat(
      mav.getViewName(),
      equalTo("redirect:/evento-personalizado?error=salonNoDisponible")
    );
    verify(sessionMock, never()).removeAttribute(EVENTO);
  }

  @Test
  void siLaFechaEsInvalidaDeberiaVolverConElErrorCorrespondiente() {
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().minusDays(1), 30);
    when(sessionMock.getAttribute(USUARIO)).thenReturn(new Usuario());
    when(sessionMock.getAttribute(EVENTO)).thenReturn(evento);
    when(servicioReservaMock.reservar(any(), any(), any())).thenThrow(new FechaInvalida());

    ModelAndView mav = controlador.reservar(TipoPago.SENIA, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/evento-personalizado?error=fecha"));
  }

  @Test
  void siElEventoEstaIncompletoDeberiaVolverConElErrorCorrespondiente() {
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().plusDays(30), 30);
    when(sessionMock.getAttribute(USUARIO)).thenReturn(new Usuario());
    when(sessionMock.getAttribute(EVENTO)).thenReturn(evento);
    when(servicioReservaMock.reservar(any(), any(), any())).thenThrow(new EventoIncompleto());

    ModelAndView mav = controlador.reservar(TipoPago.SENIA, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/evento-personalizado?error=incompleto"));
  }
}
