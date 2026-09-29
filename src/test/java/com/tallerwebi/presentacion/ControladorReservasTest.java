package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Reserva;
import com.tallerwebi.dominio.ServicioReserva;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorReservasTest {

  private ServicioReserva servicioReservaMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private ControladorReservas controladorReservas;

  @BeforeEach
  public void init() {
    servicioReservaMock = mock(ServicioReserva.class);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    controladorReservas = new ControladorReservas(servicioReservaMock);
  }

  @Test
  public void deberiaMostrarLasReservasDelUsuarioLogueado() {
    Usuario usuarioLogueado = new Usuario();
    List<Reserva> reservasEsperadas = List.of(new Reserva(), new Reserva());

    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioLogueado);
    when(servicioReservaMock.obtenerReservasDe(usuarioLogueado)).thenReturn(reservasEsperadas);

    ModelAndView mav = controladorReservas.misReservas(requestMock);

    assertThat(mav.getViewName(), equalTo("reservas"));
    assertThat(mav.getModel().get("reservas"), equalTo(reservasEsperadas));
  }

  @Test
  public void deberiaRedirigirALoginSiNoHayUsuarioLogueado() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView mav = controladorReservas.misReservas(requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }
}
