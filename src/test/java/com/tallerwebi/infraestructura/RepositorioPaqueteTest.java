package com.tallerwebi.infraestructura;

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
}
