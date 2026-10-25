package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorBuscarPaqueteTest {

  private ServicioBuscarPaquete servicioBuscarPaqueteMock;
  private ControladorBuscarPaquete controladorBuscarPaquete;

  @BeforeEach
  public void init() {
    servicioBuscarPaqueteMock = mock(ServicioBuscarPaquete.class);
    controladorBuscarPaquete = new ControladorBuscarPaquete(servicioBuscarPaqueteMock);
  }

  @Test
  public void deberiaMostrarLosPaquetesDelTipoIndicado() {
    // Preparación
    Paquete cumpleanios = crearPaquete("Cumpleaños familiar", TipoPaquete.CUMPLEANIOS);

    Paquete cumpleaniosPremium = crearPaquete("Cumpleaños Premium", TipoPaquete.CUMPLEANIOS);

    List<Paquete> paquetesEsperados = List.of(cumpleanios, cumpleaniosPremium);

    when(servicioBuscarPaqueteMock.buscarPorFiltros(TipoPaquete.CUMPLEANIOS, null, null))
      .thenReturn(paquetesEsperados);

    //Ejecución
    ModelAndView mav = controladorBuscarPaquete.buscarPorFiltros(
      TipoPaquete.CUMPLEANIOS,
      null,
      null
    );

    // Validación
    assertThat(mav.getViewName(), equalTo("paquetes"));
    assertThat(mav.getModel().get("paquetes"), equalTo(paquetesEsperados));
  }

  @Test
  public void deberiaMostrarUnaListaVaciaSiNoHayPaquetesDelTipoIndicado() {
    // Preparación
    List<Paquete> paquetesEsperados = List.of();

    when(servicioBuscarPaqueteMock.buscarPorFiltros(TipoPaquete.EMPRESARIAL, null, null))
      .thenReturn(paquetesEsperados);

    // Ejecución
    ModelAndView mav = controladorBuscarPaquete.buscarPorFiltros(
      TipoPaquete.EMPRESARIAL,
      null,
      null
    );

    // Validación
    assertThat(mav.getViewName(), equalTo("paquetes"));
    assertThat(mav.getModel().get("paquetes"), equalTo(paquetesEsperados));
  }

  @Test
  public void deberiaMostrarLosPaquetesQueCumplenLosTresFiltros() {
    // Preparación
    Salon salon = new Salon("Salón grande", 90000.0);
    salon.setCapacidad(120);

    Paquete cumpleanios = new Paquete(
      null,
      "Cumpleaños familiar",
      TipoPaquete.CUMPLEANIOS,
      salon,
      new Catering("Catering básico", 1500.0),
      List.of(new ServicioAdicional("DJ", 40000.0, false)),
      200000.0
    );

    List<Paquete> paquetesEsperados = List.of(cumpleanios);

    when(servicioBuscarPaqueteMock.buscarPorFiltros(TipoPaquete.CUMPLEANIOS, 250000.0, 80))
      .thenReturn(paquetesEsperados);

    // Ejecución
    ModelAndView mav = controladorBuscarPaquete.buscarPorFiltros(
      TipoPaquete.CUMPLEANIOS,
      250000.0,
      80
    );

    // Validación
    assertThat(mav.getViewName(), equalTo("paquetes"));

    assertThat(mav.getModel().get("paquetes"), equalTo(paquetesEsperados));
  }

  // Método auxiliar para crear paquetes en los tests
  private Paquete crearPaquete(String nombre, TipoPaquete tipo) {
    return new Paquete(
      null,
      nombre,
      tipo,
      new Salon("Salón de prueba", 50000.0),
      new Catering("Catering de prueba", 1500.0),
      List.of(new ServicioAdicional("DJ", 40000.0, false)),
      100000.0
    );
  }
}
