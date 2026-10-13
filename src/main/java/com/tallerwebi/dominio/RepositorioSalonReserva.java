package com.tallerwebi.dominio;

import java.time.LocalDate;
import java.util.List;

@FunctionalInterface
public interface RepositorioSalonReserva {
    List<SalonReserva> obtenerEntreFechas(LocalDate desde, LocalDate hasta);
}