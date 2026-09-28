package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioReservaTest {

  private RepositorioReserva repositorioReservaMock;
  private ServicioReserva servicioReserva;

  @BeforeEach
  public void init() {
    repositorioReservaMock = mock(RepositorioReserva.class);
    servicioReserva = new ServicioReservaImpl(repositorioReservaMock);
  }

  @Test
  void deberiaDevolverLasReservasDelUsuario() {
    Usuario usuario = new Usuario();
    List<Reserva> reservasEsperadas = List.of(new Reserva(), new Reserva());

    when(repositorioReservaMock.buscarPorUsuario(usuario)).thenReturn(reservasEsperadas);

    List<Reserva> reservasObtenidas = servicioReserva.obtenerReservasDe(usuario);

    assertThat(reservasObtenidas, equalTo(reservasEsperadas));
  }

  @Test
  void deberiaDevolverUnaListaVaciaSiElUsuarioNoTieneReservas() {
    Usuario usuario = new Usuario();

    when(repositorioReservaMock.buscarPorUsuario(usuario)).thenReturn(List.of());

    List<Reserva> reservasObtenidas = servicioReserva.obtenerReservasDe(usuario);

    assertThat(reservasObtenidas, equalTo(List.of()));
  }
}
