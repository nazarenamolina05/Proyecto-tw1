package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Paquete;
import com.tallerwebi.dominio.ServicioConsultarPaquetesPredeterminados;
import com.tallerwebi.dominio.ServicioValidarSeleccionPaquete;
import com.tallerwebi.dominio.TipoPaquete;
import com.tallerwebi.dominio.excepcion.PaqueteNoEncontrado;
import com.tallerwebi.dominio.excepcion.SeleccionInvalida;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorConsultarPaquetesPredeterminados {

  private ServicioConsultarPaquetesPredeterminados servicio;
  private ServicioValidarSeleccionPaquete servicioValidacion;

  public ControladorConsultarPaquetesPredeterminados(
    ServicioConsultarPaquetesPredeterminados servicio,
    ServicioValidarSeleccionPaquete servicioValidacion
  ) {
    this.servicio = servicio;
    this.servicioValidacion = servicioValidacion;
  }

  @GetMapping(path = "/paquetes-predeterminados/{id}")
  public ModelAndView mostrarDetalle(@PathVariable("id") Long id) {
    return new ModelAndView("paquete-detalle", armarModeloDetalle(id));
  }

  @PostMapping(path = "/paquetes-predeterminados/{id}/reservar")
  public ModelAndView reservar(
    @PathVariable("id") Long id,
    @RequestParam(value = "tipoEvento", required = false) TipoPaquete tipoEvento,
    @RequestParam(value = "fecha", required = false) @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE
    ) LocalDate fecha,
    @RequestParam(value = "cantidadInvitados", required = false) Integer cantidadInvitados
  ) {
    Map<String, Object> modelo = armarModeloDetalle(id);
    modelo.put("tipoSeleccionado", tipoEvento);
    modelo.put("fechaSeleccionada", fecha == null ? null : fecha.toString());
    modelo.put("invitadosSeleccionados", cantidadInvitados);

    try {
      servicioValidacion.validar(id, tipoEvento, fecha, cantidadInvitados);
      modelo.put("mensajeExito", "¡Todo listo para reservar!");
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
