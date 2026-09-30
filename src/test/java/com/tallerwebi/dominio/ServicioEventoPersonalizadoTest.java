package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioEventoPersonalizadoTest {

  private RepositorioSalon repositorioSalonMock;
  private RepositorioCatering repositorioCateringMock;
  private RepositorioServicioAdicional repositorioServicioAdicionalMock;
  private ServicioEventoPersonalizado servicioEventoPersonalizado;

  @BeforeEach
  public void init() {
    repositorioSalonMock = mock(RepositorioSalon.class);
    repositorioCateringMock = mock(RepositorioCatering.class);
    repositorioServicioAdicionalMock = mock(RepositorioServicioAdicional.class);
    servicioEventoPersonalizado =
      new ServicioEventoPersonalizadoImpl(
        repositorioSalonMock,
        repositorioCateringMock,
        repositorioServicioAdicionalMock
      );
  }

  @Test
  void deberiaAsignarAlEventoElSalonElegido() {
    Salon salon = new Salon("Salon Los Alamos", 50000.0);
    EventoPersonalizado evento = new EventoPersonalizado(
      TipoPaquete.CUMPLEANIOS,
      LocalDate.now(),
      30
    );

    when(repositorioSalonMock.buscarPorId(1L)).thenReturn(salon);

    servicioEventoPersonalizado.elegirSalon(evento, 1L);

    assertThat(evento.getSalon(), equalTo(salon));
  }

  @Test
  void deberiaAsignarAlEventoElCateringElegido() {
    Catering catering = new Catering("Catering Completo", 5000.0);
    EventoPersonalizado evento = new EventoPersonalizado(
      TipoPaquete.CUMPLEANIOS,
      LocalDate.now(),
      30
    );

    when(repositorioCateringMock.buscarPorId(1L)).thenReturn(catering);

    servicioEventoPersonalizado.elegirCatering(evento, 1L);

    assertThat(evento.getCatering(), equalTo(catering));
  }

  @Test
  void deberiaQuitarUnservicioAdicionalConUnId() {
    ServicioAdicional cotillon = new ServicioAdicional("Cotillon", 500.0, true);
    cotillon.setId(5L);

    ServicioAdicional dj = new ServicioAdicional("DJ", 40000.0, false);
    dj.setId(6L);

    EventoPersonalizado evento = new EventoPersonalizado(
      TipoPaquete.CUMPLEANIOS,
      LocalDate.now(),
      30
    );

    evento.agregarServicio(cotillon);
    evento.agregarServicio(dj);

    servicioEventoPersonalizado.quitarServicio(evento, 5L);

    assertThat(evento.getServiciosAdicionales(), contains(dj));
  }

  @Test
  void deberiaAgregarAlEventoElServicioAdicionalElegido() {
    ServicioAdicional dj = new ServicioAdicional("DJ", 40000.0, false);
    EventoPersonalizado evento = new EventoPersonalizado(
      TipoPaquete.CUMPLEANIOS,
      LocalDate.now(),
      30
    );

    when(repositorioServicioAdicionalMock.buscarPorId(6L)).thenReturn(dj);

    servicioEventoPersonalizado.agregarServicio(evento, 6L);

    assertThat(evento.getServiciosAdicionales(), contains(dj));
  }
}
