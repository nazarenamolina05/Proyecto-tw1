package com.tallerwebi.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class Reserva {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Usuario usuario;

  private String nombrePaquete; // provisorio, hasta que Paquete/Evento sea una entidad persistente

  @Enumerated(EnumType.STRING)
  private TipoPaquete tipo;

  private LocalDate fecha;
  private Double presupuesto;

  @Enumerated(EnumType.STRING)
  private EstadoReserva estado;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public String getNombrePaquete() {
    return nombrePaquete;
  }

  public void setNombrePaquete(String nombrePaquete) {
    this.nombrePaquete = nombrePaquete;
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

  public Double getPresupuesto() {
    return presupuesto;
  }

  public void setPresupuesto(Double presupuesto) {
    this.presupuesto = presupuesto;
  }

  public EstadoReserva getEstado() {
    return estado;
  }

  public void setEstado(EstadoReserva estado) {
    this.estado = estado;
  }
}
