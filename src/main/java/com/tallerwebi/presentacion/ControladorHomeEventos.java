package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorHomeEventos {

  @RequestMapping(value = "/home-eventos")
  public ModelAndView inicioPagina() {
    return new ModelAndView("home-eventos");
  }
}
