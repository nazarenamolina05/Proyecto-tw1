package com.tallerwebi.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Catering {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private Double precioPorPersona;

  @ManyToMany
  @JoinTable(
    name = "Catering_Comida",
    joinColumns = @JoinColumn(name = "catering_id"),
    inverseJoinColumns = @JoinColumn(name = "comida_id")
  )
  private Set<Comida> comidas = new LinkedHashSet<>();

  public Catering() {}

  public Catering(String nombre, Double precioPorPersona) {
    this.nombre = nombre;
    this.precioPorPersona = precioPorPersona;
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
    if (comida == null) {
      throw new IllegalArgumentException("La comida no puede ser nula");
    }
    comidas.add(comida);
  }

  public void quitarComida(Comida comida) {
    comidas.remove(comida);
  }
}
