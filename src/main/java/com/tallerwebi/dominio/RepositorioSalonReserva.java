package com.tallerwebi.dominio;

import java.time.LocalDate;
import java.util.List;

public interface RepositorioSalonReserva {
    List<SalonReserva> obtenerEntreFechas(LocalDate desde, LocalDate hasta);
}