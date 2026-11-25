package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.DatosDePerfilInvalidos;
import com.tallerwebi.dominio.excepcion.PasswordIncorrecta;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorPerfil {

  private static final String USUARIO = "USUARIO";
  private static final String VISTA_PERFIL = "perfil";
  private static final String IR_AL_LOGIN = "redirect:/login";
  private static final String RUTA_PERFIL = "/perfil";
  private static final String ATR_ERROR_DATOS = "errorDatos";
  private static final String ATR_EXITO_DATOS = "mensajeExitoDatos";
  private static final String ATR_ERROR_PASSWORD = "errorPassword";
  private static final String ATR_EXITO_PASSWORD = "mensajeExitoPassword";

  private final ServicioPerfil servicioPerfil;

  @Autowired
  public ControladorPerfil(ServicioPerfil servicioPerfil) {
    this.servicioPerfil = servicioPerfil;
  }

  @RequestMapping(path = RUTA_PERFIL, method = RequestMethod.GET)
  public ModelAndView verPerfil(HttpServletRequest request) {
    Usuario usuarioEnSesion = this.obtenerUsuarioEnSesion(request);
    if (usuarioEnSesion == null) {
      return this.irAlLoginYVolverAlPerfil(request);
    }
    return this.mostrarPerfil(request, usuarioEnSesion.getId(), new LinkedHashMap<>());
  }

  @RequestMapping(path = "/perfil/datos", method = RequestMethod.POST)
  public ModelAndView actualizarDatos(
    @ModelAttribute("datosPerfil") DatosPerfil datos,
    HttpServletRequest request
  ) {
    Usuario usuarioEnSesion = this.obtenerUsuarioEnSesion(request);
    if (usuarioEnSesion == null) {
      return this.irAlLoginYVolverAlPerfil(request);
    }

    Map<String, Object> modelo = new LinkedHashMap<>();
    try {
      this.servicioPerfil.actualizarDatos(
          usuarioEnSesion.getId(),
          datos.getNombreUsuario(),
          datos.getEmail()
        );
      modelo.put(ATR_EXITO_DATOS, "Tus datos se actualizaron correctamente.");
    } catch (UsuarioExistente excepcion) {
      modelo.put(ATR_ERROR_DATOS, "Ese email ya está en uso.");
    } catch (DatosDePerfilInvalidos excepcion) {
      modelo.put(ATR_ERROR_DATOS, excepcion.getMessage());
    }
    return this.mostrarPerfil(request, usuarioEnSesion.getId(), modelo);
  }

  @RequestMapping(path = "/perfil/password", method = RequestMethod.POST)
  public ModelAndView cambiarPassword(
    @ModelAttribute("datosPassword") DatosCambioPassword datos,
    HttpServletRequest request
  ) {
    Usuario usuarioEnSesion = this.obtenerUsuarioEnSesion(request);
    if (usuarioEnSesion == null) {
      return this.irAlLoginYVolverAlPerfil(request);
    }

    Map<String, Object> modelo = new LinkedHashMap<>();
    this.procesarCambioDePassword(usuarioEnSesion.getId(), datos, modelo);
    return this.mostrarPerfil(request, usuarioEnSesion.getId(), modelo);
  }

  private void procesarCambioDePassword(
    Long idUsuario,
    DatosCambioPassword datos,
    Map<String, Object> modelo
  ) {
    if (!datos.passwordsCoinciden()) {
      modelo.put(ATR_ERROR_PASSWORD, "Las contraseñas nuevas no coinciden.");
      return;
    }
    try {
      this.servicioPerfil.cambiarPassword(
          idUsuario,
          datos.getPasswordActual(),
          datos.getPasswordNueva()
        );
      modelo.put(ATR_EXITO_PASSWORD, "Tu contraseña se actualizó correctamente.");
    } catch (PasswordIncorrecta excepcion) {
      modelo.put(ATR_ERROR_PASSWORD, "La contraseña actual no es correcta.");
    } catch (DatosDePerfilInvalidos excepcion) {
      modelo.put(ATR_ERROR_PASSWORD, excepcion.getMessage());
    }
  }

  private ModelAndView mostrarPerfil(
    HttpServletRequest request,
    Long idUsuario,
    Map<String, Object> modelo
  ) {
    Usuario usuario = this.servicioPerfil.obtenerPerfil(idUsuario);
    request.getSession().setAttribute(USUARIO, usuario);
    modelo.put("usuario", usuario);
    return new ModelAndView(VISTA_PERFIL, modelo);
  }

  private Usuario obtenerUsuarioEnSesion(HttpServletRequest request) {
    return (Usuario) request.getSession().getAttribute(USUARIO);
  }

  private ModelAndView irAlLoginYVolverAlPerfil(HttpServletRequest request) {
    request.getSession().setAttribute(ControladorLogin.DESTINO_POST_LOGIN, RUTA_PERFIL);
    return new ModelAndView(IR_AL_LOGIN);
  }
}
