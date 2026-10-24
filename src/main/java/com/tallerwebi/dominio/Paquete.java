package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Paquete {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;

  private String descripcion;

  @Enumerated(EnumType.STRING)
  private TipoPaquete tipo;

  @ManyToOne
  @JoinColumn(name = "salon_id")
  private Salon salon;

  @ManyToOne
  @JoinColumn(name = "catering_id")
  private Catering catering;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "paquete_servicio",
    joinColumns = @JoinColumn(name = "paquete_id"),
    inverseJoinColumns = @JoinColumn(name = "servicio_id")
  )
  private List<ServicioAdicional> serviciosAdicionales = new ArrayList<>();

  private Double precioEstimado;

  private boolean predeterminado;

  @ManyToOne
  @JoinColumn(name = "publicador_id")
  private Usuario publicador;

  // Constructor requerido por Hibernate
  public Paquete() {}

  // Único constructor público para crear paquetes con datos
  public Paquete(
    Long id,
    String nombre,
    TipoPaquete tipo,
    Salon salon,
    Catering catering,
    List<ServicioAdicional> serviciosAdicionales,
    Double precioEstimado
  ) {
    this.id = id;
    this.nombre = nombre;
    this.tipo = tipo;
    this.salon = salon;
    this.catering = catering;
    this.serviciosAdicionales = new ArrayList<>(serviciosAdicionales);
    this.precioEstimado = precioEstimado;
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public TipoPaquete getTipo() {
    return tipo;
  }

  public Salon getSalon() {
    return salon;
  }

  public Catering getCatering() {
    return catering;
  }

  public List<ServicioAdicional> getServiciosAdicionales() {
    return serviciosAdicionales;
  }

  public Double getPrecioEstimado() {
    return precioEstimado;
  }

  public boolean isPredeterminado() {
    return predeterminado;
  }

  public void setPredeterminado(boolean predeterminado) {
    this.predeterminado = predeterminado;
  }

  // Agrupa las comidas del catering por tipo, en el orden del enum (ENTRADA, PRINCIPAL, ...).
  // Los tipos sin comidas no aparecen.
  public Map<TipoComida, List<Comida>> getMenu() {
    Map<TipoComida, List<Comida>> menu = new LinkedHashMap<>();
    if (catering == null) {
      return menu;
    }
    for (TipoComida tipoComida : TipoComida.values()) {
      List<Comida> comidas = catering
        .obtenerComidasPorTipo(tipoComida)
        .stream()
        .sorted(Comparator.comparing(Comida::getNombre))
        .toList();
      if (!comidas.isEmpty()) {
        menu.put(tipoComida, comidas);
      }
    }
    return menu;
  }

  public Usuario getPublicador() {
    return publicador;
  }

  public void setPublicador(Usuario publicador) {
    this.publicador = publicador;
  }
}
