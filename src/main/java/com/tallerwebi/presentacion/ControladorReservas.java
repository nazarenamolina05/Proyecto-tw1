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

  public static final String EVENTO = "eventoPersonalizado";
  private static final String USUARIO = "USUARIO";
  private static final String RUTA_EVENTO = "/evento-personalizado"; // NUEVO
  private static final String VOLVER_AL_EVENTO = "redirect:" + RUTA_EVENTO;

  private ServicioReserva servicioReserva;

  @Autowired
  public ControladorReservas(ServicioReserva servicioReserva) {
    this.servicioReserva = servicioReserva;
  }

  // muestra el formulario de datos + el ticket con el resumen
  @RequestMapping(path = "/reservar", method = RequestMethod.GET)
  public ModelAndView verResumenDeReserva(HttpServletRequest request) {
    HttpSession session = request.getSession();

    if (session.getAttribute(USUARIO) == null) {
      return irAlLoginYVolverAlEvento(session);
    }
    if (!(session.getAttribute(EVENTO) instanceof Evento evento)) {
      return new ModelAndView(VOLVER_AL_EVENTO);
    }
    if (!evento.isCompleto()) {
      return new ModelAndView(VOLVER_AL_EVENTO + "?error=incompleto");
    }

    Double presupuesto = evento.calcularPresupuesto();
    Map<String, Object> modelo = new ModelMap();
    modelo.put("evento", evento);
    modelo.put("presupuesto", presupuesto);
    modelo.put("precioBase", evento.calcularPrecioBase());
    modelo.put("adicional", evento.calcularAdicional());
    modelo.put("montoSenia", Reserva.calcularSenia(presupuesto));
    modelo.put("porcentajeSenia", (int) (Reserva.PORCENTAJE_SENIA * 100));
    return new ModelAndView("reservar", modelo);
  }

  @RequestMapping(path = "/reservas", method = RequestMethod.POST)
  public ModelAndView reservar(
    @RequestParam("tipoPago") TipoPago tipoPago,
    HttpServletRequest request
  ) {
    HttpSession session = request.getSession();
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);

    if (usuario == null) {
      return irAlLoginYVolverAlEvento(session);
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

  // deja anotado en la sesión a dónde volver y manda al login
  private ModelAndView irAlLoginYVolverAlEvento(HttpSession session) {
    session.setAttribute(ControladorLogin.DESTINO_POST_LOGIN, RUTA_EVENTO);
    return new ModelAndView("redirect:/login");
  }
}
