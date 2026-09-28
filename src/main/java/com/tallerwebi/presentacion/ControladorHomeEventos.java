package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Paquete;
import com.tallerwebi.dominio.ServicioConsultarPaquetesPredeterminados;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorHomeEventos {

  private ServicioConsultarPaquetesPredeterminados servicio;

  public ControladorHomeEventos(ServicioConsultarPaquetesPredeterminados servicio) {
    this.servicio = servicio;
  }

  @RequestMapping(value = "/home-eventos")
  public ModelAndView inicioPagina() {
    Map<String, Object> modelo = new ModelMap();
    List<Paquete> paquetes = servicio.consultarTodos();

    modelo.put("paquetes", paquetes);
    return new ModelAndView("home-eventos", modelo);
  }
}
