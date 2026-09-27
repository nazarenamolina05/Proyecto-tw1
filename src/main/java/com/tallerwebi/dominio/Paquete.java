package com.tallerwebi.dominio;

import java.util.List;

public class Paquete {

  private Long id;
  private String nombre;
  private TipoPaquete tipo;
  private String salon;
  private String catering;
  private List<String> serviciosAdicionales;
  private Double precioEstimado;

  public Paquete(String nombre, TipoPaquete tipo) {
    this.nombre = nombre;
    this.tipo = tipo;
  }

  public Paquete(Long id, String nombre, TipoPaquete tipo, String salon,
                 String catering, List<String> serviciosAdicionales, Double precioEstimado) {
    this.id = id;
    this.nombre = nombre;
    this.tipo = tipo;
    this.salon = salon;
    this.catering = catering;
    this.serviciosAdicionales = serviciosAdicionales;
    this.precioEstimado = precioEstimado;
  }

  public String getSalon() {
    return salon;
  }

  public String getCatering() {
    return catering;
  }

  public List<String> getServiciosAdicionales() {
    return serviciosAdicionales;
  }

  public Double getPrecioEstimado() {
    return precioEstimado;
  }

  public Long getId() {
    return id;
  }
  public String getNombre() {
    return nombre;
  }

  public TipoPaquete getTipo() {
    return tipo;
  }
}


