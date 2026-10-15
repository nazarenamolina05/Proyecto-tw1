package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;

public class ServicioSugerenciaServiciosTest {

  private RepositorioServicioAdicional repositorioServicioAdicional;
  private ServicioSugerenciaServicios servicioSugerenciaServicios;

  @BeforeEach
  public void init() {
    repositorioServicioAdicional = mock(RepositorioServicioAdicional.class);
    servicioSugerenciaServicios = mock(ServicioSugerenciaServicios.class);
  }

  @Test
  @Transactional
  @Rollback
  public void dadoUnCumpleaniosLosServiciosDeAnimacionCotillonYDjQuedanComoSugeridos() {
    when(repositorioServicioAdicional.obtenerTodos())
      .thenReturn(
        List.of(
          new ServicioAdicional("DJ", 40000.0, false, CategoriaServicio.DJ),
          new ServicioAdicional("Video", 45000.0, false, CategoriaServicio.VIDEO),
          new ServicioAdicional("Animacion", 35000.0, false, CategoriaServicio.ANIMACION)
        )
      );
    servicioSugerenciaServicios = new ServicioSugerenciaServiciosImpl(repositorioServicioAdicional);
    EventoPersonalizado evento = new EventoPersonalizado(
      TipoPaquete.CUMPLEANIOS,
      LocalDate.now(),
      30
    );

    ServiciosClasificados resultado = servicioSugerenciaServicios.clasificar(evento);

    assertThat(resultado.getSugeridos(), hasSize(2));
    assertThat(resultado.getRestantes(), hasSize(1));
    assertThat(resultado.getRestantes().get(0).getNombre(), is("Video"));
  }
}
