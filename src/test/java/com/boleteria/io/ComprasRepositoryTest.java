package com.boleteria.io;

import com.boleteria.exception.ArchivoException;
import com.boleteria.model.Compra;
import com.boleteria.model.Comprador;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComprasRepositoryTest {

    @TempDir
    Path temp;

    @Test
    void guardaYCargaUnaCompraEnJson() throws ArchivoException {
        ComprasRepository repo = new ComprasRepository(temp.resolve("compras.json").toString());
        Compra compra = new Compra("CMP-001", "EV001",
                new Comprador("Laura Gomez", "123", "laura@mail.com"),
                List.of("VIP-A-01", "VIP-A-02"),
                700000, 70000, 770000, "2026-10-20 10:00:00");

        repo.guardar(new ArrayList<>(List.of(compra)));
        List<Compra> cargadas = repo.cargar();

        assertEquals(1, cargadas.size());
        assertEquals("CMP-001", cargadas.get(0).getId());
        assertEquals(770000, cargadas.get(0).getTotal(), 0.001);
        assertEquals(2, cargadas.get(0).getAsientos().size());
    }
}
