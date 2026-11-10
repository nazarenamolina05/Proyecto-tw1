package com.tallerwebi.dominio;

import jakarta.persistence.*;

// [REFACTOR PAQUETE] Clase intermedia que reemplaza al @ManyToMany entre Paquete y
// ServicioAdicional (el profe pidió usar solo @OneToOne y @ManyToOne).
// Cada fila dice "este paquete incluye este servicio". Es el mismo esquema que
// La tabla se sigue llamando Paquete_Servicios, así los INSERT de data.sql no cambian.
@Entity
@Table(name = "Paquete_Servicios")
public class PaqueteServicio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "paquete_id")
  private Paquete paquete;

  @ManyToOne(optional = false)
  @JoinColumn(name = "servicio_id")
  private ServicioAdicional servicio;

  public PaqueteServicio() {}

  public PaqueteServicio(Paquete paquete, ServicioAdicional servicio) {
    this.paquete = paquete;
    this.servicio = servicio;
  }

  public Long getId() {
    return id;
  }

  public Paquete getPaquete() {
    return paquete;
  }

  public ServicioAdicional getServicio() {
    return servicio;
  }
}
