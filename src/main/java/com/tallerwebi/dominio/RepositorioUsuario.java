package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Usuario;

public interface RepositorioUsuario {
  Usuario buscarUsuario(String email, String password);
  void guardar(Usuario usuario);
  Usuario buscar(String email);
  Usuario buscarPorId(Long id);
  void modificar(Usuario usuario);
}