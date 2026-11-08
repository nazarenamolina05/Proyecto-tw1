package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PaqueteTest {

  //este test prueba que la clase Paquete en sí misma guarda y devuelve bien sus propios datos
  @Test
  public void seCreaUnPaqueteConTodosSusDatos() {
    // given
    Long idEsperado = 1L;
    String nombreEsperado = "Básico";
    TipoPaquete tipoEsperado = TipoPaquete.CUMPLEANIOS;
    Salon salonEsperado = new Salon("Salón los Alamos", 50000.0);
    Catering cateringEsperado = new Catering("Catering Basico", 1500.0);
    Double precioEsperado = 150000.0;

    // when
    Paquete paquete = new Paquete(
      idEsperado,
      nombreEsperado,
      tipoEsperado,
      salonEsperado,
      cateringEsperado,
      precioEsperado
    );

    // then
    assertEquals(idEsperado, paquete.getId());
    assertEquals(nombreEsperado, paquete.getNombre());
    assertEquals(tipoEsperado, paquete.getTipo());
    assertEquals(salonEsperado, paquete.getSalon());
    assertEquals(cateringEsperado, paquete.getCatering());
    assertEquals(precioEsperado, paquete.getPrecioEstimado());
  }

  // [REFACTOR PAQUETE] Paquete ya no tiene la lista de servicios: en los tests se arma
  // aparte (como si viniera de RepositorioPaquete.obtenerServiciosDe) y se pasa por parámetro.

  @Test
  public void alCrearUnEventoSeCopianSalonCateringYServiciosDelPaquete() {
    Paquete paquete = this.dadoQueTengoUnPaqueteBasico();
    List<ServicioAdicional> serviciosDelPaquete = this.dadoQueElPaqueteTieneDosServicios();

    Evento evento = this.cuandoCreoUnEventoDesde(paquete, serviciosDelPaquete);

    assertSame(paquete.getSalon(), evento.getSalon());
    assertSame(paquete.getCatering(), evento.getCatering());
    assertEquals(serviciosDelPaquete, evento.getServiciosAdicionales());
  }

  @Test
  public void alCrearUnEventoSeGuardanElTipoLaFechaYLosInvitadosElegidos() {
    Paquete paquete = this.dadoQueTengoUnPaqueteBasico();
    LocalDate fecha = LocalDate.now().plusDays(30);

    Evento evento = paquete.crearEvento(
      TipoPaquete.FIESTA_DE_15,
      fecha,
      40,
      this.dadoQueElPaqueteTieneDosServicios()
    );

    assertEquals(TipoPaquete.FIESTA_DE_15, evento.getTipoPaquete());
    assertEquals(fecha, evento.getFecha());
    assertEquals(40, evento.getCantidadInvitados());
  }

  @Test
  public void elEventoRecuerdaElPaqueteDelQueSalio() {
    Paquete paquete = this.dadoQueTengoUnPaqueteBasico();

    Evento evento = this.cuandoCreoUnEventoDesde(paquete, this.dadoQueElPaqueteTieneDosServicios());

    assertSame(paquete, evento.getPaqueteOrigen());
  }

  @Test
  public void modificarElEventoNoModificaLosServiciosDelPaquete() {
    Paquete paquete = this.dadoQueTengoUnPaqueteBasico();
    List<ServicioAdicional> serviciosDelPaquete = new ArrayList<>(
      this.dadoQueElPaqueteTieneDosServicios()
    );
    Evento evento = this.cuandoCreoUnEventoDesde(paquete, serviciosDelPaquete);

    evento.agregarServicio(new ServicioAdicional("Cotillon", 500.0, true));

    assertEquals(2, serviciosDelPaquete.size());
    assertEquals(3, evento.getServiciosAdicionales().size());
  }

  @Test
  public void elPrecioDelPaqueteSumaSalonCateringPorInvitadoYServicios() {
    Paquete paquete = this.dadoQueTengoUnPaqueteBasico();

    Double precio = paquete.calcularPrecio(30, this.dadoQueElPaqueteTieneDosServicios());

    // 50000 (salón) + 1500 * 30 (catering) + 40000 (DJ) + 30000 (fotografía)
    assertEquals(165000.0, precio);
  }

  @Test
  void unPaqueteSinPublicadorNoDeberiaEstarPublicado() {
    Paquete paquete = new Paquete();

    assertThat(paquete.isPublicado(), is(false));
  }

  @Test
  void unPaqueteConPublicadorDeberiaEstarPublicado() {
    Paquete paquete = new Paquete();
    paquete.setPublicador(new Usuario());

    assertThat(paquete.isPublicado(), is(true));
  }

  @Test
  void alPublicarUnEventoSeCreaUnPaqueteConSusDatosYSuPublicador() {
    Usuario publicador = new Usuario();
    Evento evento = this.dadoQueTengoUnEventoConSalonCateringYServicios();

    Paquete paquete = Paquete.desdeEvento(evento, "Mi cumple de 30", publicador);

    assertEquals("Mi cumple de 30", paquete.getNombre());
    assertEquals(evento.getTipoPaquete(), paquete.getTipo());
    assertSame(evento.getSalon(), paquete.getSalon());
    assertSame(evento.getCatering(), paquete.getCatering());
    assertSame(publicador, paquete.getPublicador());
  }

  // [REFACTOR PAQUETE] Se quitó modificarLosServiciosDelEventoNoDeberiaAlterarLosDelPaquetePublicado:
  // el paquete ya no guarda una lista propia. Que se guarden los servicios del evento
  // como PaqueteServicio se prueba en ServicioPublicarPaqueteTest.

  @Test
  void elPrecioEstimadoDelPaquetePublicadoDeberiaSerElPresupuestoDelEvento() {
    Evento evento = this.dadoQueTengoUnEventoConSalonCateringYServicios();

    Paquete paquete = Paquete.desdeEvento(evento, "Mi cumple de 30", new Usuario());

    assertEquals(evento.calcularPresupuesto(), paquete.getPrecioEstimado());
  }

  @Test
  void elPaquetePublicadoNoDeberiaSerPredeterminado() {
    Evento evento = this.dadoQueTengoUnEventoConSalonCateringYServicios();

    Paquete paquete = Paquete.desdeEvento(evento, "Mi cumple de 30", new Usuario());

    assertFalse(paquete.isPredeterminado());
  }

  private Paquete dadoQueTengoUnPaqueteBasico() {
    return new Paquete(
      1L,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      new Salon("Salón los Alamos", 50000.0),
      new Catering("Catering Basico", 1500.0),
      150000.0
    );
  }

  private List<ServicioAdicional> dadoQueElPaqueteTieneDosServicios() {
    return List.of(
      new ServicioAdicional("DJ", 40000.0, false),
      new ServicioAdicional("Fotografia", 30000.0, false)
    );
  }

  private Evento cuandoCreoUnEventoDesde(
    Paquete paquete,
    List<ServicioAdicional> serviciosDelPaquete
  ) {
    return paquete.crearEvento(
      TipoPaquete.CUMPLEANIOS,
      LocalDate.now().plusDays(30),
      30,
      serviciosDelPaquete
    );
  }

  private Evento dadoQueTengoUnEventoConSalonCateringYServicios() {
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().minusDays(10), 40);
    evento.setSalon(new Salon("Salón los Alamos", 50000.0));
    evento.setCatering(new Catering("Catering Basico", 1500.0));
    evento.agregarServicio(new ServicioAdicional("DJ", 40000.0, false));
    return evento;
  }
}
