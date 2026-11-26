//package com.tallerwebi.presentacion;
//
//import com.tallerwebi.dominio.*;
//import com.tallerwebi.dominio.excepcion.FechaInvalida;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@Controller
//public class ControladorArmadoAutomatico {
//
//  private static final String EVENTO = "eventoPersonalizado";
//  private static final String ERROR = "errorArmado";
//  private static final String MONTO_MINIMO = "montoMinimo";
//
//  private ServicioArmadoAutomatico servicioArmadoAutomatico;
//  private ServicioEvento servicioEvento;
//
//  @Autowired
//  public ControladorArmadoAutomatico(
//    ServicioArmadoAutomatico servicioArmadoAutomatico,
//    ServicioEvento servicioEvento
//  ) {
//    this.servicioArmadoAutomatico = servicioArmadoAutomatico;
//    this.servicioEvento = servicioEvento;
//  }
//
//  @RequestMapping(value = "/evento-personalizado/armado-automatico", method = RequestMethod.POST)
//  public ModelAndView armarAutomaticamente(
//    @ModelAttribute("datos") ArmadoAutomaticoForm datos,
//    HttpSession session,
//    RedirectAttributes redirect
//  ) {
//    if (datosIncompletos(datos)) {
//      redirect.addFlashAttribute(ERROR, "Completá todos los datos con valores mayores a cero.");
//      return volverAlEvento();
//    }
//
//    ResultadoArmado resultado;
//    try {
//      resultado =
//        servicioArmadoAutomatico.armar(
//          datos.getTipo(),
//          datos.getCantidadInvitados(),
//          datos.getFecha(),
//          datos.getPresupuestoMaximo(),
//          datos.getPrioridad()
//        );
//    } catch (FechaInvalida e) {
//      redirect.addFlashAttribute(ERROR, "Elegí una fecha posterior a hoy.");
//      return volverAlEvento();
//    }
//
//    if (resultado.getTipoResultado() == TipoResultado.ARMADO) {
//      Evento evento = servicioEvento.crearEventoDesdeArmadoAutomatico(
//        datos.getTipo(),
//        datos.getFecha(),
//        datos.getCantidadInvitados(),
//        resultado
//      );
//      session.setAttribute(EVENTO, evento);
//    } else if (resultado.getTipoResultado() == TipoResultado.PRESUPUESTO_INSUFICIENTE) {
//      redirect.addFlashAttribute(ERROR, "El presupuesto no alcanza para armar el evento.");
//      redirect.addFlashAttribute(MONTO_MINIMO, resultado.getMontoMinimo());
//    } else {
//      redirect.addFlashAttribute(
//        ERROR,
//        "No hay salones ni catering disponibles para esa fecha y cantidad de invitados."
//      );
//    }
//
//    return volverAlEvento();
//  }
//
//  private boolean datosIncompletos(ArmadoAutomaticoForm datos) {
//    return (
//      datos.getTipo() == null ||
//      datos.getFecha() == null ||
//      datos.getPrioridad() == null ||
//      datos.getCantidadInvitados() == null ||
//      datos.getCantidadInvitados() <= 0 ||
//      datos.getPresupuestoMaximo() == null ||
//      datos.getPresupuestoMaximo() <= 0
//    );
//  }
//
//  private ModelAndView volverAlEvento() {
//    return new ModelAndView("redirect:/evento-personalizado");
//  }
//}

package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.FechaInvalida;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorArmadoAutomatico {

  private static final String EVENTO = "eventoPersonalizado";
  private static final String ERROR = "errorArmado";

  private ServicioArmadoAutomatico servicioArmadoAutomatico;
  private ServicioEvento servicioEvento;

  @Autowired
  public ControladorArmadoAutomatico(
    ServicioArmadoAutomatico servicioArmadoAutomatico,
    ServicioEvento servicioEvento
  ) {
    this.servicioArmadoAutomatico = servicioArmadoAutomatico;
    this.servicioEvento = servicioEvento;
  }

  @RequestMapping(value = "/evento-personalizado/armado-automatico", method = RequestMethod.POST)
  public ModelAndView armarAutomaticamente(
    @ModelAttribute("datos") ArmadoAutomaticoForm datos,
    HttpSession session,
    RedirectAttributes redirect
  ) {
    Evento evento = (Evento) session.getAttribute(EVENTO);
    if (evento == null) {
      return volverAlEvento();
    }

    if (!datosValidos(datos, evento)) {
      redirect.addFlashAttribute(
        ERROR,
        "Ingresá un presupuesto mayor a cero y elegí qué priorizar."
      );

      return volverAlEvento();
    }

    session.setAttribute("armadoEventoId", evento.getId());
    session.setAttribute("armadoPresupuesto", datos.getPresupuestoMaximo());
    session.setAttribute("armadoPrioridad", datos.getPrioridad());

    ResultadoArmado resultado;
    try {
      resultado =
        servicioArmadoAutomatico.armar(
          evento.getTipoPaquete(),
          evento.getCantidadInvitados(),
          evento.getFecha(),
          datos.getPresupuestoMaximo(),
          datos.getPrioridad()
        );
    } catch (FechaInvalida e) {
      redirect.addFlashAttribute(
        ERROR,
        "Elegí una fecha posterior a hoy para poder armar el evento."
      );
      return volverAlEvento();
    }

    if (resultado.getTipoResultado() == TipoResultado.ARMADO) {
      servicioEvento.aplicarArmado(evento, resultado);
    } else if (resultado.getTipoResultado() == TipoResultado.PRESUPUESTO_INSUFICIENTE) {
      redirect.addFlashAttribute(ERROR, "Con ese presupuesto no alcanza para armar el evento.");
    } else {
      redirect.addFlashAttribute(
        ERROR,
        "No hay salones ni catering disponibles para esa fecha y cantidad de invitados."
      );
    }

    return volverAlEvento();
  }

  private boolean datosValidos(ArmadoAutomaticoForm datos, Evento evento) {
    return (
      datos.getPresupuestoMaximo() != null &&
      datos.getPresupuestoMaximo() > 0 &&
      datos.getPrioridad() != null &&
      evento.getTipoPaquete() != null &&
      evento.getFecha() != null &&
      evento.getCantidadInvitados() != null &&
      evento.getCantidadInvitados() > 0
    );
  }

  private ModelAndView volverAlEvento() {
    return new ModelAndView("redirect:/evento-personalizado");
  }
}
