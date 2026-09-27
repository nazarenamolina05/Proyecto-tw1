package com.tallerwebi.dominio;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioConsultarPaquetesPredeterminadosImpl implements ServicioConsultarPaquetesPredeterminados{

    private RepositorioPaquete repositorioPaquete;

    public ServicioConsultarPaquetesPredeterminadosImpl(RepositorioPaquete repositorioPaquete) {
        this.repositorioPaquete = repositorioPaquete;
    }

    @Override
    public List<Paquete> consultarTodos() {
        return repositorioPaquete.obtenerTodos();
    }
}
