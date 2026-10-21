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
}
