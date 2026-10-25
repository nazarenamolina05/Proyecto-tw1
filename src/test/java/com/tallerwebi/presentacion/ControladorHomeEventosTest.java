package com.tallerwebi.presentacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorHomeEventosTest {

  private ServicioConsultarPaquetesPredeterminados servicio;
  private ControladorHomeEventos controlador;

  @BeforeEach
  public void init() {
    servicio = mock(ServicioConsultarPaquetesPredeterminados.class);
    controlador = new ControladorHomeEventos(servicio);
  }

  @Test
  public void muestraElHomeConLosPaquetesPredeterminados() {
    // Preparación
    Salon salon = new Salon("Salón Los Álamos", 50000.0);

    Paquete paqueteBasico = new Paquete(
      1L,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      salon,
      new Catering("Catering Basico", 1500.0),
      List.of(new ServicioAdicional("DJ", 40000.0, false)),
      80000.0
    );

    paqueteBasico.setPredeterminado(true);

    List<Paquete> paquetesEsperados = List.of(paqueteBasico);

    when(servicio.consultarTodos()).thenReturn(paquetesEsperados);

    // Ejecución
    ModelAndView modelAndView = controlador.inicioPagina();

    // Validación
    assertEquals("home-eventos", modelAndView.getViewName());
    assertEquals(paquetesEsperados, modelAndView.getModel().get("paquetes"));
  }

  @Test
  public void muestraElHomeSinPaquetesCuandoNoHayPredeterminados() {
    // Preparación
    when(servicio.consultarTodos()).thenReturn(List.of());

    // Ejecución
    ModelAndView modelAndView = controlador.inicioPagina();

    // Validación
    assertEquals("home-eventos", modelAndView.getViewName());
    assertEquals(List.of(), modelAndView.getModel().get("paquetes"));
  }
}
