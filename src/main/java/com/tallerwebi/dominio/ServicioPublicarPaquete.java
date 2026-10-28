package com.tallerwebi.dominio;

@FunctionalInterface
public interface ServicioPublicarPaquete {
  Paquete publicar(Long idReserva, Usuario usuario, String nombre);
}
