package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Evento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate fecha;

  @Enumerated(EnumType.STRING)
  private TipoPaquete tipoPaquete;

  private Integer cantidadInvitados;

  @ManyToOne
  private Salon salon;

  @ManyToOne
  private Catering catering;

  @OneToMany
  private List<ServicioAdicional> servicioAdicionalesList;

  public Evento() {}

  public Evento(
    LocalDate fecha,
    TipoPaquete tipoPaquete,
    Integer cantidadInvitados,
    Salon salon,
    Catering catering,
    List<ServicioAdicional> servicioAdicionalesList
  ) {
    this.fecha = fecha;
    this.tipoPaquete = tipoPaquete;
    this.cantidadInvitados = cantidadInvitados;
    this.salon = salon;
    this.catering = catering;
    this.servicioAdicionalesList = servicioAdicionalesList;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public void setFecha(LocalDate fecha) {
    this.fecha = fecha;
  }

  public TipoPaquete getTipoPaquete() {
    return tipoPaquete;
  }

  public void setTipoPaquete(TipoPaquete tipoPaquete) {
    this.tipoPaquete = tipoPaquete;
  }

  public Integer getCantidadInvitados() {
    return cantidadInvitados;
  }

  public void setCantidadInvitados(Integer cantidadInvitados) {
    this.cantidadInvitados = cantidadInvitados;
  }

  public Salon getSalon() {
    return salon;
  }

  public void setSalon(Salon salon) {
    this.salon = salon;
  }

  public Catering getCatering() {
    return catering;
  }

  public void setCatering(Catering catering) {
    this.catering = catering;
  }

  public List<ServicioAdicional> getServicioAdicionalsList() {
    return servicioAdicionalesList;
  }

  public void setServicioAdicionalsList(List<ServicioAdicional> servicioAdicionalesList) {
    this.servicioAdicionalesList = servicioAdicionalesList;
  }

  // Logica
  public void agregarServicio(ServicioAdicional servicio) {
    servicioAdicionalesList.add(servicio);
  }

  public void quitarServicio(ServicioAdicional servicio) {
    servicioAdicionalesList.remove(servicio);
  }

  public Double calcularPresupuesto() {
    Double total = 0.0;

    if (salon != null) {
      total += salon.getPrecio();
    }
    if (catering != null) {
      total += catering.getPrecioPorPersona() * cantidadInvitados;
    }

    for (ServicioAdicional servicio : servicioAdicionalesList) {
      total += servicio.calcularCosto(cantidadInvitados);
    }
    return total;
  }
}
