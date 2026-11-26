package com.tallerwebi.dominio;

public class MontoMinimo {

  private Double salon;
  private Double catering;

  public MontoMinimo(Double salon, Double catering) {
    this.salon = salon;
    this.catering = catering;
  }

  public Double getSalon() {
    return salon;
  }

  public Double getCatering() {
    return catering;
  }

  public Double getTotal() {
    return salon + catering;
  }
}
