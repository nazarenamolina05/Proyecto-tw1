package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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
public class RepositorioReservaTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioReserva repositorioReserva;

  @BeforeEach
  public void init() {
    repositorioReserva = new RepositorioReservaImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerSoloLasReservasDelUsuarioIndicado() {
    Usuario usuario = this.dadoQueExisteUnUsuario("usuario@test.com");
    Usuario otroUsuario = this.dadoQueExisteUnUsuario("otro@test.com");
    this.dadoQueExisteUnaReservaDe(usuario);
    this.dadoQueExisteUnaReservaDe(usuario);
    this.dadoQueExisteUnaReservaDe(otroUsuario);

    List<Reserva> obtenidas = this.cuandoBuscoLasReservasDe(usuario);

    this.entoncesObtengoLaCantidadDeReservas(obtenidas, 2);
    this.entoncesTodasLasReservasSonDelUsuario(obtenidas, usuario);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverUnaListaVaciaSiElUsuarioNoTieneReservas() {
    Usuario usuario = this.dadoQueExisteUnUsuario("usuario@test.com");

    List<Reserva> obtenidas = this.cuandoBuscoLasReservasDe(usuario);

    assertThat(obtenidas, is(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnaReservaJuntoConSuEvento() {
    Usuario usuario = this.dadoQueExisteUnUsuario("usuario@test.com");
    Evento evento = this.dadoQueTengoUnEvento(null, LocalDate.now().plusDays(30));
    Reserva reserva = this.dadoQueTengoUnaReserva(usuario, evento, EstadoReserva.SENIADA);

    repositorioReserva.guardar(reserva);

    Reserva obtenida = this.cuandoBuscoLasReservasDe(usuario).get(0);
    assertThat(obtenida.getEvento().getId(), is(notNullValue()));
    assertThat(obtenida.getEstado(), is(equalTo(EstadoReserva.SENIADA)));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerSoloLasFechasOcupadasDelSalonIndicado() {
    Salon salon = this.dadoQueExisteUnSalon("Salón A");
    Salon otroSalon = this.dadoQueExisteUnSalon("Salón B");
    LocalDate fechaDelSalon = LocalDate.now().plusDays(10);
    this.dadoQueExisteUnaReservaEnSalon(salon, fechaDelSalon, EstadoReserva.CONFIRMADA);
    this.dadoQueExisteUnaReservaEnSalon(
        otroSalon,
        LocalDate.now().plusDays(30),
        EstadoReserva.CONFIRMADA
      );

    List<LocalDate> fechas = this.cuandoBuscoLasFechasOcupadasDe(salon);

    assertThat(fechas, contains(fechaDelSalon));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaConsiderarOcupadasLasReservasSenadasYConfirmadas() {
    Salon salon = this.dadoQueExisteUnSalon("Salón A");
    LocalDate fechaSenada = LocalDate.now().plusDays(10);
    LocalDate fechaConfirmada = LocalDate.now().plusDays(20);
    this.dadoQueExisteUnaReservaEnSalon(salon, fechaSenada, EstadoReserva.SENIADA);
    this.dadoQueExisteUnaReservaEnSalon(salon, fechaConfirmada, EstadoReserva.CONFIRMADA);

    List<LocalDate> fechas = this.cuandoBuscoLasFechasOcupadasDe(salon);

    assertThat(fechas, containsInAnyOrder(fechaSenada, fechaConfirmada));
  }

  @Test
  @Transactional
  @Rollback
  public void noDeberiaConsiderarOcupadasLasReservasCanceladasNiFinalizadas() {
    Salon salon = this.dadoQueExisteUnSalon("Salón A");
    this.dadoQueExisteUnaReservaEnSalon(
        salon,
        LocalDate.now().plusDays(10),
        EstadoReserva.CANCELADA
      );
    this.dadoQueExisteUnaReservaEnSalon(
        salon,
        LocalDate.now().minusDays(10),
        EstadoReserva.FINALIZADA
      );

    List<LocalDate> fechas = this.cuandoBuscoLasFechasOcupadasDe(salon);

    assertThat(fechas, is(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerLasReservasEntreDosFechasInclusive() {
    LocalDate desde = LocalDate.now().plusDays(10);
    LocalDate hasta = LocalDate.now().plusDays(20);
    this.dadoQueExisteUnaReservaEnLaFecha(desde.minusDays(1));
    this.dadoQueExisteUnaReservaEnLaFecha(desde);
    this.dadoQueExisteUnaReservaEnLaFecha(hasta);
    this.dadoQueExisteUnaReservaEnLaFecha(hasta.plusDays(1));

    List<Reserva> obtenidas = repositorioReserva.buscarEntreFechas(desde, hasta);

    this.entoncesObtengoLaCantidadDeReservas(obtenidas, 2);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverUnaListaVaciaSiNoHayReservasEnElRango() {
    List<Reserva> obtenidas = repositorioReserva.buscarEntreFechas(
      LocalDate.now().plusDays(10),
      LocalDate.now().plusDays(20)
    );

    assertThat(obtenidas, is(empty()));
  }

  // ---------- dado ----------

  private Usuario dadoQueExisteUnUsuario(String email) {
    Usuario usuario = new Usuario();
    usuario.setEmail(email);
    usuario.setPassword("123");
    usuario.setRol("USER");
    this.sessionFactory.getCurrentSession().persist(usuario);
    return usuario;
  }

  private Salon dadoQueExisteUnSalon(String nombre) {
    Salon salon = new Salon(nombre, 50000.0);
    this.sessionFactory.getCurrentSession().persist(salon);
    return salon;
  }

  private Evento dadoQueTengoUnEvento(Salon salon, LocalDate fecha) {
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, fecha, 50);
    evento.setSalon(salon);
    return evento;
  }

  private Reserva dadoQueTengoUnaReserva(Usuario usuario, Evento evento, EstadoReserva estado) {
    Reserva reserva = new Reserva();
    reserva.setUsuario(usuario);
    reserva.setEvento(evento);
    reserva.setPresupuesto(100000.0);
    reserva.setEstado(estado);
    return reserva;
  }

  private void dadoQueExisteUnaReservaDe(Usuario usuario) {
    Evento evento = this.dadoQueTengoUnEvento(null, LocalDate.now().plusDays(30));
    this.sessionFactory.getCurrentSession()
      .persist(this.dadoQueTengoUnaReserva(usuario, evento, EstadoReserva.CONFIRMADA));
  }

  private void dadoQueExisteUnaReservaEnSalon(Salon salon, LocalDate fecha, EstadoReserva estado) {
    Evento evento = this.dadoQueTengoUnEvento(salon, fecha);
    this.sessionFactory.getCurrentSession()
      .persist(this.dadoQueTengoUnaReserva(null, evento, estado));
  }

  private void dadoQueExisteUnaReservaEnLaFecha(LocalDate fecha) {
    this.dadoQueExisteUnaReservaEnSalon(null, fecha, EstadoReserva.CONFIRMADA);
  }

  // ---------- cuando ----------

  private List<Reserva> cuandoBuscoLasReservasDe(Usuario usuario) {
    return repositorioReserva.buscarPorUsuario(usuario);
  }

  private List<LocalDate> cuandoBuscoLasFechasOcupadasDe(Salon salon) {
    this.sessionFactory.getCurrentSession().flush();
    this.sessionFactory.getCurrentSession().clear();
    return repositorioReserva.obtenerFechasOcupadasPorSalon(salon.getId());
  }

  // ---------- entonces ----------

  private void entoncesObtengoLaCantidadDeReservas(List<Reserva> reservas, int esperada) {
    assertThat(reservas.size(), is(equalTo(esperada)));
  }

  private void entoncesTodasLasReservasSonDelUsuario(List<Reserva> reservas, Usuario usuario) {
    for (Reserva reserva : reservas) {
      assertThat(reserva.getUsuario().getEmail(), is(equalTo(usuario.getEmail())));
    }
  }
}
