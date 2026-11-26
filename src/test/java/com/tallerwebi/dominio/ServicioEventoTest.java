package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.ElementoInexistenteException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioEventoTest {

  private RepositorioSalon repositorioSalonMock;
  private RepositorioCatering repositorioCateringMock;
  private RepositorioServicioAdicional repositorioServicioAdicionalMock;
  private RepositorioEvento repositorioEventoMock;
  private RepositorioPaquete repositorioPaqueteMock;
  private ServicioEvento servicioEvento;

  @BeforeEach
  public void init() {
    repositorioSalonMock = mock(RepositorioSalon.class);
    repositorioCateringMock = mock(RepositorioCatering.class);
    repositorioServicioAdicionalMock = mock(RepositorioServicioAdicional.class);
    repositorioEventoMock = mock(RepositorioEvento.class);
    repositorioPaqueteMock = mock(RepositorioPaquete.class);
    servicioEvento =
      new ServicioEventoImpl(
        repositorioSalonMock,
        repositorioCateringMock,
        repositorioServicioAdicionalMock,
        repositorioEventoMock,
        repositorioPaqueteMock
      );
  }

  @Test
  void deberiaCrearUnEventoConLosDatosIndicados() {
    LocalDate fecha = LocalDate.now();

    Evento evento = servicioEvento.crearEvento(TipoPaquete.CASAMIENTO, fecha, 80);

    assertThat(evento.getTipoPaquete(), equalTo(TipoPaquete.CASAMIENTO));
    assertThat(evento.getFecha(), equalTo(fecha));
    assertThat(evento.getCantidadInvitados(), equalTo(80));
  }

  // [REFACTOR EVENTO] El evento se guarda apenas se crea.
  @Test
  void deberiaGuardarElEventoAlCrearlo() {
    Evento evento = servicioEvento.crearEvento(TipoPaquete.CASAMIENTO, LocalDate.now(), 80);

    verify(repositorioEventoMock).guardar(evento);
  }

  @Test
  void alCrearUnEventoDesdeUnPaqueteDeberiaCopiarSalonCateringYPaqueteDeOrigen() {
    Paquete paquete = this.dadoQueExisteUnPaqueteConId(1L);

    Evento evento = servicioEvento.crearEventoDesdePaquete(
      paquete,
      TipoPaquete.CUMPLEANIOS,
      LocalDate.now().plusDays(30),
      50
    );

    verify(repositorioEventoMock).guardar(evento);
    assertThat(evento.getSalon(), equalTo(paquete.getSalon()));
    assertThat(evento.getCatering(), equalTo(paquete.getCatering()));
    assertThat(evento.getPaqueteOrigen(), equalTo(paquete));
  }

  // [REFACTOR EVENTO] Antes lo hacía Paquete.crearEvento copiando la lista.
  @Test
  void alCrearUnEventoDesdeUnPaqueteDeberiaGuardarUnEventoServicioPorCadaServicioDelPaquete() {
    Paquete paquete = this.dadoQueExisteUnPaqueteConId(1L);
    ServicioAdicional dj = new ServicioAdicional("DJ", 40000.0, false);
    ServicioAdicional fotografia = new ServicioAdicional("Fotografia", 30000.0, false);
    when(repositorioPaqueteMock.obtenerServiciosDe(1L)).thenReturn(List.of(dj, fotografia));

    Evento evento = servicioEvento.crearEventoDesdePaquete(
      paquete,
      TipoPaquete.CUMPLEANIOS,
      LocalDate.now().plusDays(30),
      50
    );

    verify(repositorioEventoMock).agregarServicio(evento, dj);
    verify(repositorioEventoMock).agregarServicio(evento, fotografia);
  }

  @Test
  void deberiaActualizarLosDatosDelEventoYGuardarlos() {
    Evento evento = this.dadoQueExisteUnEventoConId(10L);
    LocalDate nuevaFecha = LocalDate.now().plusDays(60);

    servicioEvento.actualizarDatos(evento, TipoPaquete.FIESTA_DE_15, nuevaFecha, 120);

    assertThat(evento.getTipoPaquete(), equalTo(TipoPaquete.FIESTA_DE_15));
    assertThat(evento.getFecha(), equalTo(nuevaFecha));
    assertThat(evento.getCantidadInvitados(), equalTo(120));
    verify(repositorioEventoMock).actualizar(evento);
  }

  @Test
  void deberiaObtenerLosServiciosDelEventoDesdeElRepositorio() {
    Evento evento = this.dadoQueExisteUnEventoConId(10L);
    ServicioAdicional dj = new ServicioAdicional("DJ", 40000.0, false);
    when(repositorioEventoMock.buscarServicio(10L)).thenReturn(List.of(dj));

    List<ServicioAdicional> servicios = servicioEvento.obtenerServiciosDe(evento);

    assertThat(servicios, contains(dj));
  }

  @Test
  void noDeberiaDuplicarUnServicioQueElEventoYaTiene() {
    Evento evento = this.dadoQueExisteUnEventoConId(10L);
    ServicioAdicional dj = new ServicioAdicional("DJ", 40000.0, false);
    when(repositorioServicioAdicionalMock.buscarPorId(6L)).thenReturn(dj);
    when(repositorioEventoMock.tieneServicio(10L, 6L)).thenReturn(true);

    servicioEvento.agregarServicio(evento, 6L);

    verify(repositorioEventoMock, never()).agregarServicio(any(), any());
  }

  @Test
  void deberiaRechazarUnSalonInexistente() {
    Evento evento = this.dadoQueExisteUnEventoConId(10L);
    when(repositorioSalonMock.buscarPorId(99L)).thenReturn(null);

    assertThrows(ElementoInexistenteException.class, () -> servicioEvento.elegirSalon(evento, 99L));
    assertThat(evento.getSalon(), is(nullValue()));
    verify(repositorioEventoMock, never()).actualizar(any());
  }

  @Test
  void deberiaRechazarUnCateringInexistente() {
    Evento evento = this.dadoQueExisteUnEventoConId(10L);
    when(repositorioCateringMock.buscarPorId(99L)).thenReturn(null);

    assertThrows(
      ElementoInexistenteException.class,
      () -> servicioEvento.elegirCatering(evento, 99L)
    );
    assertThat(evento.getCatering(), is(nullValue()));
    verify(repositorioEventoMock, never()).actualizar(any());
  }

  @Test
  void deberiaRechazarAlAgregarUnServicioInexistente() {
    Evento evento = this.dadoQueExisteUnEventoConId(10L);
    when(repositorioServicioAdicionalMock.buscarPorId(99L)).thenReturn(null);

    assertThrows(
      ElementoInexistenteException.class,
      () -> servicioEvento.agregarServicio(evento, 99L)
    );
    verify(repositorioEventoMock, never()).agregarServicio(any(), any());
  }

  @Test
  void deberiaRechazarQuitarUnServicioQueElEventoNoTiene() {
    Evento evento = this.dadoQueExisteUnEventoConId(10L);
    when(repositorioEventoMock.tieneServicio(10L, 99L)).thenReturn(false);

    assertThrows(
      ElementoInexistenteException.class,
      () -> servicioEvento.quitarServicio(evento, 99L)
    );
    verify(repositorioEventoMock, never()).quitarServicio(anyLong(), anyLong());
  }

  @Test
  void deberiaAsignarAlEventoElSalonElegidoYGuardarlo() {
    Salon salon = new Salon("Salon Los Alamos", 50000.0);
    Evento evento = this.dadoQueExisteUnEventoConId(10L);
    when(repositorioSalonMock.buscarPorId(1L)).thenReturn(salon);

    servicioEvento.elegirSalon(evento, 1L);

    assertThat(evento.getSalon(), equalTo(salon));
    verify(repositorioEventoMock).actualizar(evento);
  }

  @Test
  void deberiaAsignarAlEventoElCateringElegidoYGuardarlo() {
    Catering catering = new Catering("Catering completo", 5000.0);
    Evento evento = this.dadoQueExisteUnEventoConId(10L);
    when(repositorioCateringMock.buscarPorId(1L)).thenReturn(catering);

    servicioEvento.elegirCatering(evento, 1L);

    assertThat(evento.getCatering(), equalTo(catering));
    verify(repositorioEventoMock).actualizar(evento);
  }

  @Test
  void deberiaAgregarAlEventoServicioAdicionalElegido() {
    ServicioAdicional dj = new ServicioAdicional("DJ", 40000.0, false);
    Evento evento = this.dadoQueExisteUnEventoConId(10L);
    when(repositorioServicioAdicionalMock.buscarPorId(1L)).thenReturn(dj);
    when(repositorioEventoMock.tieneServicio(10L, 1L)).thenReturn(false);

    servicioEvento.agregarServicio(evento, 1L);

    verify(repositorioEventoMock).agregarServicio(evento, dj);
  }

  @Test
  void deberiaQuitarSoloElServicioIndicado() {
    Evento evento = this.dadoQueExisteUnEventoConId(10L);
    when(repositorioEventoMock.tieneServicio(10L, 5L)).thenReturn(true);

    servicioEvento.quitarServicio(evento, 5L);

    verify(repositorioEventoMock).quitarServicio(10L, 5L);
    verify(repositorioEventoMock, never()).quitarServicio(10L, 6L);
  }

  @Test
  void alElegirOtroSalonReemplazaAlAnterior() {
    Salon chico = new Salon("Salon Los Alamos", 50000.0);
    Salon grande = new Salon("Salon Gran Bahia", 150000.0);
    Evento evento = this.dadoQueExisteUnEventoConId(10L);
    when(repositorioSalonMock.buscarPorId(1L)).thenReturn(chico);
    when(repositorioSalonMock.buscarPorId(2L)).thenReturn(grande);

    servicioEvento.elegirSalon(evento, 1L);
    servicioEvento.elegirSalon(evento, 2L);

    assertThat(evento.getSalon(), equalTo(grande));
  }

  @Test
  void alCrearUnEventoDesdeElArmadoAutomaticoDeberiaGuardarloConElSalonYElCateringElegidos() {
    Salon salon = new Salon("Salon Los Alamos", 80000.0);
    Catering catering = new Catering("Catering basico", 3000.0);
    ResultadoArmado resultado = new ResultadoArmado(
      TipoResultado.ARMADO,
      salon,
      catering,
      List.of()
    );
    LocalDate fecha = LocalDate.now().plusMonths(2);

    Evento evento = servicioEvento.crearEventoDesdeArmadoAutomatico(
      TipoPaquete.CUMPLEANIOS,
      fecha,
      30,
      resultado
    );

    verify(repositorioEventoMock).guardar(evento);
    assertThat(evento.getTipoPaquete(), equalTo(TipoPaquete.CUMPLEANIOS));
    assertThat(evento.getFecha(), equalTo(fecha));
    assertThat(evento.getCantidadInvitados(), equalTo(30));
    assertThat(evento.getSalon(), equalTo(salon));
    assertThat(evento.getCatering(), equalTo(catering));
    assertThat(evento.getPaqueteOrigen(), is(nullValue()));
  }

  @Test
  void alCrearUnEventoDesdeElArmadoAutomaticoDeberiaAgregarCadaServicioElegido() {
    ServicioAdicional animacion = new ServicioAdicional(
      "Animacion",
      20000.0,
      false,
      CategoriaServicio.ANIMACION
    );
    ServicioAdicional dj = new ServicioAdicional("DJ", 15000.0, false, CategoriaServicio.DJ);
    ResultadoArmado resultado = new ResultadoArmado(
      TipoResultado.ARMADO,
      new Salon("Salon Los Alamos", 80000.0),
      new Catering("Catering basico", 3000.0),
      List.of(animacion, dj)
    );

    Evento evento = servicioEvento.crearEventoDesdeArmadoAutomatico(
      TipoPaquete.CUMPLEANIOS,
      LocalDate.now().plusMonths(2),
      30,
      resultado
    );

    verify(repositorioEventoMock).agregarServicio(evento, animacion);
    verify(repositorioEventoMock).agregarServicio(evento, dj);
  }

  @Test
  void alCrearUnEventoDesdeElArmadoAutomaticoSinServiciosNoDeberiaAgregarNinguno() {
    ResultadoArmado resultado = new ResultadoArmado(
      TipoResultado.ARMADO,
      new Salon("Salon Los Alamos", 80000.0),
      new Catering("Catering basico", 3000.0),
      List.of()
    );

    servicioEvento.crearEventoDesdeArmadoAutomatico(
      TipoPaquete.CUMPLEANIOS,
      LocalDate.now().plusMonths(2),
      30,
      resultado
    );

    verify(repositorioEventoMock, never()).agregarServicio(any(), any());
  }

  @Test
  void noDeberiaCrearUnEventoSiElResultadoDelArmadoNoFueExitoso() {
    ResultadoArmado resultado = new ResultadoArmado(TipoResultado.SIN_DISPONIBILIDAD, null);

    assertThrows(
      IllegalArgumentException.class,
      () ->
        servicioEvento.crearEventoDesdeArmadoAutomatico(
          TipoPaquete.CUMPLEANIOS,
          LocalDate.now().plusMonths(2),
          30,
          resultado
        )
    );
    verify(repositorioEventoMock, never()).guardar(any());
  }

  @Test
  void alAplicarUnArmadoDeberiaAsignarAlEventoElSalonYElCateringElegidos() {
    Evento evento = this.dadoQueExisteUnEventoConId(10L);
    Salon salon = new Salon("Salon Los Alamos", 80000.0);
    salon.setId(1L);
    Catering catering = new Catering("Catering basico", 3000.0);
    catering.setId(2L);
    when(repositorioSalonMock.buscarPorId(1L)).thenReturn(salon);
    when(repositorioCateringMock.buscarPorId(2L)).thenReturn(catering);
    ResultadoArmado resultado = new ResultadoArmado(
      TipoResultado.ARMADO,
      salon,
      catering,
      List.of()
    );

    servicioEvento.aplicarArmado(evento, resultado);

    assertThat(evento.getSalon(), equalTo(salon));
    assertThat(evento.getCatering(), equalTo(catering));
  }

  @Test
  void alAplicarUnArmadoDeberiaReemplazarLosServiciosQueTeniaElEvento() {
    Evento evento = this.dadoQueExisteUnEventoConId(10L);
    Salon salon = new Salon("Salon Los Alamos", 80000.0);
    salon.setId(1L);
    Catering catering = new Catering("Catering basico", 3000.0);
    catering.setId(2L);
    ServicioAdicional viejo = new ServicioAdicional("DJ", 40000.0, false);
    viejo.setId(5L);
    ServicioAdicional nuevo = new ServicioAdicional(
      "Animacion",
      20000.0,
      false,
      CategoriaServicio.ANIMACION
    );
    nuevo.setId(6L);
    when(repositorioSalonMock.buscarPorId(1L)).thenReturn(salon);
    when(repositorioCateringMock.buscarPorId(2L)).thenReturn(catering);
    when(repositorioEventoMock.buscarServicio(10L)).thenReturn(List.of(viejo));
    when(repositorioEventoMock.tieneServicio(10L, 5L)).thenReturn(true);
    when(repositorioServicioAdicionalMock.buscarPorId(6L)).thenReturn(nuevo);
    ResultadoArmado resultado = new ResultadoArmado(
      TipoResultado.ARMADO,
      salon,
      catering,
      List.of(nuevo)
    );

    servicioEvento.aplicarArmado(evento, resultado);

    verify(repositorioEventoMock).quitarServicio(10L, 5L);
    verify(repositorioEventoMock).agregarServicio(evento, nuevo);
  }

  @Test
  void noDeberiaAplicarUnArmadoQueNoFueExitoso() {
    Evento evento = this.dadoQueExisteUnEventoConId(10L);
    ResultadoArmado resultado = new ResultadoArmado(TipoResultado.SIN_DISPONIBILIDAD, null);

    assertThrows(
      IllegalArgumentException.class,
      () -> servicioEvento.aplicarArmado(evento, resultado)
    );
    verify(repositorioEventoMock, never()).actualizar(any());
  }

  // ---------- Volver al paquete original ----------

  @Test
  void alRestaurarElPaqueteDeberiaQuitarLosServiciosQueAgregoElUsuario() {
    // Preparación
    Evento evento = dadoQueExisteUnEventoDePaqueteConId(10L);
    ServicioAdicional dj = servicioConId(1L, "DJ");
    ServicioAdicional cotillon = servicioConId(2L, "Cotillon");
    when(repositorioEventoMock.buscarServicio(10L)).thenReturn(List.of(dj, cotillon));

    // Ejecución
    servicioEvento.restaurarServiciosDelPaquete(evento, List.of(dj));

    // Validación
    verify(repositorioEventoMock).quitarServicio(10L, 2L);
    verify(repositorioEventoMock, never()).quitarServicio(10L, 1L);
    verify(repositorioEventoMock, never()).agregarServicio(any(), any());
  }

  @Test
  void alRestaurarElPaqueteDeberiaVolverAAgregarLosServiciosQueQuitoElUsuario() {
    // Preparación
    Evento evento = dadoQueExisteUnEventoDePaqueteConId(10L);
    ServicioAdicional dj = servicioConId(1L, "DJ");
    ServicioAdicional fotografia = servicioConId(2L, "Fotografia");
    when(repositorioEventoMock.buscarServicio(10L)).thenReturn(List.of(dj));

    // Ejecución
    servicioEvento.restaurarServiciosDelPaquete(evento, List.of(dj, fotografia));

    // Validación
    verify(repositorioEventoMock).agregarServicio(evento, fotografia);
    verify(repositorioEventoMock, never()).quitarServicio(anyLong(), anyLong());
  }

  @Test
  void alRestaurarSinCambiosNoDeberiaTocarLosServicios() {
    // Preparación
    Evento evento = dadoQueExisteUnEventoDePaqueteConId(10L);
    ServicioAdicional dj = servicioConId(1L, "DJ");
    when(repositorioEventoMock.buscarServicio(10L)).thenReturn(List.of(dj));

    // Ejecución
    servicioEvento.restaurarServiciosDelPaquete(evento, List.of(dj));

    // Validación
    verify(repositorioEventoMock, never()).agregarServicio(any(), any());
    verify(repositorioEventoMock, never()).quitarServicio(anyLong(), anyLong());
  }

  private Evento dadoQueExisteUnEventoDePaqueteConId(Long id) {
    Evento evento = dadoQueExisteUnEventoConId(id);
    evento.setPaqueteOrigen(dadoQueExisteUnPaqueteConId(1L));
    return evento;
  }

  private ServicioAdicional servicioConId(Long id, String nombre) {
    ServicioAdicional servicio = new ServicioAdicional(nombre, 10000.0, false);
    servicio.setId(id);
    return servicio;
  }

  // [REFACTOR EVENTO] Los servicios se guardan en EventoServicio, que necesita el id del
  // evento: por eso los eventos de estos tests tienen id (como si ya estuvieran guardados).
  private Evento dadoQueExisteUnEventoConId(Long id) {
    Evento evento = new Evento(TipoPaquete.CASAMIENTO, LocalDate.now(), 80);
    evento.setId(id);
    return evento;
  }

  private Paquete dadoQueExisteUnPaqueteConId(Long id) {
    return new Paquete(
      id,
      "Básico",
      TipoPaquete.CUMPLEANIOS,
      new Salon("Salon Los Alamos", 50000.0),
      new Catering("Catering Basico", 1500.0),
      150000.0
    );
  }
}
