package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Reserva;
import com.tallerwebi.dominio.ServicioReserva;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorReservas {

  private ServicioReserva servicioReserva;

  @Autowired
  public ControladorReservas(ServicioReserva servicioReserva) {
    this.servicioReserva = servicioReserva;
  }

  @RequestMapping(path = "/mis-reservas", method = RequestMethod.GET)
  public ModelAndView misReservas(HttpServletRequest request) {
    Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute("USUARIO");

    Map<String, Object> modelo = new ModelMap();

    if (usuarioLogueado == null) {
      return new ModelAndView("redirect:/login");
    }

    List<Reserva> reservas = servicioReserva.obtenerReservasDe(usuarioLogueado);
    modelo.put("reservas", reservas);
    return new ModelAndView("reservas", modelo);
  }
}
