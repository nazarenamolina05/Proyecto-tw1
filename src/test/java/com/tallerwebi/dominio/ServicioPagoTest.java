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

import com.tallerwebi.dominio.excepcion.ReservaNoEncontrada;
import java.time.LocalDate;
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

  private Reserva dadoQueExisteUnaReservaPendiente(Usuario usuario, TipoPago tipoPago) {
    Evento evento = new Evento(TipoPaquete.CUMPLEANIOS, LocalDate.now().plusDays(30), 30);
    evento.setSalon(new Salon("Salon Los Alamos", 100000.0));
    Reserva reserva = new Reserva(usuario, evento, tipoPago);

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
