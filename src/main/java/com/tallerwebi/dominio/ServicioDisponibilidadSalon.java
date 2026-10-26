package com.tallerwebi.dominio;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface ServicioDisponibilidadSalon {
  List<Salon> obtenerSalonesDisponibles(LocalDate desde, LocalDate hasta);

  Map<LocalDate, Boolean> obtenerDisponibilidadDelMes(YearMonth mes);
}
