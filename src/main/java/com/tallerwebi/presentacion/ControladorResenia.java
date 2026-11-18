package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioResenia;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.EventoNoFinalizado;
import com.tallerwebi.dominio.excepcion.PuntajeInvalido;
import com.tallerwebi.dominio.excepcion.ReseniaExistente;
import com.tallerwebi.dominio.excepcion.ReservaNoEncontrada;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorResenia {

  private static final String USUARIO = "USUARIO";

  private ServicioResenia servicioResenia;

  @Autowired
  public ControladorResenia(ServicioResenia servicioResenia) {
    this.servicioResenia = servicioResenia;
  }

  @PostMapping(path = "/mis-reservas/{id}/resenia")
  public ModelAndView reseniar(
    @PathVariable("id") Long idReserva,
    @ModelAttribute("datos") DatosResenia datos,
    HttpServletRequest request
  ) {
    Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    String volverAlDetalle = "redirect:/mis-reservas/" + idReserva;
    try {
      servicioResenia.reseniar(idReserva, usuario, datos.getPuntaje(), datos.getComentario());
    } catch (ReservaNoEncontrada e) {
      return new ModelAndView("redirect:/mis-reservas");
    } catch (EventoNoFinalizado e) {
      return new ModelAndView(volverAlDetalle + "?error=reseniaNoDisponible");
    } catch (PuntajeInvalido e) {
      return new ModelAndView(volverAlDetalle + "?error=puntaje");
    } catch (ReseniaExistente e) {
      return new ModelAndView(volverAlDetalle + "?error=reseniaExistente");
    }

    return new ModelAndView(volverAlDetalle);
  }
}
