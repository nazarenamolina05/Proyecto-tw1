package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Set;
import org.junit.jupiter.api.Test;

public class CateringTest {

  @Test
  public void dadoQueExisteUnaComidaQuieroAgregarlaAlCatering() {
    Comida empanadas = new Comida("Empanadas", TipoComida.ENTRADA);
    Catering catering = new Catering("Catering Completo", 5000.0);

    catering.agregarComida(empanadas);

    assertThat(catering.getComidas(), hasSize(1));
    assertThat(catering.getComidas(), contains(empanadas));
  }

  @Test
  public void UnCateringNuevoNoTieneComidas() {
    Catering catering = new Catering("Catering Basico", 1500.0);

    assertThat(catering.getComidas(), is(empty()));
  }

  @Test
  public void AgregarLaMismaComidaDosVecesNoLaDuplica() {
    Comida empanadas = new Comida("Empanadas", TipoComida.ENTRADA);
    Catering catering = new Catering("Catering Completo", 5000.0);

    catering.agregarComida(empanadas);
    catering.agregarComida(empanadas);

    assertThat(catering.getComidas(), hasSize(1));
  }

  @Test
  public void dadoQueCreoUnaComidaQuieroObtenerlaPorElTipo() {
    Comida empanadas = new Comida("Empanadas", TipoComida.ENTRADA);
    Catering catering = new Catering("Catering Completo", 5000.0);

    catering.agregarComida(empanadas);

    Set<Comida> entradas = catering.obtenerComidasPorTipo(TipoComida.ENTRADA);

    assertThat(entradas, hasSize(1));
    assertThat(entradas, contains(empanadas));
  }

  @Test
  public void dadoQueExisteUnaComidaVegetarianaDevolverVerde() {
    Comida agua = new Comida("Agua mineral", TipoComida.BEBIDA);
    agua.setVegetariana(true);
    Catering catering = new Catering("Catering Completo", 5000.0);
    catering.agregarComida(agua);

    Set<Comida> bebida = catering.obtenerComidasVegetarianas();

    assertThat(bebida, hasSize(1));
    assertThat(bebida, contains(agua));
  }
}
