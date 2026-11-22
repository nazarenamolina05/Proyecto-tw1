package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.servlet.ModelAndView;

public class ControladorLoginTest {

  private ControladorLogin controladorLogin;
  private DatosRegistro datosRegistro;
  private DatosLogin datosLoginMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private ServicioLogin servicioLoginMock;

  @BeforeEach
  public void init() {
    datosLoginMock = new DatosLogin("dami@unlam.com", "123");
    datosRegistro = new DatosRegistro("dami@unlam.com", "123", "123");
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    servicioLoginMock = mock(ServicioLogin.class);
    controladorLogin = new ControladorLogin(servicioLoginMock);
  }

  @Test
  public void loginConUsuarioYPasswordInorrectosDeberiaLlevarALoginNuevamente() {
    // preparacion
    when(servicioLoginMock.consultarUsuario(anyString(), anyString())).thenReturn(null);

    // ejecucion
    ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Usuario o clave incorrecta")
    );
    verify(sessionMock, times(0)).setAttribute("ROL", "ADMIN");
  }

  @Test
  public void loginConUsuarioYPasswordCorrectosDeberiaLLevarAHome() {
    // preparacion
    Usuario usuarioEncontradoMock = mock(Usuario.class);
    when(usuarioEncontradoMock.getRol()).thenReturn("ADMIN");

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(servicioLoginMock.consultarUsuario(anyString(), anyString()))
      .thenReturn(usuarioEncontradoMock);

    // ejecucion
    ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/home-eventos"));
    verify(sessionMock, times(1)).setAttribute("ROL", usuarioEncontradoMock.getRol());
  }

  @Test
  public void registrameSiUsuarioNoExisteDeberiaCrearUsuarioYVolverAlLogin()
    throws UsuarioExistente {
    // ejecucion
    ModelAndView modelAndView = controladorLogin.registrarme(datosRegistro);

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
    verify(servicioLoginMock, times(1)).registrar(any(Usuario.class));
  }

  @Test
  public void registrarmeDeberiaCrearElUsuarioSoloConEmailYPassword() throws UsuarioExistente {
    // preparacion
    ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);

    // ejecucion
    controladorLogin.registrarme(datosRegistro);

    // validacion
    verify(servicioLoginMock).registrar(captor.capture());
    Usuario usuarioCreado = captor.getValue();
    assertThat(usuarioCreado.getEmail(), equalTo("dami@unlam.com"));
    assertThat(usuarioCreado.getPassword(), equalTo("123"));
    assertThat(usuarioCreado.getRol(), nullValue());
    assertThat(usuarioCreado.getActivo(), equalTo(false));
  }

  @Test
  public void registrarmeConPasswordsDistintasDeberiaVolverAFormularioSinRegistrar()
    throws UsuarioExistente {
    // preparacion
    DatosRegistro datosConPasswordsDistintas = new DatosRegistro("dami@unlam.com", "123", "456");

    // ejecucion
    ModelAndView modelAndView = controladorLogin.registrarme(datosConPasswordsDistintas);

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Las contraseñas no coinciden")
    );
    verify(servicioLoginMock, never()).registrar(any(Usuario.class));
  }

  @Test
  public void registrarmeSiUsuarioExisteDeberiaVolverAFormularioYMostrarError()
    throws UsuarioExistente {
    // preparacion
    doThrow(UsuarioExistente.class).when(servicioLoginMock).registrar(any(Usuario.class));

    // ejecucion
    ModelAndView modelAndView = controladorLogin.registrarme(datosRegistro);

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("El usuario ya existe")
    );
  }

  @Test
  public void errorEnRegistrarmeDeberiaVolverAFormularioYMostrarError() throws UsuarioExistente {
    // preparacion
    doThrow(RuntimeException.class).when(servicioLoginMock).registrar(any(Usuario.class));

    // ejecucion
    ModelAndView modelAndView = controladorLogin.registrarme(datosRegistro);

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Error al registrar el nuevo usuario")
    );
  }

  @Test
  public void irALoginDeberiaRetornarVistaLoginConDatosLogin() {
    // ejecucion
    ModelAndView modelAndView = controladorLogin.irALogin();

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
    assertThat(modelAndView.getModel().get("datosLogin"), instanceOf(DatosLogin.class));
  }

  @Test
  public void nuevoUsuarioDeberiaRetornarVistaNuevoUsuarioConDatosRegistroVacios() {
    // ejecucion
    ModelAndView modelAndView = controladorLogin.nuevoUsuario();

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
    assertThat(modelAndView.getModel().get("datosRegistro"), instanceOf(DatosRegistro.class));
  }

  @Test
  public void irAHomeDeberiaRetornarVistaHome() {
    // ejecucion
    ModelAndView modelAndView = controladorLogin.irAHome();

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("home"));
  }

  @Test
  public void inicioDeberiaRedirigirALogin() {
    // ejecucion
    ModelAndView modelAndView = controladorLogin.inicio();

    // validacion
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void loginCorrectoConDestinoGuardadoDeberiaLlevarAEseDestino() {
    Usuario usuarioEncontradoMock = mock(Usuario.class);
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(servicioLoginMock.consultarUsuario(anyString(), anyString()))
      .thenReturn(usuarioEncontradoMock);
    when(sessionMock.getAttribute(ControladorLogin.DESTINO_POST_LOGIN))
      .thenReturn("/evento-personalizado");

    ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/evento-personalizado"));
    verify(sessionMock).removeAttribute(ControladorLogin.DESTINO_POST_LOGIN);
  }

  @Test
  public void alCerrarSesionInvalidaLaSesionYVuelveAlHome() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    when(request.getSession()).thenReturn(session);

    ModelAndView mav = controladorLogin.cerrarSesion(request);

    verify(session).invalidate();
    assertThat(mav.getViewName(), equalTo("redirect:/home-eventos"));
  }
}
