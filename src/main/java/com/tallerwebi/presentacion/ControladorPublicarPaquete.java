package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPublicarPaquete;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.EventoNoFinalizado;
import com.tallerwebi.dominio.excepcion.NombrePaqueteInvalido;
import com.tallerwebi.dominio.excepcion.ReservaNoEncontrada;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorPublicarPaquete {

  private static final String USUARIO = "USUARIO";

  private ServicioPublicarPaquete servicioPublicarPaquete;

  @Autowired
  public ControladorPublicarPaquete(ServicioPublicarPaquete servicioPublicarPaquete) {
    this.servicioPublicarPaquete = servicioPublicarPaquete;
  }

  @PostMapping(path = "/mis-reservas/{id}/publicar")
  public ModelAndView publicar(
    @PathVariable("id") Long idReserva,
    @RequestParam("nombre") String nombre,
    HttpServletRequest request
  ) {
    Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    String volverAlDetalle = "redirect:/mis-reservas/" + idReserva;
    try {
      servicioPublicarPaquete.publicar(idReserva, usuario, nombre);
    } catch (ReservaNoEncontrada e) {
      return new ModelAndView("redirect:/mis-reservas");
    } catch (EventoNoFinalizado e) {
      return new ModelAndView(volverAlDetalle + "?error=noFinalizado");
    } catch (NombrePaqueteInvalido e) {
      return new ModelAndView(volverAlDetalle + "?error=nombre");
    }

    return new ModelAndView("redirect:/paquetes");
  }
}
