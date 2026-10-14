package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.EstadoReserva;
import com.tallerwebi.dominio.RepositorioReserva;
import com.tallerwebi.dominio.Reserva;
import com.tallerwebi.dominio.Usuario;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioReserva")
public class RepositorioReservaImpl implements RepositorioReserva {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioReservaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public List<Reserva> buscarPorUsuario(Usuario usuario) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from Reserva where usuario.id = :idUsuario", Reserva.class)
      .setParameter("idUsuario", usuario.getId())
      .getResultList();
  }

  @Override
  public List<LocalDate> obtenerFechasOcupadasPorSalon(Long salonId) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "select r.fecha from Reserva r where r.salon.id = :salonId and r.estado in (:estados)",
        LocalDate.class
      )
      .setParameter("salonId", salonId)
      .setParameterList("estados", List.of(EstadoReserva.SENIADA, EstadoReserva.CONFIRMADA))
      .getResultList();
  }

  @Override
  public List<Reserva> buscarEntreFechas(LocalDate desde, LocalDate hasta) {
    return sessionFactory
            .getCurrentSession()
            .createQuery("from Reserva where fecha between :desde and :hasta", Reserva.class)
            .setParameter("desde", desde)
            .setParameter("hasta", hasta)
            .getResultList();
  }
}
