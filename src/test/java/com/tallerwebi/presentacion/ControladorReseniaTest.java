package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioResenia;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.EventoNoFinalizado;
import com.tallerwebi.dominio.excepcion.PuntajeInvalido;
import com.tallerwebi.dominio.excepcion.ReseniaExistente;
import com.tallerwebi.dominio.excepcion.ReservaNoEncontrada;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorReseniaTest {

  private ServicioResenia servicioReseniaMock;
  private ControladorResenia controlador;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private DatosResenia datos;
  private Usuario usuario;

  @BeforeEach
  void init() {
    servicioReseniaMock = mock(ServicioResenia.class);
    controlador = new ControladorResenia(servicioReseniaMock);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    datos = new DatosResenia();
    datos.setPuntaje(5);
    datos.setComentario("Excelente");
    usuario = new Usuario();
  }

  @Test
  void sinUsuarioLogueadoDeberiaRedirigirAlLogin() {
    ModelAndView mav = controlador.reseniar(10L, datos, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
    verify(servicioReseniaMock, never()).reseniar(any(), any(), any(), any());
  }

  @Test
  void deberiaReseniarYVolverAlDetalleDeLaReserva() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuario);

    ModelAndView mav = controlador.reseniar(10L, datos, requestMock);

    verify(servicioReseniaMock).reseniar(10L, usuario, 5, "Excelente");
    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas/10"));
  }

  @Test
  void siLaReservaNoEsDelUsuarioDeberiaVolverAMisReservas() {
    this.dadoQueElServicioLanza(new ReservaNoEncontrada());

    ModelAndView mav = controlador.reseniar(10L, datos, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas"));
  }

  @Test
  void siElEventoNoFinalizoDeberiaVolverConElError() {
    this.dadoQueElServicioLanza(new EventoNoFinalizado());

    ModelAndView mav = controlador.reseniar(10L, datos, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas/10?error=reseniaNoDisponible"));
  }

  @Test
  void siElPuntajeEsInvalidoDeberiaVolverConElError() {
    this.dadoQueElServicioLanza(new PuntajeInvalido());

    ModelAndView mav = controlador.reseniar(10L, datos, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas/10?error=puntaje"));
  }

  @Test
  void siYaHabiaReseniadoDeberiaVolverConElError() {
    this.dadoQueElServicioLanza(new ReseniaExistente());

    ModelAndView mav = controlador.reseniar(10L, datos, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-reservas/10?error=reseniaExistente"));
  }

  private void dadoQueElServicioLanza(RuntimeException excepcion) {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioReseniaMock.reseniar(any(), any(), any(), any())).thenThrow(excepcion);
  }
}
