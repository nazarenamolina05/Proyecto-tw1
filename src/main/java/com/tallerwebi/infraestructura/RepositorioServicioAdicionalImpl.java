package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioServicioAdicional;
import com.tallerwebi.dominio.ServicioAdicional;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioServicioAdicional")
public class RepositorioServicioAdicionalImpl implements RepositorioServicioAdicional {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioServicioAdicionalImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public List<ServicioAdicional> obtenerTodos() {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from ServicioAdicional order by precio desc", ServicioAdicional.class)
      .getResultList();
  }

  @Override
  public ServicioAdicional buscarPorId(Long id) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from ServicioAdicional  where id= :id", ServicioAdicional.class)
      .setParameter("id", id)
      .uniqueResult();
  }
}
