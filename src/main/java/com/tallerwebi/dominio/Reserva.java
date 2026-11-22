package com.tallerwebi.dominio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Reserva {

  public static final double PORCENTAJE_SENIA = 0.30;
  private static final double TOLERANCIA = 0.01;
  private static final int CIEN_POR_CIENTO = 100;

  public Reserva() {}

  public static Double calcularSenia(Double presupuesto) {
    return presupuesto * PORCENTAJE_SENIA;
  }

  // [REFACTOR EVENTO] Recibe los servicios del evento para calcular el presupuesto,
  // porque Evento ya no tiene la lista.
  public Reserva(
    Usuario usuario,
    Evento evento,
    TipoPago tipoPago,
    List<ServicioAdicional> serviciosDelEvento
  ) {
    this.usuario = usuario;
    this.evento = evento;
    this.tipoPago = tipoPago;
    this.presupuesto = evento.calcularPresupuesto(serviciosDelEvento);
    this.estado = EstadoReserva.PENDIENTE_PAGO;
    this.montoAbonado = 0.0;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Usuario usuario;

  // [REFACTOR EVENTO] Sin cascade: el evento ya se guarda al crearlo (ServicioEvento),
  // así que cuando se reserva ya existe en la base.
  @OneToOne(optional = false)
  private Evento evento;

  private Double presupuesto;

  @Enumerated(EnumType.STRING)
  private EstadoReserva estado;

  @Enumerated(EnumType.STRING)
  private TipoPago tipoPago;

  private Double montoAbonado;

  // Datos de contacto que carga el usuario al reservar.
  // cascade PERSIST: al guardar la reserva se guarda también su contacto
  // (el contacto se crea junto con la reserva y no existe sin ella).
  @OneToOne(cascade = CascadeType.PERSIST)
  private ContactoReserva contacto;

  public Double calcularSaldoPendiente() {
    if (presupuesto == null || montoAbonado == null) {
      return 0.0;
    }
    return Math.max(0.0, presupuesto - montoAbonado);
  }

  public Double calcularMontoAPagar() {
    return tipoPago == TipoPago.SENIA ? calcularSenia(presupuesto) : presupuesto;
  }

  public void registrarPago(Double monto) {
    double abonadoHastaAhora = montoAbonado == null ? 0.0 : montoAbonado;
    this.montoAbonado = abonadoHastaAhora + monto;
    this.estado =
      calcularSaldoPendiente() < TOLERANCIA ? EstadoReserva.CONFIRMADA : EstadoReserva.SENIADA;
  }

  public boolean permiteAbonos() {
    return estado == EstadoReserva.SENIADA && !evento.getFecha().isBefore(LocalDate.now());
  }

  public boolean admiteAbonoDe(Double monto) {
    return monto != null && monto > 0 && monto <= calcularSaldoPendiente() + TOLERANCIA;
  }

  // Se puede reseñar cuando el evento ya ocurrió y la reserva llegó a pagarse
  // (una reserva pendiente de pago o cancelada nunca se realizó).
  public boolean admiteResenia() {
    return (
      evento != null &&
      evento.yaOcurrio() &&
      (estado == EstadoReserva.SENIADA ||
        estado == EstadoReserva.CONFIRMADA ||
        estado == EstadoReserva.FINALIZADA)
    );
  }

  public int calcularPorcentajeAbonado() {
    if (presupuesto == null || presupuesto <= 0) {
      return 0;
    }
    double abonado = montoAbonado == null ? 0.0 : montoAbonado;
    return (int) Math.min(CIEN_POR_CIENTO, Math.round((abonado * CIEN_POR_CIENTO) / presupuesto));
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Evento getEvento() {
    return evento;
  }

  public void setEvento(Evento evento) {
    this.evento = evento;
  }

  public Double getPresupuesto() {
    return presupuesto;
  }

  public void setPresupuesto(Double presupuesto) {
    this.presupuesto = presupuesto;
  }

  public EstadoReserva getEstado() {
    return estado;
  }

  public void setEstado(EstadoReserva estado) {
    this.estado = estado;
  }

  public TipoPago getTipoPago() {
    return tipoPago;
  }

  public void setTipoPago(TipoPago tipoPago) {
    this.tipoPago = tipoPago;
  }

  public Double getMontoAbonado() {
    return montoAbonado;
  }

  public void setMontoAbonado(Double montoAbonado) {
    this.montoAbonado = montoAbonado;
  }

  public ContactoReserva getContacto() {
    return contacto;
  }

  public void setContacto(ContactoReserva contacto) {
    this.contacto = contacto;
  }
}
