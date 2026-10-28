package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Paquete;
import com.tallerwebi.dominio.RepositorioPaquete;
import com.tallerwebi.dominio.TipoPaquete;
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

  @Override
  public Paquete obtenerPredeterminadoPorId(Long id) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from Paquete where id = :id and predeterminado = true", Paquete.class)
      .setParameter("id", id)
      .uniqueResult();
  }

  @Override
  public List<Paquete> buscarPorFiltros(TipoPaquete tipo, Double precioMaximo, Integer invitados) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM Paquete p " +
        "WHERE (:tipo IS NULL OR p.tipo = :tipo) " +
        "AND (:precioMaximo IS NULL OR p.precioEstimado <= :precioMaximo) " +
        "AND (:invitados IS NULL OR p.salon.capacidad >= :invitados) " +
        "ORDER BY p.id",
        Paquete.class
      )
      .setParameter("tipo", tipo)
      .setParameter("precioMaximo", precioMaximo)
      .setParameter("invitados", invitados)
      .getResultList();
  }

  @Override
  public void guardar(Paquete paquete) {
    sessionFactory.getCurrentSession().persist(paquete);
  }
}
