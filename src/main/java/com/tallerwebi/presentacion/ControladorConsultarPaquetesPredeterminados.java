package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Paquete;
import com.tallerwebi.dominio.ServicioConsultarPaquetesPredeterminados;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorConsultarPaquetesPredeterminados {

  private ServicioConsultarPaquetesPredeterminados servicio;

  public ControladorConsultarPaquetesPredeterminados(ServicioConsultarPaquetesPredeterminados servicio) {
    this.servicio = servicio;
  }

  @GetMapping(path = "/paquetes-predeterminados")
  public ModelAndView listarPaquetesPredeterminados() {
    Map<String, Object> modelo = new ModelMap();
    List<Paquete> paquetes = servicio.consultarTodos();
    modelo.put("paquetes", paquetes);
    return new ModelAndView("paquetes-predeterminados", modelo);
  }

  @GetMapping(path = "/paquetes-predeterminados/{id}")
  public ModelAndView mostrarDetalle(@PathVariable("id") Long id) {
    Map<String, Object> modelo = new ModelMap();
    Paquete paquete = servicio.consultarPorId(id);

    modelo.put("paquete", paquete);
    return new ModelAndView("paquete-detalle", modelo);
  }



}
