package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tallerwebi.dominio.excepcion.ReservaNoCancelable;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ReservaTest {

  @Test
  public void unaReservaConSeniaDebeQuedarSeniada() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.SENIA);

    assertThat(reserva.getEstado(), is(equalTo(EstadoReserva.SENIADA)));
  }

  @Test
  public void laSeniaDebeSerElPorcentajeDelPresupuesto() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.SENIA);

    assertThat(reserva.getMontoAbonado(), is(closeTo(30000.0, 0.01)));
  }

  @Test
  public void unaReservaConPagoTotalDebeQuedarConfirmada() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.TOTAL);

    assertThat(reserva.getEstado(), is(equalTo(EstadoReserva.CONFIRMADA)));
  }

  @Test
  public void elPagoTotalDebeAbonarTodoElPresupuesto() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.TOTAL);

    assertThat(reserva.getMontoAbonado(), is(closeTo(100000.0, 0.01)));
  }

  @Test
  public void laReservaDebeGuardarElPresupuestoDelEvento() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.SENIA);

    assertThat(reserva.getPresupuesto(), is(closeTo(100000.0, 0.01)));
  }

  @Test
  public void laReservaDebeQuedarAsociadaAlUsuarioYAlEvento() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);
    Usuario usuario = new Usuario();

    Reserva reserva = new Reserva(usuario, evento, TipoPago.SENIA, List.of());

    assertThat(reserva.getUsuario(), is(equalTo(usuario)));
    assertThat(reserva.getEvento(), is(equalTo(evento)));
  }

  @Test
  public void unaReservaConfirmadaDeUnEventoQueYaOcurrioDebeAdmitirResenia() {
    Reserva reserva =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().minusDays(1),
          EstadoReserva.CONFIRMADA
        );

    assertThat(reserva.admiteResenia(), is(true));
  }

  @Test
  public void unaReservaSeniadaDeUnEventoQueYaOcurrioDebeAdmitirResenia() {
    Reserva reserva =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().minusDays(1),
          EstadoReserva.SENIADA
        );

    assertThat(reserva.admiteResenia(), is(true));
  }

  @Test
  public void unaReservaFinalizadaDebeAdmitirResenia() {
    Reserva reserva =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().minusDays(1),
          EstadoReserva.FINALIZADA
        );

    assertThat(reserva.admiteResenia(), is(true));
  }

  @Test
  public void unaReservaDeUnEventoFuturoNoDebeAdmitirResenia() {
    Reserva reserva =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().plusDays(1),
          EstadoReserva.CONFIRMADA
        );

    assertThat(reserva.admiteResenia(), is(false));
  }

  @Test
  public void unaReservaDeUnEventoDeHoyTodaviaNoDebeAdmitirResenia() {
    Reserva reserva =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(LocalDate.now(), EstadoReserva.CONFIRMADA);

    assertThat(reserva.admiteResenia(), is(false));
  }

  @Test
  public void unaReservaPendienteDePagoNoDebeAdmitirReseniaAunqueElEventoHayaPasado() {
    Reserva reserva =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().minusDays(1),
          EstadoReserva.PENDIENTE_PAGO
        );

    assertThat(reserva.admiteResenia(), is(false));
  }

  @Test
  public void unaReservaCanceladaNoDebeAdmitirReseniaAunqueElEventoHayaPasado() {
    Reserva reserva =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().minusDays(1),
          EstadoReserva.CANCELADA
        );

    assertThat(reserva.admiteResenia(), is(false));
  }

  private Reserva dadoQueTengoUnaReservaDeUnEventoDelDia(LocalDate fecha, EstadoReserva estado) {
    Reserva reserva = new Reserva();
    reserva.setEvento(new Evento(TipoPaquete.CUMPLEANIOS, fecha, 30));
    reserva.setEstado(estado);
    return reserva;
  }

  @Test
  void laSeniaDebeSerElTreintaPorCientoDelPresupuesto() {
    assertThat(Reserva.calcularSenia(100000.0), is(closeTo(30000.0, 0.01)));
  }

  private Evento dadoQueTengoUnEventoQueCuesta(Double precioSalon) {
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().plusDays(30), 30);
    evento.setSalon(new Salon("Salon Los Alamos", precioSalon));
    return evento;
  }

  private Reserva cuandoReservoCon(Evento evento, TipoPago tipoPago) {
    return new Reserva(new Usuario(), evento, tipoPago, List.of());
  }

  // [REFACTOR EVENTO] Reserva recibe los servicios del evento para calcular el presupuesto.
  @Test
  public void elPresupuestoDeLaReservaDebeIncluirLosServiciosDelEvento() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);
    List<ServicioAdicional> servicios = List.of(new ServicioAdicional("DJ", 40000.0, false));

    Reserva reserva = new Reserva(new Usuario(), evento, TipoPago.TOTAL, servicios);

    assertThat(reserva.getPresupuesto(), is(closeTo(140000.0, 0.01)));
  }

  private Reserva cuandoReservoYPago(Evento evento, TipoPago tipoPago) {
    Reserva reserva = this.cuandoReservoCon(evento, tipoPago);
    reserva.registrarPago(reserva.calcularMontoAPagar());
    return reserva;
  }

  @Test
  public void elSaldoPendienteDeUnaReservaSeniadaDebeSerLoQueFaltaAbonar() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.SENIA);

    assertThat(reserva.calcularSaldoPendiente(), is(closeTo(70000.0, 0.01)));
  }

  @Test
  public void elSaldoPendienteDeUnaReservaPagadaPorCompletoDebeSerCero() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.TOTAL);

    assertThat(reserva.calcularSaldoPendiente(), is(closeTo(0.0, 0.01)));
  }

  @Test
  public void unaReservaRecienCreadaDebeQuedarPendienteDePago() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoCon(evento, TipoPago.SENIA);

    assertThat(reserva.getEstado(), is(equalTo(EstadoReserva.PENDIENTE_PAGO)));
  }

  @Test
  public void unaReservaRecienCreadaNoDebeTenerNadaAbonado() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoCon(evento, TipoPago.TOTAL);

    assertThat(reserva.getMontoAbonado(), is(closeTo(0.0, 0.01)));
  }

  @Test
  public void elMontoAPagarDeUnaReservaConSeniaDebeSerLaSenia() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoCon(evento, TipoPago.SENIA);

    assertThat(reserva.calcularMontoAPagar(), is(closeTo(30000.0, 0.01)));
  }

  @Test
  public void elMontoAPagarDeUnaReservaConPagoTotalDebeSerElPresupuesto() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoCon(evento, TipoPago.TOTAL);

    assertThat(reserva.calcularMontoAPagar(), is(closeTo(100000.0, 0.01)));
  }

  @Test
  public void unAbonoDespuesDeLaSeniaDebeSumarseAlMontoAbonado() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);
    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.SENIA);

    reserva.registrarPago(20000.0);

    assertThat(reserva.getMontoAbonado(), is(closeTo(50000.0, 0.01)));
    assertThat(reserva.calcularSaldoPendiente(), is(closeTo(50000.0, 0.01)));
    assertThat(reserva.getEstado(), is(equalTo(EstadoReserva.SENIADA)));
  }

  @Test
  public void alAbonarElSaldoCompletoLaReservaDebeQuedarConfirmada() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);
    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.SENIA);

    reserva.registrarPago(70000.0);

    assertThat(reserva.getEstado(), is(equalTo(EstadoReserva.CONFIRMADA)));
    assertThat(reserva.calcularSaldoPendiente(), is(closeTo(0.0, 0.01)));
  }

  @Test
  public void unaReservaSeniadaConEventoFuturoDebePermitirAbonos() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.SENIA);

    assertThat(reserva.permiteAbonos(), is(true));
  }

  @Test
  public void unaReservaPendienteDePagoNoDebePermitirAbonos() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoCon(evento, TipoPago.SENIA);

    assertThat(reserva.permiteAbonos(), is(false));
  }

  @Test
  public void unaReservaConfirmadaNoDebePermitirAbonos() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.TOTAL);

    assertThat(reserva.permiteAbonos(), is(false));
  }

  @Test
  public void unaReservaSeniadaConEventoYaPasadoNoDebePermitirAbonos() {
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().minusDays(1), 30);
    evento.setSalon(new Salon("Salon Los Alamos", 100000.0));

    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.SENIA);

    assertThat(reserva.permiteAbonos(), is(false));
  }

  @Test
  public void deberiaAdmitirUnAbonoMenorAlSaldoPendiente() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);
    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.SENIA);

    assertThat(reserva.admiteAbonoDe(10000.0), is(true));
  }

  @Test
  public void deberiaAdmitirUnAbonoIgualAlSaldoPendiente() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);
    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.SENIA);

    assertThat(reserva.admiteAbonoDe(70000.0), is(true));
  }

  @Test
  public void noDeberiaAdmitirUnAbonoMayorAlSaldoPendiente() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);
    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.SENIA);

    assertThat(reserva.admiteAbonoDe(70001.0), is(false));
  }

  @Test
  public void noDeberiaAdmitirUnAbonoSinMontoPositivo() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);
    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.SENIA);

    assertThat(reserva.admiteAbonoDe(0.0), is(false));
    assertThat(reserva.admiteAbonoDe(-5.0), is(false));
    assertThat(reserva.admiteAbonoDe(null), is(false));
  }

  @Test
  public void elPorcentajeAbonadoDeUnaReservaPendienteDebeSerCero() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoCon(evento, TipoPago.SENIA);

    assertThat(reserva.calcularPorcentajeAbonado(), is(0));
  }

  @Test
  public void elPorcentajeAbonadoDeUnaReservaSeniadaDebeSerElDeLaSenia() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.SENIA);

    assertThat(reserva.calcularPorcentajeAbonado(), is(30));
  }

  @Test
  public void elPorcentajeAbonadoDeUnaReservaPagadaPorCompletoDebeSerCien() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.TOTAL);

    assertThat(reserva.calcularPorcentajeAbonado(), is(100));
  }

  @Test
  public void elPorcentajeAbonadoDebeRedondearseAlEnteroMasCercano() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);
    Reserva reserva = this.cuandoReservoYPago(evento, TipoPago.SENIA);

    reserva.registrarPago(3333.0);

    assertThat(reserva.calcularPorcentajeAbonado(), is(33));
  }

  @Test
  public void unaReservaPendienteDePagoConEventoFuturoDebeAdmitirCancelacion() {
    Reserva reserva =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().plusDays(10),
          EstadoReserva.PENDIENTE_PAGO
        );

    assertThat(reserva.admiteCancelacion(), is(true));
  }

  @Test
  public void unaReservaSeniadaOConfirmadaConEventoFuturoDebeAdmitirCancelacion() {
    Reserva seniada =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().plusDays(10),
          EstadoReserva.SENIADA
        );
    Reserva confirmada =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().plusDays(10),
          EstadoReserva.CONFIRMADA
        );

    assertThat(seniada.admiteCancelacion(), is(true));
    assertThat(confirmada.admiteCancelacion(), is(true));
  }

  @Test
  public void unaReservaNoDebeAdmitirCancelacionElDiaDelEvento() {
    Reserva reserva =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(LocalDate.now(), EstadoReserva.CONFIRMADA);

    assertThat(reserva.admiteCancelacion(), is(false));
  }

  @Test
  public void unaReservaNoDebeAdmitirCancelacionSiElEventoYaPaso() {
    Reserva reserva =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().minusDays(1),
          EstadoReserva.CONFIRMADA
        );

    assertThat(reserva.admiteCancelacion(), is(false));
  }

  @Test
  public void unaReservaYaCanceladaOFinalizadaNoDebeAdmitirCancelacion() {
    Reserva cancelada =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().plusDays(10),
          EstadoReserva.CANCELADA
        );
    Reserva finalizada =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().plusDays(10),
          EstadoReserva.FINALIZADA
        );

    assertThat(cancelada.admiteCancelacion(), is(false));
    assertThat(finalizada.admiteCancelacion(), is(false));
  }

  @Test
  public void alCancelarUnaReservaVigenteDebeQuedarCancelada() {
    Reserva reserva =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().plusDays(10),
          EstadoReserva.SENIADA
        );

    reserva.cancelar();

    assertThat(reserva.getEstado(), is(equalTo(EstadoReserva.CANCELADA)));
  }

  @Test
  public void noDebeCancelarseUnaReservaQueNoAdmiteCancelacion() {
    Reserva reserva =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().minusDays(1),
          EstadoReserva.CONFIRMADA
        );

    assertThrows(ReservaNoCancelable.class, reserva::cancelar);
    assertThat(reserva.getEstado(), is(equalTo(EstadoReserva.CONFIRMADA)));
  }

  @Test
  public void unPagoTardioNoDebeReactivarUnaReservaCancelada() {
    Reserva reserva =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().plusDays(10),
          EstadoReserva.PENDIENTE_PAGO
        );
    reserva.setPresupuesto(100000.0);
    reserva.setMontoAbonado(0.0);
    reserva.cancelar();

    reserva.registrarPago(30000.0);

    assertThat(reserva.getEstado(), is(equalTo(EstadoReserva.CANCELADA)));
    assertThat(reserva.getMontoAbonado(), is(closeTo(30000.0, 0.01)));
  }

  @Test
  public void unaReservaDebeAdmitirCancelacionHastaElLimiteDeDiasMinimos() {
    Reserva reserva =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().plusDays(Reserva.DIAS_MINIMOS_PARA_CANCELAR),
          EstadoReserva.SENIADA
        );

    assertThat(reserva.admiteCancelacion(), is(true));
  }

  @Test
  public void unaReservaNoDebeAdmitirCancelacionConMenosDiasDeLosMinimos() {
    Reserva reserva =
      this.dadoQueTengoUnaReservaDeUnEventoDelDia(
          LocalDate.now().plusDays(Reserva.DIAS_MINIMOS_PARA_CANCELAR - 1),
          EstadoReserva.SENIADA
        );

    assertThat(reserva.admiteCancelacion(), is(false));
  }
}
