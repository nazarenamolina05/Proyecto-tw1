package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.FechaInvalida;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioDisponibilidadSalonTest {

  private static final LocalDate FECHA = LocalDate.now().plusDays(10);

  private RepositorioSalon repositorioSalon;
  private RepositorioReserva repositorioReserva;
  private ServicioDisponibilidadSalon servicioDisponibilidadSalon;

  @BeforeEach
  public void init() {
    repositorioSalon = mock(RepositorioSalon.class);
    repositorioReserva = mock(RepositorioReserva.class);
    servicioDisponibilidadSalon =
            new ServicioDisponibilidadSalonImpl(repositorioSalon, repositorioReserva);
  }

  @Test
  void deberiaExcluirElSalonOcupadoYDevolverSoloElDisponible() {
    Salon salonOcupado = unSalon(1L, "Norte");
    Salon salonLibre = unSalon(2L, "Sur");
    conSalones(salonOcupado, salonLibre);
    conReservas(FECHA, FECHA, unaReserva(salonOcupado, FECHA, EstadoReserva.CONFIRMADA));

    List<Salon> disponibles = servicioDisponibilidadSalon.obtenerSalonesDisponibles(FECHA, FECHA);

    assertThat(disponibles, contains(salonLibre));
  }

  @Test
  void dadoQueSeIngresoUnaFechaPasadaDeberiaDarError() {
    LocalDate ayer = LocalDate.now().minusDays(1);

    assertThrows(
            FechaInvalida.class,
            () -> servicioDisponibilidadSalon.obtenerSalonesDisponibles(ayer, FECHA)
    );
  }

  @Test
  void dadoQueSeIngresoLaFechaActualDeberiaDarError() {
    LocalDate hoy = LocalDate.now();

    assertThrows(
            FechaInvalida.class,
            () -> servicioDisponibilidadSalon.obtenerSalonesDisponibles(hoy, FECHA)
    );
  }

  @Test
  void dadoQueLaFechaEsNulaDeberiaDarError() {
    assertThrows(
            FechaInvalida.class,
            () -> servicioDisponibilidadSalon.obtenerSalonesDisponibles(null, FECHA)
    );
  }

  @Test
  void deberiaDevolverTodosLosSalonesYaQueElSalonTieneLaReservaCancelada() {
    Salon salonConReservaCancelada = unSalon(1L, "Norte");
    Salon salonLibre = unSalon(2L, "Sur");
    conSalones(salonConReservaCancelada, salonLibre);
    conReservas(FECHA, FECHA, unaReserva(salonConReservaCancelada, FECHA, EstadoReserva.CANCELADA));

    List<Salon> disponibles = servicioDisponibilidadSalon.obtenerSalonesDisponibles(FECHA, FECHA);

    assertThat(disponibles, containsInAnyOrder(salonLibre, salonConReservaCancelada));
  }

  @Test
  void dadoUnaReservaSinEventoNoSeRompeNiBloqueaSalones() {
    Salon salon = unSalon(1L, "Norte");
    conSalones(salon);
    Reserva sinEvento = new Reserva();
    sinEvento.setEstado(EstadoReserva.CONFIRMADA);
    conReservas(FECHA, FECHA, sinEvento);

    List<Salon> disponibles = servicioDisponibilidadSalon.obtenerSalonesDisponibles(FECHA, FECHA);

    assertThat(disponibles, contains(salon));
  }

  @Test
  void dadoUnaReservaConEventoSinSalonNoSeRompeNiBloqueaSalones() {
    Salon salon = unSalon(1L, "Norte");
    conSalones(salon);
    conReservas(FECHA, FECHA, unaReserva(null, FECHA, EstadoReserva.CONFIRMADA));

    List<Salon> disponibles = servicioDisponibilidadSalon.obtenerSalonesDisponibles(FECHA, FECHA);

    assertThat(disponibles, contains(salon));
  }

  private Salon unSalon(Long id, String nombre) {
    Salon salon = new Salon(nombre, 1000.0);
    salon.setId(id);
    return salon;
  }

  private Reserva unaReserva(Salon salon, LocalDate fecha, EstadoReserva estado) {
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, fecha, 50);
    evento.setSalon(salon);

    Reserva reserva = new Reserva();
    reserva.setEvento(evento);
    reserva.setEstado(estado);
    return reserva;
  }

  private void conSalones(Salon... salones) {
    when(repositorioSalon.obtenerTodos()).thenReturn(List.of(salones));
  }

  private void conReservas(LocalDate desde, LocalDate hasta, Reserva... reservas) {
    when(repositorioReserva.buscarEntreFechas(desde, hasta)).thenReturn(List.of(reservas));
  }
}