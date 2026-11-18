package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioResenia {
  Resenia reseniar(Long idReserva, Usuario usuario, Integer puntaje, String comentario);

  Resenia obtenerReseniaDe(Long idReserva);

  List<Resenia> obtenerReseniasDelPaquete(Long idPaquete);

  Double calcularPromedio(List<Resenia> resenias);
}
