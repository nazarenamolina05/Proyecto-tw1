package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Catering;
import com.tallerwebi.dominio.RepositorioCatering;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioCatering")
public class RepositorioCateringImpl implements RepositorioCatering {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioCateringImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public List<Catering> obtenerTodos() {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from Catering order by precioPorPersona desc", Catering.class)
      .getResultList();
  }

  @Override
  public Catering buscarPorId(Long id) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from Catering where id= :id", Catering.class)
      .setParameter("id", id)
      .uniqueResult();
  }
}
