package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.dominio.*;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
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
public class RepositorioPaqueteTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioPaquete repositorioPaquete;

  @BeforeEach
  public void init() {
    repositorioPaquete = new RepositorioPaqueteImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerTodosLosPaquetesGuardados() {
    // Preparación
    Salon salon = new Salon("Salón Los Álamos", 50000.0);
    session().persist(salon);

    guardarPaquete("Cumpleaños familiar", TipoPaquete.CUMPLEANIOS, salon, 100000.0, false);
    guardarPaquete("Casamiento clásico", TipoPaquete.CASAMIENTO, salon, 300000.0, false);

    session().flush();
    session().clear();

    // Ejecución
    List<Paquete> paquetesObtenidos = repositorioPaquete.obtenerTodos();

    // Validación
    assertEquals(2, paquetesObtenidos.size());
    assertEquals("Cumpleaños familiar", paquetesObtenidos.get(0).getNombre());
    assertEquals("Casamiento clásico", paquetesObtenidos.get(1).getNombre());
    assertEquals(TipoPaquete.CUMPLEANIOS, paquetesObtenidos.get(0).getTipo());
    assertEquals("Salón Los Álamos", paquetesObtenidos.get(0).getSalon().getNombre());
    assertEquals("Catering Basico", paquetesObtenidos.get(0).getCatering().getNombre());
    assertEquals(1, paquetesObtenidos.get(0).getServiciosAdicionales().size());
    assertEquals("DJ", paquetesObtenidos.get(0).getServiciosAdicionales().get(0).getNombre());
    assertEquals(100000.0, paquetesObtenidos.get(0).getPrecioEstimado());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerSoloLosPaquetesPredeterminados() {
    // Preparación
    Salon salon = new Salon("Salón de prueba", 50000.0);
    session().persist(salon);

    guardarPaquete("Básico", TipoPaquete.CUMPLEANIOS, salon, 80000.0, true);
    guardarPaquete("Premium", TipoPaquete.CASAMIENTO, salon, 300000.0, false);

    session().flush();
    session().clear();

    // Ejecución
    List<Paquete> paquetesObtenidos = repositorioPaquete.obtenerPredeterminados();

    // Validación
    assertEquals(1, paquetesObtenidos.size());
    assertEquals("Básico", paquetesObtenidos.get(0).getNombre());
    assertTrue(paquetesObtenidos.get(0).isPredeterminado());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverListaVaciaSiNoExistenPaquetes() {
    // Ejecución
    List<Paquete> paquetesObtenidos = repositorioPaquete.obtenerTodos();

    // Validación
    assertTrue(paquetesObtenidos.isEmpty());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverListaVaciaSiNoHayPaquetesPredeterminados() {
    // Preparación
    Salon salon = new Salon("Salón de prueba", 50000.0);
    session().persist(salon);

    guardarPaquete("Cumpleaños familiar", TipoPaquete.CUMPLEANIOS, salon, 100000.0, false);

    // Ejecución
    List<Paquete> paquetesObtenidos = repositorioPaquete.obtenerPredeterminados();

    // Validación
    assertTrue(paquetesObtenidos.isEmpty());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerUnPaquetePredeterminadoPorSuId() {
    // Preparación
    Salon salon = new Salon("Salón de prueba", 50000.0);
    session().persist(salon);

    Paquete basico = guardarPaquete("Básico", TipoPaquete.CUMPLEANIOS, salon, 80000.0, true);
    session().flush();
    session().clear();

    // Ejecución
    Paquete obtenido = repositorioPaquete.obtenerPredeterminadoPorId(basico.getId());

    // Validación
    assertEquals("Básico", obtenido.getNombre());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverNullSiElPaqueteExistePeroNoEsPredeterminado() {
    // Preparación
    Salon salon = new Salon("Salón de prueba", 50000.0);
    session().persist(salon);

    Paquete premium = guardarPaquete("Premium", TipoPaquete.CASAMIENTO, salon, 300000.0, false);
    session().flush();
    session().clear();

    // Ejecución
    Paquete obtenido = repositorioPaquete.obtenerPredeterminadoPorId(premium.getId());

    // Validación
    assertNull(obtenido);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverNullSiElIdNoExiste() {
    // Ejecución
    Paquete obtenido = repositorioPaquete.obtenerPredeterminadoPorId(9999L);

    // Validación
    assertNull(obtenido);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerLosPaquetesPredeterminadosOrdenadosPorId() {
    // Preparación
    Salon salon = new Salon("Salón de prueba", 50000.0);
    session().persist(salon);

    guardarPaquete("Básico", TipoPaquete.CUMPLEANIOS, salon, 80000.0, true);
    guardarPaquete("Estándar", TipoPaquete.FIESTA_DE_15, salon, 150000.0, true);
    guardarPaquete("Premium", TipoPaquete.CASAMIENTO, salon, 300000.0, true);

    session().flush();
    session().clear();

    // Ejecución
    List<Paquete> paquetesObtenidos = repositorioPaquete.obtenerPredeterminados();

    // Validación
    assertEquals(3, paquetesObtenidos.size());
    assertEquals("Básico", paquetesObtenidos.get(0).getNombre());
    assertEquals("Estándar", paquetesObtenidos.get(1).getNombre());
    assertEquals("Premium", paquetesObtenidos.get(2).getNombre());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerLosPaquetesDentroDelPresupuestoMaximo() {
    // Preparación
    Salon salon = new Salon("Salón de prueba", 50000.0);
    session().persist(salon);

    guardarPaquete("Básico", TipoPaquete.CUMPLEANIOS, salon, 80000.0, false);
    guardarPaquete("Estándar", TipoPaquete.FIESTA_DE_15, salon, 250000.0, false);
    guardarPaquete("Premium", TipoPaquete.CASAMIENTO, salon, 300000.0, false);

    session().flush();
    session().clear();

    // Ejecución
    List<Paquete> obtenidos = repositorioPaquete.buscarPorFiltros(null, 250000.0, null);

    // Validación
    assertEquals(2, obtenidos.size());
    assertEquals("Básico", obtenidos.get(0).getNombre());
    assertEquals("Estándar", obtenidos.get(1).getNombre());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaFiltrarPorTipoYPresupuesto() {
    // Preparación
    Salon salon = new Salon("Salón de prueba", 50000.0);
    session().persist(salon);

    guardarPaquete("Cumpleaños económico", TipoPaquete.CUMPLEANIOS, salon, 150000.0, false);
    guardarPaquete("Cumpleaños premium", TipoPaquete.CUMPLEANIOS, salon, 350000.0, false);
    guardarPaquete("Casamiento económico", TipoPaquete.CASAMIENTO, salon, 200000.0, false);

    session().flush();
    session().clear();

    // Ejecución
    List<Paquete> obtenidos = repositorioPaquete.buscarPorFiltros(
      TipoPaquete.CUMPLEANIOS,
      250000.0,
      null
    );

    // Validación
    assertEquals(1, obtenidos.size());
    assertEquals("Cumpleaños económico", obtenidos.get(0).getNombre());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverListaVaciaSiSuperanElPresupuesto() {
    // Preparación
    Salon salon = new Salon("Salón de prueba", 50000.0);
    session().persist(salon);

    guardarPaquete("Paquete premium", TipoPaquete.CASAMIENTO, salon, 400000.0, false);

    // Ejecución
    List<Paquete> obtenidos = repositorioPaquete.buscarPorFiltros(null, 100000.0, null);

    // Validación
    assertTrue(obtenidos.isEmpty());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerPaquetesConCapacidadSuficiente() {
    // Preparación
    Salon salonChico = guardarSalon("Salón chico", 50000.0, 50);
    Salon salonMediano = guardarSalon("Salón mediano", 90000.0, 80);
    Salon salonGrande = guardarSalon("Salón grande", 150000.0, 120);

    guardarPaquete("Cumpleaños chico", TipoPaquete.CUMPLEANIOS, salonChico, 80000.0, false);
    guardarPaquete("Cumpleaños mediano", TipoPaquete.CUMPLEANIOS, salonMediano, 150000.0, false);
    guardarPaquete("Cumpleaños grande", TipoPaquete.CUMPLEANIOS, salonGrande, 250000.0, false);

    session().flush();
    session().clear();

    // Ejecución
    List<Paquete> obtenidos = repositorioPaquete.buscarPorFiltros(null, null, 80);

    // Validación
    assertThat(obtenidos.size(), equalTo(2));
    assertThat(obtenidos.get(0).getNombre(), equalTo("Cumpleaños mediano"));
    assertThat(obtenidos.get(1).getNombre(), equalTo("Cumpleaños grande"));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerPaquetesQueCumplanLosTresFiltros() {
    // Preparación
    Salon salonMediano = guardarSalon("Salón mediano", 90000.0, 80);
    Salon salonGrande = guardarSalon("Salón grande", 150000.0, 120);
    Salon salonChico = guardarSalon("Salón chico", 50000.0, 50);

    // Cumple los tres filtros
    guardarPaquete("Cumpleaños válido", TipoPaquete.CUMPLEANIOS, salonMediano, 250000.0, false);
    // No cumple el tipo de evento
    guardarPaquete("Casamiento", TipoPaquete.CASAMIENTO, salonGrande, 200000.0, false);
    // Supera el presupuesto
    guardarPaquete("Cumpleaños caro", TipoPaquete.CUMPLEANIOS, salonGrande, 300000.0, false);
    // No tiene capacidad suficiente
    guardarPaquete("Cumpleaños chico", TipoPaquete.CUMPLEANIOS, salonChico, 150000.0, false);

    session().flush();
    session().clear();

    // Ejecución
    List<Paquete> obtenidos = repositorioPaquete.buscarPorFiltros(
      TipoPaquete.CUMPLEANIOS,
      250000.0,
      80
    );

    // Validación
    assertThat(obtenidos.size(), equalTo(1));
    assertThat(obtenidos.get(0).getNombre(), equalTo("Cumpleaños válido"));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnPaquetePublicadoConSuPublicador() {
    //Preparación
    Salon salon = guardarSalon("Salón Los Álamos", 50000.0, 100);

    Catering catering = new Catering("Catering Basico", 1500.0);
    session().persist(catering);

    Usuario publicador = new Usuario();
    publicador.setEmail("publicador@test.com");
    session().persist(publicador);

    Paquete paquete = new Paquete(
      null,
      "Mi cumple de 30",
      TipoPaquete.CUMPLEANIOS,
      salon,
      catering,
      List.of(),
      400000.0
    );
    paquete.setPublicador(publicador);

    // Ejecución
    repositorioPaquete.guardar(paquete);
    session().flush();
    session().clear();

    // Validación
    Paquete obtenido = session().get(Paquete.class, paquete.getId());

    assertNotNull(obtenido);
    assertEquals("Mi cumple de 30", obtenido.getNombre());
    assertEquals(publicador.getId(), obtenido.getPublicador().getId());
    assertTrue(obtenido.isPublicado());
  }

  // ---------- Métodos auxiliares ----------

  private Session session() {
    return sessionFactory.getCurrentSession();
  }

  private Salon guardarSalon(String nombre, Double precio, Integer capacidad) {
    Salon salon = new Salon(nombre, precio);
    salon.setCapacidad(capacidad);
    session().persist(salon);
    return salon;
  }

  // Guarda primero el catering y el servicio (que el paquete referencia) y después el paquete
  private Paquete guardarPaquete(
    String nombre,
    TipoPaquete tipo,
    Salon salon,
    Double precio,
    boolean predeterminado
  ) {
    Catering catering = new Catering("Catering Basico", 1500.0);
    ServicioAdicional dj = new ServicioAdicional("DJ", 40000.0, false);
    session().persist(catering);
    session().persist(dj);

    Paquete paquete = new Paquete(null, nombre, tipo, salon, catering, List.of(dj), precio);
    paquete.setPredeterminado(predeterminado);
    session().persist(paquete);
    return paquete;
  }
}
