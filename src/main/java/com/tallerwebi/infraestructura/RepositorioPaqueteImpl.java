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
}
