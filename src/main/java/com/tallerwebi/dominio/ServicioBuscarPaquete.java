package com.tallerwebi.dominio;

import java.util.List;

@FunctionalInterface
public interface ServicioBuscarPaquete {
  List<Paquete> buscarPorTipo(TipoPaquete tipoPaquete);
}
