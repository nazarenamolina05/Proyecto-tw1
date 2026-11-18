package com.tallerwebi.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;

@Entity
public class Resenia {

  public static final int PUNTAJE_MINIMO = 1;
  public static final int PUNTAJE_MAXIMO = 5;
  public static final int LARGO_MAXIMO_COMENTARIO = 1000;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Una reseña por reserva: el unique hace que la base también lo impida.
  @OneToOne(optional = false)
  @JoinColumn(unique = true)
  private Reserva reserva;

  private Integer puntaje;

  @Column(length = LARGO_MAXIMO_COMENTARIO)
  private String comentario;

  private LocalDate fecha;

  public Resenia() {}

  public Resenia(Reserva reserva, Integer puntaje, String comentario) {
    this.reserva = reserva;
    this.puntaje = puntaje;
    this.comentario = comentario;
    this.fecha = LocalDate.now();
  }

  public static boolean esPuntajeValido(Integer puntaje) {
    return puntaje != null && puntaje >= PUNTAJE_MINIMO && puntaje <= PUNTAJE_MAXIMO;
  }

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

  public Integer getPuntaje() {
    return puntaje;
  }

  public void setPuntaje(Integer puntaje) {
    this.puntaje = puntaje;
  }

  public String getComentario() {
    return comentario;
  }

  public void setComentario(String comentario) {
    this.comentario = comentario;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public void setFecha(LocalDate fecha) {
    this.fecha = fecha;
  }
}
