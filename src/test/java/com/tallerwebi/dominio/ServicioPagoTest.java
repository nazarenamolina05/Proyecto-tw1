package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.PagoNoCoincide;
import com.tallerwebi.dominio.excepcion.PagoNoEncontrado;
import com.tallerwebi.dominio.excepcion.ReservaNoEncontrada;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ServicioPagoTest {

  private RepositorioPago repositorioPagoMock;
  private ServicioReserva servicioReservaMock;
  private PasarelaDePago pasarelaMock;
  private ServicioPago servicioPago;

  @BeforeEach
  public void init() {
    repositorioPagoMock = mock(RepositorioPago.class);
    servicioReservaMock = mock(ServicioReserva.class);
    pasarelaMock = mock(PasarelaDePago.class);
    servicioPago = new ServicioPagoImpl(repositorioPagoMock, servicioReservaMock, pasarelaMock);
  }

  @Test
  void deberiaGuardarUnPagoPendienteConLaSeniaComoMontoSiLaReservaEsDeSenia() {
    Usuario usuario = new Usuario();
    Reserva reserva = this.dadoQueExisteUnaReservaPendiente(usuario, TipoPago.SENIA);

    servicioPago.iniciarPago(10L, usuario);

    Pago pagoGuardado = this.obtenerElPagoGuardado();
    assertThat(pagoGuardado.getEstado(), equalTo(EstadoPago.PENDIENTE));
    assertThat(pagoGuardado.getTipo(), equalTo(TipoPago.SENIA));
    assertThat(pagoGuardado.getMonto(), closeTo(30000.0, 0.01));
    assertThat(pagoGuardado.getReserva(), equalTo(reserva));
  }

  @Test
  void deberiaGuardarUnPagoPendienteConElTotalComoMontoSiLaReservaEsDePagoTotal() {
    Usuario usuario = new Usuario();
    this.dadoQueExisteUnaReservaPendiente(usuario, TipoPago.TOTAL);

    servicioPago.iniciarPago(10L, usuario);

    Pago pagoGuardado = this.obtenerElPagoGuardado();
    assertThat(pagoGuardado.getTipo(), equalTo(TipoPago.TOTAL));
    assertThat(pagoGuardado.getMonto(), closeTo(100000.0, 0.01));
  }

  @Test
  void deberiaDevolverLaUrlDePagoQueEntregaLaPasarela() {
    Usuario usuario = new Usuario();
    this.dadoQueExisteUnaReservaPendiente(usuario, TipoPago.SENIA);

    String url = servicioPago.iniciarPago(10L, usuario);

    assertThat(url, equalTo("https://mercadopago.test/pagar"));
  }

  @Test
  void noDeberiaIniciarElPagoSiLaReservaNoEsDelUsuario() {
    Usuario usuario = new Usuario();
    when(servicioReservaMock.obtenerReservaDe(10L, usuario)).thenThrow(ReservaNoEncontrada.class);

    assertThrows(ReservaNoEncontrada.class, () -> servicioPago.iniciarPago(10L, usuario));
    verify(repositorioPagoMock, never()).guardar(any(Pago.class));
  }

  @Test
  void deberiaAprobarElPagoYConfirmarLaReservaSiMercadoPagoLoAprobo() {
    Pago pago = this.dadoQueExisteUnPagoPendienteDeSenia(5L);
    this.dadoQueMercadoPagoRespondeQueElPago(777L, EstadoPago.APROBADO, "5");

    servicioPago.confirmarPago(5L, 777L);

    assertThat(pago.getEstado(), equalTo(EstadoPago.APROBADO));
    assertThat(pago.getMpPaymentId(), equalTo(777L));
    assertThat(pago.getReserva().getEstado(), equalTo(EstadoReserva.SENIADA));
    assertThat(pago.getReserva().getMontoAbonado(), closeTo(30000.0, 0.01));
  }

  @Test
  void deberiaMarcarElPagoComoRechazadoYNoConfirmarLaReserva() {
    Pago pago = this.dadoQueExisteUnPagoPendienteDeSenia(5L);
    this.dadoQueMercadoPagoRespondeQueElPago(777L, EstadoPago.RECHAZADO, "5");

    servicioPago.confirmarPago(5L, 777L);

    assertThat(pago.getEstado(), equalTo(EstadoPago.RECHAZADO));
    assertThat(pago.getReserva().getEstado(), equalTo(EstadoReserva.PENDIENTE_PAGO));
  }

  @Test
  void noDeberiaCambiarNadaSiElPagoSigueEnProceso() {
    Pago pago = this.dadoQueExisteUnPagoPendienteDeSenia(5L);
    this.dadoQueMercadoPagoRespondeQueElPago(777L, EstadoPago.PENDIENTE, "5");

    servicioPago.confirmarPago(5L, 777L);

    assertThat(pago.getEstado(), equalTo(EstadoPago.PENDIENTE));
    assertThat(pago.getReserva().getEstado(), equalTo(EstadoReserva.PENDIENTE_PAGO));
  }

  @Test
  void noDeberiaConfirmarDosVecesElMismoPago() {
    Pago pago = this.dadoQueExisteUnPagoPendienteDeSenia(5L);
    pago.setEstado(EstadoPago.APROBADO);

    servicioPago.confirmarPago(5L, 777L);

    verify(pasarelaMock, never()).consultarPago(any());
  }

  @Test
  void noDeberiaConfirmarUnPagoQueNoExiste() {
    when(repositorioPagoMock.buscarPorId(5L)).thenReturn(null);

    assertThrows(PagoNoEncontrado.class, () -> servicioPago.confirmarPago(5L, 777L));
  }

  @Test
  void noDeberiaConfirmarSiElPagoDeMercadoPagoCorrespondeAOtroPago() {
    Pago pago = this.dadoQueExisteUnPagoPendienteDeSenia(5L);
    this.dadoQueMercadoPagoRespondeQueElPago(777L, EstadoPago.APROBADO, "99");

    assertThrows(PagoNoCoincide.class, () -> servicioPago.confirmarPago(5L, 777L));
    assertThat(pago.getEstado(), equalTo(EstadoPago.PENDIENTE));
  }

  private Pago dadoQueExisteUnPagoPendienteDeSenia(Long pagoId) {
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().plusDays(30), 30);
    evento.setSalon(new Salon("Salon Los Alamos", 100000.0));
    Reserva reserva = new Reserva(new Usuario(), evento, TipoPago.SENIA, List.of());

    Pago pago = new Pago();
    pago.setId(pagoId);
    pago.setReserva(reserva);
    pago.setTipo(TipoPago.SENIA);
    pago.setMonto(30000.0);
    pago.setEstado(EstadoPago.PENDIENTE);
    when(repositorioPagoMock.buscarPorId(pagoId)).thenReturn(pago);
    return pago;
  }

  private void dadoQueMercadoPagoRespondeQueElPago(
    Long mpPaymentId,
    EstadoPago estado,
    String referencia
  ) {
    when(pasarelaMock.consultarPago(mpPaymentId))
      .thenReturn(new DatosPagoExterno(estado, referencia));
  }

  private Reserva dadoQueExisteUnaReservaPendiente(Usuario usuario, TipoPago tipoPago) {
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().plusDays(30), 30);
    evento.setSalon(new Salon("Salon Los Alamos", 100000.0));
    Reserva reserva = new Reserva(usuario, evento, tipoPago, List.of());

    when(servicioReservaMock.obtenerReservaDe(10L, usuario)).thenReturn(reserva);
    when(pasarelaMock.crearPago(any(), anyString(), anyDouble()))
      .thenReturn("https://mercadopago.test/pagar");
    return reserva;
  }

  private Pago obtenerElPagoGuardado() {
    ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);
    verify(repositorioPagoMock).guardar(captor.capture());
    return captor.getValue();
  }
}
