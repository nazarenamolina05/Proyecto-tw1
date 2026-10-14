package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.tallerwebi.dominio.EstadoReserva;
import com.tallerwebi.dominio.Paquete;
import com.tallerwebi.dominio.RepositorioReserva;
import com.tallerwebi.dominio.Reserva;
import com.tallerwebi.dominio.TipoPaquete;
import com.tallerwebi.dominio.Usuario;
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

  private Usuario dadoQueExisteUnUsuario(String email) {
    Usuario usuario = new Usuario();
    usuario.setEmail(email);
    usuario.setPassword("123");
    usuario.setRol("USER");
    this.sessionFactory.getCurrentSession().persist(usuario);
    return usuario;
  }

  private Paquete dadoQueExisteUnPaquete(String nombre) {
    Paquete paquete = new Paquete(
            null,
            nombre,
            TipoPaquete.CUMPLEANIOS,
            null,
            "Catering básico",
            List.of("DJ"),
            100000.0
    );
    this.sessionFactory.getCurrentSession().persist(paquete);
    return paquete;
  }

  private Reserva dadoQueTengoUnaReserva(Usuario usuario, Paquete paquete) {
    Reserva reserva = new Reserva();
    reserva.setUsuario(usuario);
    reserva.setPaquete(paquete);
    reserva.setFecha(LocalDate.now().plusDays(30));
    reserva.setPresupuesto(paquete.getPrecioEstimado());
    reserva.setEstado(EstadoReserva.CONFIRMADA);
    return reserva;
  }

  private void dadoQueExisteUnaReserva(Usuario usuario, Paquete paquete) {
    this.sessionFactory.getCurrentSession().persist(this.dadoQueTengoUnaReserva(usuario, paquete));
  }

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

  private void entoncesLaReservaQuedoGuardada(Usuario usuario, Reserva esperada) {
    Reserva obtenida = this.sessionFactory.getCurrentSession()
            .createQuery("from Reserva where usuario.id = :idUsuario", Reserva.class)
            .setParameter("idUsuario", usuario.getId())
            .getSingleResult();
    assertThat(obtenida.getPaquete().getNombre(), is(equalTo(esperada.getPaquete().getNombre())));
    assertThat(obtenida.getEstado(), is(equalTo(esperada.getEstado())));
  }
}