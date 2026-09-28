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

public class ControladorConsultarPaquetesPredeterminadosTest {

  private ServicioConsultarPaquetesPredeterminados servicio;
  private ControladorConsultarPaquetesPredeterminados controlador;

  @BeforeEach
  public void init() {
    servicio = mock(ServicioConsultarPaquetesPredeterminados.class);
    controlador = new ControladorConsultarPaquetesPredeterminados(servicio);
  }

  @Test
  public void muestraElDetalleDeUnPaqueteSegunSuId() {
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
    when(servicio.consultarPorId(1L)).thenReturn(paqueteBasico);

    //ejecucion
    ModelAndView modelAndView = controlador.mostrarDetalle(1L);

    //validacion
    assertEquals("paquete-detalle", modelAndView.getViewName());
    assertEquals(paqueteBasico, modelAndView.getModel().get("paquete"));
  }
}
