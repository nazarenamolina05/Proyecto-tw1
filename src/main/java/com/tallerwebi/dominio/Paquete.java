package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Paquete {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;

  @Enumerated(EnumType.STRING)
  private TipoPaquete tipo;

  @ManyToOne
  @JoinColumn(name = "salon_id")
  private Salon salon;

  private String catering;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "Paquete_Servicios", joinColumns = @JoinColumn(name = "paquete_id"))
  @Column(name = "servicio")
  private List<String> serviciosAdicionales = new ArrayList<>();

  private Double precioEstimado;

  private boolean predeterminado;

  // Constructor requerido por Hibernate
  public Paquete() {}

  // Único constructor público para crear paquetes con datos
  public Paquete(
    Long id,
    String nombre,
    TipoPaquete tipo,
    Salon salon,
    String catering,
    List<String> serviciosAdicionales,
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

  public TipoPaquete getTipo() {
    return tipo;
  }

  public Salon getSalon() {
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

  public boolean isPredeterminado() {
    return predeterminado;
  }

  public void setPredeterminado(boolean predeterminado) {
    this.predeterminado = predeterminado;
  }
}
