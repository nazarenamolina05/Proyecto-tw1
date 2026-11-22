package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession; // NUEVO
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorLogin {

  public static final String DESTINO_POST_LOGIN = "DESTINO_POST_LOGIN";
  private static final String HOME = "/home-eventos";
  private static final String VISTA_NUEVO_USUARIO = "nuevo-usuario";
  private static final String DATOS_REGISTRO = "datosRegistro";

  private ServicioLogin servicioLogin;

  @Autowired
  public ControladorLogin(ServicioLogin servicioLogin) {
    this.servicioLogin = servicioLogin;
  }

  @RequestMapping("/login")
  public ModelAndView irALogin() {
    Map<String, Object> modelo = new ModelMap();
    modelo.put("datosLogin", new DatosLogin());
    return new ModelAndView("login", modelo);
  }

  @RequestMapping(path = "/validar-login", method = RequestMethod.POST)
  public ModelAndView validarLogin(
    @ModelAttribute("datosLogin") DatosLogin datosLogin,
    HttpServletRequest request
  ) {
    Usuario usuarioBuscado = servicioLogin.consultarUsuario(
      datosLogin.getEmail(),
      datosLogin.getPassword()
    );
    if (usuarioBuscado != null) {
      HttpSession session = request.getSession();
      session.setAttribute("ROL", usuarioBuscado.getRol());
      session.setAttribute("USUARIO", usuarioBuscado);
      return new ModelAndView("redirect:" + obtenerDestinoTrasLogin(session));
    } else {
      Map<String, Object> model = new ModelMap();
      model.put("error", "El email o la contraseña no son correctos");
      return new ModelAndView("login", model);
    }
  }

  @RequestMapping(path = "/registrarme", method = RequestMethod.POST)
  public ModelAndView registrarme(@ModelAttribute("datosRegistro") DatosRegistro datosRegistro) {
    if (!datosRegistro.passwordsCoinciden()) {
      return volverARegistro(datosRegistro, "Las contraseñas no coinciden");
    }
    try {
      servicioLogin.registrar(crearUsuario(datosRegistro));
    } catch (UsuarioExistente e) {
      return volverARegistro(datosRegistro, "Ya hay una cuenta con ese email");
    } catch (Exception e) {
      return volverARegistro(
        datosRegistro,
        "No se pudo crear la cuenta. Probá de nuevo en unos minutos"
      );
    }
    return new ModelAndView("redirect:/login");
  }

  @RequestMapping(path = "/nuevo-usuario", method = RequestMethod.GET)
  public ModelAndView nuevoUsuario() {
    Map<String, Object> model = new ModelMap();
    model.put(DATOS_REGISTRO, new DatosRegistro());
    return new ModelAndView(VISTA_NUEVO_USUARIO, model);
  }

  // Se copian solo email y password: rol y activo los decide la aplicación, no el form.
  private Usuario crearUsuario(DatosRegistro datosRegistro) {
    Usuario usuario = new Usuario();
    usuario.setEmail(datosRegistro.getEmail());
    usuario.setPassword(datosRegistro.getPassword());
    return usuario;
  }

  private ModelAndView volverARegistro(DatosRegistro datosRegistro, String error) {
    Map<String, Object> model = new ModelMap();
    model.put(DATOS_REGISTRO, datosRegistro);
    model.put("error", error);
    return new ModelAndView(VISTA_NUEVO_USUARIO, model);
  }

  // La raíz del sitio lleva al home de eventos, que se puede ver sin estar logueado.
  @RequestMapping(path = "/", method = RequestMethod.GET)
  public ModelAndView inicio() {
    return new ModelAndView("redirect:" + HOME);
  }

  // si alguien nos mandó al login desde otra pantalla, vuelve ahí; si no, al home
  // se borra apenas se lee para que valga una sola vez.
  private String obtenerDestinoTrasLogin(HttpSession session) {
    Object destino = session.getAttribute(DESTINO_POST_LOGIN);
    session.removeAttribute(DESTINO_POST_LOGIN);
    return destino instanceof String ruta ? ruta : HOME;
  }

  @RequestMapping(path = "/cerrar-sesion", method = RequestMethod.POST)
  public ModelAndView cerrarSesion(HttpServletRequest request) {
    request.getSession().invalidate();
    return new ModelAndView("redirect:" + HOME);
  }
}
