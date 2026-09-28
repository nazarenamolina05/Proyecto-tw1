package com.tallerwebi.presentacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Paquete;
import com.tallerwebi.dominio.ServicioConsultarPaquetesPredeterminados;
import com.tallerwebi.dominio.TipoPaquete;
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
    //preparacion
    Paquete paqueteBasico = new Paquete(
      1L,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      "Salón Los Álamos",
      "Catering Don José",
      List.of("DJ"),
      80000.0
    );
    when(servicio.consultarTodos()).thenReturn(List.of(paqueteBasico));

    //ejecucion
    ModelAndView modelAndView = controlador.inicioPagina();

    //validacion
    assertEquals("home-eventos", modelAndView.getViewName());
    assertEquals(List.of(paqueteBasico), modelAndView.getModel().get("paquetes"));
  }
}
