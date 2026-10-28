package com.tallerwebi.dominio;

import java.time.LocalDate;
import java.util.List;

public interface ServicioDisponibilidadSalon {
  List<Salon> obtenerSalonesDisponibles(LocalDate desde, LocalDate hasta);
}