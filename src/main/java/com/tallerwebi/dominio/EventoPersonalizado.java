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
  private Paquete paqueteOrigen;

  public EventoPersonalizado(TipoPaquete tipo, LocalDate fecha, Integer cantidadInvitados) {
    this.tipo = tipo;
    this.fecha = fecha;
    this.cantidadInvitados = cantidadInvitados;
  }

  public Double calcularPresupuesto() {
    return sumar(salon, catering, serviciosAdicionales);
  }

  public Double calcularPrecioBase() {
    if (paqueteOrigen == null) {
      return 0.0;
    }
    return sumar(
      paqueteOrigen.getSalon(),
      paqueteOrigen.getCatering(),
      paqueteOrigen.getServiciosAdicionales()
    );
  }

  public Double calcularAdicional() {
    if (paqueteOrigen == null) {
      return 0.0;
    }
    return Math.max(0.0, calcularPresupuesto() - calcularPrecioBase());
  }

  public boolean esServicioIncluido(ServicioAdicional servicio) {
    if (paqueteOrigen == null) {
      return false;
    }
    return paqueteOrigen
      .getServiciosAdicionales()
      .stream()
      .anyMatch(incluido ->
        incluido.equals(servicio) ||
        (incluido.getId() != null && incluido.getId().equals(servicio.getId()))
      );
  }

  private Double sumar(Salon unSalon, Catering unCatering, List<ServicioAdicional> servicios) {
    Double total = 0.0;

    if (unSalon != null) {
      total += unSalon.getPrecio();
    }
    if (unCatering != null) {
      total += unCatering.getPrecioPorPersona() * cantidadInvitados;
    }
    for (ServicioAdicional servicio : servicios) {
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

  public Paquete getPaqueteOrigen() {
    return paqueteOrigen;
  }

  public void setPaqueteOrigen(Paquete paqueteOrigen) {
    this.paqueteOrigen = paqueteOrigen;
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
