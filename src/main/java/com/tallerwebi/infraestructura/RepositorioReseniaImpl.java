package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioResenia;
import com.tallerwebi.dominio.Resenia;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioResenia")
public class RepositorioReseniaImpl implements RepositorioResenia {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioReseniaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Resenia resenia) {
    sessionFactory.getCurrentSession().persist(resenia);
  }

  @Override
  public Resenia buscarPorReserva(Long idReserva) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from Resenia r where r.reserva.id = :idReserva", Resenia.class)
      .setParameter("idReserva", idReserva)
      .uniqueResult();
  }

  // Hibernate arma los joins navegando reserva -> evento -> paqueteOrigen.
  @Override
  public List<Resenia> buscarPorPaquete(Long idPaquete) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "select r from Resenia r " +
        "where r.reserva.evento.paqueteOrigen.id = :idPaquete " +
        "order by r.fecha desc, r.id desc",
        Resenia.class
      )
      .setParameter("idPaquete", idPaquete)
      .getResultList();
  }
}
