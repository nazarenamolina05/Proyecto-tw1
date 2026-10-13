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

  @Test
  public void deberiaDevolverSoloLosPacksDelTipoDeEventoBuscado() {
    // Preparación
    Paquete casamiento = crearPaquete("Casamiento clásico", TipoPaquete.CASAMIENTO);

    Paquete cumpleanios = crearPaquete("Cumpleaños familiar", TipoPaquete.CUMPLEANIOS);

    Paquete fiestaDe15 = crearPaquete("Fiesta de quince", TipoPaquete.FIESTA_DE_15);

    Paquete cumpleaniosPremium = crearPaquete("Cumpleaños Premium", TipoPaquete.CUMPLEANIOS);

    List<Paquete> disponibles = List.of(casamiento, cumpleanios, fiestaDe15, cumpleaniosPremium);

    when(repositorioPaqueteMock.obtenerTodos()).thenReturn(disponibles);

    // Ejecución
    List<Paquete> obtenidos = servicioBuscarPaquete.buscarPorTipo(TipoPaquete.CUMPLEANIOS);

    // Validación
    assertThat(obtenidos, equalTo(List.of(cumpleanios, cumpleaniosPremium)));
  }

  @Test
  public void deberiaDevolverUnaListaVaciaSiNoHayEventosDelTipoBuscado() {
    // Preparación
    Paquete casamiento = crearPaquete("Casamiento clásico", TipoPaquete.CASAMIENTO);

    Paquete cumpleanios = crearPaquete("Cumpleaños familiar", TipoPaquete.CUMPLEANIOS);

    when(repositorioPaqueteMock.obtenerTodos()).thenReturn(List.of(casamiento, cumpleanios));

    // Ejecución
    List<Paquete> obtenidos = servicioBuscarPaquete.buscarPorTipo(TipoPaquete.EMPRESARIAL);

    // Validación
    assertThat(obtenidos, equalTo(List.of()));
  }
}
