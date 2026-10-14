package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.dominio.Paquete;
import com.tallerwebi.dominio.RepositorioPaquete;
import com.tallerwebi.dominio.Salon;
import com.tallerwebi.dominio.TipoPaquete;
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
    sessionFactory.getCurrentSession().persist(salon);

    Paquete cumpleanios = new Paquete(
      null,
      "Cumpleaños familiar",
      TipoPaquete.CUMPLEANIOS,
      salon,
      "Catering Básico",
      List.of("DJ"),
      100000.0
    );

    Paquete casamiento = new Paquete(
      null,
      "Casamiento clásico",
      TipoPaquete.CASAMIENTO,
      salon,
      "Catering Completo",
      List.of("DJ", "Fotografía"),
      300000.0
    );

    sessionFactory.getCurrentSession().persist(cumpleanios);
    sessionFactory.getCurrentSession().persist(casamiento);
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    // Ejecución
    List<Paquete> paquetesObtenidos = repositorioPaquete.obtenerTodos();

    // Validación
    assertEquals(2, paquetesObtenidos.size());
    assertEquals("Cumpleaños familiar", paquetesObtenidos.get(0).getNombre());
    assertEquals("Casamiento clásico", paquetesObtenidos.get(1).getNombre());
    assertEquals(TipoPaquete.CUMPLEANIOS, paquetesObtenidos.get(0).getTipo());
    assertEquals("Salón Los Álamos", paquetesObtenidos.get(0).getSalon().getNombre());
    assertEquals(List.of("DJ"), paquetesObtenidos.get(0).getServiciosAdicionales());
    assertEquals(100000.0, paquetesObtenidos.get(0).getPrecioEstimado());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerSoloLosPaquetesPredeterminados() {
    // Preparación
    Salon salon = new Salon("Salón de prueba", 50000.0);
    sessionFactory.getCurrentSession().persist(salon);

    Paquete basico = new Paquete(
      null,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      salon,
      "Catering Básico",
      List.of("DJ"),
      80000.0
    );
    basico.setPredeterminado(true);

    Paquete premium = new Paquete(
      null,
      "Premium",
      TipoPaquete.CASAMIENTO,
      salon,
      "Catering Completo",
      List.of("DJ", "Fotografía"),
      300000.0
    );
    premium.setPredeterminado(false);

    sessionFactory.getCurrentSession().persist(basico);
    sessionFactory.getCurrentSession().persist(premium);
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

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
    sessionFactory.getCurrentSession().persist(salon);

    Paquete paquete = new Paquete(
      null,
      "Cumpleaños familiar",
      TipoPaquete.CUMPLEANIOS,
      salon,
      "Catering Básico",
      List.of("DJ"),
      100000.0
    );
    paquete.setPredeterminado(false);
    sessionFactory.getCurrentSession().persist(paquete);

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
    sessionFactory.getCurrentSession().persist(salon);

    Paquete basico = new Paquete(
      null,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      salon,
      "Catering Básico",
      List.of("DJ"),
      80000.0
    );
    basico.setPredeterminado(true);
    sessionFactory.getCurrentSession().persist(basico);
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

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
    sessionFactory.getCurrentSession().persist(salon);

    Paquete premium = new Paquete(
      null,
      "Premium",
      TipoPaquete.CASAMIENTO,
      salon,
      "Catering Completo",
      List.of("DJ"),
      300000.0
    );
    premium.setPredeterminado(false);
    sessionFactory.getCurrentSession().persist(premium);
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

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
    sessionFactory.getCurrentSession().persist(salon);

    Paquete primero = new Paquete(
      null,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      salon,
      "Catering Básico",
      List.of("DJ"),
      80000.0
    );
    primero.setPredeterminado(true);

    Paquete segundo = new Paquete(
      null,
      "Estándar",
      TipoPaquete.FIESTA_DE_15,
      salon,
      "Catering Estándar",
      List.of("DJ"),
      150000.0
    );
    segundo.setPredeterminado(true);

    Paquete tercero = new Paquete(
      null,
      "Premium",
      TipoPaquete.CASAMIENTO,
      salon,
      "Catering Completo",
      List.of("DJ", "Fotografía"),
      300000.0
    );
    tercero.setPredeterminado(true);

    sessionFactory.getCurrentSession().persist(primero);
    sessionFactory.getCurrentSession().persist(segundo);
    sessionFactory.getCurrentSession().persist(tercero);
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

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
    sessionFactory.getCurrentSession().persist(salon);

    Paquete basico = new Paquete(
      null,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      salon,
      "Catering Básico",
      List.of("DJ"),
      80000.0
    );

    Paquete estandar = new Paquete(
      null,
      "Estándar",
      TipoPaquete.FIESTA_DE_15,
      salon,
      "Catering Completo",
      List.of("DJ"),
      250000.0
    );

    Paquete premium = new Paquete(
      null,
      "Premium",
      TipoPaquete.CASAMIENTO,
      salon,
      "Catering Premium",
      List.of("DJ", "Fotografía"),
      300000.0
    );

    sessionFactory.getCurrentSession().persist(basico);
    sessionFactory.getCurrentSession().persist(estandar);
    sessionFactory.getCurrentSession().persist(premium);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

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
    sessionFactory.getCurrentSession().persist(salon);

    Paquete cumpleaniosEconomico = new Paquete(
      null,
      "Cumpleaños económico",
      TipoPaquete.CUMPLEANIOS,
      salon,
      "Catering básico",
      List.of("DJ"),
      150000.0
    );

    Paquete cumpleaniosCaro = new Paquete(
      null,
      "Cumpleaños premium",
      TipoPaquete.CUMPLEANIOS,
      salon,
      "Catering premium",
      List.of("DJ"),
      350000.0
    );

    Paquete casamiento = new Paquete(
      null,
      "Casamiento económico",
      TipoPaquete.CASAMIENTO,
      salon,
      "Catering básico",
      List.of("DJ"),
      200000.0
    );

    sessionFactory.getCurrentSession().persist(cumpleaniosEconomico);
    sessionFactory.getCurrentSession().persist(cumpleaniosCaro);
    sessionFactory.getCurrentSession().persist(casamiento);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

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
    sessionFactory.getCurrentSession().persist(salon);

    Paquete paquete = new Paquete(
      null,
      "Paquete premium",
      TipoPaquete.CASAMIENTO,
      salon,
      "Catering completo",
      List.of("DJ"),
      400000.0
    );

    sessionFactory.getCurrentSession().persist(paquete);

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

    Salon salonChico = new Salon("Salón chico", 50000.0);
    salonChico.setCapacidad(50);

    Salon salonMediano = new Salon("Salón mediano", 90000.0);
    salonMediano.setCapacidad(80);

    Salon salonGrande = new Salon("Salón grande", 150000.0);
    salonGrande.setCapacidad(120);

    sessionFactory.getCurrentSession().persist(salonChico);
    sessionFactory.getCurrentSession().persist(salonMediano);
    sessionFactory.getCurrentSession().persist(salonGrande);

    Paquete paqueteChico = new Paquete(
      null,
      "Cumpleaños chico",
      TipoPaquete.CUMPLEANIOS,
      salonChico,
      "Catering básico",
      List.of("DJ"),
      80000.0
    );

    Paquete paqueteMediano = new Paquete(
      null,
      "Cumpleaños mediano",
      TipoPaquete.CUMPLEANIOS,
      salonMediano,
      "Catering básico",
      List.of("DJ"),
      150000.0
    );

    Paquete paqueteGrande = new Paquete(
      null,
      "Cumpleaños grande",
      TipoPaquete.CUMPLEANIOS,
      salonGrande,
      "Catering completo",
      List.of("DJ"),
      250000.0
    );

    sessionFactory.getCurrentSession().persist(paqueteChico);
    sessionFactory.getCurrentSession().persist(paqueteMediano);
    sessionFactory.getCurrentSession().persist(paqueteGrande);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    // Ejecución

    List<Paquete> obtenidos = repositorioPaquete.buscarPorFiltros(null, null, 80);

    // Validación
    assertThat(obtenidos.size(), equalTo(2));
    assertThat(obtenidos.get(0).getNombre(), equalTo("Cumpleaños mediano"));
    assertThat(obtenidos.get(1).getNombre(), equalTo("Cumpleaños grande"));
  }
}
