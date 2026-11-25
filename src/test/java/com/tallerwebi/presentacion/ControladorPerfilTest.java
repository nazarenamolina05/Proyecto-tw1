package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.DatosDePerfilInvalidos;
import com.tallerwebi.dominio.excepcion.PasswordIncorrecta;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorPerfilTest {

  private static final Long ID = 1L;
  private static final String VISTA = "perfil";
  private static final String USUARIO = "USUARIO";

  private ServicioPerfil servicioPerfilMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private ControladorPerfil controladorPerfil;

  private Usuario usuarioEnSesion;
  private Usuario usuarioDelPerfil;

  @BeforeEach
  public void init() {
    this.servicioPerfilMock = mock(ServicioPerfil.class);
    this.requestMock = mock(HttpServletRequest.class);
    this.sessionMock = mock(HttpSession.class);
    when(this.requestMock.getSession()).thenReturn(this.sessionMock);
    this.controladorPerfil = new ControladorPerfil(this.servicioPerfilMock);

    this.usuarioEnSesion = this.unUsuario();
    this.usuarioDelPerfil = this.unUsuario();
    when(this.servicioPerfilMock.obtenerPerfil(ID)).thenReturn(this.usuarioDelPerfil);
  }

  // ---------- ver el perfil ----------

  @Test
  public void verPerfilSinSesionDeberiaIrAlLoginYRecordarElDestino() {
    ModelAndView modelAndView = this.controladorPerfil.verPerfil(this.requestMock);

    this.entoncesSeRedirigeAlLogin(modelAndView);
    verify(this.sessionMock).setAttribute(ControladorLogin.DESTINO_POST_LOGIN, "/perfil");
  }

  @Test
  public void verPerfilConSesionDeberiaMostrarLaVistaConElUsuarioDeLaBase() {
    this.dadoUnUsuarioLogueado();

    ModelAndView modelAndView = this.controladorPerfil.verPerfil(this.requestMock);

    this.entoncesSeMuestraElPerfil(modelAndView);
  }

  // ---------- actualizar datos ----------

  @Test
  public void actualizarDatosSinSesionDeberiaIrAlLogin() {
    ModelAndView modelAndView =
      this.controladorPerfil.actualizarDatos(this.datosPerfil(), this.requestMock);

    this.entoncesSeRedirigeAlLogin(modelAndView);
    verify(this.servicioPerfilMock, never()).obtenerPerfil(any());
  }

  @Test
  public void actualizarDatosCorrectosDeberiaMostrarExitoYRefrescarLaSesion()
    throws UsuarioExistente {
    this.dadoUnUsuarioLogueado();

    ModelAndView modelAndView =
      this.controladorPerfil.actualizarDatos(this.datosPerfil(), this.requestMock);

    verify(this.servicioPerfilMock).actualizarDatos(ID, "Ana", "ana@test.com");
    verify(this.sessionMock).setAttribute(USUARIO, this.usuarioDelPerfil);
    this.entoncesSeMuestraElPerfil(modelAndView);
    assertThat(modelAndView.getModel().get("mensajeExitoDatos"), notNullValue());
    assertThat(modelAndView.getModel().get("errorDatos"), nullValue());
  }

  @Test
  public void actualizarDatosConEmailEnUsoDeberiaMostrarElError() throws UsuarioExistente {
    this.dadoUnUsuarioLogueado();
    doThrow(new UsuarioExistente())
      .when(this.servicioPerfilMock)
      .actualizarDatos(ID, "Ana", "ana@test.com");

    ModelAndView modelAndView =
      this.controladorPerfil.actualizarDatos(this.datosPerfil(), this.requestMock);

    this.entoncesSeMuestraElPerfil(modelAndView);
    assertThat(modelAndView.getModel().get("errorDatos"), equalTo("Ese email ya está en uso."));
    assertThat(modelAndView.getModel().get("mensajeExitoDatos"), nullValue());
  }

  @Test
  public void actualizarDatosInvalidosDeberiaMostrarElMensajeDelServicio() throws UsuarioExistente {
    this.dadoUnUsuarioLogueado();
    doThrow(new DatosDePerfilInvalidos("El nombre es obligatorio."))
      .when(this.servicioPerfilMock)
      .actualizarDatos(ID, "Ana", "ana@test.com");

    ModelAndView modelAndView =
      this.controladorPerfil.actualizarDatos(this.datosPerfil(), this.requestMock);

    this.entoncesSeMuestraElPerfil(modelAndView);
    assertThat(modelAndView.getModel().get("errorDatos"), equalTo("El nombre es obligatorio."));
  }

  // ---------- cambiar contraseña ----------

  @Test
  public void cambiarPasswordSinSesionDeberiaIrAlLogin() {
    ModelAndView modelAndView =
      this.controladorPerfil.cambiarPassword(this.datosPassword("nueva12"), this.requestMock);

    this.entoncesSeRedirigeAlLogin(modelAndView);
    verify(this.servicioPerfilMock, never()).cambiarPassword(any(), any(), any());
  }

  @Test
  public void cambiarPasswordCorrectaDeberiaMostrarExito() {
    this.dadoUnUsuarioLogueado();

    ModelAndView modelAndView =
      this.controladorPerfil.cambiarPassword(this.datosPassword("nueva12"), this.requestMock);

    verify(this.servicioPerfilMock).cambiarPassword(ID, "vieja1", "nueva12");
    this.entoncesSeMuestraElPerfil(modelAndView);
    assertThat(modelAndView.getModel().get("mensajeExitoPassword"), notNullValue());
    assertThat(modelAndView.getModel().get("errorPassword"), nullValue());
  }

  @Test
  public void cambiarPasswordConConfirmacionDistintaNoDeberiaLlamarAlServicio() {
    this.dadoUnUsuarioLogueado();

    ModelAndView modelAndView =
      this.controladorPerfil.cambiarPassword(this.datosPassword("otra123"), this.requestMock);

    verify(this.servicioPerfilMock, never()).cambiarPassword(any(), any(), any());
    this.entoncesSeMuestraElPerfil(modelAndView);
    assertThat(modelAndView.getModel().get("errorPassword"), notNullValue());
  }

  @Test
  public void cambiarPasswordConClaveActualIncorrectaDeberiaMostrarElError() {
    this.dadoUnUsuarioLogueado();
    doThrow(new PasswordIncorrecta())
      .when(this.servicioPerfilMock)
      .cambiarPassword(ID, "vieja1", "nueva12");

    ModelAndView modelAndView =
      this.controladorPerfil.cambiarPassword(this.datosPassword("nueva12"), this.requestMock);

    this.entoncesSeMuestraElPerfil(modelAndView);
    assertThat(
      modelAndView.getModel().get("errorPassword"),
      equalTo("La contraseña actual no es correcta.")
    );
  }

  @Test
  public void cambiarPasswordInvalidaDeberiaMostrarElMensajeDelServicio() {
    this.dadoUnUsuarioLogueado();
    doThrow(new DatosDePerfilInvalidos("La contraseña debe tener al menos 6 caracteres."))
      .when(this.servicioPerfilMock)
      .cambiarPassword(ID, "vieja1", "nueva12");

    ModelAndView modelAndView =
      this.controladorPerfil.cambiarPassword(this.datosPassword("nueva12"), this.requestMock);

    this.entoncesSeMuestraElPerfil(modelAndView);
    assertThat(
      modelAndView.getModel().get("errorPassword"),
      equalTo("La contraseña debe tener al menos 6 caracteres.")
    );
  }

  // ---------- auxiliares ----------

  private Usuario unUsuario() {
    Usuario usuario = new Usuario();
    usuario.setId(ID);
    return usuario;
  }

  private void dadoUnUsuarioLogueado() {
    when(this.sessionMock.getAttribute(USUARIO)).thenReturn(this.usuarioEnSesion);
  }

  private DatosPerfil datosPerfil() {
    return new DatosPerfil("Ana", "ana@test.com");
  }

  private DatosCambioPassword datosPassword(String confirmacion) {
    return new DatosCambioPassword("vieja1", "nueva12", confirmacion);
  }

  private void entoncesSeRedirigeAlLogin(ModelAndView modelAndView) {
    assertThat(modelAndView.getViewName(), equalTo("redirect:/login"));
  }

  private void entoncesSeMuestraElPerfil(ModelAndView modelAndView) {
    assertThat(modelAndView.getViewName(), equalTo(VISTA));
    assertThat(modelAndView.getModel().get("usuario"), equalTo(this.usuarioDelPerfil));
  }
}
