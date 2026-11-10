package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Evento;
import com.tallerwebi.dominio.EventoServicio;
import com.tallerwebi.dominio.RepositorioEvento;
import com.tallerwebi.dominio.ServicioAdicional;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioEventoImpl implements RepositorioEvento {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioEventoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Evento evento) {
    sessionFactory.getCurrentSession().persist(evento);
  }

  // El Evento vive en la sesión HTTP entre requests, así que cuando vuelve ya no está
  // asociado a la sesión de Hibernate. merge copia sus cambios al registro de la base
  // (igual que RepositorioUsuarioImpl.modificar).
  @Override
  public void actualizar(Evento evento) {
    sessionFactory.getCurrentSession().merge(evento);
  }

  @Override
  public Evento buscarPorId(Long id) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("FROM Evento e WHERE e.id = :id", Evento.class)
      .setParameter("id", id)
      .uniqueResult();
  }

  @Override
  public List<ServicioAdicional> buscarServicio(Long eventoId) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT es.servicio FROM EventoServicio es " +
        "WHERE es.evento.id = :eventoId ORDER BY es.id",
        ServicioAdicional.class
      )
      .setParameter("eventoId", eventoId)
      .getResultList();
  }

  @Override
  public boolean tieneServicio(Long eventoId, Long servicioId) {
    Long cantidad = sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT COUNT(es) FROM EventoServicio es " +
        "WHERE es.evento.id = :eventoId AND es.servicio.id = :servicioId",
        Long.class
      )
      .setParameter("eventoId", eventoId)
      .setParameter("servicioId", servicioId)
      .uniqueResult();
    return cantidad > 0;
  }

  @Override
  public void agregarServicio(Evento evento, ServicioAdicional servicio) {
    EventoServicio eventoServicio = new EventoServicio(evento, servicio);
    sessionFactory.getCurrentSession().persist(eventoServicio);
  }

  @Override
  public void quitarServicio(Long eventoId, Long servicioId) {
    EventoServicio eventoServicio = sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM EventoServicio es " +
        "WHERE es.evento.id = :eventoId AND es.servicio.id = :servicioId",
        EventoServicio.class
      )
      .setParameter("eventoId", eventoId)
      .setParameter("servicioId", servicioId)
      .uniqueResult();

    if (eventoServicio != null) {
      sessionFactory.getCurrentSession().remove(eventoServicio);
    }
  }
}
