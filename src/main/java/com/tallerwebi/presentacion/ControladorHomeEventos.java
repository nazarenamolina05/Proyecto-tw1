package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioLoginImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorHomeEventos {

  //  private final ServicioLoginImpl servicioLogin;
  //
  //  public ControladorHomeEventos(ServicioLoginImpl servicioLogin) {
  //    this.servicioLogin = servicioLogin;
  //  }

  @RequestMapping(value = "/home-eventos", method = RequestMethod.GET)
  public ModelAndView home() {
    return new ModelAndView("home-eventos");
  }
}
