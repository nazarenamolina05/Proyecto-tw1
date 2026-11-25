package com.tallerwebi.presentacion;

public class DatosPerfil {

  private String nombreUsuario;
  private String email;

  public DatosPerfil() {}

  public DatosPerfil(String nombreUsuario, String email) {
    this.nombreUsuario = nombreUsuario;
    this.email = email;
  }

  public String getNombreUsuario() {
    return nombreUsuario;
  }

  public void setNombreUsuario(String nombreUsuario) {
    this.nombreUsuario = nombreUsuario;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
