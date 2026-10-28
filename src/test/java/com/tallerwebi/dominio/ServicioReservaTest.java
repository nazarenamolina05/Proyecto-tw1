package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.EventoIncompleto;
import com.tallerwebi.dominio.excepcion.FechaInvalida;
import com.tallerwebi.dominio.excepcion.ReservaNoEncontrada;
import com.tallerwebi.dominio.excepcion.SalonNoDisponible;
import java.time.LocalDate;
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

  @Test
  void deberiaGuardarLaReservaSiElSalonEstaLibre() {
    Evento evento = this.dadoQueTengoUnEventoConSalonParaElDia(1L, LocalDate.now().plusDays(30));
    when(repositorioReservaMock.obtenerFechasOcupadasPorSalon(1L)).thenReturn(List.of());

    Reserva reserva = servicioReserva.reservar(new Usuario(), evento, TipoPago.SENIA);

    verify(repositorioReservaMock).guardar(reserva);
    assertThat(reserva.getEstado(), equalTo(EstadoReserva.SENIADA));
  }

  @Test
  void noDeberiaReservarSiElSalonYaEstaOcupadoEseDia() {
    LocalDate fecha = LocalDate.now().plusDays(30);
    Evento evento = this.dadoQueTengoUnEventoConSalonParaElDia(1L, fecha);
    when(repositorioReservaMock.obtenerFechasOcupadasPorSalon(1L)).thenReturn(List.of(fecha));

    assertThrows(
      SalonNoDisponible.class,
      () -> servicioReserva.reservar(new Usuario(), evento, TipoPago.SENIA)
    );
    verify(repositorioReservaMock, never()).guardar(any(Reserva.class));
  }

  @Test
  void deberiaReservarSiElSalonEstaOcupadoPeroOtroDia() {
    Evento evento = this.dadoQueTengoUnEventoConSalonParaElDia(1L, LocalDate.now().plusDays(30));
    when(repositorioReservaMock.obtenerFechasOcupadasPorSalon(1L))
      .thenReturn(List.of(LocalDate.now().plusDays(31)));

    Reserva reserva = servicioReserva.reservar(new Usuario(), evento, TipoPago.TOTAL);

    verify(repositorioReservaMock).guardar(reserva);
  }

  @Test
  void noDeberiaReservarConUnaFechaPasada() {
    Evento evento = this.dadoQueTengoUnEventoConSalonParaElDia(1L, LocalDate.now().minusDays(1));

    assertThrows(
      FechaInvalida.class,
      () -> servicioReserva.reservar(new Usuario(), evento, TipoPago.SENIA)
    );
    verify(repositorioReservaMock, never()).guardar(any(Reserva.class));
  }

  @Test
  void noDeberiaReservarUnEventoSinFecha() {
    Evento evento = this.dadoQueTengoUnEventoConSalonParaElDia(1L, null);

    assertThrows(
      FechaInvalida.class,
      () -> servicioReserva.reservar(new Usuario(), evento, TipoPago.SENIA)
    );
  }

  @Test
  void noDeberiaReservarUnEventoSinSalon() {
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().plusDays(30), 30);

    assertThrows(
      EventoIncompleto.class,
      () -> servicioReserva.reservar(new Usuario(), evento, TipoPago.SENIA)
    );
    verify(repositorioReservaMock, never()).guardar(any(Reserva.class));
  }

  @Test
  void deberiaObtenerLaReservaPorIdSiPerteneceAlUsuario() {
    Usuario usuario = this.dadoQueExisteUnUsuarioConId(1L);
    Reserva reserva = this.dadoQueExisteUnaReservaDe(usuario);
    when(repositorioReservaMock.buscarPorId(10L)).thenReturn(reserva);

    Reserva obtenida = servicioReserva.obtenerReservaDe(10L, usuario);

    assertThat(obtenida, equalTo(reserva));
  }

  @Test
  void noDeberiaObtenerUnaReservaQueNoExiste() {
    Usuario usuario = this.dadoQueExisteUnUsuarioConId(1L);
    when(repositorioReservaMock.buscarPorId(10L)).thenReturn(null);

    assertThrows(ReservaNoEncontrada.class, () -> servicioReserva.obtenerReservaDe(10L, usuario));
  }

  @Test
  void noDeberiaObtenerLaReservaDeOtroUsuario() {
    Usuario duenio = this.dadoQueExisteUnUsuarioConId(1L);
    Usuario otroUsuario = this.dadoQueExisteUnUsuarioConId(2L);
    when(repositorioReservaMock.buscarPorId(10L))
      .thenReturn(this.dadoQueExisteUnaReservaDe(duenio));

    assertThrows(
      ReservaNoEncontrada.class,
      () -> servicioReserva.obtenerReservaDe(10L, otroUsuario)
    );
  }

  private Usuario dadoQueExisteUnUsuarioConId(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    return usuario;
  }

  private Reserva dadoQueExisteUnaReservaDe(Usuario usuario) {
    Reserva reserva = new Reserva();
    reserva.setUsuario(usuario);
    return reserva;
  }

  private Evento dadoQueTengoUnEventoConSalonParaElDia(Long idSalon, LocalDate fecha) {
    Salon salon = new Salon("Salon Los Alamos", 50000.0);
    salon.setId(idSalon);
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, fecha, 30);
    evento.setSalon(salon);
    return evento;
  }
}
