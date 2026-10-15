package com.tallerwebi.dominio;

import jakarta.persistence.*;

@Entity
public class ServicioAdicional {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private Double precio;
  private Boolean precioPorInvitado;

  @Enumerated(EnumType.STRING)
  private CategoriaServicio categoriaServicio;

  public ServicioAdicional() {}

  // constructor para que funciones los test existentes
  public ServicioAdicional(String nombre, Double precio, Boolean precioPorInvitado) {
    this.nombre = nombre;
    this.precio = precio;
    this.precioPorInvitado = precioPorInvitado;
  }

  // constructor para HU-03 filtrar los servicios por tipo de envento para los test
  public ServicioAdicional(
    String nombre,
    Double precio,
    Boolean precioPorInvitado,
    CategoriaServicio categoriaServicio
  ) {
    this.nombre = nombre;
    this.precio = precio;
    this.precioPorInvitado = precioPorInvitado;
    this.categoriaServicio = categoriaServicio;
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

  public CategoriaServicio getCategoriaServicio() {
    return categoriaServicio;
  }

  public void setCategoriaServicio(CategoriaServicio categoriaServicio) {
    this.categoriaServicio = categoriaServicio;
  }

  public Double calcularCosto(Integer cantidadInvitados) {
    if (Boolean.TRUE.equals(precioPorInvitado)) {
      return precio * cantidadInvitados;
    }
    return precio;
  }
}
