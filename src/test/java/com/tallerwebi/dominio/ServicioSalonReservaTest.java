package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import com.tallerwebi.dominio.excepcion.FechaInvalida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioSalonReservaTest {

    private Salon unSalon(Long id, String nombre) {
        Salon salon = new Salon(nombre, 1000.0);
        salon.setId(id);
        return salon;
    }

    private void conSalones(Salon... salones) {
        when(repositorioSalon.obtenerTodos()).thenReturn(List.of(salones));
    }

    private void conOcupaciones(YearMonth mes, SalonReserva... ocupaciones) {
        when(repositorioSalonReserva.obtenerEntreFechas(mes.atDay(1), mes.atEndOfMonth()))
                .thenReturn(List.of(ocupaciones));
    }

    private static final LocalDate FECHA = LocalDate.now().plusDays(10);

    private RepositorioSalon repositorioSalon;
    private RepositorioSalonReserva repositorioSalonReserva;
    private ServicioSalonReserva servicioSalonReserva;

    @BeforeEach
    public void init() {
        repositorioSalon = mock(RepositorioSalon.class);
        repositorioSalonReserva = mock(RepositorioSalonReserva.class);
        servicioSalonReserva = new ServicioSalonReservaImpl(repositorioSalon, repositorioSalonReserva);
    }

    @Test
    void deberiaExcluirElSalonOcupadoYDevolverSoloElDisponible() {
        Salon salonOcupado = unSalon(1L, "Norte");
        Salon salonLibre = unSalon(2L, "Sur");
        when(repositorioSalon.obtenerTodos()).thenReturn(List.of(salonOcupado, salonLibre));
        when(repositorioSalonReserva.obtenerEntreFechas(FECHA, FECHA))
                .thenReturn(List.of(new SalonReserva(salonOcupado, FECHA, EstadoReserva.CONFIRMADA)));

        List<Salon> disponibles = servicioSalonReserva.obtenerSalonesDisponibles(FECHA, FECHA);

        assertThat(disponibles, contains(salonLibre));
    }

    @Test
    void dadoQueSeIngresoUnaFechaPasadaDeberiaDarError() {
        LocalDate ayer = LocalDate.now().minusDays(1);

        assertThrows(FechaInvalida.class, () -> servicioSalonReserva.obtenerSalonesDisponibles(ayer, FECHA));
    }

    @Test
    void dadoQueSeIngresoLaFechaActualDeberiaDarError() {
        LocalDate ayer = LocalDate.now();

        assertThrows(FechaInvalida.class, () -> servicioSalonReserva.obtenerSalonesDisponibles(ayer, FECHA));
    }

    @Test
    void deberiaDevolverTodosLosSalonesYaQueSalonOcupadoTieneLaReservaCancelada() {
        Salon salonOcupado = unSalon(1L, "Norte");
        Salon salonLibre = unSalon(2L, "Sur");
        when(repositorioSalon.obtenerTodos()).thenReturn(List.of(salonOcupado, salonLibre));
        when(repositorioSalonReserva.obtenerEntreFechas(FECHA, FECHA))
                .thenReturn(List.of(new SalonReserva(salonOcupado, FECHA, EstadoReserva.CANCELADA)));

        List<Salon> disponibles = servicioSalonReserva.obtenerSalonesDisponibles(FECHA, FECHA);

        assertThat(disponibles, containsInAnyOrder(salonLibre, salonOcupado));
    }

    @Test
    void dadoUnMesFuturoSinOcupacionesTodosLosDiasEstanDisponibles() {
        YearMonth mes = YearMonth.now().plusMonths(1);
        conSalones(unSalon(1L, "Norte"), unSalon(2L, "Sur"));

        Map<LocalDate, Boolean> calendario = servicioSalonReserva.obtenerDisponibilidadDelMes(mes);

        assertThat(calendario.size(), equalTo(mes.lengthOfMonth()));
        assertThat(calendario.values(), everyItem(is(true)));
    }

    @Test
    void dadoQueElUnicoSalonEstaOcupadoUnDiaEseDiaNoEstaDisponible() {
        YearMonth mes = YearMonth.now().plusMonths(1);
        LocalDate diaOcupado = mes.atDay(10);
        Salon salon = unSalon(1L, "Norte");
        conSalones(salon);
        conOcupaciones(mes, new SalonReserva(salon, diaOcupado, EstadoReserva.CONFIRMADA));

        Map<LocalDate, Boolean> calendario = servicioSalonReserva.obtenerDisponibilidadDelMes(mes);

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
        conOcupaciones(mes, new SalonReserva(norte, dia, EstadoReserva.CONFIRMADA));

        Map<LocalDate, Boolean> calendario = servicioSalonReserva.obtenerDisponibilidadDelMes(mes);

        assertThat(calendario.get(dia), is(true));
    }

    @Test
    void dadoQueTodosLosSalonesEstanOcupadosElMismoDiaEseDiaNoEstaDisponible() {
        YearMonth mes = YearMonth.now().plusMonths(1);
        LocalDate dia = mes.atDay(10);
        Salon norte = unSalon(1L, "Norte");
        Salon sur = unSalon(2L, "Sur");
        conSalones(norte, sur);
        conOcupaciones(
                mes,
                new SalonReserva(norte, dia, EstadoReserva.CONFIRMADA),
                new SalonReserva(sur, dia, EstadoReserva.SENIADA));

        Map<LocalDate, Boolean> calendario = servicioSalonReserva.obtenerDisponibilidadDelMes(mes);

        assertThat(calendario.get(dia), is(false));
    }

    @Test
    void dadoQueUnMismoSalonTieneDosOcupacionesElDiaSigueDisponibleSiHayOtroLibre() {
        YearMonth mes = YearMonth.now().plusMonths(1);
        LocalDate dia = mes.atDay(10);
        Salon norte = unSalon(1L, "Norte");
        Salon sur = unSalon(2L, "Sur");
        conSalones(norte, sur);
        conOcupaciones(
                mes,
                new SalonReserva(norte, dia, EstadoReserva.SENIADA),
                new SalonReserva(norte, dia, EstadoReserva.CONFIRMADA));

        Map<LocalDate, Boolean> calendario = servicioSalonReserva.obtenerDisponibilidadDelMes(mes);

        assertThat(calendario.get(dia), is(true));
    }

    @Test
    void dadoUnaOcupacionCanceladaElDiaSigueDisponible() {
        YearMonth mes = YearMonth.now().plusMonths(1);
        LocalDate dia = mes.atDay(10);
        Salon salon = unSalon(1L, "Norte");
        conSalones(salon);
        conOcupaciones(mes, new SalonReserva(salon, dia, EstadoReserva.CANCELADA));

        Map<LocalDate, Boolean> calendario = servicioSalonReserva.obtenerDisponibilidadDelMes(mes);

        assertThat(calendario.get(dia), is(true));
    }

    @Test
    void dadoElMesActualHoyYLosDiasPasadosNoEstanDisponibles() {
        LocalDate hoy = LocalDate.now();
        YearMonth mes = YearMonth.from(hoy);
        conSalones(unSalon(1L, "Norte"));

        Map<LocalDate, Boolean> calendario = servicioSalonReserva.obtenerDisponibilidadDelMes(mes);

        assertThat(calendario.get(hoy), is(false));
        calendario.forEach(
                (fecha, disponible) -> assertThat(disponible, equalTo(fecha.isAfter(hoy))));
    }

    @Test
    void dadoUnMesPasadoNingunDiaEstaDisponibleYNoHayError() {
        YearMonth mes = YearMonth.now().minusMonths(1);
        conSalones(unSalon(1L, "Norte"));

        Map<LocalDate, Boolean> calendario = servicioSalonReserva.obtenerDisponibilidadDelMes(mes);

        assertThat(calendario.size(), equalTo(mes.lengthOfMonth()));
        assertThat(calendario.values(), everyItem(is(false)));
    }

    @Test
    void dadoQueNoHaySalonesCargadosNingunDiaEstaDisponible() {
        YearMonth mes = YearMonth.now().plusMonths(1);
        conSalones();

        Map<LocalDate, Boolean> calendario = servicioSalonReserva.obtenerDisponibilidadDelMes(mes);

        assertThat(calendario.values(), everyItem(is(false)));
    }

    @Test
    void elCalendarioConsultaLasOcupacionesDelMesEnUnaSolaLlamada() {
        YearMonth mes = YearMonth.now().plusMonths(1);
        conSalones(unSalon(1L, "Norte"));

        servicioSalonReserva.obtenerDisponibilidadDelMes(mes);

        verify(repositorioSalonReserva).obtenerEntreFechas(mes.atDay(1), mes.atEndOfMonth());
        verifyNoMoreInteractions(repositorioSalonReserva);
    }


}