package com.tallerwebi.dominio;

import java.time.LocalDate;

public interface ServicioArmadoAutomatico {
  ResultadoArmado armar(
    TipoPaquete tipoPaquete,
    Integer invitados,
    LocalDate fecha,
    Double presupuestoMaximo,
    PrioridadArmado prioridadArmado
  );

  MontoMinimo calcularMontoMinimo(Integer invitados, LocalDate fecha);
}
