package com.tallerwebi.dominio;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioArmadoAutomaticoImpl implements ServicioArmadoAutomatico {

  private static final double FACTOR_CAPACIDAD_MAXIMA = 2.0;

  private ServicioDisponibilidadSalon servicioDisponibilidadSalon;
  private RepositorioCatering repositorioCatering;
  private RepositorioServicioAdicional repositorioServicioAdicional;
  private CategoriaPorTipoEvento categoriaPorTipoEvento = new CategoriaPorTipoEvento();

  public ServicioArmadoAutomaticoImpl(
    ServicioDisponibilidadSalon servicioDisponibilidadSalon,
    RepositorioCatering repositorioCatering,
    RepositorioServicioAdicional repositorioServicioAdicional
  ) {
    this.servicioDisponibilidadSalon = servicioDisponibilidadSalon;
    this.repositorioCatering = repositorioCatering;
    this.repositorioServicioAdicional = repositorioServicioAdicional;
  }

  //    @Override
  //    public ResultadoArmado armar(TipoPaquete tipoPaquete, Integer invitados, LocalDate fecha, Double presupuestoMaximo, PrioridadArmado prioridadArmado) {
  //        List<Salon> salonesLibres = servicioDisponibilidadSalon.obtenerSalonesDisponibles(fecha, fecha);
  //
  //        List<Salon> salonesConCapacidad = salonesLibres.stream().filter(salon -> salon.getCapacidad() != null && salon.getCapacidad() >= invitados).collect(Collectors.toList());
  //
  //        if (salonesConCapacidad.isEmpty()) {
  //            return new ResultadoArmado(TipoResultado.SIN_DISPONIBILIDAD, null);
  //        }
  //
  //        List<Catering> caterings = repositorioCaterin.obtenerTodos();
  //
  //        if (caterings.isEmpty()) {
  //            return new ResultadoArmado(TipoResultado.SIN_DISPONIBILIDAD, null);
  //        }
  //
  //        Salon salonMinimo = salonesConCapacidad.get(salonesConCapacidad.size() - 1);
  //        Catering cateringMinimo = caterings.get(caterings.size() - 1);
  //
  //        Double montoMinimo = salonMinimo.getPrecio() + costoCatering(cateringMinimo, invitados);
  //
  //
  //        if (montoMinimo > presupuestoMaximo) {
  //            return new ResultadoArmado(TipoResultado.PRESUPUESTO_INSUFICIENTE, montoMinimo);
  //        }
  //
  //
  //        if (prioridadArmado == PrioridadArmado.SALON) {
  //            Double topeSalon = presupuestoMaximo - costoCatering(cateringMinimo, invitados);
  //            Salon salonElegido = elegirMejorSalon(salonesConCapacidad, topeSalon);
  //
  //            Double topeCatering = presupuestoMaximo - salonElegido.getPrecio();
  //            Catering cateringElegido = elegirMejorCatering(caterings, topeCatering, invitados);
  //
  //            return new ResultadoArmado(TipoResultado.ARMADO, salonElegido, cateringElegido);
  //        }
  //        if (prioridadArmado == PrioridadArmado.CATERING) {
  //            Double topeCatering = presupuestoMaximo - salonMinimo.getPrecio();
  //            Catering cateringElegido = elegirMejorCatering(caterings, topeCatering, invitados);
  //
  //            Double topeSalon = presupuestoMaximo - costoCatering(cateringElegido, invitados);
  //            Salon salonElegido = elegirMejorSalon(salonesConCapacidad, topeSalon);
  //
  //            return new ResultadoArmado(TipoResultado.ARMADO, salonElegido, cateringElegido);
  //        }
  //
  //        throw new IllegalArgumentException("Prioridad de armado no válida: " + prioridadArmado);
  //    }

  @Override
  public ResultadoArmado armar(
    TipoPaquete tipoPaquete,
    Integer invitados,
    LocalDate fecha,
    Double presupuestoMaximo,
    PrioridadArmado prioridadArmado
  ) {
    List<Salon> salonesConCapacidad = obtenerSalonesConCapacidad(invitados, fecha);

    if (salonesConCapacidad.isEmpty()) {
      return new ResultadoArmado(TipoResultado.SIN_DISPONIBILIDAD, null);
    }

    List<Catering> caterings = repositorioCatering.obtenerTodos();

    if (caterings.isEmpty()) {
      return new ResultadoArmado(TipoResultado.SIN_DISPONIBILIDAD, null);
    }

    Salon salonMinimo = salonesConCapacidad.get(salonesConCapacidad.size() - 1);
    Catering cateringMinimo = caterings.get(caterings.size() - 1);

    Double montoMinimo = salonMinimo.getPrecio() + costoCatering(cateringMinimo, invitados);

    if (montoMinimo > presupuestoMaximo) {
      return new ResultadoArmado(TipoResultado.PRESUPUESTO_INSUFICIENTE, montoMinimo);
    }

    Salon salonElegido;
    Catering cateringElegido;

    if (prioridadArmado == PrioridadArmado.SALON) {
      Double topeSalon = presupuestoMaximo - costoCatering(cateringMinimo, invitados);
      salonElegido = elegirMejorSalon(salonesConCapacidad, topeSalon, invitados);

      Double topeCatering = presupuestoMaximo - salonElegido.getPrecio();
      cateringElegido = elegirMejorCatering(caterings, topeCatering, invitados);
    } else if (prioridadArmado == PrioridadArmado.CATERING) {
      Double topeCatering = presupuestoMaximo - salonMinimo.getPrecio();
      cateringElegido = elegirMejorCatering(caterings, topeCatering, invitados);

      Double topeSalon = presupuestoMaximo - costoCatering(cateringElegido, invitados);
      salonElegido = elegirMejorSalon(salonesConCapacidad, topeSalon, invitados);
    } else {
      throw new IllegalArgumentException("Prioridad de armado no válida: " + prioridadArmado);
    }

    Double saldo =
      presupuestoMaximo - salonElegido.getPrecio() - costoCatering(cateringElegido, invitados);
    List<ServicioAdicional> servicios = elegirServicios(tipoPaquete, invitados, saldo);

    return new ResultadoArmado(TipoResultado.ARMADO, salonElegido, cateringElegido, servicios);
  }

  @Override
  public MontoMinimo calcularMontoMinimo(Integer invitados, LocalDate fecha) {
    List<Salon> salones = obtenerSalonesConCapacidad(invitados, fecha);
    List<Catering> caterings = repositorioCatering.obtenerTodos();

    if (salones.isEmpty() || caterings.isEmpty()) {
      return null;
    }

    Salon salonMinimo = salones.get(salones.size() - 1);
    Catering cateringMinimo = caterings.get(caterings.size() - 1);

    return new MontoMinimo(salonMinimo.getPrecio(), costoCatering(cateringMinimo, invitados));
  }

  private List<Salon> obtenerSalonesConCapacidad(Integer invitados, LocalDate fecha) {
    return servicioDisponibilidadSalon
      .obtenerSalonesDisponibles(fecha, fecha)
      .stream()
      .filter(salon -> salon.getCapacidad() != null && salon.getCapacidad() >= invitados)
      .collect(Collectors.toList());
  }

  private Double costoCatering(Catering catering, Integer invitados) {
    return catering.getPrecioPorPersona() * invitados;
  }

  private Salon elegirMejorSalon(List<Salon> salones, Double tope, Integer invitados) {
    double capacidadMaxima = invitados * FACTOR_CAPACIDAD_MAXIMA;

    return salones
      .stream()
      .filter(salon -> salon.getPrecio() <= tope && salon.getCapacidad() <= capacidadMaxima)
      .findFirst()
      .orElseGet(() ->
        salones
          .stream()
          .filter(salon -> salon.getPrecio() <= tope)
          .min(Comparator.comparingInt((Salon salon) -> salon.getCapacidad()))
          .orElse(null)
      );
  }

  private Catering elegirMejorCatering(List<Catering> caterings, Double tope, Integer invitados) {
    return caterings
      .stream()
      .filter(catering -> costoCatering(catering, invitados) <= tope)
      .findFirst()
      .orElse(null);
  }

  private List<ServicioAdicional> elegirServicios(
    TipoPaquete tipoPaquete,
    Integer invitados,
    Double saldo
  ) {
    List<ServicioAdicional> todos = repositorioServicioAdicional.obtenerTodos();
    List<CategoriaServicio> sugeridas = categoriaPorTipoEvento.getCategoriaSugerida(tipoPaquete);

    List<ServicioAdicional> elegidos = new ArrayList<>();

    // Fase 1: categorías sugeridas para el tipo de evento
    List<ServicioAdicional> candidatosSugeridos = masBaratoPorCategoria(
      todos,
      invitados,
      categoria -> sugeridas.contains(categoria)
    );
    Double saldoRestante = agregarLosQueEntran(candidatosSugeridos, elegidos, saldo, invitados);

    // Fase 2: con lo que sobre, categorías no sugeridas
    List<ServicioAdicional> candidatosRestantes = masBaratoPorCategoria(
      todos,
      invitados,
      categoria -> !sugeridas.contains(categoria)
    );
    agregarLosQueEntran(candidatosRestantes, elegidos, saldoRestante, invitados);

    return elegidos;
  }

  private List<ServicioAdicional> masBaratoPorCategoria(
    List<ServicioAdicional> servicios,
    Integer invitados,
    Predicate<CategoriaServicio> categoriaValida
  ) {
    Map<CategoriaServicio, ServicioAdicional> masBaratoDeCadaCategoria = servicios
      .stream()
      .filter(servicio ->
        servicio.getCategoriaServicio() != null &&
        categoriaValida.test(servicio.getCategoriaServicio())
      )
      .collect(
        Collectors.toMap(
          ServicioAdicional::getCategoriaServicio,
          servicio -> servicio,
          (a, b) -> a.calcularCosto(invitados) <= b.calcularCosto(invitados) ? a : b
        )
      );

    return masBaratoDeCadaCategoria
      .values()
      .stream()
      .sorted(Comparator.comparingDouble(servicio -> servicio.calcularCosto(invitados)))
      .collect(Collectors.toList());
  }

  private Double agregarLosQueEntran(
    List<ServicioAdicional> candidatos,
    List<ServicioAdicional> elegidos,
    Double saldo,
    Integer invitados
  ) {
    Double saldoRestante = saldo;
    for (ServicioAdicional servicio : candidatos) {
      Double costo = servicio.calcularCosto(invitados);
      if (costo > saldoRestante) {
        break;
      }
      elegidos.add(servicio);
      saldoRestante -= costo;
    }
    return saldoRestante;
  }
}
