package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.EventoNoFinalizado;
import com.tallerwebi.dominio.excepcion.PuntajeInvalido;
import com.tallerwebi.dominio.excepcion.ReseniaExistente;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioResenia")
@Transactional
public class ServicioReseniaImpl implements ServicioResenia {

  private ServicioReserva servicioReserva;
  private RepositorioResenia repositorioResenia;

  @Autowired
  public ServicioReseniaImpl(
    ServicioReserva servicioReserva,
    RepositorioResenia repositorioResenia
  ) {
    this.servicioReserva = servicioReserva;
    this.repositorioResenia = repositorioResenia;
  }

  @Override
  public Resenia reseniar(Long idReserva, Usuario usuario, Integer puntaje, String comentario) {
    // Reutiliza la validación de dueño: lanza ReservaNoEncontrada si no es del usuario.
    Reserva reserva = servicioReserva.obtenerReservaDe(idReserva, usuario);

    if (!reserva.admiteResenia()) {
      throw new EventoNoFinalizado();
    }
    if (!Resenia.esPuntajeValido(puntaje)) {
      throw new PuntajeInvalido();
    }
    if (repositorioResenia.buscarPorReserva(idReserva) != null) {
      throw new ReseniaExistente();
    }

    Resenia resenia = new Resenia(reserva, puntaje, normalizarComentario(comentario));
    repositorioResenia.guardar(resenia);
    return resenia;
  }

  @Override
  public Resenia obtenerReseniaDe(Long idReserva) {
    return repositorioResenia.buscarPorReserva(idReserva);
  }

  @Override
  public List<Resenia> obtenerReseniasDelPaquete(Long idPaquete) {
    return repositorioResenia.buscarPorPaquete(idPaquete);
  }

  // Devuelve null si no hay reseñas, así la vista puede distinguir "sin opiniones".
  @Override
  public Double calcularPromedio(List<Resenia> resenias) {
    if (resenias == null || resenias.isEmpty()) {
      return null;
    }
    return resenias.stream().mapToInt(Resenia::getPuntaje).average().orElse(0.0);
  }

  // El comentario es opcional: vacío se guarda como null. Se recorta al largo de la
  // columna por si alguien saltea el maxlength del formulario.
  private String normalizarComentario(String comentario) {
    if (comentario == null || comentario.isBlank()) {
      return null;
    }
    String limpio = comentario.trim();
    return limpio.length() > Resenia.LARGO_MAXIMO_COMENTARIO
      ? limpio.substring(0, Resenia.LARGO_MAXIMO_COMENTARIO)
      : limpio;
  }
}
