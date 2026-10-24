package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.dominio.Catering;
import com.tallerwebi.dominio.Paquete;
import com.tallerwebi.dominio.RepositorioPaquete;
import com.tallerwebi.dominio.Salon;
import com.tallerwebi.dominio.ServicioAdicional;
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
      dadoQueExisteUnCatering("Catering Básico"),
      List.of(dadoQueExisteUnServicio("DJ")),
      100000.0
    );

    Paquete casamiento = new Paquete(
      null,
      "Casamiento clásico",
      TipoPaquete.CASAMIENTO,
      salon,
      dadoQueExisteUnCatering("Catering Completo"),
      List.of(dadoQueExisteUnServicio("DJ"), dadoQueExisteUnServicio("Fotografia")),
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
    assertEquals("Catering Básico", paquetesObtenidos.get(0).getCatering().getNombre());
    assertEquals(List.of("DJ"), nombresDeServicios(paquetesObtenidos.get(0)));
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
      dadoQueExisteUnCatering("Catering Básico"),
      List.of(dadoQueExisteUnServicio("DJ")),
      80000.0
    );
    basico.setPredeterminado(true);

    Paquete premium = new Paquete(
      null,
      "Premium",
      TipoPaquete.CASAMIENTO,
      salon,
      dadoQueExisteUnCatering("Catering Completo"),
      List.of(dadoQueExisteUnServicio("DJ"), dadoQueExisteUnServicio("Fotografia")),
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
      dadoQueExisteUnCatering("Catering Básico"),
      List.of(dadoQueExisteUnServicio("DJ")),
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
      dadoQueExisteUnCatering("Catering Básico"),
      List.of(dadoQueExisteUnServicio("DJ")),
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
      dadoQueExisteUnCatering("Catering Completo"),
      List.of(dadoQueExisteUnServicio("DJ")),
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
      dadoQueExisteUnCatering("Catering Básico"),
      List.of(dadoQueExisteUnServicio("DJ")),
      80000.0
    );
    primero.setPredeterminado(true);

    Paquete segundo = new Paquete(
      null,
      "Estándar",
      TipoPaquete.FIESTA_DE_15,
      salon,
      dadoQueExisteUnCatering("Catering Estándar"),
      List.of(dadoQueExisteUnServicio("DJ")),
      150000.0
    );
    segundo.setPredeterminado(true);

    Paquete tercero = new Paquete(
      null,
      "Premium",
      TipoPaquete.CASAMIENTO,
      salon,
      dadoQueExisteUnCatering("Catering Completo"),
      List.of(dadoQueExisteUnServicio("DJ"), dadoQueExisteUnServicio("Fotografia")),
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
      dadoQueExisteUnCatering("Catering Básico"),
      List.of(dadoQueExisteUnServicio("DJ")),
      80000.0
    );

    Paquete estandar = new Paquete(
      null,
      "Estándar",
      TipoPaquete.FIESTA_DE_15,
      salon,
      dadoQueExisteUnCatering("Catering Completo"),
      List.of(dadoQueExisteUnServicio("DJ")),
      250000.0
    );

    Paquete premium = new Paquete(
      null,
      "Premium",
      TipoPaquete.CASAMIENTO,
      salon,
      dadoQueExisteUnCatering("Catering Premium"),
      List.of(dadoQueExisteUnServicio("DJ"), dadoQueExisteUnServicio("Fotografia")),
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
      dadoQueExisteUnCatering("Catering básico"),
      List.of(dadoQueExisteUnServicio("DJ")),
      150000.0
    );

    Paquete cumpleaniosCaro = new Paquete(
      null,
      "Cumpleaños premium",
      TipoPaquete.CUMPLEANIOS,
      salon,
      dadoQueExisteUnCatering("Catering premium"),
      List.of(dadoQueExisteUnServicio("DJ")),
      350000.0
    );

    Paquete casamiento = new Paquete(
      null,
      "Casamiento económico",
      TipoPaquete.CASAMIENTO,
      salon,
      dadoQueExisteUnCatering("Catering básico"),
      List.of(dadoQueExisteUnServicio("DJ")),
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
      dadoQueExisteUnCatering("Catering completo"),
      List.of(dadoQueExisteUnServicio("DJ")),
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
      dadoQueExisteUnCatering("Catering básico"),
      List.of(dadoQueExisteUnServicio("DJ")),
      80000.0
    );

    Paquete paqueteMediano = new Paquete(
      null,
      "Cumpleaños mediano",
      TipoPaquete.CUMPLEANIOS,
      salonMediano,
      dadoQueExisteUnCatering("Catering básico"),
      List.of(dadoQueExisteUnServicio("DJ")),
      150000.0
    );

    Paquete paqueteGrande = new Paquete(
      null,
      "Cumpleaños grande",
      TipoPaquete.CUMPLEANIOS,
      salonGrande,
      dadoQueExisteUnCatering("Catering completo"),
      List.of(dadoQueExisteUnServicio("DJ")),
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

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerPaquetesQueCumplanLosTresFiltros() {
    // Preparación
    Salon salonMediano = new Salon("Salón mediano", 90000.0);
    salonMediano.setCapacidad(80);

    Salon salonGrande = new Salon("Salón grande", 150000.0);
    salonGrande.setCapacidad(120);

    Salon salonChico = new Salon("Salón chico", 50000.0);
    salonChico.setCapacidad(50);

    sessionFactory.getCurrentSession().persist(salonMediano);
    sessionFactory.getCurrentSession().persist(salonGrande);
    sessionFactory.getCurrentSession().persist(salonChico);

    // Cumple los tres filtros
    Paquete cumpleaniosValido = new Paquete(
      null,
      "Cumpleaños válido",
      TipoPaquete.CUMPLEANIOS,
      salonMediano,
      dadoQueExisteUnCatering("Catering básico"),
      List.of(dadoQueExisteUnServicio("DJ")),
      250000.0
    );

    // No cumple el tipo de evento
    Paquete casamiento = new Paquete(
      null,
      "Casamiento",
      TipoPaquete.CASAMIENTO,
      salonGrande,
      dadoQueExisteUnCatering("Catering completo"),
      List.of(dadoQueExisteUnServicio("DJ")),
      200000.0
    );

    // Supera el presupuesto
    Paquete cumpleaniosCaro = new Paquete(
      null,
      "Cumpleaños caro",
      TipoPaquete.CUMPLEANIOS,
      salonGrande,
      dadoQueExisteUnCatering("Catering premium"),
      List.of(dadoQueExisteUnServicio("DJ")),
      300000.0
    );

    // No tiene capacidad suficiente
    Paquete cumpleaniosChico = new Paquete(
      null,
      "Cumpleaños chico",
      TipoPaquete.CUMPLEANIOS,
      salonChico,
      dadoQueExisteUnCatering("Catering básico"),
      List.of(dadoQueExisteUnServicio("DJ")),
      150000.0
    );

    sessionFactory.getCurrentSession().persist(cumpleaniosValido);
    sessionFactory.getCurrentSession().persist(casamiento);
    sessionFactory.getCurrentSession().persist(cumpleaniosCaro);
    sessionFactory.getCurrentSession().persist(cumpleaniosChico);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

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
  public void deberiaGuardarYRecuperarElCateringYLosServiciosDelPaquete() {
    // Preparación
    Salon salon = new Salon("Salón de prueba", 50000.0);
    sessionFactory.getCurrentSession().persist(salon);

    Paquete premium = new Paquete(
      null,
      "Premium",
      TipoPaquete.CASAMIENTO,
      salon,
      dadoQueExisteUnCatering("Catering Completo"),
      List.of(dadoQueExisteUnServicio("DJ"), dadoQueExisteUnServicio("Fotografia")),
      300000.0
    );
    sessionFactory.getCurrentSession().persist(premium);
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    // Ejecución
    Paquete obtenido = repositorioPaquete.obtenerTodos().get(0);

    // Validación
    assertEquals("Catering Completo", obtenido.getCatering().getNombre());
    assertEquals(List.of("DJ", "Fotografia"), nombresDeServicios(obtenido));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerUnPaquetePublicadoPorSuId() {
    // Preparación
    Salon salon = new Salon("Salón de prueba", 50000.0);
    sessionFactory.getCurrentSession().persist(salon);

    Paquete publicado = new Paquete(
      null,
      "Cumpleaños de Ana",
      TipoPaquete.CUMPLEANIOS,
      salon,
      dadoQueExisteUnCatering("Catering Basico"),
      List.of(dadoQueExisteUnServicio("DJ")),
      120000.0
    );
    publicado.setPredeterminado(false);
    sessionFactory.getCurrentSession().persist(publicado);
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    // Ejecución
    Paquete obtenido = repositorioPaquete.obtenerPorId(publicado.getId());

    // Validación
    assertEquals("Cumpleaños de Ana", obtenido.getNombre());
    assertFalse(obtenido.isPredeterminado());
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverNullSiNoExisteElPaqueteBuscadoPorId() {
    // Ejecución
    Paquete obtenido = repositorioPaquete.obtenerPorId(9999L);

    // Validación
    assertNull(obtenido);
  }

  // Métodos auxiliares: Paquete referencia a Catering y ServicioAdicional,
  // así que tienen que estar guardados en la base antes que el paquete.
  private Catering dadoQueExisteUnCatering(String nombre) {
    Catering catering = new Catering(nombre, 1500.0);
    sessionFactory.getCurrentSession().persist(catering);
    return catering;
  }

  private ServicioAdicional dadoQueExisteUnServicio(String nombre) {
    ServicioAdicional servicio = new ServicioAdicional(nombre, 40000.0, false);
    sessionFactory.getCurrentSession().persist(servicio);
    return servicio;
  }

  private List<String> nombresDeServicios(Paquete paquete) {
    return paquete.getServiciosAdicionales().stream().map(ServicioAdicional::getNombre).toList();
  }
}
