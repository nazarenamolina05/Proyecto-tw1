package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.TipoPaquete;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class DatosEventoForm {

  private Long paqueteId;
  private TipoPaquete tipo;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate fecha;

  private Integer cantidadInvitados;

  public Long getPaqueteId() {
    return paqueteId;
  }

  public void setPaqueteId(Long paqueteId) {
    this.paqueteId = paqueteId;
  }

  public TipoPaquete getTipo() {
    return tipo;
  }

  public void setTipo(TipoPaquete tipo) {
    this.tipo = tipo;
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
