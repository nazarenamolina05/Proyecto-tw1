package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
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
        this.dadoQueExisteUnaReserva(usuario, "Cumpleanios familiar");
        this.dadoQueExisteUnaReserva(usuario, "Casamiento clásico");
        this.dadoQueExisteUnaReserva(otroUsuario, "Fiesta de quince");

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

    private Usuario dadoQueExisteUnUsuario(String email) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword("123");
        usuario.setRol("USER");
        this.sessionFactory.getCurrentSession().persist(usuario);
        return usuario;
    }

    private void dadoQueExisteUnaReserva(Usuario usuario, String nombrePaquete) {
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setNombrePaquete(nombrePaquete);
        reserva.setTipo(TipoPaquete.CUMPLEANIOS);
        reserva.setFecha(LocalDate.now().plusDays(30));
        reserva.setPresupuesto(100000.0);
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        this.sessionFactory.getCurrentSession().persist(reserva);
    }

    private List<Reserva> cuandoBuscoLasReservasDe(Usuario usuario) {
        return repositorioReserva.buscarPorUsuario(usuario);
    }

    private void entoncesObtengoLaCantidadDeReservas(List<Reserva> reservas, int esperada) {
        assertThat(reservas.size(), is(equalTo(esperada)));
    }

    private void entoncesTodasLasReservasSonDelUsuario(List<Reserva> reservas, Usuario usuario) {
        for (Reserva reserva : reservas) {
            assertThat(reserva.getUsuario().getEmail(), is(equalTo(usuario.getEmail())));
        }
    }
}