package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Catering {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private Double precioPorPersona;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "catering_comida",
    joinColumns = @JoinColumn(name = "catering_id"),
    inverseJoinColumns = @JoinColumn(name = "comida_id")
  )
  private Set<Comida> comidas = new HashSet<>();

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

  public Set<Comida> getComidas() {
    return comidas;
  }

  public void agregarComida(Comida comida) {
    comidas.add(comida);
  }
}
