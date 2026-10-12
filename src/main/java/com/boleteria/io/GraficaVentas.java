package com.boleteria.io;

import com.boleteria.exception.ArchivoException;
import com.boleteria.model.ReporteEvento;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Genera el reporte de ventas en PDF: grafica de barras y resumen por evento. */
public class GraficaVentas {

    public String generar(List<ReporteEvento> datos, String ruta) throws ArchivoException {
        try {
            CategoryChart grafica = new CategoryChartBuilder()
                    .width(900).height(500)
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
                    float ancho = 470;
                    float alto = ancho * imagen.getHeight() / imagen.getWidth();
                    float topImagen = 780;
                    contenido.drawImage(imagenPdf, 60, topImagen - alto, ancho, alto);

                    float y = topImagen - alto - 35;
                    contenido.beginText();
                    contenido.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contenido.newLineAtOffset(60, y);
                    contenido.showText("Resumen por evento");
                    contenido.endText();

                    contenido.beginText();
                    contenido.setFont(PDType1Font.HELVETICA, 11);
                    contenido.setLeading(17);
                    contenido.newLineAtOffset(60, y - 22);
                    for (ReporteEvento dato : datos) {
                        contenido.showText(dato.getIdEvento() + " - " + dato.getNombre()
                                + "   |   boletas vendidas: " + dato.getBoletasVendidas() + "/" + dato.getTotalAsientos()
                                + "   |   ocupacion: " + porcentaje(dato.getOcupacion()) + "%"
                                + "   |   ingresos: $" + moneda(dato.getIngresos()));
                        contenido.newLine();
                    }
                    contenido.endText();
                }

                documento.save(ruta);
            }
        } catch (IOException e) {
            throw new ArchivoException("No se pudo generar el reporte de ventas: " + ruta, e);
        }
        return ruta;
    }

    private String porcentaje(double valor) {
        return String.format(Locale.US, "%.1f", valor);
    }

    private String moneda(double valor) {
        return String.format(Locale.US, "%,.0f", valor);
    }
}
