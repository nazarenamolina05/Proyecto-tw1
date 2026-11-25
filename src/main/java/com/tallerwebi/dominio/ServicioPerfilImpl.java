package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.DatosDePerfilInvalidos;
import com.tallerwebi.dominio.excepcion.PasswordIncorrecta;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioPerfilImpl implements ServicioPerfil {

  private static final int MAX_NOMBRE = 30;
  private static final int MIN_PASSWORD = 6;

  private final RepositorioUsuario repositorioUsuario;

  @Autowired
  public ServicioPerfilImpl(RepositorioUsuario repositorioUsuario) {
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public Usuario obtenerPerfil(Long idUsuario) {
    return this.buscarUsuarioObligatorio(idUsuario);
  }

  @Override
  public void actualizarDatos(Long idUsuario, String nombreUsuario, String email)
    throws UsuarioExistente {
    Usuario usuario = this.buscarUsuarioObligatorio(idUsuario);

    String nombreLimpio = nombreUsuario == null ? null : nombreUsuario.trim();
    String emailLimpio = email == null ? null : email.trim();

    this.validarNombre(nombreLimpio);
    this.validarEmail(emailLimpio);

    Usuario usuarioConMismoEmail = this.repositorioUsuario.buscar(emailLimpio);
    if (usuarioConMismoEmail != null && !usuarioConMismoEmail.getId().equals(usuario.getId())) {
      throw new UsuarioExistente();
    }

    usuario.setNombreUsuario(nombreLimpio);
    usuario.setEmail(emailLimpio);
    this.repositorioUsuario.modificar(usuario);
  }

  @Override
  public void cambiarPassword(Long idUsuario, String passwordActual, String passwordNueva) {
    Usuario usuario = this.buscarUsuarioObligatorio(idUsuario);

    if (passwordActual == null || !passwordActual.equals(usuario.getPassword())) {
      throw new PasswordIncorrecta();
    }

    this.validarPasswordNueva(passwordNueva);

    usuario.setPassword(passwordNueva);
    this.repositorioUsuario.modificar(usuario);
  }

  private Usuario buscarUsuarioObligatorio(Long idUsuario) {
    Usuario usuario = idUsuario == null ? null : this.repositorioUsuario.buscarPorId(idUsuario);
    if (usuario == null) {
      throw new UsuarioNoEncontrado();
    }
    return usuario;
  }

  private void validarNombre(String nombre) {
    if (nombre == null || nombre.isEmpty()) {
      throw new DatosDePerfilInvalidos("El nombre es obligatorio.");
    }
    if (nombre.length() > MAX_NOMBRE) {
      throw new DatosDePerfilInvalidos(
        "El nombre no puede superar los " + MAX_NOMBRE + " caracteres."
      );
    }
  }

  private void validarEmail(String email) {
    if (email == null || email.isEmpty()) {
      throw new DatosDePerfilInvalidos("El email es obligatorio.");
    }
    if (!email.contains("@") || !email.contains(".")) {
      throw new DatosDePerfilInvalidos("Ingresá un email válido.");
    }
  }

  private void validarPasswordNueva(String passwordNueva) {
    if (passwordNueva == null || passwordNueva.length() < MIN_PASSWORD) {
      throw new DatosDePerfilInvalidos(
        "La contraseña debe tener al menos " + MIN_PASSWORD + " caracteres."
      );
    }
  }
}
