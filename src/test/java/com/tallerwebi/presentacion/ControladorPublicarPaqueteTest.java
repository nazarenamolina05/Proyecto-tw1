package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioPublicarPaquete;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.EventoNoFinalizado;
import com.tallerwebi.dominio.excepcion.NombrePaqueteInvalido;
import com.tallerwebi.dominio.excepcion.ReservaNoEncontrada;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorPublicarPaqueteTest {

  private static final String USUARIO = "USUARIO";

  private ServicioPublicarPaquete servicioMock;
  private ControladorPublicarPaquete controlador;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  void init() {
    servicioMock = mock(ServicioPublicarPaquete.class);
    controlador = new ControladorPublicarPaquete(servicioMock);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
  }

  @Test
  void siNoHayUsuarioLogueadoDeberiaRedirigirAlLogin() {
    ModelAndView mav = controlador.publicar(10L, "Mi cumple", requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    verify(servicioMock, never()).publicar(any(), any(), any());
  }

  @Test
  void deberiaPublicarYRedirigirALosPaquetes() {
    Usuario usuario = new Usuario();
    when(sessionMock.getAttribute(USUARIO)).thenReturn(usuario);

    ModelAndView mav = controlador.publicar(10L, "Mi cumple", requestMock);

    verify(servicioMock).publicar(10L, usuario, "Mi cumple");
    assertThat(mav.getViewName(), equalTo("redirect:/paquetes"));
  }

  @Test
  void siLaReservaNoEsDelUsuarioDeberiaVolverAMisReservas() {
    when(sessionMock.getAttribute(USUARIO)).thenReturn(new Usuario());
    when(servicioMock.publicar(any(), any(), any())).thenThrow(new ReservaNoEncontrada());

    ModelAndView mav = controlador.publicar(10L, "Mi cumple", requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas"));
  }

  @Test
  void siElEventoNoOcurrioDeberiaVolverAlDetalleConError() {
    when(sessionMock.getAttribute(USUARIO)).thenReturn(new Usuario());
    when(servicioMock.publicar(any(), any(), any())).thenThrow(new EventoNoFinalizado());

    ModelAndView mav = controlador.publicar(10L, "Mi cumple", requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas/10?error=noFinalizado"));
  }

  @Test
  void siElNombreEsInvalidoDeberiaVolverAlDetalleConError() {
    when(sessionMock.getAttribute(USUARIO)).thenReturn(new Usuario());
    when(servicioMock.publicar(any(), any(), any())).thenThrow(new NombrePaqueteInvalido());

    ModelAndView mav = controlador.publicar(10L, "  ", requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas/10?error=nombre"));
  }
}
