package com.boleteria.io;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Registra operaciones y errores relevantes en un archivo de texto. */
public class Auditoria {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String ruta;

    public Auditoria(String ruta) {
        this.ruta = ruta;
    }

    public void registrar(String mensaje) {
        String linea = LocalDateTime.now().format(FORMATO) + " - " + mensaje + System.lineSeparator();
        try (FileWriter escritor = new FileWriter(ruta, true)) {
            escritor.write(linea);
        } catch (IOException e) {
            System.out.println("Aviso: no se pudo escribir en la auditoria (" + e.getMessage() + ")");
        }
    }
}
