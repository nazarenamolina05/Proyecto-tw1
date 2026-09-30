package com.tallerwebi.dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventoPersonalizado {

  private TipoPaquete tipo;
  private LocalDate fecha;
  private Integer cantidadInvitados;
  private Salon salon;
  private Catering catering;
  private List<ServicioAdicional> serviciosAdicionales = new ArrayList<>();

  public EventoPersonalizado(TipoPaquete tipo, LocalDate fecha, Integer cantidadInvitados) {
    this.tipo = tipo;
    this.fecha = fecha;
    this.cantidadInvitados = cantidadInvitados;
  }

  public Double calcularPresupuesto() {
    Double total = 0.0;

    if (salon != null) {
      total += salon.getPrecio();
    }
    if (catering != null) {
      total += catering.getPrecioPorPersona() * cantidadInvitados;
    }

    for (ServicioAdicional servicio : serviciosAdicionales) {
      total += servicio.calcularCosto(cantidadInvitados);
    }
    return total;
  }

  public TipoPaquete getTipo() {
    return tipo;
  }

  public void setTipo(TipoPaquete tipo) {
    this.tipo = tipo;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public void setFecha(LocalDate fecha) {
    this.fecha = fecha;
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

  public List<ServicioAdicional> getServiciosAdicionales() {
    return serviciosAdicionales;
  }

  public void agregarServicio(ServicioAdicional servicio) {
    serviciosAdicionales.add(servicio);
  }

  public void quitarServicio(ServicioAdicional servicio) {
    serviciosAdicionales.remove(servicio);
  }
}
