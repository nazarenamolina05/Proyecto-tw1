package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDate;
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
    List<ServicioAdicional> serviciosEsperados = List.of(
      new ServicioAdicional("DJ", 40000.0, false),
      new ServicioAdicional("Fotografia", 30000.0, false)
    );
    Double precioEsperado = 150000.0;

    // when
    Paquete paquete = new Paquete(
      idEsperado,
      nombreEsperado,
      tipoEsperado,
      salonEsperado,
      cateringEsperado,
      serviciosEsperados,
      precioEsperado
    );

    // then
    assertEquals(idEsperado, paquete.getId());
    assertEquals(nombreEsperado, paquete.getNombre());
    assertEquals(tipoEsperado, paquete.getTipo());
    assertEquals(salonEsperado, paquete.getSalon());
    assertEquals(cateringEsperado, paquete.getCatering());
    assertEquals(serviciosEsperados, paquete.getServiciosAdicionales());
    assertEquals(precioEsperado, paquete.getPrecioEstimado());
  }

  @Test
  public void alCrearUnEventoSeCopianSalonCateringYServiciosDelPaquete() {
    Paquete paquete = this.dadoQueTengoUnPaqueteConDosServicios();

    Evento evento = this.cuandoCreoUnEventoDesde(paquete);

    assertSame(paquete.getSalon(), evento.getSalon());
    assertSame(paquete.getCatering(), evento.getCatering());
    assertEquals(paquete.getServiciosAdicionales(), evento.getServicioAdicionalsList());
  }

  @Test
  public void alCrearUnEventoSeGuardanElTipoLaFechaYLosInvitadosElegidos() {
    Paquete paquete = this.dadoQueTengoUnPaqueteConDosServicios();
    LocalDate fecha = LocalDate.now().plusDays(30);

    Evento evento = paquete.crearEvento(TipoPaquete.FIESTA_DE_15, fecha, 40);

    assertEquals(TipoPaquete.FIESTA_DE_15, evento.getTipoPaquete());
    assertEquals(fecha, evento.getFecha());
    assertEquals(40, evento.getCantidadInvitados());
  }

  @Test
  public void elEventoRecuerdaElPaqueteDelQueSalio() {
    Paquete paquete = this.dadoQueTengoUnPaqueteConDosServicios();

    Evento evento = this.cuandoCreoUnEventoDesde(paquete);

    assertSame(paquete, evento.getPaqueteOrigen());
  }

  @Test
  public void modificarElEventoNoModificaLosServiciosDelPaquete() {
    Paquete paquete = this.dadoQueTengoUnPaqueteConDosServicios();
    Evento evento = this.cuandoCreoUnEventoDesde(paquete);

    evento.agregarServicio(new ServicioAdicional("Cotillon", 500.0, true));

    assertEquals(2, paquete.getServiciosAdicionales().size());
    assertEquals(3, evento.getServicioAdicionalsList().size());
  }

  private Paquete dadoQueTengoUnPaqueteConDosServicios() {
    return new Paquete(
      1L,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      new Salon("Salón los Alamos", 50000.0),
      new Catering("Catering Basico", 1500.0),
      List.of(
        new ServicioAdicional("DJ", 40000.0, false),
        new ServicioAdicional("Fotografia", 30000.0, false)
      ),
      150000.0
    );
  }

  private Evento cuandoCreoUnEventoDesde(Paquete paquete) {
    return paquete.crearEvento(TipoPaquete.CUMPLEANIOS, LocalDate.now().plusDays(30), 30);
  }
}
