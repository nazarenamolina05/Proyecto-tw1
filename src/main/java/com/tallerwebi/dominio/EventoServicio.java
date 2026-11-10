package com.tallerwebi.dominio;

import jakarta.persistence.*;

@Entity
public class EventoServicio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Evento evento;

  @ManyToOne
  private ServicioAdicional servicio;

  public EventoServicio() {}

  public EventoServicio(Evento evento, ServicioAdicional servicio) {
    this.evento = evento;
    this.servicio = servicio;
  }

  public Long getId() {
    return id;
  }

  public Evento getEvento() {
    return evento;
  }

  public ServicioAdicional getServicio() {
    return servicio;
  }
}
