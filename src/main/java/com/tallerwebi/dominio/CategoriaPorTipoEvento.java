//package com.tallerwebi.dominio;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CategoriaPorTipoEvento {
//
//  public List<CategoriaServicio> getCategoriaSugerida(EventoPersonalizado evento) {
//    List<CategoriaServicio> listaSugerida = new ArrayList<>();
//
//    switch (evento.getTipo()) {
//      case TipoPaquete.CUMPLEANIOS:
//        listaSugerida.add(CategoriaServicio.ANIMACION);
//        listaSugerida.add(CategoriaServicio.COTILLON);
//        listaSugerida.add(CategoriaServicio.DJ);
//        break;
//      case TipoPaquete.FIESTA_DE_15:
//        listaSugerida.add(CategoriaServicio.DECORACION);
//        listaSugerida.add(CategoriaServicio.FOTOGRAFIA);
//        listaSugerida.add(CategoriaServicio.VIDEO);
//        listaSugerida.add(CategoriaServicio.DJ);
//        break;
//      case TipoPaquete.CASAMIENTO:
//        listaSugerida.add(CategoriaServicio.DECORACION);
//        listaSugerida.add(CategoriaServicio.FOTOGRAFIA);
//        listaSugerida.add(CategoriaServicio.VIDEO);
//        listaSugerida.add(CategoriaServicio.DJ);
//        break;
//      case TipoPaquete.EMPRESARIAL:
//        listaSugerida.add(CategoriaServicio.VIDEO);
//        listaSugerida.add(CategoriaServicio.DJ);
//        break;
//      default:
//        throw new IllegalArgumentException("Tipo de evento no soportado: " + evento.getTipo());
//    }
//    return listaSugerida;
//  }
//}

package com.tallerwebi.dominio;

import java.util.List;

public class CategoriaPorTipoEvento {

  public List<CategoriaServicio> getCategoriaSugerida(EventoPersonalizado evento) {
    return switch (evento.getTipo()) {
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
