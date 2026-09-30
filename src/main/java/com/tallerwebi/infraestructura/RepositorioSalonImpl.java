package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioSalon;
import com.tallerwebi.dominio.Salon;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioSalon")
public class RepositorioSalonImpl implements RepositorioSalon {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioSalonImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public List<Salon> obtenerTodos() {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from Salon order by precio desc", Salon.class)
      .getResultList();
  }

  @Override
  public Salon buscarPorId(Long id) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from Salon where id= :id", Salon.class)
      .setParameter("id", id)
      .uniqueResult();
  }
}
