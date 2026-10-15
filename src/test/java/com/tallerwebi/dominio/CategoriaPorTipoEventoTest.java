package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CategoriaPorTipoEventoTest {

  // 1- primer test que verifica si trae la lista con la categoria por el tipo de evento CUMPLEANIOS
  @Test
  public void dadoQueSeleccionoUnTipoDeEVentoCumpleaniosQuieroQueMeSugieraLosServiciosParaElTipoDeEVento() {
    EventoPersonalizado evento = new EventoPersonalizado(
      TipoPaquete.CUMPLEANIOS,
      LocalDate.now(),
      30
    );

    CategoriaPorTipoEvento categoriaPorTipoEvento = new CategoriaPorTipoEvento();

    List<CategoriaServicio> obtenida = categoriaPorTipoEvento.getCategoriaSugerida(evento);

    assertThat(
      obtenida,
      contains(CategoriaServicio.ANIMACION, CategoriaServicio.COTILLON, CategoriaServicio.DJ)
    );
  }

  // 2- segundo test que verifica si trae la lista con la categoria por el tipo de evento FIESTA_DE_15
  @Test
  public void dadoQueSeleccionoUnTipoDeEVentoFiestaDe15QuieroQueMeSugieraLosServiciosParaElTipoDeEVento() {
    EventoPersonalizado evento = new EventoPersonalizado(
      TipoPaquete.FIESTA_DE_15,
      LocalDate.now(),
      30
    );

    CategoriaPorTipoEvento categoriaPorTipoEvento = new CategoriaPorTipoEvento();

    List<CategoriaServicio> obtenida = categoriaPorTipoEvento.getCategoriaSugerida(evento);

    assertThat(
      obtenida,
      contains(
        CategoriaServicio.DECORACION,
        CategoriaServicio.FOTOGRAFIA,
        CategoriaServicio.VIDEO,
        CategoriaServicio.DJ
      )
    );
  }

  // 3- tercer test que verifica si trae la lista con la categoria por el tipo de evento CASAMIENTO

  @Test
  public void dadoQueSeleccionoUnTipoDeEVentoCasamientoQuieroQueMeSugieraLosServiciosParaElTipoDeEVento() {
    EventoPersonalizado evento = new EventoPersonalizado(
      TipoPaquete.CASAMIENTO,
      LocalDate.now(),
      30
    );

    CategoriaPorTipoEvento categoriaPorTipoEvento = new CategoriaPorTipoEvento();

    List<CategoriaServicio> obtenida = categoriaPorTipoEvento.getCategoriaSugerida(evento);

    assertThat(
      obtenida,
      contains(
        CategoriaServicio.DECORACION,
        CategoriaServicio.FOTOGRAFIA,
        CategoriaServicio.VIDEO,
        CategoriaServicio.DJ
      )
    );
  }

  // 4- cuarto test que verifica si trae la lista con la categoria por el tipo de evento EMPRESARIAL

  @Test
  public void dadoQueSeleccionoUnTipoDeEVentoEmpresarialQuieroQueMeSugieraLosServiciosParaElTipoDeEVento() {
    EventoPersonalizado evento = new EventoPersonalizado(
      TipoPaquete.EMPRESARIAL,
      LocalDate.now(),
      30
    );

    CategoriaPorTipoEvento categoriaPorTipoEvento = new CategoriaPorTipoEvento();

    List<CategoriaServicio> obtenida = categoriaPorTipoEvento.getCategoriaSugerida(evento);

    assertThat(obtenida, contains(CategoriaServicio.VIDEO, CategoriaServicio.DJ));
  }

  @Test
  public void alLlamarDosVecesConElMismoEventoDevuelveLasMismasCategorias() {
    EventoPersonalizado evento = new EventoPersonalizado(
      TipoPaquete.EMPRESARIAL,
      LocalDate.now(),
      30
    );

    CategoriaPorTipoEvento categoriaPorTipoEvento = new CategoriaPorTipoEvento();

    List<CategoriaServicio> primera = categoriaPorTipoEvento.getCategoriaSugerida(evento);
    List<CategoriaServicio> segunda = categoriaPorTipoEvento.getCategoriaSugerida(evento);

    assertThat(primera, contains(CategoriaServicio.VIDEO, CategoriaServicio.DJ));
    assertThat(segunda, contains(CategoriaServicio.VIDEO, CategoriaServicio.DJ));
  }
}
