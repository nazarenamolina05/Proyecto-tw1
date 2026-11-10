package com.tallerwebi.infraestructura;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.tallerwebi.dominio.DatosPagoExterno;
import com.tallerwebi.dominio.EstadoPago;
import com.tallerwebi.dominio.PasarelaDePago;
import com.tallerwebi.dominio.excepcion.PasarelaNoDisponible;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;

@Component("pasarelaMercadoPago")
public class PasarelaMercadoPago implements PasarelaDePago {

  // El proyecto corre en Jetty con contexto "/spring"
  private static final String URL_BASE = "http://localhost:8080/spring";
  private static final String ESTADO_APROBADO = "approved";
  private static final String ESTADO_RECHAZADO = "rejected";
  private static final String ESTADO_CANCELADO = "cancelled";

  @Override
  public String crearPago(Long pagoId, String descripcion, Double monto) {
    MercadoPagoConfig.setAccessToken(this.obtenerToken());

    PreferenceItemRequest item = PreferenceItemRequest
      .builder()
      .title(descripcion)
      .quantity(1)
      .currencyId("ARS")
      .unitPrice(BigDecimal.valueOf(monto).setScale(2, RoundingMode.HALF_UP))
      .build();

    PreferenceBackUrlsRequest urlsDeRetorno = PreferenceBackUrlsRequest
      .builder()
      .success(URL_BASE + "/pago/exito")
      .failure(URL_BASE + "/pago/fallo")
      .pending(URL_BASE + "/pago/pendiente")
      .build();

    PreferenceRequest pedido = PreferenceRequest
      .builder()
      .items(List.of(item))
      .backUrls(urlsDeRetorno)
      .externalReference(String.valueOf(pagoId))
      .build();

    try {
      Preference preferencia = new PreferenceClient().create(pedido);
      return preferencia.getInitPoint();
    } catch (MPApiException e) {
      throw new PasarelaNoDisponible(
        "Mercado Pago rechazó la solicitud: " + e.getApiResponse().getContent(),
        e
      );
    } catch (MPException e) {
      throw new PasarelaNoDisponible("No se pudo conectar con Mercado Pago", e);
    }
  }

  @Override
  public DatosPagoExterno consultarPago(Long mpPaymentId) {
    MercadoPagoConfig.setAccessToken(this.obtenerToken());

    try {
      Payment pago = new PaymentClient().get(mpPaymentId);
      return new DatosPagoExterno(
        this.traducirEstado(pago.getStatus()),
        pago.getExternalReference()
      );
    } catch (MPApiException e) {
      throw new PasarelaNoDisponible(
        "Mercado Pago rechazó la consulta: " + e.getApiResponse().getContent(),
        e
      );
    } catch (MPException e) {
      throw new PasarelaNoDisponible("No se pudo conectar con Mercado Pago", e);
    }
  }

  private EstadoPago traducirEstado(String estadoMercadoPago) {
    if (ESTADO_APROBADO.equals(estadoMercadoPago)) {
      return EstadoPago.APROBADO;
    }
    if (ESTADO_RECHAZADO.equals(estadoMercadoPago) || ESTADO_CANCELADO.equals(estadoMercadoPago)) {
      return EstadoPago.RECHAZADO;
    }
    return EstadoPago.PENDIENTE;
  }

  private String obtenerToken() {
    String token = System.getenv("MP_ACCESS_TOKEN");
    if (token == null || token.isBlank()) {
      throw new PasarelaNoDisponible("Falta la variable de entorno MP_ACCESS_TOKEN");
    }
    return token;
  }
}
