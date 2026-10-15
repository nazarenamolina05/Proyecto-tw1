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

  @ManyToOne
  private Paquete paquete;

  private LocalDate fecha;
  private Double presupuesto;

  @Enumerated(EnumType.STRING)
  private EstadoReserva estado;

  @Enumerated(EnumType.STRING)
  private TipoPago tipoPago;

  private Double montoAbonado;
  private String nombreContacto;
  private String telefonoContacto;

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

  public Paquete getPaquete() {
    return paquete;
  }

  public void setPaquete(Paquete paquete) {
    this.paquete = paquete;
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

  public TipoPago getTipoPago() {
    return tipoPago;
  }

  public void setTipoPago(TipoPago tipoPago) {
    this.tipoPago = tipoPago;
  }

  public Double getMontoAbonado() {
    return montoAbonado;
  }

  public void setMontoAbonado(Double montoAbonado) {
    this.montoAbonado = montoAbonado;
  }

  public String getNombreContacto() {
    return nombreContacto;
  }

  public void setNombreContacto(String nombreContacto) {
    this.nombreContacto = nombreContacto;
  }

  public String getTelefonoContacto() {
    return telefonoContacto;
  }

  public void setTelefonoContacto(String telefonoContacto) {
    this.telefonoContacto = telefonoContacto;
  }
}
