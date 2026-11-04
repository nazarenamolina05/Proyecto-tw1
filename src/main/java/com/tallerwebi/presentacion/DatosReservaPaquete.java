package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.TipoPaquete;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class DatosReservaPaquete {

  private TipoPaquete tipoEvento;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate fecha;

  private Integer cantidadInvitados;

  public TipoPaquete getTipoEvento() {
    return tipoEvento;
  }

  public void setTipoEvento(TipoPaquete tipoEvento) {
    this.tipoEvento = tipoEvento;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public void setFecha(LocalDate fecha) {
    this.fecha = fecha;
  }

  public Integer getCantidadInvitados() {
    return cantidadInvitados;
  }

  public void setCantidadInvitados(Integer cantidadInvitados) {
    this.cantidadInvitados = cantidadInvitados;
  }
}
