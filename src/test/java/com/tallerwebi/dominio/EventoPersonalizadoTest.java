package com.tallerwebi.dominio;


import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class EventoPersonalizado {



    @Test
    public void elPresupuestoDebeSerCeroSiNoHayNadaSeleccionado() {
        EventoPersonalizado evento = new EventoPersonalizado(TipoPaquete.CUMPLEANIOS);

    }

}
