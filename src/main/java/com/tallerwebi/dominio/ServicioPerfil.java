package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;

public interface ServicioPerfil {
  Usuario obtenerPerfil(Long idUsuario);

  void actualizarDatos(Long idUsuario, String nombreUsuario, String email) throws UsuarioExistente;

  void cambiarPassword(Long idUsuario, String passwordActual, String passwordNueva);
}
