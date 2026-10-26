package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.ElementoInexistenteException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.core.Local;

public class ServicioEventoTest {

  private RepositorioSalon repositorioSalonMock;
  private RepositorioCatering repositorioCateringMock;
  private RepositorioServicioAdicional repositorioServicioAdicionalMock;
  private ServicioEvento servicioEvento;

  @BeforeEach
  public void init() {
    repositorioSalonMock = mock(RepositorioSalon.class);
    repositorioCateringMock = mock(RepositorioCatering.class);
    repositorioServicioAdicionalMock = mock(RepositorioServicioAdicional.class);
    servicioEvento =
      new ServicioEventoImpl(
        repositorioSalonMock,
        repositorioCateringMock,
        repositorioServicioAdicionalMock
      );
  }

  @Test
  void deberiaCrearUnEventoConLosDatosIndicados() {
    LocalDate fecha = LocalDate.now();

    Evento evento = servicioEvento.crearEvento(TipoPaquete.CASAMIENTO, fecha, 80);

    assertThat(evento.getTipoPaquete(), equalTo(TipoPaquete.CASAMIENTO));
    assertThat(evento.getFecha(), equalTo(fecha));
    assertThat(evento.getCantidadInvitados(), equalTo(80));
  }

  @Test
  void noDeberiaDuplicarUnServicioAgregadoDosVeces() {
    Evento evento = servicioEvento.crearEvento(TipoPaquete.CASAMIENTO, LocalDate.now(), 80);

    ServicioAdicional dj = new ServicioAdicional("DJ", 40000.0, false);
    when(repositorioServicioAdicionalMock.buscarPorId(6L)).thenReturn(dj);

    servicioEvento.agregarServicio(evento, 6L);
    servicioEvento.agregarServicio(evento, 6L);

    assertThat(evento.getServiciosAdicionales(), hasSize(1));
  }

  @Test
  void deberiaRechazarUnSalonInexistente() {
    Evento evento = servicioEvento.crearEvento(TipoPaquete.CASAMIENTO, LocalDate.now(), 80);
    when(repositorioSalonMock.buscarPorId(99L)).thenReturn(null);

    assertThrows(ElementoInexistenteException.class, () -> servicioEvento.elegirSalon(evento, 99L));
    assertThat(evento.getSalon(), is(nullValue()));
  }

  @Test
  void deberiaRechazarUnCateringInexistente() {
    Evento evento = servicioEvento.crearEvento(TipoPaquete.CASAMIENTO, LocalDate.now(), 80);
    when(repositorioCateringMock.buscarPorId(99L)).thenReturn(null);

    assertThrows(
      ElementoInexistenteException.class,
      () -> servicioEvento.elegirCatering(evento, 99L)
    );
    assertThat(evento.getCatering(), is(nullValue()));
  }

  @Test
  void deberiaRechazarAlAgregarUnServicioInexistente() {
    Evento evento = servicioEvento.crearEvento(TipoPaquete.CASAMIENTO, LocalDate.now(), 80);
    when(repositorioServicioAdicionalMock.buscarPorId(99L)).thenReturn(null);

    assertThrows(
      ElementoInexistenteException.class,
      () -> servicioEvento.agregarServicio(evento, 99L)
    );
    assertThat(evento.getServiciosAdicionales(), is(empty()));
  }

  @Test
  void deberiaRechazarQuitarUnServicioQueElEventoNoTiene() {
    Evento evento = servicioEvento.crearEvento(TipoPaquete.CASAMIENTO, LocalDate.now(), 80);
    when(repositorioServicioAdicionalMock.buscarPorId(99L)).thenReturn(null);

    assertThrows(
      ElementoInexistenteException.class,
      () -> servicioEvento.quitarServicio(evento, 99L)
    );
    assertThat(evento.getServiciosAdicionales(), is(empty()));
  }

  @Test
  void deberiaAsignarAlEventoElSalonElegido() {
    Salon salon = new Salon("Salon Los Alamos", 50000.0);
    Evento evento = new Evento(TipoPaquete.CASAMIENTO, LocalDate.now(), 80);
    when(repositorioSalonMock.buscarPorId(1L)).thenReturn(salon);

    servicioEvento.elegirSalon(evento, 1L);

    assertThat(evento.getSalon(), equalTo(salon));
  }

  @Test
  void deberiaAsignarAlEventoElCateringElegido() {
    Catering catering = new Catering("Catering completo", 5000.0);
    Evento evento = new Evento(TipoPaquete.CASAMIENTO, LocalDate.now(), 80);
    when(repositorioCateringMock.buscarPorId(1L)).thenReturn(catering);

    servicioEvento.elegirCatering(evento, 1L);

    assertThat(evento.getCatering(), equalTo(catering));
  }

  @Test
  void deberiaAgregarAlEventoServicioAdicionalElegido() {
    ServicioAdicional dj = new ServicioAdicional("DJ", 40000.0, false);
    Evento evento = new Evento(TipoPaquete.CASAMIENTO, LocalDate.now(), 80);
    when(repositorioServicioAdicionalMock.buscarPorId(1L)).thenReturn(dj);

    servicioEvento.agregarServicio(evento, 1L);

    assertThat(evento.getServiciosAdicionales(), contains(dj));
  }

  @Test
  void deberiaQuitarSoloElServicioIndicado() {
    ServicioAdicional cotillon = new ServicioAdicional("Cotillon", 500.0, true);
    Evento evento = new Evento(TipoPaquete.CASAMIENTO, LocalDate.now(), 80);
    cotillon.setId(5L);
    ServicioAdicional dj = new ServicioAdicional("DJ", 40000.0, false);
    dj.setId(6L);

    evento.agregarServicio(cotillon);
    evento.agregarServicio(dj);

    servicioEvento.quitarServicio(evento, 5L);

    assertThat(evento.getServiciosAdicionales(), contains(dj));
  }

  @Test
  void alElegirOtroSalonReemplazaAlAnterior() {
    Salon chico = new Salon("Salon Los Alamos", 50000.0);
    Salon grande = new Salon("Salon Gran Bahia", 150000.0);
    Evento evento = new Evento(TipoPaquete.CASAMIENTO, LocalDate.now(), 80);
    when(repositorioSalonMock.buscarPorId(1L)).thenReturn(chico);
    when(repositorioSalonMock.buscarPorId(2L)).thenReturn(grande);

    servicioEvento.elegirSalon(evento, 1L);
    servicioEvento.elegirSalon(evento, 2L);

    assertThat(evento.getSalon(), equalTo(grande));
  }
}
