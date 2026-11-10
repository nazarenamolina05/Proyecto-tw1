package com.tallerwebi.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Evento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate fecha;

  @Enumerated(EnumType.STRING)
  private TipoPaquete tipoPaquete;

  private Integer cantidadInvitados;

  // Paquete (molde) del que se creó este evento. Es null si se armó desde cero.
  @ManyToOne
  private Paquete paqueteOrigen;

  @ManyToOne
  private Salon salon;

  @ManyToOne
  private Catering catering;

  // [REFACTOR EVENTO] Se eliminó el @ManyToMany con ServicioAdicional, igual que en Paquete.
  // Ahora la relación vive en EventoServicio (dos @ManyToOne) y los servicios de un
  // evento se consultan con RepositorioEvento.buscarServicio(eventoId).

  public Evento() {}

  public Evento(TipoPaquete tipoPaquete, LocalDate fecha, Integer cantidadInvitados) {
    this.tipoPaquete = tipoPaquete;
    this.fecha = fecha;
    this.cantidadInvitados = cantidadInvitados;
  }

  // Logica
  // [REFACTOR EVENTO] Evento ya no tiene la lista: los servicios llegan por parámetro.
  // Se obtienen con servicioEvento.obtenerServiciosDe(evento).
  public Double calcularPresupuesto(List<ServicioAdicional> serviciosDelEvento) {
    Double total = 0.0;

    if (salon != null) {
      total += salon.getPrecio();
    }
    if (catering != null) {
      total += catering.getPrecioPorPersona() * cantidadInvitados;
    }

    for (ServicioAdicional servicio : serviciosDelEvento) {
      total += servicio.calcularCosto(cantidadInvitados);
    }
    return total;
  }

  // [REFACTOR PAQUETE -> EVENTO] Paquete ya no tiene la lista de servicios, así que
  // estos tres métodos reciben los servicios del paquete de origen por parámetro.
  // Se obtienen con servicioConsultarPaquetesPredeterminados.consultarServiciosDe(paqueteOrigen).
  // Antes: calcularPrecioBase(), calcularAdicional() y esServicioIncluido(servicio).
  public Double calcularPrecioBase(List<ServicioAdicional> serviciosDelPaquete) {
    if (paqueteOrigen == null) {
      return 0.0;
    }
    return paqueteOrigen.calcularPrecio(cantidadInvitados, serviciosDelPaquete);
  }

  // [REFACTOR EVENTO] Ahora también recibe los servicios del evento (para el presupuesto).
  public Double calcularAdicional(
    List<ServicioAdicional> serviciosDelEvento,
    List<ServicioAdicional> serviciosDelPaquete
  ) {
    if (paqueteOrigen == null) {
      return 0.0;
    }
    return Math.max(
      0.0,
      calcularPresupuesto(serviciosDelEvento) - calcularPrecioBase(serviciosDelPaquete)
    );
  }

  public boolean esServicioIncluido(
    ServicioAdicional servicio,
    List<ServicioAdicional> serviciosDelPaquete
  ) {
    if (paqueteOrigen == null) {
      return false;
    }
    return serviciosDelPaquete
      .stream()
      .anyMatch(incluido ->
        incluido.equals(servicio) ||
        (incluido.getId() != null && incluido.getId().equals(servicio.getId()))
      );
  }

  public String getNombre() {
    if (paqueteOrigen == null) {
      return "Evento personalizado";
    }
    return paqueteOrigen.getNombre();
  }

  public boolean yaOcurrio() {
    return fecha != null && fecha.isBefore(LocalDate.now());
  }

  public boolean isCompleto() {
    return salon != null && catering != null;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public void setFecha(LocalDate fecha) {
    this.fecha = fecha;
  }

  public TipoPaquete getTipoPaquete() {
    return tipoPaquete;
  }

  public void setTipoPaquete(TipoPaquete tipoPaquete) {
    this.tipoPaquete = tipoPaquete;
  }

  public Integer getCantidadInvitados() {
    return cantidadInvitados;
  }

  public void setCantidadInvitados(Integer cantidadInvitados) {
    this.cantidadInvitados = cantidadInvitados;
  }

  public Paquete getPaqueteOrigen() {
    return paqueteOrigen;
  }

  public void setPaqueteOrigen(Paquete paqueteOrigen) {
    this.paqueteOrigen = paqueteOrigen;
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
}
