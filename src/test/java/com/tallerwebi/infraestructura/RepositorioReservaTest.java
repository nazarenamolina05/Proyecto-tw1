package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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
    Paquete paquete = this.dadoQueExisteUnPaquete("Cumpleaños familiar");
    this.dadoQueExisteUnaReserva(usuario, paquete);
    this.dadoQueExisteUnaReserva(usuario, paquete);
    this.dadoQueExisteUnaReserva(otroUsuario, paquete);

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

  //nuevo
  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnaReserva() {
    Usuario usuario = this.dadoQueExisteUnUsuario("usuario@test.com");
    Paquete paquete = this.dadoQueExisteUnPaquete("Cumpleaños familiar");
    Reserva reserva = this.dadoQueTengoUnaReserva(usuario, paquete);

    this.cuandoGuardoLaReserva(reserva);

    this.entoncesLaReservaQuedoGuardada(usuario, reserva);
  }

  //-------------

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
    Usuario usuario = this.dadoQueExisteUnUsuario("usuario@test.com");
    Paquete paquete = this.dadoQueExisteUnPaquete("Cumpleaños familiar");
    LocalDate desde = LocalDate.now().plusDays(10);
    LocalDate hasta = LocalDate.now().plusDays(20);
    this.dadoQueExisteUnaReservaEnLaFecha(usuario, paquete, desde.minusDays(1));
    this.dadoQueExisteUnaReservaEnLaFecha(usuario, paquete, desde);
    this.dadoQueExisteUnaReservaEnLaFecha(usuario, paquete, hasta);
    this.dadoQueExisteUnaReservaEnLaFecha(usuario, paquete, hasta.plusDays(1));

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

  private Usuario dadoQueExisteUnUsuario(String email) {
    Usuario usuario = new Usuario();
    usuario.setEmail(email);
    usuario.setPassword("123");
    usuario.setRol("USER");
    this.sessionFactory.getCurrentSession().persist(usuario);
    return usuario;
  }

  //nuevo
  private Paquete dadoQueExisteUnPaquete(String nombre) {
    return this.guardarPaquete(nombre, null);
  }

  //nuevo
  private Reserva dadoQueTengoUnaReserva(Usuario usuario, Paquete paquete) {
    Reserva reserva = new Reserva();
    reserva.setUsuario(usuario);
    reserva.setPaquete(paquete);
    reserva.setFecha(LocalDate.now().plusDays(30));
    reserva.setPresupuesto(paquete.getPrecio());
    reserva.setEstado(EstadoReserva.CONFIRMADA);
    return reserva;
  }

  private void dadoQueExisteUnaReserva(Usuario usuario, Paquete paquete) {
    this.sessionFactory.getCurrentSession().persist(this.dadoQueTengoUnaReserva(usuario, paquete));
  }

  //  private void dadoQueExisteUnaReserva(Usuario usuario, String nombrePaquete) {
  //    Reserva reserva = new Reserva();
  //    reserva.setUsuario(usuario);
  //    reserva.setNombrePaquete(nombrePaquete);
  //    reserva.setTipo(TipoPaquete.CUMPLEANIOS);
  //    reserva.setFecha(LocalDate.now().plusDays(30));
  //    reserva.setPresupuesto(100000.0);
  //    reserva.setEstado(EstadoReserva.CONFIRMADA);
  //    this.sessionFactory.getCurrentSession().persist(reserva);
  //  }

  private List<Reserva> cuandoBuscoLasReservasDe(Usuario usuario) {
    return repositorioReserva.buscarPorUsuario(usuario);
  }

  private void cuandoGuardoLaReserva(Reserva reserva) {
    repositorioReserva.guardar(reserva);
  }

  private void entoncesObtengoLaCantidadDeReservas(List<Reserva> reservas, int esperada) {
    assertThat(reservas.size(), is(equalTo(esperada)));
  }

  private void entoncesTodasLasReservasSonDelUsuario(List<Reserva> reservas, Usuario usuario) {
    for (Reserva reserva : reservas) {
      assertThat(reserva.getUsuario().getEmail(), is(equalTo(usuario.getEmail())));
    }
  }

  //nuevo
  private void entoncesLaReservaQuedoGuardada(Usuario usuario, Reserva esperada) {
    Reserva obtenida =
      this.sessionFactory.getCurrentSession()
        .createQuery("from Reserva where usuario.id = :idUsuario", Reserva.class)
        .setParameter("idUsuario", usuario.getId())
        .getSingleResult();
    assertThat(obtenida.getPaquete().getNombre(), is(equalTo(esperada.getPaquete().getNombre())));
    assertThat(obtenida.getEstado(), is(equalTo(esperada.getEstado())));
  }

  private Salon dadoQueExisteUnSalon(String nombre) {
    Salon salon = new Salon(nombre, 50000.0);
    this.sessionFactory.getCurrentSession().persist(salon);
    return salon;
  }

  private void dadoQueExisteUnaReservaEnSalon(Salon salon, LocalDate fecha, EstadoReserva estado) {
    Paquete paquete = this.guardarPaquete("Paquete de prueba", salon);

    Reserva reserva = new Reserva();
    reserva.setPaquete(paquete);
    reserva.setFecha(fecha);
    reserva.setEstado(estado);
    this.sessionFactory.getCurrentSession().persist(reserva);
  }

  private List<LocalDate> cuandoBuscoLasFechasOcupadasDe(Salon salon) {
    this.sessionFactory.getCurrentSession().flush();
    this.sessionFactory.getCurrentSession().clear();
    return repositorioReserva.obtenerFechasOcupadasPorSalon(salon.getId());
  }

 private Paquete guardarPaquete(String nombre, Salon salon) {
    Catering catering = new Catering("Catering basico", 1500.0);
    ServicioAdicional dj = new ServicioAdicional("DJ", 40000.0, false);
    this.sessionFactory.getCurrentSession().persist(catering);
    this.sessionFactory.getCurrentSession().persist(dj);

    Paquete paquete = new Paquete(
        null,
        nombre,
        TipoPaquete.CUMPLEANIOS,
        salon,
        catering,
        List.of(dj),
        100000.0
    );

    this.sessionFactory.getCurrentSession().persist(paquete);
    return paquete;
}
  private void dadoQueExisteUnaReservaEnLaFecha(Usuario usuario, Paquete paquete, LocalDate fecha) {
    Reserva reserva = this.dadoQueTengoUnaReserva(usuario, paquete);
    reserva.setFecha(fecha);
    this.sessionFactory.getCurrentSession().persist(reserva);
  }
}
