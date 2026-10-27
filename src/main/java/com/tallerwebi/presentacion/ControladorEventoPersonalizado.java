package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.FechaInvalida;
import com.tallerwebi.dominio.excepcion.PaqueteNoEncontrado;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorEventoPersonalizado {

  private static final String EVENTO = "eventoPersonalizado";
  private static final String MODO = "modo";
  private ServicioEvento servicioEvento;
  private ServicioSugerenciaServicios servicioSugerenciaServicios;
  private ServicioConsultarPaquetesPredeterminados servicioPaquetes;
  private ServicioDisponibilidadSalon servicioDisponibilidadSalon;

  @Autowired
  public ControladorEventoPersonalizado(
    ServicioEvento servicioEvento,
    ServicioSugerenciaServicios servicioSugerenciaServicios,
    ServicioConsultarPaquetesPredeterminados servicioPaquetes,
    ServicioDisponibilidadSalon servicioDisponibilidadSalon
  ) {
    this.servicioEvento = servicioEvento;
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
    Evento evento = obtenerEvento(session);
    if (evento != null) {
      modelo.put("evento", evento);
      modelo.put("salones", obtenerSalonesDisponibles(evento, modelo));
      modelo.put("caterings", servicioEvento.obtenerCaterings());
      //      modelo.put("servicio", servicioEvento.obtenerServicioAdicionales());
      ServiciosClasificados clasificados = servicioSugerenciaServicios.clasificar(evento);
      modelo.put("serviciosSugeridos", clasificados.getSugeridos());
      modelo.put("serviciosRestantes", clasificados.getRestantes());
      modelo.put("presupuesto", evento.calcularPresupuesto());
      modelo.put("precioBase", evento.calcularPrecioBase());
      modelo.put("adicional", evento.calcularAdicional());
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
    Evento evento = obtenerEvento(session);
    if (evento == null) {
      session.setAttribute(EVENTO, servicioEvento.crearEvento(tipo, fecha, cantidadInvitados));
    } else {
      evento.setTipoPaquete(tipo);
      evento.setFecha(fecha);
      evento.setCantidadInvitados(cantidadInvitados);
    }
    return volverAlEvento();
  }

  @RequestMapping(value = "/evento-personalizado/salon", method = RequestMethod.POST)
  public ModelAndView elegirSalon(@RequestParam("id") Long id, HttpSession session) {
    Evento evento = obtenerEvento(session);
    if (evento != null) {
      servicioEvento.elegirSalon(evento, id);
    }
    return volverAlEvento("salon", id);
  }

  @RequestMapping(value = "/evento-personalizado/catering", method = RequestMethod.POST)
  public ModelAndView elegirCatering(@RequestParam("id") Long id, HttpSession session) {
    Evento evento = obtenerEvento(session);

    if (evento != null) {
      servicioEvento.elegirCatering(evento, id);
    }

    return volverAlEvento("catering", id);
  }

  @RequestMapping(value = "/evento-personalizado/servicio/agregar", method = RequestMethod.POST)
  public ModelAndView agregarServicio(@RequestParam("id") Long id, HttpSession session) {
    Evento evento = obtenerEvento(session);
    if (evento != null) {
      servicioEvento.agregarServicio(evento, id);
    }
    return volverAlEvento("agregarServicio", id);
  }

  @RequestMapping(value = "/evento-personalizado/servicio/quitar", method = RequestMethod.POST)
  public ModelAndView quitarServicio(@RequestParam("id") Long id, HttpSession session) {
    Evento evento = obtenerEvento(session);
    if (evento != null) {
      servicioEvento.quitarServicio(evento, id);
    }
    return volverAlEvento("quitarServicio", id);
  }

  //  @RequestMapping(value = "/evento-personalizado/reservar", method = RequestMethod.POST)
  //  public ModelAndView reservarEvento(HttpSession session) {
  //    Evento evento = obtenerEvento(session);
  //    Usuario user = obtenerUsuario(session);
  //    if (evento == null) {
  //      return volverAlEvento();
  //    }
  //
  //    if (user == null) {
  //      return new ModelAndView("redirect:/login");
  //    }
  //
  //    if (evento.getSalon() == null || evento.getCatering() == null) {
  //      return new ModelAndView("redirect:/evento-personalizado?error=incompleto");
  //    }
  //    return volverAlEvento();
  //  }

  // Solo se ofrecen los salones sin reserva activa en la fecha del evento.
  private List<Salon> obtenerSalonesDisponibles(Evento evento, Map<String, Object> modelo) {
    try {
      return servicioDisponibilidadSalon.obtenerSalonesDisponibles(
        evento.getFecha(),
        evento.getFecha()
      );
    } catch (FechaInvalida excepcion) {
      modelo.put("errorFecha", "Elegí una fecha posterior a hoy para ver los salones disponibles.");
      // Sin fecha válida no sabemos qué salones están libres,
      // pero si el evento ya trae uno (el del paquete) lo seguimos mostrando.
      return evento.getSalon() == null ? List.of() : List.of(evento.getSalon());
    }
  }

  @RequestMapping(value = "/evento-personalizado/paquete/{id}", method = RequestMethod.GET)
  public ModelAndView modificarPaquete(@PathVariable("id") Long id, HttpSession session) {
    try {
      Paquete paquete = servicioPaquetes.consultarPorId(id);
      Evento anterior = obtenerEvento(session);

      LocalDate fecha = anterior == null ? null : anterior.getFecha();
      Integer invitados = anterior == null
        ? paquete.getSalon().getCapacidad()
        : anterior.getCantidadInvitados();

      session.setAttribute(EVENTO, paquete.crearEvento(paquete.getTipo(), fecha, invitados));
    } catch (PaqueteNoEncontrado e) {
      return new ModelAndView("redirect:/home-eventos");
    }
    return volverAlEvento();
  }

  private Evento obtenerEvento(HttpSession sesion) {
    return (Evento) sesion.getAttribute(EVENTO);
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
