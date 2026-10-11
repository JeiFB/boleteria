package com.boleteria.io;

import com.boleteria.exception.ArchivoException;
import com.boleteria.model.Asiento;
import com.boleteria.model.Evento;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Carga eventos y asientos desde archivos CSV separados por punto y coma. */
public class CsvLoader {

    private static final String SEPARADOR = ";";

    public List<Evento> cargarEventos(String ruta) throws ArchivoException {
        List<Evento> eventos = new ArrayList<>();
        try (BufferedReader lector = Files.newBufferedReader(Path.of(ruta))) {
            String linea = lector.readLine(); // encabezado
            while ((linea = lector.readLine()) != null) {
                if (!linea.isBlank()) {
                    eventos.add(parsearEvento(linea));
                }
            }
        } catch (IOException e) {
            throw new ArchivoException("No se pudo leer el archivo de eventos: " + ruta, e);
        }
        return eventos;
    }

    public List<Asiento> cargarAsientos(String ruta) throws ArchivoException {
        List<Asiento> asientos = new ArrayList<>();
        try (BufferedReader lector = Files.newBufferedReader(Path.of(ruta))) {
            String linea = lector.readLine(); // encabezado
            while ((linea = lector.readLine()) != null) {
                if (!linea.isBlank()) {
                    asientos.add(parsearAsiento(linea));
                }
            }
        } catch (IOException e) {
            throw new ArchivoException("No se pudo leer el archivo de asientos: " + ruta, e);
        }
        return asientos;
    }

    public Evento parsearEvento(String linea) throws ArchivoException {
        String[] campos = linea.split(SEPARADOR);
        if (campos.length < 6) {
            throw new ArchivoException("Fila de evento incompleta: " + linea);
        }
        return new Evento(campos[0], campos[1], campos[2], campos[3], campos[4], campos[5]);
    }

    public Asiento parsearAsiento(String linea) throws ArchivoException {
        String[] campos = linea.split(SEPARADOR);
        if (campos.length < 6) {
            throw new ArchivoException("Fila de asiento incompleta: " + linea);
        }
        try {
            int numero = Integer.parseInt(campos[4].trim());
            double precio = Double.parseDouble(campos[5].trim());
            return new Asiento(campos[0], campos[1], campos[2], campos[3], numero, precio);
        } catch (NumberFormatException e) {
            throw new ArchivoException("Numero o precio invalido en la fila: " + linea);
        }
    }
}
