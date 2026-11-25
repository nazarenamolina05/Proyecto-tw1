package com.tallerwebi.presentacion;

public class DatosCambioPassword {

  private String passwordActual;
  private String passwordNueva;
  private String confirmacionPassword;

  public DatosCambioPassword() {}

  public DatosCambioPassword(
    String passwordActual,
    String passwordNueva,
    String confirmacionPassword
  ) {
    this.passwordActual = passwordActual;
    this.passwordNueva = passwordNueva;
    this.confirmacionPassword = confirmacionPassword;
  }

  public String getPasswordActual() {
    return passwordActual;
  }

  public void setPasswordActual(String passwordActual) {
    this.passwordActual = passwordActual;
  }

  public String getPasswordNueva() {
    return passwordNueva;
  }

  public void setPasswordNueva(String passwordNueva) {
    this.passwordNueva = passwordNueva;
  }

  public String getConfirmacionPassword() {
    return confirmacionPassword;
  }

  public void setConfirmacionPassword(String confirmacionPassword) {
    this.confirmacionPassword = confirmacionPassword;
  }

  public boolean passwordsCoinciden() {
    if (this.passwordNueva == null || this.confirmacionPassword == null) {
      return false;
    }
    return this.passwordNueva.equals(this.confirmacionPassword);
  }
}
