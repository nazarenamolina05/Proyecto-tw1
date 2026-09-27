package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Paquete;
import com.tallerwebi.dominio.RepositorioPaquete;
import com.tallerwebi.dominio.TipoPaquete;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioPaqueteImpl implements RepositorioPaquete {

  @Override
  public List<Paquete> obtenerTodos() {
    return List.of(
      new Paquete("Casamiento clásico", TipoPaquete.CASAMIENTO),
      new Paquete("Cumpleaños familiar", TipoPaquete.CUMPLEANIOS),
      new Paquete("Fiesta de quince", TipoPaquete.FIESTA_DE_15),
      new Paquete("Cumpleaños Premium", TipoPaquete.CUMPLEANIOS)
    );
  }

  @Override
  public List<Paquete> obtenerPredeterminados() {
    return List.of(
      new Paquete(
        1L,
        "Básico",
        TipoPaquete.CUMPLEANIOS,
        "Salón Los Álamos",
        "Catering Don José",
        List.of("DJ"),
        80000.0
      ),
      new Paquete(
        2L,
        "Estándar",
        TipoPaquete.FIESTA_DE_15,
        "Salón Jardín del Sol",
        "Catering Fiesta Plena",
        List.of("DJ", "Fotografía"),
        150000.0
      ),
      new Paquete(
        3L,
        "Premium",
        TipoPaquete.CASAMIENTO,
        "Salón Gran Bahía",
        "Catering Alta Cocina",
        List.of("DJ", "Fotografía", "Video", "Decoración"),
        300000.0
      )
    );
  }
}
