package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioBuscarPaqueteTest {

  private RepositorioPaquete repositorioPaqueteMock;
  private ServicioBuscarPaquete servicioBuscarPaquete;

  @BeforeEach
  public void init() {
    repositorioPaqueteMock = mock(RepositorioPaquete.class);
    servicioBuscarPaquete = new ServicioBuscarPaqueteImpl(repositorioPaqueteMock);
  }

  @Test
  public void deberiaDevolverSoloLosPacksDelTipoDeEventoBuscado() {
    // Preparación
    Paquete cumpleanios = crearPaquete("Cumpleaños familiar", TipoPaquete.CUMPLEANIOS);

    Paquete cumpleaniosPremium = crearPaquete("Cumpleaños Premium", TipoPaquete.CUMPLEANIOS);

    List<Paquete> paquetesEsperados = List.of(cumpleanios, cumpleaniosPremium);

    when(repositorioPaqueteMock.buscarPorFiltros(TipoPaquete.CUMPLEANIOS, null, null))
      .thenReturn(paquetesEsperados);

    // Ejecución
    List<Paquete> obtenidos = servicioBuscarPaquete.buscarPorTipo(TipoPaquete.CUMPLEANIOS);

    // Validación
    assertThat(obtenidos, equalTo(paquetesEsperados));
  }

  @Test
  public void deberiaDevolverUnaListaVaciaSiNoHayEventosDelTipoBuscado() {
    // Preparación
    when(repositorioPaqueteMock.buscarPorFiltros(TipoPaquete.EMPRESARIAL, null, null))
      .thenReturn(List.of());

    // Ejecución
    List<Paquete> obtenidos = servicioBuscarPaquete.buscarPorTipo(TipoPaquete.EMPRESARIAL);

    // Validación
    assertThat(obtenidos, equalTo(List.of()));
  }

  @Test
  public void deberiaBuscarPaquetesPorTipoPresupuestoYCapacidad() {
    // Preparación
    Salon salon = new Salon("Salón grande", 90000.0);
    salon.setCapacidad(120);

    Paquete cumpleanios = new Paquete(
      null,
      "Cumpleaños familiar",
      TipoPaquete.CUMPLEANIOS,
      salon,
      "Catering básico",
      List.of("DJ"),
      200000.0
    );

    List<Paquete> paquetesEsperados = List.of(cumpleanios);

    when(repositorioPaqueteMock.buscarPorFiltros(TipoPaquete.CUMPLEANIOS, 250000.0, 80))
      .thenReturn(paquetesEsperados);

    // Ejecución
    List<Paquete> obtenidos = servicioBuscarPaquete.buscarPorFiltros(
      TipoPaquete.CUMPLEANIOS,
      250000.0,
      80
    );

    // Validación
    assertThat(obtenidos, equalTo(paquetesEsperados));
  }

  @Test
  public void deberiaDevolverListaVaciaSiNoHayPaquetesQueCumplanLosFiltros() {
    // Preparación
    when(repositorioPaqueteMock.buscarPorFiltros(TipoPaquete.CASAMIENTO, 100000.0, 100))
      .thenReturn(List.of());

    // Ejecución
    List<Paquete> obtenidos = servicioBuscarPaquete.buscarPorFiltros(
      TipoPaquete.CASAMIENTO,
      100000.0,
      100
    );

    // Validación
    assertThat(obtenidos, equalTo(List.of()));
  }

  private Paquete crearPaquete(String nombre, TipoPaquete tipo) {
    return new Paquete(
      null,
      nombre,
      tipo,
      new Salon("Salón de prueba", 50000.0),
      "Catering de prueba",
      List.of("DJ"),
      100000.0
    );
  }
}
