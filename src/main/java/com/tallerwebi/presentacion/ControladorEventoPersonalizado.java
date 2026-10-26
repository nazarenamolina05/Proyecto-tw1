package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.FechaInvalida;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
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
public class ControladorEventoPersonalizado {

  private static final String EVENTO = "eventoPersonalizado";
  private static final String USUARIO = "USUARIO";
  private static final String MODO = "modo";
  private ServicioEventoPersonalizado servicioEventoPersonalizado;
  private ServicioSugerenciaServicios servicioSugerenciaServicios;
  private ServicioConsultarPaquetesPredeterminados servicioPaquetes;
  private ServicioDisponibilidadSalon servicioDisponibilidadSalon;

  @Autowired
  public ControladorEventoPersonalizado(
    ServicioEventoPersonalizado servicioEventoPersonalizado,
    ServicioSugerenciaServicios servicioSugerenciaServicios,
    ServicioConsultarPaquetesPredeterminados servicioPaquetes,
    ServicioDisponibilidadSalon servicioDisponibilidadSalon
  ) {
    this.servicioEventoPersonalizado = servicioEventoPersonalizado;
    this.servicioSugerenciaServicios = servicioSugerenciaServicios;
    this.servicioPaquetes = servicioPaquetes;
    this.servicioDisponibilidadSalon = servicioDisponibilidadSalon;
  }

  @RequestMapping(value = "/evento-personalizado", method = RequestMethod.GET)
  public ModelAndView verEventoPersonalizado(
    @RequestParam(value = MODO, defaultValue = "predefinidos") String modo,
    HttpSession session
  ) {
    Map<String, Object> modelo = new ModelMap();
    modelo.put(MODO, modo);
    modelo.put("paquetes", servicioPaquetes.consultarTodos());
    EventoPersonalizado evento = obtenerEvento(session);
    if (evento != null) {
      modelo.put("evento", evento);
      modelo.put("salones", obtenerSalonesDisponibles(evento, modelo));
      modelo.put("caterings", servicioEventoPersonalizado.obtenerCaterings());
      //      modelo.put("servicio", servicioEventoPersonalizado.obtenerServicioAdicionales());
      ServiciosClasificados clasificados = servicioSugerenciaServicios.clasificar(evento);
      modelo.put("serviciosSugeridos", clasificados.getSugeridos());
      modelo.put("serviciosRestantes", clasificados.getRestantes());
      modelo.put("presupuesto", evento.calcularPresupuesto());
      modelo.put("salonElegidoId", evento.getSalon() == null ? null : evento.getSalon().getId());
      modelo.put(
        "cateringElegidoId",
        evento.getCatering() == null ? null : evento.getCatering().getId()
      );
      modelo.put(
        "serviciosElegidosIds",
        evento.getServiciosAdicionales().stream().map(ServicioAdicional::getId).toList()
      );
    }

    return new ModelAndView("evento-personalizado", modelo);
  }

  @RequestMapping(value = "/evento-personalizado/nuevo", method = RequestMethod.GET)
  public ModelAndView volverAEmpezar(
    @RequestParam(value = MODO, defaultValue = "desde-cero") String modo,
    HttpSession session
  ) {
    session.removeAttribute(EVENTO);
    return volverAlEvento(MODO, modo);
  }

  @RequestMapping(value = "/evento-personalizado", method = RequestMethod.POST)
  public ModelAndView guardarDatosDelEvento(
    @RequestParam("tipo") TipoPaquete tipo,
    @RequestParam("fecha") LocalDate fecha,
    @RequestParam("cantidadInvitados") Integer cantidadInvitados,
    HttpSession session
  ) {
    EventoPersonalizado evento = obtenerEvento(session);
    if (evento == null) {
      session.setAttribute(
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
  public ModelAndView elegirSalon(@RequestParam("id") Long id, HttpSession session) {
    EventoPersonalizado evento = obtenerEvento(session);
    if (evento != null) {
      servicioEventoPersonalizado.elegirSalon(evento, id);
    }
    return volverAlEvento("salon", id);
  }

  @RequestMapping(value = "/evento-personalizado/catering", method = RequestMethod.POST)
  public ModelAndView elegirCatering(@RequestParam("id") Long id, HttpSession session) {
    EventoPersonalizado evento = obtenerEvento(session);

    if (evento != null) {
      servicioEventoPersonalizado.elegirCatering(evento, id);
    }

    return volverAlEvento("catering", id);
  }

  @RequestMapping(value = "/evento-personalizado/servicio/agregar", method = RequestMethod.POST)
  public ModelAndView agregarServicio(@RequestParam("id") Long id, HttpSession session) {
    EventoPersonalizado evento = obtenerEvento(session);
    if (evento != null) {
      servicioEventoPersonalizado.agregarServicio(evento, id);
    }
    return volverAlEvento("agregarServicio", id);
  }

  @RequestMapping(value = "/evento-personalizado/servicio/quitar", method = RequestMethod.POST)
  public ModelAndView quitarServicio(@RequestParam("id") Long id, HttpSession session) {
    EventoPersonalizado evento = obtenerEvento(session);
    if (evento != null) {
      servicioEventoPersonalizado.quitarServicio(evento, id);
    }
    return volverAlEvento("quitarServicio", id);
  }

  @RequestMapping(value = "/evento-personalizado/reservar", method = RequestMethod.POST)
  public ModelAndView reservarEvento(HttpSession session) {
    EventoPersonalizado evento = obtenerEvento(session);
    Usuario user = obtenerUsuario(session);
    if (evento == null) {
      return volverAlEvento();
    }

    if (user == null) {
      return new ModelAndView("redirect:/login");
    }

    if (evento.getSalon() == null || evento.getCatering() == null) {
      return new ModelAndView("redirect:/evento-personalizado?error=incompleto");
    }
    return volverAlEvento();
  }

  // Solo se ofrecen los salones sin reserva activa en la fecha del evento.
  private List<Salon> obtenerSalonesDisponibles(
    EventoPersonalizado evento,
    Map<String, Object> modelo
  ) {
    try {
      return servicioDisponibilidadSalon.obtenerSalonesDisponibles(
        evento.getFecha(),
        evento.getFecha()
      );
    } catch (FechaInvalida excepcion) {
      modelo.put("errorFecha", "Elegí una fecha posterior a hoy para ver los salones disponibles.");
      return List.of();
    }
  }

  private EventoPersonalizado obtenerEvento(HttpSession sesion) {
    return (EventoPersonalizado) sesion.getAttribute(EVENTO);
  }

  private Usuario obtenerUsuario(HttpSession session) {
    return (Usuario) session.getAttribute(USUARIO);
  }

  private ModelAndView volverAlEvento() {
    return new ModelAndView("redirect:/evento-personalizado");
  }

  private ModelAndView volverAlEvento(String clave, Object valor) {
    Map<String, Object> modelo = new ModelMap();
    modelo.put(clave, valor);
    return new ModelAndView("redirect:/evento-personalizado", modelo);
  }
}
