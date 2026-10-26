package com.tallerwebi.dominio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Reserva {

  public static final double PORCENTAJE_SENIA = 0.30;

  public Reserva() {}

  public Reserva(Usuario usuario, Evento evento, TipoPago tipoPago) {
    this.usuario = usuario;
    this.evento = evento;
    this.tipoPago = tipoPago;
    this.presupuesto = evento.calcularPresupuesto();

    if (tipoPago == TipoPago.SENIA) {
      this.estado = EstadoReserva.SENIADA;
      this.montoAbonado = presupuesto * PORCENTAJE_SENIA;
    } else {
      this.estado = EstadoReserva.CONFIRMADA;
      this.montoAbonado = presupuesto;
    }
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Usuario usuario;

  @OneToOne(optional = false, cascade = CascadeType.PERSIST)
  private Evento evento;

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

  public Evento getEvento() {
    return evento;
  }

  public void setEvento(Evento evento) {
    this.evento = evento;
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
