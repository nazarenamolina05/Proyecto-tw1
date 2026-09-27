package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Paquete;
import com.tallerwebi.dominio.ServicioBuscarPaquete;
import com.tallerwebi.dominio.TipoPaquete;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorBuscarPaquete {

  private ServicioBuscarPaquete servicioBuscarPaquete;

  public ControladorBuscarPaquete(ServicioBuscarPaquete servicioBuscarPaquete) {
    this.servicioBuscarPaquete = servicioBuscarPaquete;
  }

  @GetMapping(path = "/paquetes")
  public ModelAndView buscarPorTipo(@RequestParam("tipo") TipoPaquete tipoPaquete) {
    Map<String, Object> modelo = new ModelMap();
    List<Paquete> paquetesBuscados = servicioBuscarPaquete.buscarPorTipo(tipoPaquete);

    modelo.put("paquetes", paquetesBuscados);
    return new ModelAndView("paquetes", modelo);
  }
}
