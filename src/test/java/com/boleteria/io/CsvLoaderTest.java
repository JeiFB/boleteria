package com.boleteria.io;

import com.boleteria.exception.ArchivoException;
import com.boleteria.model.Asiento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvLoaderTest {

    private final CsvLoader loader = new CsvLoader();

    @Test
    void leeFilaCsvValida() throws ArchivoException {
        Asiento asiento = loader.parsearAsiento("EV001;VIP-A-01;VIP;A;1;350000");
        assertEquals("VIP-A-01", asiento.getCodigo());
        assertEquals("VIP", asiento.getLocalidad());
        assertEquals(350000, asiento.getPrecio(), 0.001);
    }

    @Test
    void rechazaFilaConPrecioInvalido() {
        assertThrows(ArchivoException.class,
                () -> loader.parsearAsiento("EV001;VIP-A-01;VIP;A;1;precio"));
    }
}
