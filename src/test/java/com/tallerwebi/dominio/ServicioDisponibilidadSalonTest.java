package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.FechaInvalida;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
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

  @Test
  void dadoUnMesFuturoSinReservasTodosLosDiasEstanDisponibles() {
    YearMonth mes = YearMonth.now().plusMonths(1);
    conSalones(unSalon(1L, "Norte"), unSalon(2L, "Sur"));

    Map<LocalDate, Boolean> calendario = servicioDisponibilidadSalon.obtenerDisponibilidadDelMes(
      mes
    );

    assertThat(calendario.size(), equalTo(mes.lengthOfMonth()));
    assertThat(calendario.values(), everyItem(is(true)));
  }

  @Test
  void dadoQueElUnicoSalonEstaOcupadoUnDiaEseDiaNoEstaDisponible() {
    YearMonth mes = YearMonth.now().plusMonths(1);
    LocalDate diaOcupado = mes.atDay(10);
    Salon salon = unSalon(1L, "Norte");
    conSalones(salon);
    conReservasDelMes(mes, unaReserva(salon, diaOcupado, EstadoReserva.CONFIRMADA));

    Map<LocalDate, Boolean> calendario = servicioDisponibilidadSalon.obtenerDisponibilidadDelMes(
      mes
    );

    assertThat(calendario.get(diaOcupado), is(false));
    assertThat(calendario.get(mes.atDay(11)), is(true));
  }

  @Test
  void dadoQueQuedaUnSalonLibreElDiaSigueDisponible() {
    YearMonth mes = YearMonth.now().plusMonths(1);
    LocalDate dia = mes.atDay(10);
    Salon norte = unSalon(1L, "Norte");
    Salon sur = unSalon(2L, "Sur");
    conSalones(norte, sur);
    conReservasDelMes(mes, unaReserva(norte, dia, EstadoReserva.CONFIRMADA));

    Map<LocalDate, Boolean> calendario = servicioDisponibilidadSalon.obtenerDisponibilidadDelMes(
      mes
    );

    assertThat(calendario.get(dia), is(true));
  }

  @Test
  void dadoQueTodosLosSalonesEstanOcupadosElMismoDiaEseDiaNoEstaDisponible() {
    YearMonth mes = YearMonth.now().plusMonths(1);
    LocalDate dia = mes.atDay(10);
    Salon norte = unSalon(1L, "Norte");
    Salon sur = unSalon(2L, "Sur");
    conSalones(norte, sur);
    conReservasDelMes(
      mes,
      unaReserva(norte, dia, EstadoReserva.CONFIRMADA),
      unaReserva(sur, dia, EstadoReserva.SENIADA)
    );

    Map<LocalDate, Boolean> calendario = servicioDisponibilidadSalon.obtenerDisponibilidadDelMes(
      mes
    );

    assertThat(calendario.get(dia), is(false));
  }

  @Test
  void dadoQueUnMismoSalonTieneDosReservasElDiaSigueDisponibleSiHayOtroLibre() {
    YearMonth mes = YearMonth.now().plusMonths(1);
    LocalDate dia = mes.atDay(10);
    Salon norte = unSalon(1L, "Norte");
    Salon sur = unSalon(2L, "Sur");
    conSalones(norte, sur);
    conReservasDelMes(
      mes,
      unaReserva(norte, dia, EstadoReserva.SENIADA),
      unaReserva(norte, dia, EstadoReserva.CONFIRMADA)
    );

    Map<LocalDate, Boolean> calendario = servicioDisponibilidadSalon.obtenerDisponibilidadDelMes(
      mes
    );

    assertThat(calendario.get(dia), is(true));
  }

  @Test
  void dadoUnaReservaCanceladaElDiaSigueDisponible() {
    YearMonth mes = YearMonth.now().plusMonths(1);
    LocalDate dia = mes.atDay(10);
    Salon salon = unSalon(1L, "Norte");
    conSalones(salon);
    conReservasDelMes(mes, unaReserva(salon, dia, EstadoReserva.CANCELADA));

    Map<LocalDate, Boolean> calendario = servicioDisponibilidadSalon.obtenerDisponibilidadDelMes(
      mes
    );

    assertThat(calendario.get(dia), is(true));
  }

  @Test
  void dadoElMesActualHoyYLosDiasPasadosNoEstanDisponibles() {
    LocalDate hoy = LocalDate.now();
    YearMonth mes = YearMonth.from(hoy);
    conSalones(unSalon(1L, "Norte"));

    Map<LocalDate, Boolean> calendario = servicioDisponibilidadSalon.obtenerDisponibilidadDelMes(
      mes
    );

    assertThat(calendario.get(hoy), is(false));
    calendario.forEach((fecha, disponible) -> assertThat(disponible, equalTo(fecha.isAfter(hoy))));
  }

  @Test
  void dadoUnMesPasadoNingunDiaEstaDisponibleYNoHayError() {
    YearMonth mes = YearMonth.now().minusMonths(1);
    conSalones(unSalon(1L, "Norte"));

    Map<LocalDate, Boolean> calendario = servicioDisponibilidadSalon.obtenerDisponibilidadDelMes(
      mes
    );

    assertThat(calendario.size(), equalTo(mes.lengthOfMonth()));
    assertThat(calendario.values(), everyItem(is(false)));
  }

  @Test
  void dadoQueNoHaySalonesCargadosNingunDiaEstaDisponible() {
    YearMonth mes = YearMonth.now().plusMonths(1);
    conSalones();

    Map<LocalDate, Boolean> calendario = servicioDisponibilidadSalon.obtenerDisponibilidadDelMes(
      mes
    );

    assertThat(calendario.values(), everyItem(is(false)));
  }

  @Test
  void elCalendarioConsultaLasReservasDelMesEnUnaSolaLlamada() {
    YearMonth mes = YearMonth.now().plusMonths(1);
    conSalones(unSalon(1L, "Norte"));

    servicioDisponibilidadSalon.obtenerDisponibilidadDelMes(mes);

    verify(repositorioReserva).buscarEntreFechas(mes.atDay(1), mes.atEndOfMonth());
    verifyNoMoreInteractions(repositorioReserva);
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

  private void conReservasDelMes(YearMonth mes, Reserva... reservas) {
    conReservas(mes.atDay(1), mes.atEndOfMonth(), reservas);
  }
}
