package com.boleteria.io;

import com.boleteria.exception.ArchivoException;
import com.boleteria.model.Compra;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Lee y escribe las compras en un archivo JSON usando Jackson. */
public class ComprasRepository {

    private final ObjectMapper mapper = new ObjectMapper();
    private final String ruta;

    public ComprasRepository(String ruta) {
        this.ruta = ruta;
    }

    public List<Compra> cargar() throws ArchivoException {
        File archivo = new File(ruta);
        if (!archivo.exists() || archivo.length() == 0) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(archivo, new TypeReference<List<Compra>>() {});
        } catch (IOException e) {
            throw new ArchivoException("No se pudo leer el archivo de compras: " + ruta, e);
        }
    }

    public void guardar(List<Compra> compras) throws ArchivoException {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(ruta), compras);
        } catch (IOException e) {
            throw new ArchivoException("No se pudo guardar el archivo de compras: " + ruta, e);
        }
    }
}
