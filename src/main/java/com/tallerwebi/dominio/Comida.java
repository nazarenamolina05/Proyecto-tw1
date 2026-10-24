package com.tallerwebi.dominio;

import jakarta.persistence.*;

@Entity
public class Comida {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;

  @Enumerated(EnumType.STRING)
  private TipoComida tipoComida;

  private Boolean vegetariana;
  private Boolean aptaCeliacos;
  private Boolean infantil;

  public Comida() {}

  public Comida(String nombre, TipoComida tipoComida) {
    this.nombre = nombre;
    this.tipoComida = tipoComida;
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

  public TipoComida getTipoComida() {
    return tipoComida;
  }

  public void setTipoComida(TipoComida tipoComida) {
    this.tipoComida = tipoComida;
  }

  public Boolean getVegetariana() {
    return vegetariana;
  }

  public void setVegetariana(Boolean vegetariana) {
    this.vegetariana = vegetariana;
  }

  public Boolean getAptaCeliacos() {
    return aptaCeliacos;
  }

  public void setAptaCeliacos(Boolean aptaCeliacos) {
    this.aptaCeliacos = aptaCeliacos;
  }

  public Boolean getInfantil() {
    return infantil;
  }

  public void setInfantil(Boolean infantil) {
    this.infantil = infantil;
  }
}
