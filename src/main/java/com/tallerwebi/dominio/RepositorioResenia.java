package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioResenia {
  void guardar(Resenia resenia);

  Resenia buscarPorReserva(Long idReserva);

  List<Resenia> buscarPorPaquete(Long idPaquete);
}
