//package com.tallerwebi.presentacion;
//
//import com.tallerwebi.dominio.PrioridadArmado;
//import com.tallerwebi.dominio.TipoPaquete;
//import java.time.LocalDate;
//import org.springframework.format.annotation.DateTimeFormat;
//
//public class ArmadoAutomaticoForm {
//
//  private TipoPaquete tipo;
//
//  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//  private LocalDate fecha;
//
//  private Integer cantidadInvitados;
//  private Double presupuestoMaximo;
//  private PrioridadArmado prioridad;
//
//  public TipoPaquete getTipo() {
//    return tipo;
//  }
//
//  public void setTipo(TipoPaquete tipo) {
//    this.tipo = tipo;
//  }
//
//  public LocalDate getFecha() {
//    return fecha;
//  }
//
//  public void setFecha(LocalDate fecha) {
//    this.fecha = fecha;
//  }
//
//  public Integer getCantidadInvitados() {
//    return cantidadInvitados;
//  }
//
//  public void setCantidadInvitados(Integer cantidadInvitados) {
//    this.cantidadInvitados = cantidadInvitados;
//  }
//
//  public Double getPresupuestoMaximo() {
//    return presupuestoMaximo;
//  }
//
//  public void setPresupuestoMaximo(Double presupuestoMaximo) {
//    this.presupuestoMaximo = presupuestoMaximo;
//  }
//
//  public PrioridadArmado getPrioridad() {
//    return prioridad;
//  }
//
//  public void setPrioridad(PrioridadArmado prioridad) {
//    this.prioridad = prioridad;
//  }
//}

package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.PrioridadArmado;

public class ArmadoAutomaticoForm {

  private Double presupuestoMaximo;
  private PrioridadArmado prioridad;

  public Double getPresupuestoMaximo() {
    return presupuestoMaximo;
  }

  public void setPresupuestoMaximo(Double presupuestoMaximo) {
    this.presupuestoMaximo = presupuestoMaximo;
  }

  public PrioridadArmado getPrioridad() {
    return prioridad;
  }

  public void setPrioridad(PrioridadArmado prioridad) {
    this.prioridad = prioridad;
  }
}
