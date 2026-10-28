package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.EventoNoFinalizado;
import com.tallerwebi.dominio.excepcion.NombrePaqueteInvalido;
import com.tallerwebi.dominio.excepcion.ReservaNoEncontrada;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPublicarPaqueteTest {

  private RepositorioReserva repositorioReservaMock;
  private RepositorioPaquete repositorioPaqueteMock;
  private ServicioPublicarPaquete servicioPublicarPaquete;

  @BeforeEach
  public void init() {
    repositorioReservaMock = mock(RepositorioReserva.class);
    repositorioPaqueteMock = mock(RepositorioPaquete.class);
    servicioPublicarPaquete =
      new ServicioPublicarPaqueteImpl(repositorioReservaMock, repositorioPaqueteMock);
  }

  @Test
  void deberiaGuardarElPaqueteSiLaReservaEsDelUsuarioYElEventoYaOcurrio() {
    Usuario usuario = this.dadoQueExisteUnUsuarioConId(1L);
    Reserva reserva = this.dadoQueExisteUnaReservaDe(usuario, LocalDate.now().minusDays(10));
    when(repositorioReservaMock.buscarPorId(10L)).thenReturn(reserva);

    Paquete paquete = servicioPublicarPaquete.publicar(10L, usuario, "Mi cumple de 30");

    verify(repositorioPaqueteMock).guardar(paquete);
    assertThat(paquete.getNombre(), equalTo("Mi cumple de 30"));
    assertThat(paquete.getPublicador(), equalTo(usuario));
    assertThat(paquete.isPublicado(), is(true));
  }

  @Test
  void noDeberiaPublicarUnaReservaQueNoExiste() {
    Usuario usuario = this.dadoQueExisteUnUsuarioConId(1L);
    when(repositorioReservaMock.buscarPorId(10L)).thenReturn(null);

    assertThrows(
      ReservaNoEncontrada.class,
      () -> servicioPublicarPaquete.publicar(10L, usuario, "Mi cumple de 30")
    );
    verify(repositorioPaqueteMock, never()).guardar(any(Paquete.class));
  }

  @Test
  void noDeberiaPublicarLaReservaDeOtroUsuario() {
    Usuario duenio = this.dadoQueExisteUnUsuarioConId(1L);
    Usuario otroUsuario = this.dadoQueExisteUnUsuarioConId(2L);
    when(repositorioReservaMock.buscarPorId(10L))
      .thenReturn(this.dadoQueExisteUnaReservaDe(duenio, LocalDate.now().minusDays(10)));

    assertThrows(
      ReservaNoEncontrada.class,
      () -> servicioPublicarPaquete.publicar(10L, otroUsuario, "Mi cumple de 30")
    );
    verify(repositorioPaqueteMock, never()).guardar(any(Paquete.class));
  }

  @Test
  void noDeberiaPublicarSiElEventoTodaviaNoOcurrio() {
    Usuario usuario = this.dadoQueExisteUnUsuarioConId(1L);
    when(repositorioReservaMock.buscarPorId(10L))
      .thenReturn(this.dadoQueExisteUnaReservaDe(usuario, LocalDate.now().plusDays(10)));

    assertThrows(
      EventoNoFinalizado.class,
      () -> servicioPublicarPaquete.publicar(10L, usuario, "Mi cumple de 30")
    );
    verify(repositorioPaqueteMock, never()).guardar(any(Paquete.class));
  }

  @Test
  void noDeberiaPublicarUnPaqueteSinNombre() {
    Usuario usuario = this.dadoQueExisteUnUsuarioConId(1L);
    when(repositorioReservaMock.buscarPorId(10L))
      .thenReturn(this.dadoQueExisteUnaReservaDe(usuario, LocalDate.now().minusDays(10)));

    assertThrows(
      NombrePaqueteInvalido.class,
      () -> servicioPublicarPaquete.publicar(10L, usuario, "  ")
    );
    verify(repositorioPaqueteMock, never()).guardar(any(Paquete.class));
  }

  private Usuario dadoQueExisteUnUsuarioConId(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    return usuario;
  }

  private Reserva dadoQueExisteUnaReservaDe(Usuario usuario, LocalDate fechaEvento) {
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, fechaEvento, 40);
    evento.setSalon(new Salon("Salón los Alamos", 50000.0));
    evento.setCatering(new Catering("Catering Basico", 1500.0));
    Reserva reserva = new Reserva();
    reserva.setUsuario(usuario);
    reserva.setEvento(evento);
    return reserva;
  }
}
