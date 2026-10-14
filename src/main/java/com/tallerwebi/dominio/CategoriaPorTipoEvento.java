package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

public class CategoriaPorTipoEvento {

  public List<CategoriaServicio> getCategoriaSugerida(EventoPersonalizado evento) {
    List<CategoriaServicio> listaSugerida = new ArrayList<>();

    switch (evento.getTipo()) {
      case TipoPaquete.CUMPLEANIOS:
        listaSugerida.add(CategoriaServicio.ANIMACION);
        listaSugerida.add(CategoriaServicio.COTILLON);
        listaSugerida.add(CategoriaServicio.DJ);
        break;
      case TipoPaquete.FIESTA_DE_15, TipoPaquete.CASAMIENTO:
        listaSugerida.add(CategoriaServicio.DECORACION);
        listaSugerida.add(CategoriaServicio.FOTOGRAFIA);
        listaSugerida.add(CategoriaServicio.VIDEO);
        listaSugerida.add(CategoriaServicio.DJ);
        break;
      case TipoPaquete.EMPRESARIAL:
        listaSugerida.add(CategoriaServicio.VIDEO);
        listaSugerida.add(CategoriaServicio.DJ);
        break;
    }
    return listaSugerida;
  }
}
