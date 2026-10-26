package com.tallerwebi.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.ArrayList;
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

  // Paquete (molde) del que se creó este evento. Es null si se armó desde cero.
  @ManyToOne
  private Paquete paqueteOrigen;

  @ManyToOne
  private Salon salon;

  @ManyToOne
  private Catering catering;

  @ManyToMany
  @JoinTable(
    name = "evento_servicio",
    joinColumns = @JoinColumn(name = "evento_id"),
    inverseJoinColumns = @JoinColumn(name = "servicio_id")
  )
  private List<ServicioAdicional> servicioAdicionalesList = new ArrayList<>();

  public Evento() {}

  public Evento(TipoPaquete tipoPaquete, LocalDate fecha, Integer cantidadInvitados) {
    this.tipoPaquete = tipoPaquete;
    this.fecha = fecha;
    this.cantidadInvitados = cantidadInvitados;
  }

  // Logica
  public void agregarServicio(ServicioAdicional servicio) {
    if (!servicioAdicionalesList.contains(servicio)) {
      servicioAdicionalesList.add(servicio);
    }
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

  public Paquete getPaqueteOrigen() {
    return paqueteOrigen;
  }

  public void setPaqueteOrigen(Paquete paqueteOrigen) {
    this.paqueteOrigen = paqueteOrigen;
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
}
