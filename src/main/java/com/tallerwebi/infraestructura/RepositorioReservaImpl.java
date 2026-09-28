package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioReserva;
import com.tallerwebi.dominio.Reserva;
import com.tallerwebi.dominio.Usuario;
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
}