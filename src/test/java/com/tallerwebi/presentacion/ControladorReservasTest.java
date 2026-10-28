package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.EventoIncompleto;
import com.tallerwebi.dominio.excepcion.FechaInvalida;
import com.tallerwebi.dominio.excepcion.ReservaNoEncontrada;
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

  @Test
  void alVerLaReservaSinUsuarioDeberiaRedirigirAlLogin() {
    ModelAndView mav = controlador.verResumenDeReserva(requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  void alVerLaReservaSinEventoEnSesionDeberiaVolverAlArmadoDelEvento() {
    when(sessionMock.getAttribute(USUARIO)).thenReturn(new Usuario());

    ModelAndView mav = controlador.verResumenDeReserva(requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/evento-personalizado"));
  }

  @Test
  void alVerLaReservaConEventoIncompletoDeberiaVolverConElError() {
    when(sessionMock.getAttribute(USUARIO)).thenReturn(new Usuario());
    when(sessionMock.getAttribute(EVENTO))
      .thenReturn(new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().plusDays(30), 30));

    ModelAndView mav = controlador.verResumenDeReserva(requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/evento-personalizado?error=incompleto"));
  }

  @Test
  void alVerLaReservaConEventoCompletoDeberiaMostrarElResumenConLaSenia() {
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().plusDays(30), 30);
    evento.setSalon(new Salon("Salon Los Alamos", 50000.0));
    evento.setCatering(new Catering("Catering Basico", 5000.0));
    when(sessionMock.getAttribute(USUARIO)).thenReturn(new Usuario());
    when(sessionMock.getAttribute(EVENTO)).thenReturn(evento);

    ModelAndView mav = controlador.verResumenDeReserva(requestMock);

    assertThat(mav.getViewName(), equalTo("reservar"));
    assertThat(mav.getModel().get("evento"), equalTo(evento));
    assertThat((Double) mav.getModel().get("presupuesto"), is(closeTo(200000.0, 0.01)));
    assertThat((Double) mav.getModel().get("montoSenia"), is(closeTo(60000.0, 0.01)));
  }

  @Test
  void siNoHayUsuarioAlReservarDeberiaGuardarElEventoComoDestinoTrasElLogin() {
    controlador.reservar(TipoPago.SENIA, requestMock);

    verify(sessionMock).setAttribute(ControladorLogin.DESTINO_POST_LOGIN, "/evento-personalizado");
  }

  @Test
  void siNoHayUsuarioAlVerLaReservaDeberiaGuardarElEventoComoDestinoTrasElLogin() {
    controlador.verResumenDeReserva(requestMock);

    verify(sessionMock).setAttribute(ControladorLogin.DESTINO_POST_LOGIN, "/evento-personalizado");
  }

  @Test
  void alVerElDetalleSinUsuarioDeberiaRedirigirAlLogin() {
    ModelAndView mav = controlador.verDetalleDeReserva(10L, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    verify(servicioReservaMock, never()).obtenerReservaDe(any(), any());
  }

  @Test
  void alVerElDetalleDeUnaReservaPropiaDeberiaMostrarLaVistaConLaReserva() {
    Usuario usuario = new Usuario();
    Reserva reserva = new Reserva();
    when(sessionMock.getAttribute(USUARIO)).thenReturn(usuario);
    when(servicioReservaMock.obtenerReservaDe(10L, usuario)).thenReturn(reserva);

    ModelAndView mav = controlador.verDetalleDeReserva(10L, requestMock);

    assertThat(mav.getViewName(), equalTo("detalle-reserva"));
    assertThat(mav.getModel().get("reserva"), equalTo(reserva));
  }

  @Test
  void alVerElDetalleDeUnaReservaInexistenteODeOtroUsuarioDeberiaVolverAMisReservas() {
    Usuario usuario = new Usuario();
    when(sessionMock.getAttribute(USUARIO)).thenReturn(usuario);
    when(servicioReservaMock.obtenerReservaDe(10L, usuario)).thenThrow(new ReservaNoEncontrada());

    ModelAndView mav = controlador.verDetalleDeReserva(10L, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas"));
  }
}
