package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

public class ResultadoArmado {

  private TipoResultado tipoResultado;
  private Double montoMinimo;
  private Salon salon;
  private Catering catering;
  private List<ServicioAdicional> servicioAdicional = new ArrayList<>();

  public ResultadoArmado(TipoResultado tipoResultado, Double montoMinimo) {
    this.tipoResultado = tipoResultado;
    this.montoMinimo = montoMinimo;
  }

  public ResultadoArmado(
    TipoResultado tipoResultado,
    Salon salon,
    Catering catering,
    List<ServicioAdicional> servicioAdicional
  ) {
    this.tipoResultado = tipoResultado;
    this.salon = salon;
    this.catering = catering;
    this.servicioAdicional = servicioAdicional;
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

  public List<ServicioAdicional> getServicioAdicional() {
    return servicioAdicional;
  }

  public TipoResultado getTipoResultado() {
    return tipoResultado;
  }

  public void setTipoResultado(TipoResultado tipoResultado) {
    this.tipoResultado = tipoResultado;
  }

  public Double getMontoMinimo() {
    return montoMinimo;
  }

  public void setMontoMinimo(Double montoMinimo) {
    this.montoMinimo = montoMinimo;
  }
}
