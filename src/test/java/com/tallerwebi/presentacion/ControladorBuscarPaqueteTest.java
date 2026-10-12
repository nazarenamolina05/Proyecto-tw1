package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Paquete;
import com.tallerwebi.dominio.Salon;
import com.tallerwebi.dominio.ServicioBuscarPaquete;
import com.tallerwebi.dominio.TipoPaquete;
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

    when(servicioBuscarPaqueteMock.buscarPorTipo(TipoPaquete.CUMPLEANIOS))
      .thenReturn(paquetesEsperados);

    // Ejecución
    ModelAndView mav = controladorBuscarPaquete.buscarPorTipo(TipoPaquete.CUMPLEANIOS);

    // Validación
    assertThat(mav.getViewName(), equalTo("paquetes"));
    assertThat(mav.getModel().get("paquetes"), equalTo(paquetesEsperados));
  }

  @Test
  public void deberiaMostrarUnaListaVaciaSiNoHayPaquetesDelTipoIndicado() {
    // Preparación
    List<Paquete> paquetesEsperados = List.of();

    when(servicioBuscarPaqueteMock.buscarPorTipo(TipoPaquete.EMPRESARIAL))
      .thenReturn(paquetesEsperados);

    // Ejecución
    ModelAndView mav = controladorBuscarPaquete.buscarPorTipo(TipoPaquete.EMPRESARIAL);

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
      "Catering de prueba",
      List.of("DJ"),
      100000.0
    );
  }
}
