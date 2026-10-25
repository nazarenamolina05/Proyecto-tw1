package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class EventoPersonalizadoTest {

  @Test
  public void elPresupuestoDebeSerCeroSiNoHayNadaSeleccionado() {
    EventoPersonalizado evento = this.dadoQueTengoUnEventoConInvitados(30);

    Double presupuesto = this.cuandoCalculoElPresupuesto(evento);

    assertThat(presupuesto, is(equalTo(0.0)));
  }

  @Test
  public void elPresupuestoDebeSerElPrecioDelSalonSiSoloHaySalon() {
    EventoPersonalizado evento = this.dadoQueTengoUnEventoConInvitados(30);
    evento.setSalon(new Salon("Salon Los Alamos", 50000.0));

    double presupuesto = this.cuandoCalculoElPresupuesto(evento);

    assertThat(presupuesto, is(equalTo(50000.0)));
  }

  @Test
  public void elPresupuestoDebeMultiplicarElCateringPorLosInvitados() {
    EventoPersonalizado evento = this.dadoQueTengoUnEventoConInvitados(30);
    evento.setCatering(new Catering("Catering Basico", 1500.0));

    Double presupuesto = this.cuandoCalculoElPresupuesto(evento);

    assertThat(presupuesto, is(equalTo(45000.0)));
  }

  @Test
  public void ElPresupuestoConServicioDePrecioFijoConSoloDJ() {
    EventoPersonalizado evento = this.dadoQueTengoUnEventoConInvitados(30);
    evento.agregarServicio(new ServicioAdicional("DJ", 40000.0, false));

    Double presupuesto = this.cuandoCalculoElPresupuesto(evento);

    assertThat(presupuesto, is(equalTo(40000.0)));
  }

  @Test
  public void elPresupuestoConServicioAdicionalDeCotillonYElPrecioCuentaPorInvitado() {
    EventoPersonalizado evento = this.dadoQueTengoUnEventoConInvitados(30);
    evento.agregarServicio(new ServicioAdicional("Cotillon", 500.0, true));

    Double presupuesto = this.cuandoCalculoElPresupuesto(evento);

    assertThat(presupuesto, is(equalTo(15000.0)));
  }

  @Test
  public void elPresupuestoconVariosServiciosAdicionalesYCalcularElPrecioAlFinal() {
    EventoPersonalizado evento = this.dadoQueTengoUnEventoConInvitados(30);
    evento.agregarServicio(new ServicioAdicional("DJ", 40000.0, false));
    evento.agregarServicio(new ServicioAdicional("Fotografia", 30000.0, false));

    Double presupuesto = this.cuandoCalculoElPresupuesto(evento);

    assertThat(presupuesto, is(equalTo(70000.0)));
  }

  @Test
  public void elPresupuestoConTodoElegido() {
    EventoPersonalizado evento = this.dadoQueTengoUnEventoConInvitados(30);
    evento.setSalon(new Salon("Salon Los Alamos", 50000.0));
    evento.setCatering(new Catering("Catering Basico", 1500.0));
    evento.agregarServicio(new ServicioAdicional("DJ", 40000.0, false));
    evento.agregarServicio(new ServicioAdicional("Cotillon", 500.0, true));

    Double presupuesto = this.cuandoCalculoElPresupuesto(evento);

    assertThat(presupuesto, is(equalTo(150000.0)));
  }

  @Test
  public void elPresupuestoDebeBajarAlQuitarUnServicio() {
    EventoPersonalizado evento = this.dadoQueTengoUnEventoConInvitados(30);

    ServicioAdicional dj = new ServicioAdicional("DJ", 40000.0, false);
    ServicioAdicional cotillon = new ServicioAdicional("Cotillon", 500.0, true);

    evento.agregarServicio(dj);
    evento.agregarServicio(cotillon);

    evento.quitarServicio(dj);
    Double presupuesto = this.cuandoCalculoElPresupuesto(evento);

    assertThat(presupuesto, is(equalTo(15000.0)));
  }

  @Test
  public void elPresupuestoCambiaAlPonerOtroSalon() {
    EventoPersonalizado evento = this.dadoQueTengoUnEventoConInvitados(30);

    Salon salon1 = new Salon("Salon Los Alamos", 50000.0);
    Salon salon2 = new Salon("Salon Gran Bahia", 150000.0);

    evento.setSalon(salon1);

    Double presupuestoConSalon1 = this.cuandoCalculoElPresupuesto(evento);

    evento.setSalon(salon2);

    Double presupuestoConSalon2 = this.cuandoCalculoElPresupuesto(evento);

    assertThat(presupuestoConSalon1, is(equalTo(50000.0)));
    assertThat(presupuestoConSalon2, is(equalTo(150000.0)));
  }

  @Test
  public void elPresupuestoAlCambiarLaCantidadDeInvitados() {
    EventoPersonalizado evento = this.dadoQueTengoUnEventoConInvitados(30);

    evento.setCatering(new Catering("Catering Completo", 5000.0));
    evento.agregarServicio(new ServicioAdicional("Cotillon", 500.0, true));

    Double presupuestoConInvitados = this.cuandoCalculoElPresupuesto(evento);

    evento.setCantidadInvitados(50);

    Double presupuestoConMasInvitados = this.cuandoCalculoElPresupuesto(evento);

    assertThat(presupuestoConInvitados, is(equalTo(165000.0)));
    assertThat(presupuestoConMasInvitados, is(equalTo(275000.0)));
  }

  @Test
  public void elAdicionalDebeSerCeroSiElEventoTieneLoMismoQueElPaquete() {
    EventoPersonalizado evento = this.dadoQueTengoUnEventoDesdeElPaqueteBasico();

    Double adicional = evento.calcularAdicional();

    assertThat(adicional, is(equalTo(0.0)));
  }

  @Test
  public void elAdicionalDebeSerElCostoDeUnServicioExtraAgregado() {
    EventoPersonalizado evento = this.dadoQueTengoUnEventoDesdeElPaqueteBasico();
    evento.agregarServicio(new ServicioAdicional("Video", 45000.0, false));

    Double adicional = evento.calcularAdicional();

    assertThat(adicional, is(equalTo(45000.0)));
  }

  @Test
  public void noDebeCobrarAdicionalSiCambiaAUnCateringMasBarato() {
    EventoPersonalizado evento = this.dadoQueTengoUnEventoDesdeElPaqueteBasico();
    evento.setCatering(new Catering("Catering Chico", 1000.0));

    Double adicional = evento.calcularAdicional();

    assertThat(adicional, is(equalTo(0.0)));
  }

  @Test
  public void bajarLaCantidadDeInvitadosNoDebeGenerarAdicional() {
    EventoPersonalizado evento = this.dadoQueTengoUnEventoDesdeElPaqueteBasico();
    evento.setCantidadInvitados(30);

    Double adicional = evento.calcularAdicional();

    assertThat(adicional, is(equalTo(0.0)));
  }

  @Test
  public void elServicioDelPaqueteDebeEstarIncluidoYUnoExtraNo() {
    EventoPersonalizado evento = this.dadoQueTengoUnEventoDesdeElPaqueteBasico();
    ServicioAdicional dj = evento.getServiciosAdicionales().get(0);

    assertTrue(evento.esServicioIncluido(dj));
    assertFalse(evento.esServicioIncluido(new ServicioAdicional("Video", 45000.0, false)));
  }

  private EventoPersonalizado dadoQueTengoUnEventoDesdeElPaqueteBasico() {
    Salon salon = new Salon("Salon Los Alamos", 50000.0);
    Catering catering = new Catering("Catering Basico", 1500.0);
    ServicioAdicional dj = new ServicioAdicional("DJ", 40000.0, false);
    Paquete paquete = new Paquete(
      1L,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      salon,
      catering,
      List.of(dj),
      165000.0
    );

    EventoPersonalizado evento = this.dadoQueTengoUnEventoConInvitados(50);
    evento.setPaqueteOrigen(paquete);
    evento.setSalon(salon);
    evento.setCatering(catering);
    evento.agregarServicio(dj);
    return evento;
  }

  private EventoPersonalizado dadoQueTengoUnEventoConInvitados(Integer invitados) {
    return new EventoPersonalizado(TipoPaquete.CUMPLEANIOS, LocalDate.now(), invitados);
  }

  private Double cuandoCalculoElPresupuesto(EventoPersonalizado evento) {
    return evento.calcularPresupuesto();
  }
}
