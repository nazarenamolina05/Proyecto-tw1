package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Paquete {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;

  @Enumerated(EnumType.STRING)
  private TipoPaquete tipo;

  @ManyToOne
  @JoinColumn(name = "salon_id")
  private Salon salon;

  @ManyToOne
  @JoinColumn(name = "catering_id")
  private Catering catering;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "Paquete_Servicios",
    joinColumns = @JoinColumn(name = "paquete_id"),
    inverseJoinColumns = @JoinColumn(name = "servicio_id")
  )
  private List<ServicioAdicional> serviciosAdicionales = new ArrayList<>();

  private Double precioEstimado;

  private boolean predeterminado;

  public Paquete() {}

  public Paquete(
    Long id,
    String nombre,
    TipoPaquete tipo,
    Salon salon,
    Catering catering,
    List<ServicioAdicional> serviciosAdicionales,
    Double precioEstimado
  ) {
    this.id = id;
    this.nombre = nombre;
    this.tipo = tipo;
    this.salon = salon;
    this.catering = catering;
    this.serviciosAdicionales = new ArrayList<>(serviciosAdicionales);
    this.precioEstimado = precioEstimado;
  }

  // El paquete es el molde: el evento parte de su salón, catering y servicios,
  // pero con su propia lista, para que modificarlo no altere el paquete.
  public Evento crearEvento(TipoPaquete tipoEvento, LocalDate fecha, Integer cantidadInvitados) {
    Evento evento = new Evento(tipoEvento, fecha, cantidadInvitados);
    evento.setPaqueteOrigen(this);
    evento.setSalon(salon);
    evento.setCatering(catering);
    serviciosAdicionales.forEach(evento::agregarServicio);
    return evento;
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public TipoPaquete getTipo() {
    return tipo;
  }

  public Salon getSalon() {
    return salon;
  }

  public Catering getCatering() {
    return catering;
  }

  public List<ServicioAdicional> getServiciosAdicionales() {
    return serviciosAdicionales;
  }

  public Double getPrecioEstimado() {
    return precioEstimado;
  }

  public boolean isPredeterminado() {
    return predeterminado;
  }

  public void setPredeterminado(boolean predeterminado) {
    this.predeterminado = predeterminado;
  }
}
