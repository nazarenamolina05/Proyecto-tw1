package com.tallerwebi.infraestructura;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tallerwebi.dominio.Paquete;
import com.tallerwebi.dominio.TipoPaquete;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositorioPaqueteImplTest {

  private RepositorioPaqueteImpl repositorio;

  @BeforeEach
  public void init() {
    repositorio = new RepositorioPaqueteImpl();
  }

  @Test
  public void obtenerTodosDevuelveLosCuatroPaquetesPublicados() {
    //ejecucion
    List<Paquete> paquetes = repositorio.obtenerTodos();

    //validacion
    assertEquals(4, paquetes.size());
    assertEquals("Casamiento clásico", paquetes.get(0).getNombre());
    assertEquals(TipoPaquete.CASAMIENTO, paquetes.get(0).getTipo());
  }

  @Test
  public void obtenerPredeterminadosDevuelveLosTresPaquetesConSusDatos() {
    //ejecucion
    List<Paquete> paquetes = repositorio.obtenerPredeterminados();

    //validacion
    assertEquals(3, paquetes.size());

    Paquete basico = paquetes.get(0);
    assertEquals("Básico", basico.getNombre());
    assertEquals("Salón Los Álamos", basico.getSalon());
    assertEquals(80000.0, basico.getPrecioEstimado(), 0.01);
  }
}
