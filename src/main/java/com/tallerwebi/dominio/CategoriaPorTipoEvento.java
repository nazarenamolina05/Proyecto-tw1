//package com.tallerwebi.dominio;
//
//import java.util.List;
//
//public class CategoriaPorTipoEvento {
//
//  public List<CategoriaServicio> getCategoriaSugerida(Evento evento) {
//    return switch (evento.getTipoPaquete()) {
//      case CUMPLEANIOS -> List.of(
//        CategoriaServicio.ANIMACION,
//        CategoriaServicio.COTILLON,
//        CategoriaServicio.DJ
//      );
//      case FIESTA_DE_15, CASAMIENTO -> List.of(
//        CategoriaServicio.DECORACION,
//        CategoriaServicio.FOTOGRAFIA,
//        CategoriaServicio.VIDEO,
//        CategoriaServicio.DJ
//      );
//      case EMPRESARIAL -> List.of(CategoriaServicio.VIDEO, CategoriaServicio.DJ);
//    };
//  }
//}

package com.tallerwebi.dominio;

import java.util.List;

public class CategoriaPorTipoEvento {

  public List<CategoriaServicio> getCategoriaSugerida(Evento evento) {
    return getCategoriaSugerida(evento.getTipoPaquete());
  }

  public List<CategoriaServicio> getCategoriaSugerida(TipoPaquete tipoPaquete) {
    return switch (tipoPaquete) {
      case CUMPLEANIOS -> List.of(
        CategoriaServicio.ANIMACION,
        CategoriaServicio.COTILLON,
        CategoriaServicio.DJ
      );
      case FIESTA_DE_15, CASAMIENTO -> List.of(
        CategoriaServicio.DECORACION,
        CategoriaServicio.FOTOGRAFIA,
        CategoriaServicio.VIDEO,
        CategoriaServicio.DJ
      );
      case EMPRESARIAL -> List.of(CategoriaServicio.VIDEO, CategoriaServicio.DJ);
    };
  }
}
