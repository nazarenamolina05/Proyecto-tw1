package com.tallerwebi.presentacion;

// Datos que llegan del formulario de registro. Solo tiene los campos que el
// usuario puede completar: así no se puede mandar "rol" o "activo" desde el form.
public class DatosRegistro {

  private String email;
  private String password;
  private String confirmacionPassword;

  public DatosRegistro() {}

  public DatosRegistro(String email, String password, String confirmacionPassword) {
    this.email = email;
    this.password = password;
    this.confirmacionPassword = confirmacionPassword;
  }

  public boolean passwordsCoinciden() {
    return password != null && password.equals(confirmacionPassword);
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getConfirmacionPassword() {
    return confirmacionPassword;
  }

  public void setConfirmacionPassword(String confirmacionPassword) {
    this.confirmacionPassword = confirmacionPassword;
  }
}
