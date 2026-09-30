package com.tallerwebi.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Catering {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private Double precioPorPersona;

  public Catering() {}

  public Catering(String nombre, Double precioPorPersona) {
    this.precioPorPersona = precioPorPersona;
    this.nombre = nombre;
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

  public Double getPrecioPorPersona() {
    return precioPorPersona;
  }

  public void setPrecioPorPersona(Double precioPorPersona) {
    this.precioPorPersona = precioPorPersona;
  }
}
