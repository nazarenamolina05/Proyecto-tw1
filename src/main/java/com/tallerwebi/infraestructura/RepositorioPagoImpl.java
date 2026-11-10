package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Pago;
import com.tallerwebi.dominio.RepositorioPago;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioPago")
public class RepositorioPagoImpl implements RepositorioPago {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioPagoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Pago pago) {
    sessionFactory.getCurrentSession().persist(pago);
  }

  @Override
  public Pago buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(Pago.class, id);
  }
}
