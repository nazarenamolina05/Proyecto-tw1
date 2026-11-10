package com.tallerwebi.infraestructura;

import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.dominio.*;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.Session;
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
public class RepositorioEventoTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioEvento repositorioEvento;

  @BeforeEach
  public void init() {
    repositorioEvento = new RepositorioEventoImpl(sessionFactory);
  }

  // ---------- buscarPorId ----------

  @Test
  @Transactional
  @Rollback
  public void deberiaEncontrarUnEventoExistentePorSuId() {
    // Preparación
    Evento evento = guardarEvento(TipoPaquete.CUMPLEANIOS, 50);

    // Ejecución
    Evento obtenido = repositorioEvento.buscarPorId(evento.getId());

    // Validación
    assertNotNull(obtenido);
    assertEquals(evento.getId(), obtenido.getId());
    assertEquals(TipoPaquete.CUMPLEANIOS, obtenido.getTipoPaquete());
    assertEquals(50, obtenido.getCantidadInvitados());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverNullSiElEventoNoExiste() {
    // Ejecución
    Evento obtenido = repositorioEvento.buscarPorId(9999L);

    // Validación
    assertNull(obtenido);
  }

  // ---------- actualizar ----------

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarLosCambiosDeUnEventoQueVieneDeLaSesionHttp() {
    // Preparación
    Evento guardado = guardarEvento(TipoPaquete.CUMPLEANIOS, 50);
    // Simula el evento que vive en la sesión HTTP: otro objeto con el mismo id, que
    // Hibernate no está siguiendo (por eso hace falta actualizar)
    Evento enSesionHttp = new Evento(TipoPaquete.CASAMIENTO, guardado.getFecha(), 120);
    enSesionHttp.setId(guardado.getId());

    // Ejecución
    repositorioEvento.actualizar(enSesionHttp);

    // Validación
    Evento obtenido = repositorioEvento.buscarPorId(guardado.getId());
    assertEquals(TipoPaquete.CASAMIENTO, obtenido.getTipoPaquete());
    assertEquals(120, obtenido.getCantidadInvitados());
  }

  // ---------- buscarServicio ----------

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerLosServiciosDeUnEventoEnElOrdenEnQueSeAgregaron() {
    // Preparación
    Evento evento = guardarEvento(TipoPaquete.CASAMIENTO, 100);
    ServicioAdicional fotografia = guardarServicio("Fotografia", 30000.0);
    ServicioAdicional dj = guardarServicio("DJ", 40000.0);
    session().persist(new EventoServicio(evento, fotografia));
    session().persist(new EventoServicio(evento, dj));

    // Ejecución
    List<ServicioAdicional> servicios = repositorioEvento.buscarServicio(evento.getId());

    // Validación
    assertEquals(2, servicios.size());
    assertEquals("Fotografia", servicios.get(0).getNombre());
    assertEquals("DJ", servicios.get(1).getNombre());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerSoloLosServiciosDelEventoBuscado() {
    // Preparación
    Evento evento = guardarEvento(TipoPaquete.CASAMIENTO, 100);
    Evento otroEvento = guardarEvento(TipoPaquete.CUMPLEANIOS, 30);
    ServicioAdicional fotografia = guardarServicio("Fotografia", 30000.0);
    ServicioAdicional dj = guardarServicio("DJ", 40000.0);
    session().persist(new EventoServicio(evento, fotografia));
    session().persist(new EventoServicio(otroEvento, dj));

    // Ejecución
    List<ServicioAdicional> servicios = repositorioEvento.buscarServicio(evento.getId());

    // Validación
    assertEquals(1, servicios.size());
    assertEquals("Fotografia", servicios.get(0).getNombre());
  }

  @Test
  @Transactional
  @Rollback
  public void unEventoSinServiciosDeberiaDevolverUnaListaVacia() {
    // Preparación
    Evento evento = guardarEvento(TipoPaquete.CUMPLEANIOS, 50);

    // Ejecución
    List<ServicioAdicional> servicios = repositorioEvento.buscarServicio(evento.getId());

    // Validación
    assertTrue(servicios.isEmpty());
  }

  // ---------- tieneServicio ----------

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverTrueSiElEventoTieneElServicio() {
    // Preparación
    Evento evento = guardarEvento(TipoPaquete.CUMPLEANIOS, 50);
    ServicioAdicional dj = guardarServicio("DJ", 40000.0);
    session().persist(new EventoServicio(evento, dj));

    // Ejecución
    boolean tiene = repositorioEvento.tieneServicio(evento.getId(), dj.getId());

    // Validación
    assertTrue(tiene);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverFalseSiElEventoNoTieneElServicio() {
    // Preparación
    Evento evento = guardarEvento(TipoPaquete.CUMPLEANIOS, 50);
    ServicioAdicional dj = guardarServicio("DJ", 40000.0);
    ServicioAdicional fotografia = guardarServicio("Fotografia", 30000.0);
    session().persist(new EventoServicio(evento, dj));

    // Ejecución
    boolean tiene = repositorioEvento.tieneServicio(evento.getId(), fotografia.getId());

    // Validación
    assertFalse(tiene);
  }

  // ---------- agregarServicio ----------

  @Test
  @Transactional
  @Rollback
  public void deberiaAgregarUnServicioAUnEventoEnLaClaseIntermedia() {
    // Preparación
    Evento evento = guardarEvento(TipoPaquete.CASAMIENTO, 100);
    ServicioAdicional video = guardarServicio("Video", 45000.0);

    // Ejecución
    repositorioEvento.agregarServicio(evento, video);

    // Validación
    List<ServicioAdicional> servicios = repositorioEvento.buscarServicio(evento.getId());
    assertEquals(1, servicios.size());
    assertEquals("Video", servicios.get(0).getNombre());
  }

  // ---------- quitarServicio ----------

  @Test
  @Transactional
  @Rollback
  public void deberiaQuitarSoloElServicioIndicadoDelEvento() {
    // Preparación
    Evento evento = guardarEvento(TipoPaquete.CASAMIENTO, 100);
    ServicioAdicional dj = guardarServicio("DJ", 40000.0);
    ServicioAdicional fotografia = guardarServicio("Fotografia", 30000.0);
    session().persist(new EventoServicio(evento, dj));
    session().persist(new EventoServicio(evento, fotografia));

    // Ejecución
    repositorioEvento.quitarServicio(evento.getId(), dj.getId());

    // Validación
    List<ServicioAdicional> servicios = repositorioEvento.buscarServicio(evento.getId());
    assertEquals(1, servicios.size());
    assertEquals("Fotografia", servicios.get(0).getNombre());
  }

  @Test
  @Transactional
  @Rollback
  public void quitarUnServicioQueElEventoNoTieneNoDeberiaModificarSusServicios() {
    // Preparación
    Evento evento = guardarEvento(TipoPaquete.CUMPLEANIOS, 50);
    ServicioAdicional dj = guardarServicio("DJ", 40000.0);
    ServicioAdicional fotografia = guardarServicio("Fotografia", 30000.0);
    session().persist(new EventoServicio(evento, dj));

    // Ejecución
    repositorioEvento.quitarServicio(evento.getId(), fotografia.getId());

    // Validación
    List<ServicioAdicional> servicios = repositorioEvento.buscarServicio(evento.getId());
    assertEquals(1, servicios.size());
    assertEquals("DJ", servicios.get(0).getNombre());
  }

  // ---------- Métodos auxiliares ----------

  private Session session() {
    return sessionFactory.getCurrentSession();
  }

  private Evento guardarEvento(TipoPaquete tipo, Integer cantidadInvitados) {
    Evento evento = new Evento(tipo, LocalDate.now().plusMonths(1), cantidadInvitados);
    session().persist(evento);
    return evento;
  }

  private ServicioAdicional guardarServicio(String nombre, Double precio) {
    ServicioAdicional servicio = new ServicioAdicional(nombre, precio, false);
    session().persist(servicio);
    return servicio;
  }
}
