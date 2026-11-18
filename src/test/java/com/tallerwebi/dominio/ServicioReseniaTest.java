package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.EventoNoFinalizado;
import com.tallerwebi.dominio.excepcion.PuntajeInvalido;
import com.tallerwebi.dominio.excepcion.ReseniaExistente;
import com.tallerwebi.dominio.excepcion.ReservaNoEncontrada;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioReseniaTest {

  private ServicioReserva servicioReservaMock;
  private RepositorioResenia repositorioReseniaMock;
  private ServicioResenia servicioResenia;
  private Usuario usuario;

  @BeforeEach
  public void init() {
    servicioReservaMock = mock(ServicioReserva.class);
    repositorioReseniaMock = mock(RepositorioResenia.class);
    servicioResenia = new ServicioReseniaImpl(servicioReservaMock, repositorioReseniaMock);
    usuario = new Usuario();
  }

  @Test
  void deberiaGuardarLaReseniaDeUnEventoQueYaOcurrio() {
    Reserva reserva = this.dadoQueExisteUnaReservaFinalizada(10L);

    Resenia resenia = servicioResenia.reseniar(10L, usuario, 5, "Excelente");

    verify(repositorioReseniaMock).guardar(resenia);
    assertThat(resenia.getReserva(), is(equalTo(reserva)));
    assertThat(resenia.getPuntaje(), is(equalTo(5)));
    assertThat(resenia.getComentario(), is(equalTo("Excelente")));
    assertThat(resenia.getFecha(), is(equalTo(LocalDate.now())));
  }

  @Test
  void unComentarioVacioDeberiaGuardarseComoNull() {
    this.dadoQueExisteUnaReservaFinalizada(10L);

    Resenia resenia = servicioResenia.reseniar(10L, usuario, 4, "   ");

    assertThat(resenia.getComentario(), is(nullValue()));
  }

  @Test
  void noDeberiaReseniarUnEventoQueTodaviaNoOcurrio() {
    Reserva reserva = this.dadoQueExisteUnaReservaFinalizada(10L);
    reserva.getEvento().setFecha(LocalDate.now().plusDays(5));

    assertThrows(
      EventoNoFinalizado.class,
      () -> servicioResenia.reseniar(10L, usuario, 5, "Excelente")
    );
    verify(repositorioReseniaMock, never()).guardar(any());
  }

  @Test
  void noDeberiaReseniarConUnPuntajeFueraDeRango() {
    this.dadoQueExisteUnaReservaFinalizada(10L);

    assertThrows(PuntajeInvalido.class, () -> servicioResenia.reseniar(10L, usuario, 0, null));
    assertThrows(PuntajeInvalido.class, () -> servicioResenia.reseniar(10L, usuario, 6, null));
    assertThrows(PuntajeInvalido.class, () -> servicioResenia.reseniar(10L, usuario, null, null));
    verify(repositorioReseniaMock, never()).guardar(any());
  }

  @Test
  void noDeberiaReseniarDosVecesLaMismaReserva() {
    Reserva reserva = this.dadoQueExisteUnaReservaFinalizada(10L);
    when(repositorioReseniaMock.buscarPorReserva(10L)).thenReturn(new Resenia(reserva, 4, null));

    assertThrows(
      ReseniaExistente.class,
      () -> servicioResenia.reseniar(10L, usuario, 5, "Otra vez")
    );
    verify(repositorioReseniaMock, never()).guardar(any());
  }

  @Test
  void noDeberiaReseniarUnaReservaDeOtroUsuario() {
    when(servicioReservaMock.obtenerReservaDe(10L, usuario)).thenThrow(new ReservaNoEncontrada());

    assertThrows(
      ReservaNoEncontrada.class,
      () -> servicioResenia.reseniar(10L, usuario, 5, "Excelente")
    );
    verify(repositorioReseniaMock, never()).guardar(any());
  }

  @Test
  void deberiaDevolverLasReseniasDelPaquete() {
    List<Resenia> resenias = List.of(new Resenia(), new Resenia());
    when(repositorioReseniaMock.buscarPorPaquete(1L)).thenReturn(resenias);

    assertThat(servicioResenia.obtenerReseniasDelPaquete(1L), is(equalTo(resenias)));
  }

  @Test
  void elPromedioDeberiaSerLaMediaDeLosPuntajes() {
    List<Resenia> resenias = List.of(new Resenia(null, 4, null), new Resenia(null, 5, null));

    assertThat(servicioResenia.calcularPromedio(resenias), is(closeTo(4.5, 0.001)));
  }

  @Test
  void elPromedioSinReseniasDeberiaSerNull() {
    assertThat(servicioResenia.calcularPromedio(List.of()), is(nullValue()));
  }

  private Reserva dadoQueExisteUnaReservaFinalizada(Long idReserva) {
    Reserva reserva = new Reserva();
    reserva.setId(idReserva);
    reserva.setEvento(new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().minusDays(3), 30));
    reserva.setEstado(EstadoReserva.CONFIRMADA);
    when(servicioReservaMock.obtenerReservaDe(idReserva, usuario)).thenReturn(reserva);
    return reserva;
  }
}
