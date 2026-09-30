package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.RepositorioServicioAdicional;
import com.tallerwebi.dominio.ServicioAdicional;
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
public class RepositorioServicioAdicionalTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioServicioAdicional repositorioServicioAdicional;

  @BeforeEach
  public void init() {
    repositorioServicioAdicional = new RepositorioServicioAdicionalImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerLosServiciosAdicionalesDeFormaDesc() {
    // preparacion
    this.dadoQueExisteUnServicioAdicional("Fotografia", 30000.0, false);
    this.dadoQueExisteUnServicioAdicional("DJ", 40000.0, false);
    this.dadoQueExisteUnServicioAdicional("Cotillon", 500.0, true);

    // ejecucion
    List<ServicioAdicional> obtenidos = this.cuandoObtengoTodosLosServiciosAdicionales();

    // verificacion
    this.entoncesLosServiciosAdicionalesEstanOrdenados(obtenidos);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerUnaListaVaciaSiNoHayServiciosAdicionales() {
    List<ServicioAdicional> obtenidos = this.cuandoObtengoTodosLosServiciosAdicionales();

    assertThat(obtenidos, is(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaEncontrarUnServicioAdicionalExistenteCuandoBuscoPorId() {
    // preparacion
    ServicioAdicional servicioAdicional =
      this.dadoQueExisteUnServicioAdicional("Cotillon", 500.0, true);

    // ejecucion
    ServicioAdicional obtenido =
      this.cuandoBuscoUnServicioAdicionalPorId(servicioAdicional.getId());

    // verificacion
    assertThat(obtenido.getNombre(), is(equalTo("Cotillon")));
    assertThat(obtenido.getPrecio(), is(equalTo(500.0)));
    assertThat(obtenido.getPrecioPorInvitado(), is(Boolean.TRUE));
  }

  @Test
  @Transactional
  @Rollback
  public void noDeberiaEncontrarUnServicioAdicionalInexistenteCuandoBuscoPorId() {
    ServicioAdicional obtenido = this.cuandoBuscoUnServicioAdicionalPorId(999L);

    assertThat(obtenido, is(nullValue()));
  }

  private ServicioAdicional dadoQueExisteUnServicioAdicional(
    String nombre,
    Double precio,
    Boolean precioPorInvitado
  ) {
    ServicioAdicional servicio = new ServicioAdicional(nombre, precio, precioPorInvitado);

    this.sessionFactory.getCurrentSession().persist(servicio);
    return servicio;
  }

  private List<ServicioAdicional> cuandoObtengoTodosLosServiciosAdicionales() {
    return repositorioServicioAdicional.obtenerTodos();
  }

  private ServicioAdicional cuandoBuscoUnServicioAdicionalPorId(Long id) {
    return repositorioServicioAdicional.buscarPorId(id);
  }

  private void entoncesLosServiciosAdicionalesEstanOrdenados(List<ServicioAdicional> obtenidos) {
    assertThat(obtenidos, hasSize(3));
    assertThat(obtenidos.get(0).getNombre(), is(equalTo("DJ")));
    assertThat(obtenidos.get(1).getNombre(), is(equalTo("Fotografia")));
    assertThat(obtenidos.get(2).getNombre(), is(equalTo("Cotillon")));
  }
}
