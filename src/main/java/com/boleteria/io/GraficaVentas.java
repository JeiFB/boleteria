package com.boleteria.io;

import com.boleteria.exception.ArchivoException;
import com.boleteria.model.ReporteEvento;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Genera una grafica de barras de ingresos por evento en un PDF. */
public class GraficaVentas {

    public String generar(List<ReporteEvento> datos, String ruta) throws ArchivoException {
        try {
            CategoryChart grafica = new CategoryChartBuilder()
                    .width(900).height(550)
                    .title("Ingresos por evento")
                    .xAxisTitle("Evento").yAxisTitle("Ingresos (COP)")
                    .build();
            grafica.getStyler().setLegendVisible(false);

            List<String> categorias = new ArrayList<>();
            List<Double> valores = new ArrayList<>();
            for (ReporteEvento dato : datos) {
                categorias.add(dato.getIdEvento());
                valores.add(dato.getIngresos());
            }
            grafica.addSeries("Ingresos", categorias, valores);

            BufferedImage imagen = BitmapEncoder.getBufferedImage(grafica);

            try (PDDocument documento = new PDDocument()) {
                PDPage pagina = new PDPage(PDRectangle.A4);
                documento.addPage(pagina);
                PDImageXObject imagenPdf = LosslessFactory.createFromImage(documento, imagen);
                try (PDPageContentStream contenido = new PDPageContentStream(documento, pagina)) {
                    float ancho = 480;
                    float alto = ancho * imagen.getHeight() / imagen.getWidth();
                    contenido.drawImage(imagenPdf, 60, 770 - alto, ancho, alto);
                }
                documento.save(ruta);
            }
        } catch (IOException e) {
            throw new ArchivoException("No se pudo generar la grafica de ventas: " + ruta, e);
        }
        return ruta;
    }
}
