package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.FechaInvalida;
import com.tallerwebi.dominio.excepcion.PaqueteNoEncontrado;
import com.tallerwebi.dominio.excepcion.SeleccionInvalida;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorEventoPersonalizado {

  private static final String EVENTO = "eventoPersonalizado";
  private static final String MODO = "modo";
  private static final String PAQUETE_PENDIENTE = "paqueteAModificar";
  private static final String ERROR_PAQUETE = "errorPaquete";
  private static final String DATOS = "datos";

  private ServicioEvento servicioEvento;
  private ServicioSugerenciaServicios servicioSugerenciaServicios;
  private ServicioConsultarPaquetesPredeterminados servicioPaquetes;
  private ServicioDisponibilidadSalon servicioDisponibilidadSalon;
  private ServicioValidarSeleccionPaquete servicioValidacion;

  @Autowired
  public ControladorEventoPersonalizado(
    ServicioEvento servicioEvento,
    ServicioSugerenciaServicios servicioSugerenciaServicios,
    ServicioConsultarPaquetesPredeterminados servicioPaquetes,
    ServicioDisponibilidadSalon servicioDisponibilidadSalon,
    ServicioValidarSeleccionPaquete servicioValidacion
  ) {
    this.servicioEvento = servicioEvento;
    this.servicioSugerenciaServicios = servicioSugerenciaServicios;
    this.servicioPaquetes = servicioPaquetes;
    this.servicioDisponibilidadSalon = servicioDisponibilidadSalon;
    this.servicioValidacion = servicioValidacion;
  }

  @RequestMapping(value = "/evento-personalizado", method = RequestMethod.GET)
  public ModelAndView verEventoPersonalizado(
    @RequestParam(value = MODO, defaultValue = "predefinidos") String modo,
    HttpSession session
  ) {
    Map<String, Object> modelo = new ModelMap();
    modelo.put(MODO, modo);
    List<Paquete> paquetes = servicioPaquetes.consultarTodos();
    modelo.put("paquetes", paquetes);
    // [REFACTOR PAQUETE -> EVENTO] paquete-card.html lee los servicios de este mapa.
    modelo.put("serviciosPorPaquete", servicioPaquetes.consultarServiciosPorPaquete(paquetes));
    Evento evento = obtenerEvento(session);
    Long pendiente = (Long) session.getAttribute(PAQUETE_PENDIENTE);
    if (evento == null && pendiente != null) {
      modelo.put("paqueteSeleccionadoId", pendiente);
    }
    Object errorPaquete = session.getAttribute(ERROR_PAQUETE);
    if (errorPaquete != null) {
      modelo.put("errorPaquete", errorPaquete);
      session.removeAttribute(ERROR_PAQUETE);
    }
    if (evento != null) {
      modelo.put("evento", evento);

      modelo.put("salones", obtenerSalonesDisponibles(evento, modelo));
      modelo.put("caterings", servicioEvento.obtenerCaterings());

      ServiciosClasificados clasificados = servicioSugerenciaServicios.clasificar(evento);
      modelo.put("serviciosSugeridos", clasificados.getSugeridos());
      modelo.put("serviciosRestantes", clasificados.getRestantes());
      // [REFACTOR EVENTO] Los servicios del evento se consultan aparte (EventoServicio).
      // evento-personalizado.html los lee de ${serviciosDelEvento}.
      List<ServicioAdicional> serviciosDelEvento = servicioEvento.obtenerServiciosDe(evento);
      modelo.put("serviciosDelEvento", serviciosDelEvento);
      modelo.put("presupuesto", evento.calcularPresupuesto(serviciosDelEvento));
      // [REFACTOR PAQUETE -> EVENTO] Los servicios del paquete de origen se consultan
      // aparte y se pasan a Evento (antes Evento los sacaba de paqueteOrigen).
      List<ServicioAdicional> serviciosDelPaquete = servicioPaquetes.consultarServiciosDe(
        evento.getPaqueteOrigen()
      );
      modelo.put("precioBase", evento.calcularPrecioBase(serviciosDelPaquete));
      modelo.put("adicional", evento.calcularAdicional(serviciosDelEvento, serviciosDelPaquete));
      modelo.put("salonElegidoId", evento.getSalon() == null ? null : evento.getSalon().getId());
      modelo.put(
        "cateringElegidoId",
        evento.getCatering() == null ? null : evento.getCatering().getId()
      );

      modelo.put(
        "serviciosElegidosIds",
        serviciosDelEvento.stream().map(ServicioAdicional::getId).toList()
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
    session.removeAttribute(PAQUETE_PENDIENTE);
    return volverAlEvento(MODO, modo);
  }

  @RequestMapping(value = "/evento-personalizado", method = RequestMethod.POST)
  public ModelAndView guardarDatosDelEvento(
    @ModelAttribute(DATOS) DatosEventoForm datos,
    HttpSession session
  ) {
    Evento evento = obtenerEvento(session);
    if (evento == null) {
      session.setAttribute(
        EVENTO,
        servicioEvento.crearEvento(datos.getTipo(), datos.getFecha(), datos.getCantidadInvitados())
      );
    } else {
      // [REFACTOR EVENTO] El servicio actualiza los datos y los guarda en la base.
      servicioEvento.actualizarDatos(
        evento,
        datos.getTipo(),
        datos.getFecha(),
        datos.getCantidadInvitados()
      );
    }
    return volverAlEvento();
  }

  @RequestMapping(
    value = "/evento-personalizado",
    method = RequestMethod.POST,
    params = "paqueteId"
  )
  public ModelAndView modificarPaqueteConDatos(
    @ModelAttribute(DATOS) DatosEventoForm datos,
    HttpSession session
  ) {
    try {
      servicioValidacion.validar(
        datos.getPaqueteId(),
        datos.getTipo(),
        datos.getFecha(),
        datos.getCantidadInvitados()
      );
      Paquete paquete = servicioPaquetes.consultarPorId(datos.getPaqueteId());
      // [REFACTOR EVENTO] El servicio crea el evento, lo guarda y le copia los servicios
      // del paquete (antes lo hacía paquete.crearEvento en memoria).
      session.setAttribute(
        EVENTO,
        servicioEvento.crearEventoDesdePaquete(
          paquete,
          datos.getTipo(),
          datos.getFecha(),
          datos.getCantidadInvitados()
        )
      );
      session.removeAttribute(PAQUETE_PENDIENTE);
    } catch (SeleccionInvalida e) {
      session.setAttribute(ERROR_PAQUETE, e.getMessage());
    } catch (PaqueteNoEncontrado e) {
      return new ModelAndView("redirect:/home-eventos");
    }
    return volverAlEvento();
  }

  @RequestMapping(value = "/evento-personalizado/salon", method = RequestMethod.POST)
  public ModelAndView elegirSalon(@ModelAttribute(DATOS) DatosIdForm datos, HttpSession session) {
    Evento evento = obtenerEvento(session);
    if (evento != null) {
      servicioEvento.elegirSalon(evento, datos.getId());
    }
    return volverAlEvento("salon", datos.getId());
  }

  @RequestMapping(value = "/evento-personalizado/catering", method = RequestMethod.POST)
  public ModelAndView elegirCatering(
    @ModelAttribute(DATOS) DatosIdForm datos,
    HttpSession session
  ) {
    Evento evento = obtenerEvento(session);

    if (evento != null) {
      servicioEvento.elegirCatering(evento, datos.getId());
    }

    return volverAlEvento("catering", datos.getId());
  }

  @RequestMapping(value = "/evento-personalizado/servicio/agregar", method = RequestMethod.POST)
  public ModelAndView agregarServicio(
    @ModelAttribute(DATOS) DatosIdForm datos,
    HttpSession session
  ) {
    Evento evento = obtenerEvento(session);
    if (evento != null) {
      servicioEvento.agregarServicio(evento, datos.getId());
    }
    return volverAlEvento("agregarServicio", datos.getId());
  }

  @RequestMapping(value = "/evento-personalizado/servicio/quitar", method = RequestMethod.POST)
  public ModelAndView quitarServicio(
    @ModelAttribute(DATOS) DatosIdForm datos,
    HttpSession session
  ) {
    Evento evento = obtenerEvento(session);
    if (evento != null) {
      servicioEvento.quitarServicio(evento, datos.getId());
    }
    return volverAlEvento("quitarServicio", datos.getId());
  }

  private List<Salon> obtenerSalonesDisponibles(Evento evento, Map<String, Object> modelo) {
    try {
      return servicioDisponibilidadSalon.obtenerSalonesDisponibles(
        evento.getFecha(),
        evento.getFecha()
      );
    } catch (FechaInvalida excepcion) {
      modelo.put("errorFecha", "Elegí una fecha posterior a hoy para ver los salones disponibles.");
      return evento.getSalon() == null ? List.of() : List.of(evento.getSalon());
    }
  }

  @RequestMapping(value = "/evento-personalizado/paquete/{id}", method = RequestMethod.GET)
  public ModelAndView modificarPaquete(@PathVariable("id") Long id, HttpSession session) {
    try {
      servicioPaquetes.consultarPorId(id);
    } catch (PaqueteNoEncontrado e) {
      return new ModelAndView("redirect:/home-eventos");
    }
    session.removeAttribute(EVENTO);
    session.setAttribute(PAQUETE_PENDIENTE, id);
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
