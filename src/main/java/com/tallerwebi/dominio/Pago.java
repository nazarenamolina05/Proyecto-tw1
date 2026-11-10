package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Pago {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Reserva reserva;

  @Enumerated(EnumType.STRING)
  private TipoPago tipo;

  @Enumerated(EnumType.STRING)
  private EstadoPago estado;

  private Double monto;

  private Long mpPaymentId; // id del pago en Mercado Pago (se completa cuando paga)

  private LocalDateTime fecha;

  public Pago() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Reserva getReserva() {
    return reserva;
  }

  public void setReserva(Reserva reserva) {
    this.reserva = reserva;
  }

  public TipoPago getTipo() {
    return tipo;
  }

  public void setTipo(TipoPago tipo) {
    this.tipo = tipo;
  }

  public EstadoPago getEstado() {
    return estado;
  }

  public void setEstado(EstadoPago estado) {
    this.estado = estado;
  }

  public Double getMonto() {
    return monto;
  }

  public void setMonto(Double monto) {
    this.monto = monto;
  }

  public Long getMpPaymentId() {
    return mpPaymentId;
  }

  public void setMpPaymentId(Long mpPaymentId) {
    this.mpPaymentId = mpPaymentId;
  }

  public LocalDateTime getFecha() {
    return fecha;
  }

  public void setFecha(LocalDateTime fecha) {
    this.fecha = fecha;
  }
}
