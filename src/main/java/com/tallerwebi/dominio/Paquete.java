package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.time.LocalDate;
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

  // [REFACTOR PAQUETE] Se eliminó el @ManyToMany con ServicioAdicional.
  // Ahora la relación vive en PaqueteServicio (dos @ManyToOne) y los servicios
  // de un paquete se consultan con RepositorioPaquete.obtenerServiciosDe(idPaquete).

  private Double precioEstimado;

  private boolean predeterminado;

  @ManyToOne
  @JoinColumn(name = "publicador_id")
  private Usuario publicador;

  //Constructores

  public Paquete() {}

  public Paquete(
    Long id,
    String nombre,
    TipoPaquete tipo,
    Salon salon,
    Catering catering,
    Double precioEstimado
  ) {
    this.id = id;
    this.nombre = nombre;
    this.tipo = tipo;
    this.salon = salon;
    this.catering = catering;
    this.precioEstimado = precioEstimado;
  }

  // [REFACTOR PAQUETE] ServicioPublicarPaquete guarda un PaqueteServicio por cada
  // servicio del evento después de guardar el paquete.
  // [REFACTOR EVENTO] Recibe los servicios del evento solo para calcular el precio.
  public static Paquete desdeEvento(
    Evento evento,
    String nombre,
    Usuario publicador,
    List<ServicioAdicional> serviciosDelEvento
  ) {
    Paquete nuevo = new Paquete(
      null,
      nombre,
      evento.getTipoPaquete(),
      evento.getSalon(),
      evento.getCatering(),
      evento.calcularPresupuesto(serviciosDelEvento)
    );
    nuevo.setPublicador(publicador);
    return nuevo;
  }

  // El paquete es el molde: el evento parte de su salón y catering.
  // [REFACTOR EVENTO] Los servicios ya no se copian acá: Evento no tiene la lista.
  // ServicioEvento.crearEventoDesdePaquete guarda una fila de EventoServicio por cada
  // servicio del paquete, así modificar el evento no altera el paquete.
  public Evento crearEvento(TipoPaquete tipoEvento, LocalDate fecha, Integer cantidadInvitados) {
    Evento evento = new Evento(tipoEvento, fecha, cantidadInvitados);
    evento.setPaqueteOrigen(this);
    evento.setSalon(salon);
    evento.setCatering(catering);
    return evento;
  }

  // [REFACTOR PAQUETE] Igual que crearEvento: los servicios llegan por parámetro.
  public Double calcularPrecio(
    Integer cantidadInvitados,
    List<ServicioAdicional> serviciosDelPaquete
  ) {
    Double total = 0.0;

    if (salon != null) {
      total += salon.getPrecio();
    }
    if (catering != null) {
      total += catering.getPrecioPorPersona() * cantidadInvitados;
    }
    for (ServicioAdicional servicio : serviciosDelPaquete) {
      total += servicio.calcularCosto(cantidadInvitados);
    }
    return total;
  }

  public boolean isPublicado() {
    return publicador != null;
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

  public Double getPrecioEstimado() {
    return precioEstimado;
  }

  public boolean isPredeterminado() {
    return predeterminado;
  }

  public void setPredeterminado(boolean predeterminado) {
    this.predeterminado = predeterminado;
  }

  public Usuario getPublicador() {
    return publicador;
  }

  public void setPublicador(Usuario usuario) {
    this.publicador = usuario;
  }
}
