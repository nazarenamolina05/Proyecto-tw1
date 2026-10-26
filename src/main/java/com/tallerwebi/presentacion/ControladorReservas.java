package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.EventoIncompleto;
import com.tallerwebi.dominio.excepcion.FechaInvalida;
import com.tallerwebi.dominio.excepcion.SalonNoDisponible;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorReservas {

  private static final String EVENTO = "eventoPersonalizado";
  private static final String USUARIO = "USUARIO";
  private static final String VOLVER_AL_EVENTO = "redirect:/evento-personalizado";

  private ServicioReserva servicioReserva;

  @Autowired
  public ControladorReservas(ServicioReserva servicioReserva) {
    this.servicioReserva = servicioReserva;
  }

  @RequestMapping(path = "/reservas", method = RequestMethod.POST)
  public ModelAndView reservar(
    @RequestParam("tipoPago") TipoPago tipoPago,
    HttpServletRequest request
  ) {
    HttpSession session = request.getSession();
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }
    if (!(session.getAttribute(EVENTO) instanceof Evento evento)) {
      return new ModelAndView(VOLVER_AL_EVENTO);
    }

    try {
      servicioReserva.reservar(usuario, evento, tipoPago);
    } catch (SalonNoDisponible e) {
      return new ModelAndView(VOLVER_AL_EVENTO + "?error=salonNoDisponible");
    } catch (FechaInvalida e) {
      return new ModelAndView(VOLVER_AL_EVENTO + "?error=fecha");
    } catch (EventoIncompleto e) {
      return new ModelAndView(VOLVER_AL_EVENTO + "?error=incompleto");
    }

    session.removeAttribute(EVENTO);
    return new ModelAndView("redirect:/mis-reservas");
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
