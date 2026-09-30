package com.tallerwebi.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ServicioAdicional {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private Double precio;
  private Boolean precioPorInvitado;

  public ServicioAdicional() {}

  public ServicioAdicional(String nombre, Double precio, Boolean precioPorInvitado) {
    this.nombre = nombre;
    this.precio = precio;
    this.precioPorInvitado = precioPorInvitado;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Double getPrecio() {
    return precio;
  }

  public void setPrecio(Double precio) {
    this.precio = precio;
  }

  public Boolean getPrecioPorInvitado() {
    return precioPorInvitado;
  }

  public void setPrecioPorInvitado(Boolean precioPorInvitado) {
    this.precioPorInvitado = precioPorInvitado;
  }

  public Double calcularCosto(Integer cantidadInvitados) {
    if (Boolean.TRUE.equals(precioPorInvitado)) {
      return precio * cantidadInvitados;
    }
    return precio;
  }
}
