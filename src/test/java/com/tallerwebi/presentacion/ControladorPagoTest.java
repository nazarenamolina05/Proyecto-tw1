package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.EstadoPago;
import com.tallerwebi.dominio.ServicioPago;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorPagoTest {

  private static final String USUARIO = "USUARIO";
  private static final String MIS_RESERVAS_CON_ERROR = "redirect:/mis-reservas?error=pago";

  private ServicioPago servicioPagoMock;
  private ControladorPago controlador;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  void init() {
    servicioPagoMock = mock(ServicioPago.class);
    controlador = new ControladorPago(servicioPagoMock);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
  }

  @Test
  void siNoHayUsuarioAlIniciarElPagoDeberiaRedirigirAlLogin() {
    ModelAndView mav = controlador.iniciarPago(10L, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    verify(servicioPagoMock, never()).iniciarPago(any(), any());
  }

  @Test
  void deberiaRedirigirALaUrlDePagoDeMercadoPago() {
    Usuario usuario = new Usuario();
    when(sessionMock.getAttribute(USUARIO)).thenReturn(usuario);
    when(servicioPagoMock.iniciarPago(10L, usuario)).thenReturn("https://mercadopago.test/pagar");

    ModelAndView mav = controlador.iniciarPago(10L, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:https://mercadopago.test/pagar"));
  }

  @Test
  void siLaReservaNoEsDelUsuarioDeberiaVolverAMisReservas() {
    Usuario usuario = new Usuario();
    when(sessionMock.getAttribute(USUARIO)).thenReturn(usuario);
    when(servicioPagoMock.iniciarPago(10L, usuario)).thenThrow(new ReservaNoEncontrada());

    ModelAndView mav = controlador.iniciarPago(10L, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas"));
  }

  @Test
  void siMercadoPagoNoRespondeDeberiaVolverAMisReservasConError() {
    Usuario usuario = new Usuario();
    when(sessionMock.getAttribute(USUARIO)).thenReturn(usuario);
    when(servicioPagoMock.iniciarPago(10L, usuario))
      .thenThrow(new PasarelaNoDisponible("sin conexion"));

    ModelAndView mav = controlador.iniciarPago(10L, requestMock);

    assertThat(mav.getViewName(), equalTo(MIS_RESERVAS_CON_ERROR));
  }

  @Test
  void alVolverConUnPagoAprobadoDeberiaIrAMisReservasConAvisoDeAprobado() {
    when(servicioPagoMock.confirmarPago(5L, 777L)).thenReturn(EstadoPago.APROBADO);

    ModelAndView mav = controlador.pagoExitoso(5L, 777L);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas?pago=aprobado"));
  }

  @Test
  void alVolverConUnPagoRechazadoDeberiaIrAMisReservasConAvisoDeRechazado() {
    when(servicioPagoMock.confirmarPago(5L, 777L)).thenReturn(EstadoPago.RECHAZADO);

    ModelAndView mav = controlador.pagoExitoso(5L, 777L);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas?pago=rechazado"));
  }

  @Test
  void alVolverConUnPagoEnProcesoDeberiaIrAMisReservasConAvisoDePendiente() {
    when(servicioPagoMock.confirmarPago(5L, 777L)).thenReturn(EstadoPago.PENDIENTE);

    ModelAndView mav = controlador.pagoExitoso(5L, 777L);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas?pago=pendiente"));
  }

  @Test
  void siElPagoNoCoincideConElDeMercadoPagoDeberiaVolverAMisReservasConError() {
    when(servicioPagoMock.confirmarPago(5L, 777L)).thenThrow(new PagoNoCoincide());

    ModelAndView mav = controlador.pagoExitoso(5L, 777L);

    assertThat(mav.getViewName(), equalTo(MIS_RESERVAS_CON_ERROR));
  }

  @Test
  void alVolverDeUnPagoFallidoDeberiaIrAMisReservasConAvisoDeRechazado() {
    assertThat(
      controlador.pagoFallido().getViewName(),
      equalTo("redirect:/mis-reservas?pago=rechazado")
    );
  }

  @Test
  void alVolverDeUnPagoPendienteDeberiaIrAMisReservasConAvisoDePendiente() {
    assertThat(
      controlador.pagoPendiente().getViewName(),
      equalTo("redirect:/mis-reservas?pago=pendiente")
    );
  }

  @Test
  void siNoHayUsuarioAlAbonarDeberiaRedirigirAlLogin() {
    ModelAndView mav = controlador.abonar(10L, 20000.0, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    verify(servicioPagoMock, never()).iniciarAbono(any(), any(), any());
  }

  @Test
  void alAbonarDeberiaRedirigirALaUrlDePagoDeMercadoPago() {
    Usuario usuario = new Usuario();
    when(sessionMock.getAttribute(USUARIO)).thenReturn(usuario);
    when(servicioPagoMock.iniciarAbono(10L, usuario, 20000.0))
      .thenReturn("https://mercadopago.test/pagar");

    ModelAndView mav = controlador.abonar(10L, 20000.0, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:https://mercadopago.test/pagar"));
  }

  @Test
  void siElMontoDelAbonoEsInvalidoDeberiaVolverAMisReservasConErrorDeMonto() {
    Usuario usuario = new Usuario();
    when(sessionMock.getAttribute(USUARIO)).thenReturn(usuario);
    when(servicioPagoMock.iniciarAbono(10L, usuario, 0.0)).thenThrow(new MontoDeAbonoInvalido());

    ModelAndView mav = controlador.abonar(10L, 0.0, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas?error=monto"));
  }

  @Test
  void siLaReservaNoAdmiteAbonosDeberiaVolverAMisReservasConErrorDeAbono() {
    Usuario usuario = new Usuario();
    when(sessionMock.getAttribute(USUARIO)).thenReturn(usuario);
    when(servicioPagoMock.iniciarAbono(10L, usuario, 20000.0)).thenThrow(new AbonoNoPermitido());

    ModelAndView mav = controlador.abonar(10L, 20000.0, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas?error=abono"));
  }

  @Test
  void siLaReservaDelAbonoNoEsDelUsuarioDeberiaVolverAMisReservas() {
    Usuario usuario = new Usuario();
    when(sessionMock.getAttribute(USUARIO)).thenReturn(usuario);
    when(servicioPagoMock.iniciarAbono(10L, usuario, 20000.0)).thenThrow(new ReservaNoEncontrada());

    ModelAndView mav = controlador.abonar(10L, 20000.0, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas"));
  }

  @Test
  void siMercadoPagoNoRespondeAlAbonarDeberiaVolverAMisReservasConError() {
    Usuario usuario = new Usuario();
    when(sessionMock.getAttribute(USUARIO)).thenReturn(usuario);
    when(servicioPagoMock.iniciarAbono(10L, usuario, 20000.0))
      .thenThrow(new PasarelaNoDisponible("sin conexion"));

    ModelAndView mav = controlador.abonar(10L, 20000.0, requestMock);

    assertThat(mav.getViewName(), equalTo(MIS_RESERVAS_CON_ERROR));
  }
}
