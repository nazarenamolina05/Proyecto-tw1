package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioBuscarPaqueteTest {

  RepositorioPaquete repositorioPaqueteMock;
  ServicioBuscarPaquete servicioBuscarPaquete;

  @BeforeEach
  public void init() {
    repositorioPaqueteMock = mock(RepositorioPaquete.class);
    servicioBuscarPaquete = new ServicioBuscarPaqueteImpl(repositorioPaqueteMock);
  }

  @Test
  void deberiaDevolverSoloLosPacksDelTipoDeEventoBuscado() {
    //preparación
    Paquete casamiento = new Paquete("Casamiento clásico", TipoPaquete.CASAMIENTO);
    Paquete cumpleanios = new Paquete("Cumpleaños familiar", TipoPaquete.CUMPLEANIOS);
    Paquete fiestaDe15 = new Paquete("Fiesta de quince", TipoPaquete.FIESTA_DE_15);
    Paquete cumpleaniosPremium = new Paquete("Cumpleaños Premium", TipoPaquete.CUMPLEANIOS);
    List<Paquete> paquetesDisponibles = List.of(
      casamiento,
      cumpleanios,
      fiestaDe15,
      cumpleaniosPremium
    );
    List<Paquete> paquetesEsperados = List.of(cumpleanios, cumpleaniosPremium);

    when(repositorioPaqueteMock.obtenerTodos()).thenReturn(paquetesDisponibles);

    //ejecución
    List<Paquete> paquetesObtenidos = servicioBuscarPaquete.buscarPorTipo(TipoPaquete.CUMPLEANIOS);

    // then: obtengo exactamente el pack con casamiento
    //validación
    assertThat(paquetesObtenidos, equalTo(paquetesEsperados));
  }

  @Test
  void deberiaDevolverUnaListaVaciaSiNoHayEventosDelTipoBuscado() {
    //preparación
    Paquete casamiento = new Paquete("Casamiento clásico", TipoPaquete.CASAMIENTO);
    Paquete cumpleanios = new Paquete("Cumpleaños familiar", TipoPaquete.CUMPLEANIOS);
    Paquete fiestaDe15 = new Paquete("Fiesta de quince", TipoPaquete.FIESTA_DE_15);
    List<Paquete> paquetesDisponibles = List.of(casamiento, cumpleanios, fiestaDe15);
    List<Paquete> paquetesEsperados = new ArrayList<>();

    when(repositorioPaqueteMock.obtenerTodos()).thenReturn(paquetesDisponibles);

    //ejecución
    List<Paquete> paquetesObtenidos = servicioBuscarPaquete.buscarPorTipo(TipoPaquete.EMPRESARIAL);

    //validación
    assertThat(paquetesObtenidos, equalTo(paquetesEsperados));
  }
}
