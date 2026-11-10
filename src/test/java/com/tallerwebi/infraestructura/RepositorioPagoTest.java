package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.*;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
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
public class RepositorioPagoTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioPago repositorioPago;

  @BeforeEach
  public void init() {
    repositorioPago = new RepositorioPagoImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnPagoYPoderBuscarloPorId() {
    Reserva reserva = this.dadoQueExisteUnaReserva();
    Pago pago = this.dadoQueTengoUnPagoPendiente(reserva, TipoPago.SENIA, 30000.0);

    repositorioPago.guardar(pago);
    this.limpiarLaSesion();

    Pago obtenido = repositorioPago.buscarPorId(pago.getId());
    assertThat(obtenido.getEstado(), is(equalTo(EstadoPago.PENDIENTE)));
    assertThat(obtenido.getTipo(), is(equalTo(TipoPago.SENIA)));
    assertThat(obtenido.getMonto(), is(closeTo(30000.0, 0.01)));
    assertThat(obtenido.getReserva().getId(), is(equalTo(reserva.getId())));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverNullSiElPagoNoExiste() {
    Pago obtenido = repositorioPago.buscarPorId(999999L);

    assertThat(obtenido, is(nullValue()));
  }

  private Reserva dadoQueExisteUnaReserva() {
    Usuario usuario = new Usuario();
    usuario.setEmail("usuario@test.com");
    usuario.setPassword("123");
    usuario.setRol("USER");
    this.sessionFactory.getCurrentSession().persist(usuario);

    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().plusDays(30), 50);

    Reserva reserva = new Reserva();
    reserva.setUsuario(usuario);
    reserva.setEvento(evento);
    reserva.setPresupuesto(100000.0);
    reserva.setEstado(EstadoReserva.PENDIENTE_PAGO);
    this.sessionFactory.getCurrentSession().persist(reserva);
    return reserva;
  }

  private Pago dadoQueTengoUnPagoPendiente(Reserva reserva, TipoPago tipo, Double monto) {
    Pago pago = new Pago();
    pago.setReserva(reserva);
    pago.setTipo(tipo);
    pago.setMonto(monto);
    pago.setEstado(EstadoPago.PENDIENTE);
    pago.setFecha(java.time.LocalDateTime.now());
    return pago;
  }

  private void limpiarLaSesion() {
    this.sessionFactory.getCurrentSession().flush();
    this.sessionFactory.getCurrentSession().clear();
  }
}
