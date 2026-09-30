package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import com.tallerwebi.dominio.RepositorioSalon;
import com.tallerwebi.dominio.Salon;
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
public class RepositorioSalonTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioSalon repositorioSalon;

  @BeforeEach
  public void init() {
    repositorioSalon = new RepositorioSalonImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerLosSalonesOrdenadosDeFormaDesc() {
    this.dadoQueExisteUnSalon("Salón Los Álamos", 50000.0);
    this.dadoQueExisteUnSalon("Salón Gran Bahía", 150000.0);
    this.dadoQueExisteUnSalon("Salón Jardín del Sol", 90000.0);

    List<Salon> obtenidos = this.cuandoObtengoTodosLosSalones();

    this.entoncesLosSalonesEstanOrdenados(obtenidos);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerUnaListaVaciaSiNoHaySalones() {
    List<Salon> obtenidos = this.cuandoObtengoTodosLosSalones();

    assertThat(obtenidos, is(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaEncontrarUnSalonExistenteCuandoBuscoPorId() {
    Salon salon = this.dadoQueExisteUnSalon("Salón Gran Bahía", 150000.0);

    Salon obtenido = this.cuandoBuscoUnSalonPorId(salon.getId());

    assertThat(obtenido.getNombre(), is(equalTo("Salón Gran Bahía")));
    assertThat(obtenido.getPrecio(), is(equalTo(150000.0)));
  }

  @Test
  @Transactional
  @Rollback
  public void noDeberiaEncontrarUnSalonInexistenteCuandoBuscoPorId() {
    Salon obtenido = this.cuandoBuscoUnSalonPorId(9999L);

    assertThat(obtenido, is(nullValue()));
  }

  private Salon dadoQueExisteUnSalon(String nombre, Double precio) {
    Salon salon = new Salon(nombre, precio);
    this.sessionFactory.getCurrentSession().persist(salon);
    return salon;
  }

  private List<Salon> cuandoObtengoTodosLosSalones() {
    return repositorioSalon.obtenerTodos();
  }

  private Salon cuandoBuscoUnSalonPorId(Long id) {
    return repositorioSalon.buscarPorId(id);
  }

  private void entoncesLosSalonesEstanOrdenados(List<Salon> obtenidos) {
    assertThat(obtenidos, hasSize(3));
    assertThat(obtenidos.get(0).getNombre(), is(equalTo("Salón Gran Bahía")));
    assertThat(obtenidos.get(1).getNombre(), is(equalTo("Salón Jardín del Sol")));
    assertThat(obtenidos.get(2).getNombre(), is(equalTo("Salón Los Álamos")));
  }
}
