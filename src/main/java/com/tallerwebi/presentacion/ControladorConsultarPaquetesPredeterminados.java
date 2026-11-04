package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.PaqueteNoEncontrado;
import com.tallerwebi.dominio.excepcion.SeleccionInvalida;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorConsultarPaquetesPredeterminados {

  private ServicioConsultarPaquetesPredeterminados servicio;
  private ServicioValidarSeleccionPaquete servicioValidacion;
  private ServicioEvento servicioEvento;

  public ControladorConsultarPaquetesPredeterminados(
    ServicioConsultarPaquetesPredeterminados servicio,
    ServicioValidarSeleccionPaquete servicioValidacion,
    ServicioEvento servicioEvento
  ) {
    this.servicio = servicio;
    this.servicioValidacion = servicioValidacion;
    this.servicioEvento = servicioEvento;
  }

  @GetMapping(path = "/paquetes-predeterminados/{id}")
  public ModelAndView mostrarDetalle(@PathVariable("id") Long id) {
    return new ModelAndView("paquete-detalle", armarModeloDetalle(id));
  }

  @PostMapping(path = "/paquetes-predeterminados/{id}/reservar")
  public ModelAndView reservar(
    @PathVariable("id") Long id,
    @ModelAttribute("datos") DatosReservaPaquete datos,
    HttpSession session
  ) {
    Map<String, Object> modelo = armarModeloDetalle(id);
    modelo.put("tipoSeleccionado", datos.getTipoEvento());
    modelo.put("fechaSeleccionada", datos.getFecha() == null ? null : datos.getFecha().toString());
    modelo.put("invitadosSeleccionados", datos.getCantidadInvitados());
    try {
      servicioValidacion.validar(
        id,
        datos.getTipoEvento(),
        datos.getFecha(),
        datos.getCantidadInvitados()
      );

      Paquete paquete = servicio.consultarPorId(id);
      Evento evento = servicioEvento.crearEvento(
        datos.getTipoEvento(),
        datos.getFecha(),
        datos.getCantidadInvitados()
      );
      evento.setSalon(paquete.getSalon());
      evento.setCatering(paquete.getCatering());

      // CORREGIDO: Usamos getServiciosAdicionalesComoCatalogo() para obtener los ServicioAdicional planos
      paquete.getServiciosAdicionales().forEach(evento::agregarServicio);

      session.setAttribute(ControladorReservas.EVENTO, evento);
      if (session.getAttribute(ControladorReservas.USUARIO) == null) {
        session.setAttribute(ControladorLogin.DESTINO_POST_LOGIN, "/reservar");
        return new ModelAndView("redirect:/login");
      }
      return new ModelAndView("redirect:/reservar");
    } catch (SeleccionInvalida e) {
      modelo.put("error", e.getMessage());
    } catch (PaqueteNoEncontrado e) {
      modelo.put("paquete", null);
    }

    return new ModelAndView("paquete-detalle", modelo);
  }

  private Map<String, Object> armarModeloDetalle(Long id) {
    Map<String, Object> modelo = new ModelMap();

    try {
      Paquete paquete = servicio.consultarPorId(id);
      modelo.put("paquete", paquete);
      modelo.put("capacidad", paquete.getSalon().getCapacidad());
      modelo.put("fechasOcupadas", obtenerFechasOcupadasComoTexto(id));
      modelo.put("tiposEvento", TipoPaquete.values());
    } catch (PaqueteNoEncontrado e) {
      modelo.put("paquete", null);
    }

    return modelo;
  }

  private List<String> obtenerFechasOcupadasComoTexto(Long id) {
    return servicioValidacion.obtenerFechasOcupadas(id).stream().map(LocalDate::toString).toList();
  }
}
