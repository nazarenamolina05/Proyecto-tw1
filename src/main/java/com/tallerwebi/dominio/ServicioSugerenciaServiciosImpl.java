package com.tallerwebi.dominio;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioSugerenciaServiciosImpl implements ServicioSugerenciaServicios {

  private RepositorioServicioAdicional repositorioServicioAdicional;
  private CategoriaPorTipoEvento categoriaPorTipoEvento;

  @Autowired
  public ServicioSugerenciaServiciosImpl(
    RepositorioServicioAdicional repositorioServicioAdicional
  ) {
    this.repositorioServicioAdicional = repositorioServicioAdicional;
    this.categoriaPorTipoEvento = new CategoriaPorTipoEvento();
  }

  @Override
  public ServiciosClasificados clasificar(EventoPersonalizado evento) {
    List<CategoriaServicio> sugerida = categoriaPorTipoEvento.getCategoriaSugerida(evento);

    Map<Boolean, List<ServicioAdicional>> partes = repositorioServicioAdicional
      .obtenerTodos()
      .stream()
      .collect(Collectors.partitioningBy(s -> sugerida.contains(s.getCategoriaServicio())));

    return new ServiciosClasificados(partes.get(true), partes.get(false));
  }
}
