package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Paquete;
import com.tallerwebi.dominio.RepositorioPaquete;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioPaqueteImpl implements RepositorioPaquete {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioPaqueteImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public List<Paquete> obtenerTodos() {
    return sessionFactory
      .getCurrentSession()
      .createQuery("FROM Paquete p ORDER BY p.id", Paquete.class)
      .getResultList();
  }

  @Override
  public List<Paquete> obtenerPredeterminados() {
    return sessionFactory
      .getCurrentSession()
      .createQuery("FROM Paquete p WHERE p.predeterminado = true ORDER BY p.id", Paquete.class)
      .getResultList();
  }
}
