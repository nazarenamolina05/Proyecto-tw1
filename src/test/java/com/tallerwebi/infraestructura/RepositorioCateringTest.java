package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.Catering;
import com.tallerwebi.dominio.RepositorioCatering;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HibernateInfraestructuraTestConfig.class })
public class RepositorioCateringTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioCatering repositorioCatering;

  @BeforeEach
  public void init() {
    repositorioCatering = new RepositorioCateringImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerLosCateringOrdenadosDeFormaDesc() {
    //Preparacion
    this.dadoQueExisteUnCatering("Catering Medio", 3000.0);
    this.dadoQueExisteUnCatering("Catering Completo", 5000.0);
    this.dadoQueExisteUnCatering("Catering Basico", 1500.0);

    // Ejecucion
    List<Catering> obtenidos = this.cuandoObtengoTodosLosCatering();

    // Verificacion
    this.entoncesLosCateringEstanOrdenados(obtenidos);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerUnaListaVaciaSiNoHayCatering() {
    List<Catering> obtenidos = this.cuandoObtengoTodosLosCatering();

    assertThat(obtenidos, is(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaEncontrarUnCateringExistenteCuandoBuscoPorId() {
    // Preparacion
    Catering catering = this.dadoQueExisteUnCatering("Catering Completo", 5000.0);

    //  Ejecucion
    Catering obtenido = this.cuandoBuscoUnCateringPorId(catering.getId());

    // Verificacion
    assertThat(obtenido.getNombre(), is(equalTo("Catering Completo")));
    assertThat(obtenido.getPrecioPorPersona(), is(equalTo(5000.0)));
  }

  @Test
  @Transactional
  @Rollback
  public void noDeberiaEncontrarUnCateringInexistenteCuandoBuscoPorId() {
    Catering obtenido = this.cuandoBuscoUnCateringPorId(9999L);

    assertThat(obtenido, is(nullValue()));
  }

  private Catering dadoQueExisteUnCatering(String nombre, Double precioPorPersona) {
    Catering catering = new Catering(nombre, precioPorPersona);
    this.sessionFactory.getCurrentSession().persist(catering);
    return catering;
  }

  private List<Catering> cuandoObtengoTodosLosCatering() {
    return repositorioCatering.obtenerTodos();
  }

  private Catering cuandoBuscoUnCateringPorId(Long id) {
    return repositorioCatering.buscarPorId(id);
  }

  private void entoncesLosCateringEstanOrdenados(List<Catering> obtenidos) {
    assertThat(obtenidos, hasSize(3));
    assertThat(obtenidos.get(0).getNombre(), is(equalTo("Catering Completo")));
    assertThat(obtenidos.get(1).getNombre(), is(equalTo("Catering Medio")));
    assertThat(obtenidos.get(2).getNombre(), is(equalTo("Catering Basico")));
  }
}
