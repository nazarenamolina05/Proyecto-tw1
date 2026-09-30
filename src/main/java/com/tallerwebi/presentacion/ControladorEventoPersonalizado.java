package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.EventoPersonalizado;
import com.tallerwebi.dominio.ServicioAdicional;
import com.tallerwebi.dominio.ServicioEventoPersonalizado;
import com.tallerwebi.dominio.TipoPaquete;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorEventoPersonalizado {

  private static final String EVENTO = "eventoPersonalizado";

  private ServicioEventoPersonalizado servicioEventoPersonalizado;

  @Autowired
  public ControladorEventoPersonalizado(ServicioEventoPersonalizado servicioEventoPersonalizado) {
    this.servicioEventoPersonalizado = servicioEventoPersonalizado;
  }

  @RequestMapping(value = "/evento-personalizado", method = RequestMethod.GET)
  public ModelAndView verEventoPersonalizado(HttpSession session) {
    ModelAndView modelo = new ModelAndView("evento-personalizado");
    EventoPersonalizado evento = obtenerEvento(session);
    if (evento != null) {
      modelo.addObject("evento", evento);
      modelo.addObject("salones", servicioEventoPersonalizado.obtenerSalones());
      modelo.addObject("caterings", servicioEventoPersonalizado.obtenerCaterings());
      modelo.addObject("servicios", servicioEventoPersonalizado.obtenerServicioAdicionales());
      modelo.addObject("presupuesto", evento.calcularPresupuesto());
      modelo.addObject(
        "salonElegidoId",
        evento.getSalon() == null ? null : evento.getSalon().getId()
      );
      modelo.addObject(
        "cateringElegidoId",
        evento.getCatering() == null ? null : evento.getCatering().getId()
      );
      modelo.addObject(
        "serviciosElegidosIds",
        evento.getServiciosAdicionales().stream().map(ServicioAdicional::getId).toList()
      );
    }
    return modelo;
  }

  @RequestMapping(value = "/evento-personalizado/nuevo", method = RequestMethod.GET)
  public ModelAndView empezarDeCero(HttpSession sesion) {
    sesion.removeAttribute(EVENTO);
    return volverAlEvento();
  }

  @RequestMapping(value = "/evento-personalizado", method = RequestMethod.POST)
  public ModelAndView guardarDatosDelEvento(
    @RequestParam("tipo") TipoPaquete tipo,
    @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
    @RequestParam("cantidadInvitados") Integer cantidadInvitados,
    HttpSession sesion
  ) {
    EventoPersonalizado evento = obtenerEvento(sesion);
    if (evento == null) {
      sesion.setAttribute(
        EVENTO,
        servicioEventoPersonalizado.crearEvento(tipo, fecha, cantidadInvitados)
      );
    } else {
      evento.setTipo(tipo);
      evento.setFecha(fecha);
      evento.setCantidadInvitados(cantidadInvitados);
    }
    return volverAlEvento();
  }

  @RequestMapping(value = "/evento-personalizado/salon", method = RequestMethod.POST)
  public ModelAndView elegirSalon(@RequestParam("id") Long id, HttpSession sesion) {
    EventoPersonalizado evento = obtenerEvento(sesion);
    if (evento != null) {
      servicioEventoPersonalizado.elegirSalon(evento, id);
    }
    return volverAlEvento("salon", id);
  }

  @RequestMapping(value = "/evento-personalizado/catering", method = RequestMethod.POST)
  public ModelAndView elegirCatering(@RequestParam("id") Long id, HttpSession sesion) {
    EventoPersonalizado evento = obtenerEvento(sesion);
    if (evento != null) {
      servicioEventoPersonalizado.elegirCatering(evento, id);
    }
    return volverAlEvento("catering", id);
  }

  @RequestMapping(value = "/evento-personalizado/servicio/agregar", method = RequestMethod.POST)
  public ModelAndView agregarServicio(@RequestParam("id") Long id, HttpSession sesion) {
    EventoPersonalizado evento = obtenerEvento(sesion);
    if (evento != null) {
      servicioEventoPersonalizado.agregarServicio(evento, id);
    }
    return volverAlEvento("servicioAgregado", id);
  }

  @RequestMapping(value = "/evento-personalizado/servicio/quitar", method = RequestMethod.POST)
  public ModelAndView quitarServicio(@RequestParam("id") Long id, HttpSession sesion) {
    EventoPersonalizado evento = obtenerEvento(sesion);
    if (evento != null) {
      servicioEventoPersonalizado.quitarServicio(evento, id);
    }
    return volverAlEvento("servicioQuitado", id);
  }

  @RequestMapping(value = "/evento-personalizado/reservar", method = RequestMethod.POST)
  public ModelAndView reservarEvento(HttpSession sesion) {
    EventoPersonalizado evento = obtenerEvento(sesion);
    if (evento == null) {
      return volverAlEvento();
    }
    if (evento.getSalon() == null || evento.getCatering() == null) {
      return new ModelAndView("redirect:/evento-personalizado?error=incompleto");
    }
    // Acá va la creación de la Reserva (paso 3, abajo)
    return volverAlEvento();
  }

  private EventoPersonalizado obtenerEvento(HttpSession sesion) {
    return (EventoPersonalizado) sesion.getAttribute(EVENTO);
  }

  private ModelAndView volverAlEvento() {
    return new ModelAndView("redirect:/evento-personalizado");
  }

  private ModelAndView volverAlEvento(String clave, Long id) {
    ModelAndView modelo = new ModelAndView("redirect:/evento-personalizado");
    modelo.addObject(clave, id); // se agrega a la URL como ?clave=id
    return modelo;
  }
}
