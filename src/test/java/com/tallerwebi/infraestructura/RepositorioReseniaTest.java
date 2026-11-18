package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.*;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
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
public class RepositorioReseniaTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioResenia repositorioResenia;

  @BeforeEach
  public void init() {
    repositorioResenia = new RepositorioReseniaImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnaReseniaYEncontrarlaPorSuReserva() {
    Reserva reserva = this.dadoQueExisteUnaReservaDelPaquete(null);
    repositorioResenia.guardar(new Resenia(reserva, 5, "Excelente"));

    Resenia obtenida = this.cuandoBuscoLaReseniaDe(reserva);

    assertThat(obtenida, is(notNullValue()));
    assertThat(obtenida.getPuntaje(), is(equalTo(5)));
    assertThat(obtenida.getComentario(), is(equalTo("Excelente")));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverNullSiLaReservaNoTieneResenia() {
    Reserva reserva = this.dadoQueExisteUnaReservaDelPaquete(null);

    assertThat(this.cuandoBuscoLaReseniaDe(reserva), is(nullValue()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerSoloLasReseniasDeEventosDelPaqueteIndicado() {
    Paquete paquete = this.dadoQueExisteUnPaquete("Básico");
    Paquete otroPaquete = this.dadoQueExisteUnPaquete("Premium");
    this.dadoQueExisteUnaReseniaDe(this.dadoQueExisteUnaReservaDelPaquete(paquete), 5);
    this.dadoQueExisteUnaReseniaDe(this.dadoQueExisteUnaReservaDelPaquete(paquete), 3);
    this.dadoQueExisteUnaReseniaDe(this.dadoQueExisteUnaReservaDelPaquete(otroPaquete), 1);

    List<Resenia> obtenidas = this.cuandoBuscoLasReseniasDe(paquete);

    assertThat(obtenidas, hasSize(2));
    assertThat(obtenidas.stream().map(Resenia::getPuntaje).toList(), containsInAnyOrder(5, 3));
  }

  @Test
  @Transactional
  @Rollback
  public void lasReseniasDeEventosPersonalizadosNoDeberianAparecerEnNingunPaquete() {
    Paquete paquete = this.dadoQueExisteUnPaquete("Básico");
    this.dadoQueExisteUnaReseniaDe(this.dadoQueExisteUnaReservaDelPaquete(null), 4);

    assertThat(this.cuandoBuscoLasReseniasDe(paquete), is(empty()));
  }

  // ---------- dado ----------

  private Paquete dadoQueExisteUnPaquete(String nombre) {
    Paquete paquete = new Paquete(null, nombre, TipoPaquete.CUMPLEANIOS, null, null, 0.0);
    this.sessionFactory.getCurrentSession().persist(paquete);
    return paquete;
  }

  private Reserva dadoQueExisteUnaReservaDelPaquete(Paquete paquete) {
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().minusDays(5), 30);
    evento.setPaqueteOrigen(paquete);
    this.sessionFactory.getCurrentSession().persist(evento);

    Reserva reserva = new Reserva();
    reserva.setEvento(evento);
    reserva.setEstado(EstadoReserva.CONFIRMADA);
    reserva.setPresupuesto(100000.0);
    this.sessionFactory.getCurrentSession().persist(reserva);
    return reserva;
  }

  private void dadoQueExisteUnaReseniaDe(Reserva reserva, Integer puntaje) {
    this.sessionFactory.getCurrentSession().persist(new Resenia(reserva, puntaje, null));
  }

  // ---------- cuando ----------

  private Resenia cuandoBuscoLaReseniaDe(Reserva reserva) {
    this.sessionFactory.getCurrentSession().flush();
    this.sessionFactory.getCurrentSession().clear();
    return repositorioResenia.buscarPorReserva(reserva.getId());
  }

  private List<Resenia> cuandoBuscoLasReseniasDe(Paquete paquete) {
    this.sessionFactory.getCurrentSession().flush();
    this.sessionFactory.getCurrentSession().clear();
    return repositorioResenia.buscarPorPaquete(paquete.getId());
  }
}
