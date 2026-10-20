package com.tallerwebi.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Comida {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;

  @Enumerated(EnumType.STRING)
  private TipoComida tipo;

  protected Comida() {}

  public Comida(String nombre, TipoComida tipo) {
    this.nombre = nombre;
    this.tipo = tipo;
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public TipoComida getTipo() {
    return tipo;
  }
}
