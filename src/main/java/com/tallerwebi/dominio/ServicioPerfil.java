package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;

// dominio/ServicioPerfil.java
public interface ServicioPerfil {
    void actualizarDatos(Long idUsuario, String nombreUsuario, String email) throws UsuarioExistente;
}