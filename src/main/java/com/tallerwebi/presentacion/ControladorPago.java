package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.EstadoPago;
import com.tallerwebi.dominio.ServicioPago;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorPago {

  private static final Log LOG = LogFactory.getLog(ControladorPago.class);
  private static final String USUARIO = "USUARIO";
  private static final String MIS_RESERVAS = "redirect:/mis-reservas";
  private static final String MIS_RESERVAS_CON_ERROR = "redirect:/mis-reservas?error=pago";
  private static final String MIS_RESERVAS_ERROR_MONTO = "redirect:/mis-reservas?error=monto";
  private static final String MIS_RESERVAS_ERROR_ABONO = "redirect:/mis-reservas?error=abono";

  private ServicioPago servicioPago;

  @Autowired
  public ControladorPago(ServicioPago servicioPago) {
    this.servicioPago = servicioPago;
  }

  @RequestMapping(path = "/pago/iniciar/{reservaId}", method = RequestMethod.GET)
  public ModelAndView iniciarPago(
    @PathVariable("reservaId") Long reservaId,
    HttpServletRequest request
  ) {
    Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    try {
      String urlDePago = servicioPago.iniciarPago(reservaId, usuario);
      return new ModelAndView("redirect:" + urlDePago);
    } catch (ReservaNoEncontrada e) {
      LOG.warn("Reserva no encontrada al iniciar el pago", e);
      return new ModelAndView(MIS_RESERVAS);
    } catch (PasarelaNoDisponible e) {
      LOG.error("No se pudo iniciar el pago con Mercado Pago", e);
      return new ModelAndView(MIS_RESERVAS_CON_ERROR);
    } catch (ReservaCancelada e) {
      return new ModelAndView(MIS_RESERVAS + "?error=cancelada");
    }
  }

  @RequestMapping(path = "/pago/abonar/{reservaId}", method = RequestMethod.POST)
  public ModelAndView abonar(
    @PathVariable("reservaId") Long reservaId,
    @ModelAttribute("datosAbono") DatosAbono datosAbono,
    HttpServletRequest request
  ) {
    Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    try {
      // si el monto llega vacío queda en null: el servicio lo rechaza con MontoDeAbonoInvalido
      String urlDePago = servicioPago.iniciarAbono(reservaId, usuario, datosAbono.getMonto());
      return new ModelAndView("redirect:" + urlDePago);
    } catch (ReservaNoEncontrada e) {
      LOG.warn("Reserva no encontrada al iniciar el abono", e);
      return new ModelAndView(MIS_RESERVAS);
    } catch (MontoDeAbonoInvalido e) {
      return new ModelAndView(MIS_RESERVAS_ERROR_MONTO);
    } catch (AbonoNoPermitido e) {
      return new ModelAndView(MIS_RESERVAS_ERROR_ABONO);
    } catch (PasarelaNoDisponible e) {
      LOG.error("No se pudo iniciar el abono con Mercado Pago", e);
      return new ModelAndView(MIS_RESERVAS_CON_ERROR);
    }
  }

  @RequestMapping(path = "/pago/exito", method = RequestMethod.GET)
  public ModelAndView pagoExitoso(
    @RequestParam("external_reference") Long pagoId,
    @RequestParam("payment_id") Long mpPaymentId
  ) {
    try {
      EstadoPago estado = servicioPago.confirmarPago(pagoId, mpPaymentId);
      return new ModelAndView(this.volverAMisReservasSegun(estado));
    } catch (PagoNoEncontrado | PagoNoCoincide | PasarelaNoDisponible e) {
      LOG.error("No se pudo confirmar el pago", e);
      return new ModelAndView(MIS_RESERVAS_CON_ERROR);
    }
  }

  @RequestMapping(path = "/pago/fallo", method = RequestMethod.GET)
  public ModelAndView pagoFallido() {
    return new ModelAndView(MIS_RESERVAS + "?pago=rechazado");
  }

  @RequestMapping(path = "/pago/pendiente", method = RequestMethod.GET)
  public ModelAndView pagoPendiente() {
    return new ModelAndView(MIS_RESERVAS + "?pago=pendiente");
  }

  private String volverAMisReservasSegun(EstadoPago estado) {
    if (estado == EstadoPago.APROBADO) {
      return MIS_RESERVAS + "?pago=aprobado";
    }
    if (estado == EstadoPago.RECHAZADO) {
      return MIS_RESERVAS + "?pago=rechazado";
    }
    return MIS_RESERVAS + "?pago=pendiente";
  }
}
