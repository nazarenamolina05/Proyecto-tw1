package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioArmadoAutomaticoTest {

  private static final LocalDate FECHA = LocalDate.now().plusMonths(2);
  private static final Integer INVITADOS = 30;
  private static final Double PRESUPUESTO = 200000.0;

  private ServicioDisponibilidadSalon servicioDisponibilidadSalonMock;
  private RepositorioCatering repositorioCateringMock;
  private RepositorioServicioAdicional repositorioServicioAdicionalMock;
  private ServicioArmadoAutomatico servicioArmadoAutomatico;

  @BeforeEach
  public void initi() {
    servicioDisponibilidadSalonMock = mock(ServicioDisponibilidadSalon.class);
    repositorioCateringMock = mock(RepositorioCatering.class);
    repositorioServicioAdicionalMock = mock(RepositorioServicioAdicional.class);
    servicioArmadoAutomatico =
      new ServicioArmadoAutomaticoImpl(
        servicioDisponibilidadSalonMock,
        repositorioCateringMock,
        repositorioServicioAdicionalMock
      );
  }

  @Test
  public void deberiaInformarSinDisponibilidadSiTodosLosSalonesEstanOcupados() {
    //preparcion
    conSalonesLibres();
    conCaterings(new Catering("Catering basico", 3000.0));

    //resultado
    ResultadoArmado resultado = armarConPrioridadSalon();

    //validacion
    assertThat(resultado.getTipoResultado(), equalTo(TipoResultado.SIN_DISPONIBILIDAD));
  }

  @Test
  public void deberiaInformarSinDisponibilidadSiNingunSalonLibreTieneCapacidadParaLosInvitados() {
    //preparacion
    conSalonesLibres(unSalon("Salon chico", 80000.0, 20));
    conCaterings(new Catering("Catering basico", 3000.0));

    //ejecucion
    ResultadoArmado resultado = armarConPrioridadSalon();

    assertThat(resultado.getTipoResultado(), equalTo(TipoResultado.SIN_DISPONIBILIDAD));
  }

  @Test
  public void deberiaInformarSinDisponibilidadSiNoHayCatering() {
    conSalonesLibres(unSalon("Salon grande", 80000.0, 50));
    conCaterings();

    ResultadoArmado resultado = armarConPrioridadCatering(200000.0);

    assertThat(resultado.getTipoResultado(), equalTo(TipoResultado.SIN_DISPONIBILIDAD));
    verify(repositorioCateringMock).obtenerTodos();
  }

  @Test
  public void deberiaDescartarElSalonSinCapacidadCargadaSinRomper() {
    conSalonesLibres(unSalon("Salon chico", 80000.0, null));
    conCaterings(new Catering("Catering basico", 3000.0));

    ResultadoArmado resultado = armarConPrioridadSalon();

    assertThat(resultado.getTipoResultado(), equalTo(TipoResultado.SIN_DISPONIBILIDAD));
  }

  @Test
  public void deberiaInformarPresupuestoInsuficienteConElMontoMinimoSiNoAlcanzaParaSalonYCatering() {
    conSalonesLibres(unSalon("Salon grande", 150000.0, 100), unSalon("Salon chico", 80000.0, 50));

    conCaterings(new Catering("Catering Premium", 5000.0), new Catering("Catering basico", 3000.0));

    ResultadoArmado resultadoArmado = armarConPrioridadSalon(160000.0);

    assertThat(resultadoArmado.getTipoResultado(), equalTo(TipoResultado.PRESUPUESTO_INSUFICIENTE));
    assertThat(resultadoArmado.getMontoMinimo(), equalTo(170000.0));
  }

  @Test
  public void deberiaArmarElEventoSiElPresupuestoEsExactamenteElMinimo() {
    conSalonesLibres(unSalon("Salon grande", 150000.0, 100), unSalon("Salon chico", 80000.0, 50));

    conCaterings(new Catering("Catering Premium", 5000.0), new Catering("Catering basico", 3000.0));

    ResultadoArmado resultadoArmado = armarConPrioridadSalon(170000.0);

    assertThat(resultadoArmado.getTipoResultado(), equalTo(TipoResultado.ARMADO));
  }

  //Test 7
  @Test
  public void deberiaElegirElSalonMasCaroQueDejaLugarAlCateringMasBaratoSiPriorizaSalon() {
    // Preparación
    Salon salonGrande = unSalon("Salon grande", 150000.0, 60);
    Salon salonChico = unSalon("Salon chico", 80000.0, 50);
    Catering cateringPremium = new Catering("Catering Premium", 5000.0);
    Catering cateringBasico = new Catering("Catering basico", 3000.0);

    conSalonesLibres(salonGrande, salonChico);
    conCaterings(cateringPremium, cateringBasico);

    // Ejecución
    ResultadoArmado resultado = armarConPrioridadSalon(250000.0);

    // Validación
    assertThat(resultado.getTipoResultado(), equalTo(TipoResultado.ARMADO));
    assertThat(resultado.getSalon(), equalTo(salonGrande));
    assertThat(resultado.getCatering(), equalTo(cateringBasico));
  }

  //Test 8
  @Test
  public void deberiaElegirElCateringMasCaroQueDejaLugarAlSalonMasBaratoSiPriorizaCatering() {
    // Preparación
    Salon salonGrande = unSalon("Salon grande", 150000.0, 100);
    Salon salonChico = unSalon("Salon chico", 80000.0, 50);
    Catering cateringPremium = new Catering("Catering Premium", 5000.0);
    Catering cateringBasico = new Catering("Catering basico", 3000.0);

    conSalonesLibres(salonGrande, salonChico);
    conCaterings(cateringPremium, cateringBasico);

    // Ejecución
    ResultadoArmado resultado = armarConPrioridadCatering(250000.0);

    // Validación
    assertThat(resultado.getTipoResultado(), equalTo(TipoResultado.ARMADO));
    assertThat(resultado.getSalon(), equalTo(salonChico));
    assertThat(resultado.getCatering(), equalTo(cateringPremium));
  }

  @Test
  public void noDeberiaElegirUnSalonConCapacidadMuyMayorALaNecesariaAunqueElPresupuestoAlcance() {
    Salon salonEnorme = unSalon("Salon enorme", 150000.0, 1000);
    Salon salonChico = unSalon("Salon chico", 80000.0, 50);

    conSalonesLibres(salonEnorme, salonChico);
    conCaterings(new Catering("Catering basico", 3000.0));

    ResultadoArmado resultadoSalon = armarConPrioridadSalon(250000.0);
    ResultadoArmado resultadoCatering = armarConPrioridadCatering(250000.0);

    assertThat(resultadoSalon.getSalon(), equalTo(salonChico));
    assertThat(resultadoCatering.getSalon(), equalTo(salonChico));
  }

  // Test 8.2
  @Test
  public void deberiaElegirElSalonMasCaroDentroDelRangoDeCapacidadRazonable() {
    Salon salonEnorme = unSalon("Salon enorme", 150000.0, 1000);
    Salon salonMediano = unSalon("Salon mediano", 120000.0, 60);
    Salon salonChico = unSalon("Salon chico", 80000.0, 50);

    conSalonesLibres(salonEnorme, salonMediano, salonChico);
    conCaterings(new Catering("Catering basico", 3000.0));

    ResultadoArmado resultado = armarConPrioridadSalon(250000.0);

    assertThat(resultado.getSalon(), equalTo(salonMediano));
  }

  // Test 8.3
  @Test
  public void deberiaElegirElSalonDeMenorCapacidadSiTodosLosQueEntranEnElPresupuestoSonDemasiadoGrandes() {
    Salon salonEnorme = unSalon("Salon enorme", 150000.0, 1000);
    Salon salonGrande = unSalon("Salon grande", 100000.0, 500);

    conSalonesLibres(salonEnorme, salonGrande);
    conCaterings(new Catering("Catering basico", 3000.0));

    ResultadoArmado resultado = armarConPrioridadSalon(250000.0);

    assertThat(resultado.getTipoResultado(), equalTo(TipoResultado.ARMADO));
    assertThat(resultado.getSalon(), equalTo(salonGrande));
  }

  // Test 8.4: el tope de capacidad NO debe afectar al monto mínimo
  @Test
  public void deberiaCalcularElMontoMinimoConUnSalonGrandeSiEsElUnicoConCapacidadSuficiente() {
    conSalonesLibres(unSalon("Salon enorme", 150000.0, 1000));
    conCaterings(new Catering("Catering basico", 3000.0));

    MontoMinimo minimo = servicioArmadoAutomatico.calcularMontoMinimo(INVITADOS, FECHA);

    assertThat(minimo.getSalon(), equalTo(150000.0));
  }

  // Test 9
  @Test
  public void deberiaIncluirElServicioDeUnaCategoriaSugeridaSiEntraEnElSaldoSobrante() {
    ServicioAdicional animacion = new ServicioAdicional(
      "Amimacion",
      20000.0,
      false,
      CategoriaServicio.ANIMACION
    );

    conSalonesLibres(unSalon("Salon chico", 80000.0, 30));

    conCaterings(new Catering("Catering basico", 3000.0));

    conServicios(animacion);

    ResultadoArmado resultado = armarConPrioridadSalon(200000.0);

    assertThat(resultado.getServicioAdicional(), contains(animacion));
  }

  // Test 10
  @Test
  public void deberiaElegirElServicioMasBaratoDeLaCategoria() {
    ServicioAdicional animacionCara = unServicio(
      "Animacion cara",
      20000.0,
      false,
      CategoriaServicio.ANIMACION
    );
    ServicioAdicional animacionBarata = unServicio(
      "Animacion barata",
      10000.0,
      false,
      CategoriaServicio.ANIMACION
    );

    conSalonYCateringBase();
    conServicios(animacionCara, animacionBarata);

    ResultadoArmado resultado = armarConPrioridadSalon();

    assertThat(resultado.getServicioAdicional(), contains(animacionBarata));
  }

  // Test 11
  @Test
  public void deberiaCompararLosServiciosPorSuCostoRealYNoPorSuPrecioUnitario() {
    // por invitado: $1.000 x 30 = $30.000 de costo real
    ServicioAdicional porInvitado = unServicio(
      "Cotillon por invitado",
      1000.0,
      true,
      CategoriaServicio.COTILLON
    );
    ServicioAdicional fijo = unServicio(
      "Cotillon fijo",
      25000.0,
      false,
      CategoriaServicio.COTILLON
    );

    conSalonYCateringBase();
    conServicios(porInvitado, fijo);

    ResultadoArmado resultado = armarConPrioridadSalon();

    assertThat(resultado.getServicioAdicional(), contains(fijo));
  }

  // Test 12
  @Test
  public void deberiaOmitirElServicioSiSuCostoSuperaElSaldoLibre() {
    conSalonYCateringBase();
    conServicios(unServicio("Animacion premium", 40000.0, false, CategoriaServicio.ANIMACION));

    ResultadoArmado resultado = armarConPrioridadSalon();

    assertThat(resultado.getTipoResultado(), equalTo(TipoResultado.ARMADO));
    assertThat(resultado.getServicioAdicional(), equalTo(List.of()));
  }

  // Test 13
  @Test
  public void deberiaQuedarseConLaCategoriaMasBarataSiElSaldoNoAlcanzaParaTodas() {
    ServicioAdicional animacion = unServicio(
      "Animacion",
      20000.0,
      false,
      CategoriaServicio.ANIMACION
    );
    ServicioAdicional cotillon = unServicio("Cotillon", 15000.0, false, CategoriaServicio.COTILLON);

    conSalonYCateringBase();
    conServicios(animacion, cotillon);

    ResultadoArmado resultado = armarConPrioridadSalon();

    // ANIMACION va primero en la lista de sugeridas, pero manda el precio
    assertThat(resultado.getServicioAdicional(), contains(cotillon));
  }

  // Test 14
  @Test
  public void deberiaNoAgregarServiciosSiNoSobraPresupuesto() {
    conSalonYCateringBase();
    conServicios(unServicio("Animacion", 20000.0, false, CategoriaServicio.ANIMACION));

    ResultadoArmado resultado = armarConPrioridadSalon(170000.0);

    assertThat(resultado.getTipoResultado(), equalTo(TipoResultado.ARMADO));
    assertThat(resultado.getServicioAdicional(), equalTo(List.of()));
  }

  // Test 15
  @Test
  public void deberiaAgregarUnServicioDeCategoriaNoSugeridaConLoQueSobra() {
    ServicioAdicional fotografia = unServicio(
      "Fotografia",
      20000.0,
      false,
      CategoriaServicio.FOTOGRAFIA
    );

    conSalonYCateringBase();
    conServicios(fotografia);

    ResultadoArmado resultado = armarConPrioridadSalon();

    assertThat(resultado.getServicioAdicional(), contains(fotografia));
  }

  // Test 16
  @Test
  public void deberiaPriorizarLasCategoriasSugeridasSobreLasNoSugeridas() {
    ServicioAdicional cotillon = unServicio("Cotillon", 25000.0, false, CategoriaServicio.COTILLON);
    ServicioAdicional fotografia = unServicio(
      "Fotografia",
      10000.0,
      false,
      CategoriaServicio.FOTOGRAFIA
    );

    conSalonYCateringBase();
    conServicios(cotillon, fotografia);

    ResultadoArmado resultado = armarConPrioridadSalon();

    // Aunque la fotografía es más barata, la sugerida entra primero y deja $5.000
    assertThat(resultado.getServicioAdicional(), contains(cotillon));
  }

  // Test 17
  @Test
  public void deberiaIgnorarLosServiciosSinCategoriaCargada() {
    ServicioAdicional sinCategoria = new ServicioAdicional("Sin categoria", 5000.0, false);

    conSalonYCateringBase();
    conServicios(sinCategoria);

    ResultadoArmado resultado = armarConPrioridadSalon();

    assertThat(resultado.getTipoResultado(), equalTo(TipoResultado.ARMADO));
    assertThat(resultado.getServicioAdicional(), equalTo(List.of()));
  }

  // Test 18
  @Test
  public void deberiaAgregarVariosServiciosDeMenorAMayorCostoSiHaySaldo() {
    ServicioAdicional animacion = unServicio(
      "Animacion",
      20000.0,
      false,
      CategoriaServicio.ANIMACION
    );
    ServicioAdicional cotillon = unServicio("Cotillon", 15000.0, false, CategoriaServicio.COTILLON);

    conSalonYCateringBase();
    conServicios(animacion, cotillon);

    ResultadoArmado resultado = armarConPrioridadSalon(220000.0); // saldo $50.000

    assertThat(resultado.getServicioAdicional(), contains(cotillon, animacion));
  }

  @Test
  public void deberiaCalcularElMontoMinimoConElSalonYElCateringMasBaratos() {
    conSalonesLibres(unSalon("Salon grande", 150000.0, 100), unSalon("Salon chico", 80000.0, 50));
    conCaterings(new Catering("Catering Premium", 5000.0), new Catering("Catering basico", 3000.0));

    MontoMinimo minimo = servicioArmadoAutomatico.calcularMontoMinimo(INVITADOS, FECHA);

    assertThat(minimo.getSalon(), equalTo(80000.0));
    assertThat(minimo.getCatering(), equalTo(90000.0));
    assertThat(minimo.getTotal(), equalTo(170000.0));
  }

  @Test
  public void noDeberiaConsiderarParaElMinimoUnSalonSinCapacidadSuficiente() {
    conSalonesLibres(unSalon("Salon grande", 150000.0, 100), unSalon("Salon chico", 80000.0, 20));
    conCaterings(new Catering("Catering basico", 3000.0));

    MontoMinimo minimo = servicioArmadoAutomatico.calcularMontoMinimo(INVITADOS, FECHA);

    assertThat(minimo.getSalon(), equalTo(150000.0));
  }

  @Test
  public void noDeberiaCalcularElMontoMinimoSiNoHaySalonesDisponibles() {
    conSalonesLibres();
    conCaterings(new Catering("Catering basico", 3000.0));

    MontoMinimo minimo = servicioArmadoAutomatico.calcularMontoMinimo(INVITADOS, FECHA);

    assertThat(minimo, nullValue());
  }

  @Test
  public void noDeberiaCalcularElMontoMinimoSiNoHayCaterings() {
    conSalonesLibres(unSalon("Salon grande", 150000.0, 50));
    conCaterings();

    MontoMinimo minimo = servicioArmadoAutomatico.calcularMontoMinimo(INVITADOS, FECHA);

    assertThat(minimo, nullValue());
  }

  private ResultadoArmado armarConPrioridadSalon() {
    return armarConPrioridadSalon(PRESUPUESTO);
  }

  private ResultadoArmado armarConPrioridadSalon(Double presupuesto) {
    return servicioArmadoAutomatico.armar(
      TipoPaquete.CUMPLEANIOS,
      INVITADOS,
      FECHA,
      presupuesto,
      PrioridadArmado.SALON
    );
  }

  private ResultadoArmado armarConPrioridadCatering(Double presupuesto) {
    return servicioArmadoAutomatico.armar(
      TipoPaquete.CUMPLEANIOS,
      INVITADOS,
      FECHA,
      presupuesto,
      PrioridadArmado.CATERING
    );
  }

  private Salon unSalon(String nombre, Double precio, Integer capacidad) {
    Salon salon = new Salon(nombre, precio);
    salon.setCapacidad(capacidad);
    return salon;
  }

  private void conSalonesLibres(Salon... salones) {
    when(servicioDisponibilidadSalonMock.obtenerSalonesDisponibles(FECHA, FECHA))
      .thenReturn(List.of(salones));
  }

  private void conCaterings(Catering... caterings) {
    when(repositorioCateringMock.obtenerTodos()).thenReturn(List.of(caterings));
  }

  private void conServicios(ServicioAdicional... servicios) {
    when(repositorioServicioAdicionalMock.obtenerTodos()).thenReturn(List.of(servicios));
  }

  private ServicioAdicional unServicio(
    String nombre,
    Double precio,
    Boolean porInvitado,
    CategoriaServicio categoria
  ) {
    return new ServicioAdicional(nombre, precio, porInvitado, categoria);
  }

  // Salón $80.000 + catering $3.000 x 30 = $170.000. Con $200.000 de presupuesto el saldo es $30.000
  private void conSalonYCateringBase() {
    conSalonesLibres(unSalon("Salon chico", 80000.0, 50));
    conCaterings(new Catering("Catering basico", 3000.0));
  }
}
