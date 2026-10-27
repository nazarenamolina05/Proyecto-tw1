package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class ReservaTest {

  @Test
  public void unaReservaConSeniaDebeQuedarSeniada() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoCon(evento, TipoPago.SENIA);

    assertThat(reserva.getEstado(), is(equalTo(EstadoReserva.SENIADA)));
  }

  @Test
  public void laSeniaDebeSerElPorcentajeDelPresupuesto() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoCon(evento, TipoPago.SENIA);

    assertThat(reserva.getMontoAbonado(), is(closeTo(30000.0, 0.01)));
  }

  @Test
  public void unaReservaConPagoTotalDebeQuedarConfirmada() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoCon(evento, TipoPago.TOTAL);

    assertThat(reserva.getEstado(), is(equalTo(EstadoReserva.CONFIRMADA)));
  }

  @Test
  public void elPagoTotalDebeAbonarTodoElPresupuesto() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoCon(evento, TipoPago.TOTAL);

    assertThat(reserva.getMontoAbonado(), is(closeTo(100000.0, 0.01)));
  }

  @Test
  public void laReservaDebeGuardarElPresupuestoDelEvento() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);

    Reserva reserva = this.cuandoReservoCon(evento, TipoPago.SENIA);

    assertThat(reserva.getPresupuesto(), is(closeTo(100000.0, 0.01)));
  }

  @Test
  public void laReservaDebeQuedarAsociadaAlUsuarioYAlEvento() {
    Evento evento = this.dadoQueTengoUnEventoQueCuesta(100000.0);
    Usuario usuario = new Usuario();

    Reserva reserva = new Reserva(usuario, evento, TipoPago.SENIA);

    assertThat(reserva.getUsuario(), is(equalTo(usuario)));
    assertThat(reserva.getEvento(), is(equalTo(evento)));
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
    return new Reserva(new Usuario(), evento, tipoPago);
  }
}
