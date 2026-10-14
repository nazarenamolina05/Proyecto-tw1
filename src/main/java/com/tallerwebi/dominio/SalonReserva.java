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
public class SalonReserva {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Salon salon;

  private LocalDate fecha;

  @Enumerated(EnumType.STRING)
  private EstadoReserva estado;

  public SalonReserva() {}

  public SalonReserva(Salon salon, LocalDate fecha, EstadoReserva estado) {
    this.salon = salon;
    this.fecha = fecha;
    this.estado = estado;
  }

  public Salon getSalon() {
    return salon;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public EstadoReserva getEstado() {
    return estado;
  }
}
