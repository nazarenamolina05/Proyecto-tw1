package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.DatosDePerfilInvalidos;
import com.tallerwebi.dominio.excepcion.PasswordIncorrecta;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPerfilTest {

  private static final Long ID = 1L;

  private RepositorioUsuario repositorioUsuarioMock;
  private ServicioPerfil servicioPerfil;

  @BeforeEach
  public void init() {
    this.repositorioUsuarioMock = mock(RepositorioUsuario.class);
    this.servicioPerfil = new ServicioPerfilImpl(this.repositorioUsuarioMock);
  }

  // ---------- obtenerPerfil ----------

  @Test
  public void deberiaDevolverElUsuarioDelPerfil() {
    Usuario usuario = this.dadoUnUsuarioExistente("ana@test.com", "vieja1");

    Usuario obtenido = this.servicioPerfil.obtenerPerfil(ID);

    assertThat(obtenido, equalTo(usuario));
  }

  @Test
  public void obtenerElPerfilDeUnUsuarioInexistenteDeberiaLanzarUsuarioNoEncontrado() {
    assertThrows(UsuarioNoEncontrado.class, () -> this.servicioPerfil.obtenerPerfil(ID));
  }

  // ---------- actualizarDatos ----------

  @Test
  public void dadoQueElEmailYaLoUsaOtroUsuarioDeberiaDarError() {
    Usuario usuarioActual = this.dadoUnUsuarioExistente("ana@test.com", "vieja1");
    Usuario otroUsuario = this.unUsuario(2L, "luis@test.com", "clave12");
    when(this.repositorioUsuarioMock.buscar("luis@test.com")).thenReturn(otroUsuario);

    assertThrows(
      UsuarioExistente.class,
      () -> this.servicioPerfil.actualizarDatos(ID, "Ana", "luis@test.com")
    );

    assertThat(usuarioActual.getEmail(), equalTo("ana@test.com"));
    verify(this.repositorioUsuarioMock, never()).modificar(any());
  }

  @Test
  public void dadoQueElEmailEstaLibreDeberiaActualizarLosDatosYGuardar() throws UsuarioExistente {
    Usuario usuarioActual = this.dadoUnUsuarioExistente("ana@test.com", "vieja1");
    when(this.repositorioUsuarioMock.buscar("nuevo@test.com")).thenReturn(null);

    this.servicioPerfil.actualizarDatos(ID, "Ana Pérez", "nuevo@test.com");

    assertThat(usuarioActual.getNombreUsuario(), equalTo("Ana Pérez"));
    assertThat(usuarioActual.getEmail(), equalTo("nuevo@test.com"));
    verify(this.repositorioUsuarioMock).modificar(usuarioActual);
  }

  @Test
  public void dadoQueElUsuarioConservaSuEmailDeberiaPoderCambiarSoloElNombre()
    throws UsuarioExistente {
    Usuario usuarioActual = this.dadoUnUsuarioExistente("ana@test.com", "vieja1");
    when(this.repositorioUsuarioMock.buscar("ana@test.com")).thenReturn(usuarioActual);

    this.servicioPerfil.actualizarDatos(ID, "Ana Pérez", "ana@test.com");

    assertThat(usuarioActual.getNombreUsuario(), equalTo("Ana Pérez"));
    verify(this.repositorioUsuarioMock).modificar(usuarioActual);
  }

  @Test
  public void deberiaQuitarLosEspaciosSobrantesDelNombreYDelEmail() throws UsuarioExistente {
    Usuario usuarioActual = this.dadoUnUsuarioExistente("ana@test.com", "vieja1");
    when(this.repositorioUsuarioMock.buscar("nuevo@test.com")).thenReturn(null);

    this.servicioPerfil.actualizarDatos(ID, "  Ana Pérez  ", "  nuevo@test.com  ");

    assertThat(usuarioActual.getNombreUsuario(), equalTo("Ana Pérez"));
    assertThat(usuarioActual.getEmail(), equalTo("nuevo@test.com"));
    verify(this.repositorioUsuarioMock).modificar(usuarioActual);
  }

  @Test
  public void actualizarLosDatosDeUnUsuarioInexistenteDeberiaLanzarUsuarioNoEncontrado() {
    assertThrows(
      UsuarioNoEncontrado.class,
      () -> this.servicioPerfil.actualizarDatos(ID, "Ana", "ana@test.com")
    );
    verify(this.repositorioUsuarioMock, never()).modificar(any());
  }

  @Test
  public void dadoUnNombreEnBlancoDeberiaDarError() {
    this.assertDatosInvalidos("   ", "ana@test.com");
  }

  @Test
  public void dadoUnNombreNuloDeberiaDarError() {
    this.assertDatosInvalidos(null, "ana@test.com");
  }

  @Test
  public void dadoUnNombreDemasiadoLargoDeberiaDarError() {
    this.assertDatosInvalidos("a".repeat(31), "ana@test.com");
  }

  @Test
  public void dadoUnEmailEnBlancoDeberiaDarError() {
    this.assertDatosInvalidos("Ana", "   ");
  }

  @Test
  public void dadoUnEmailSinFormatoDeberiaDarError() {
    this.assertDatosInvalidos("Ana", "ana-sin-arroba");
  }

  // ---------- cambiarPassword ----------

  @Test
  public void dadoQueLaPasswordActualEsCorrectaDeberiaCambiarlaYGuardar() {
    Usuario usuario = this.dadoUnUsuarioExistente("ana@test.com", "vieja1");

    this.servicioPerfil.cambiarPassword(ID, "vieja1", "nueva1");

    assertThat(usuario.getPassword(), equalTo("nueva1"));
    verify(this.repositorioUsuarioMock).modificar(usuario);
  }

  @Test
  public void dadoQueLaPasswordActualEsIncorrectaDeberiaDarError() {
    Usuario usuario = this.dadoUnUsuarioExistente("ana@test.com", "vieja1");

    assertThrows(
      PasswordIncorrecta.class,
      () -> this.servicioPerfil.cambiarPassword(ID, "otra-clave", "nueva1")
    );

    assertThat(usuario.getPassword(), equalTo("vieja1"));
    verify(this.repositorioUsuarioMock, never()).modificar(any());
  }

  @Test
  public void dadoUnaPasswordNuevaDemasiadoCortaDeberiaDarError() {
    this.assertPasswordNuevaInvalida("12345");
  }

  @Test
  public void dadoUnaPasswordNuevaNulaDeberiaDarError() {
    this.assertPasswordNuevaInvalida(null);
  }

  @Test
  public void cambiarLaPasswordDeUnUsuarioInexistenteDeberiaLanzarUsuarioNoEncontrado() {
    assertThrows(
      UsuarioNoEncontrado.class,
      () -> this.servicioPerfil.cambiarPassword(ID, "vieja1", "nueva1")
    );
    verify(this.repositorioUsuarioMock, never()).modificar(any());
  }

  // ---------- auxiliares ----------

  private Usuario unUsuario(Long id, String email, String password) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setEmail(email);
    usuario.setPassword(password);
    return usuario;
  }

  private Usuario dadoUnUsuarioExistente(String email, String password) {
    Usuario usuario = this.unUsuario(ID, email, password);
    when(this.repositorioUsuarioMock.buscarPorId(ID)).thenReturn(usuario);
    return usuario;
  }

  private void assertDatosInvalidos(String nombre, String email) {
    Usuario usuario = this.dadoUnUsuarioExistente("ana@test.com", "vieja1");

    assertThrows(
      DatosDePerfilInvalidos.class,
      () -> this.servicioPerfil.actualizarDatos(ID, nombre, email)
    );

    assertThat(usuario.getEmail(), equalTo("ana@test.com"));
    assertThat(usuario.getNombreUsuario(), nullValue());
    verify(this.repositorioUsuarioMock, never()).modificar(any());
  }

  private void assertPasswordNuevaInvalida(String passwordNueva) {
    Usuario usuario = this.dadoUnUsuarioExistente("ana@test.com", "vieja1");

    assertThrows(
      DatosDePerfilInvalidos.class,
      () -> this.servicioPerfil.cambiarPassword(ID, "vieja1", passwordNueva)
    );

    assertThat(usuario.getPassword(), equalTo("vieja1"));
    verify(this.repositorioUsuarioMock, never()).modificar(any());
  }
}
