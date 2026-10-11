package com.boleteria.io;

import com.boleteria.exception.ArchivoException;
import com.boleteria.model.Compra;
import com.boleteria.model.Evento;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/** Genera el ticket PDF de una compra, incluyendo su codigo QR. */
public class TicketPdf {

    private final QrGenerator qrGenerator = new QrGenerator();
    private final String carpeta;

    public TicketPdf(String carpeta) {
        this.carpeta = carpeta;
    }

    public String generar(Compra compra, Evento evento) throws ArchivoException {
        String ruta = carpeta + "/" + compra.getId() + ".pdf";
        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);

            BufferedImage qr = qrGenerator.generar(contenidoQr(compra), 220);
            PDImageXObject imagenQr = LosslessFactory.createFromImage(documento, qr);

            try (PDPageContentStream contenido = new PDPageContentStream(documento, pagina)) {
                contenido.beginText();
                contenido.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contenido.newLineAtOffset(60, 770);
                contenido.showText("TICKET DE COMPRA");
                contenido.endText();

                List<String> lineas = List.of(
                        "ID de compra: " + compra.getId(),
                        "Evento: " + evento.getNombre() + " (" + evento.getIdEvento() + ")",
                        "Fecha: " + evento.getFecha() + " " + evento.getHora(),
                        "Lugar: " + evento.getLugar(),
                        "Titular: " + compra.getComprador().getNombre(),
                        "Documento: " + compra.getComprador().getDocumento(),
                        "Asientos: " + String.join(", ", compra.getAsientos()),
                        "Subtotal: $" + formato(compra.getSubtotal()),
                        "Servicio (10%): $" + formato(compra.getServicio()),
                        "Total: $" + formato(compra.getTotal())
                );

                contenido.beginText();
                contenido.setFont(PDType1Font.HELVETICA, 12);
                contenido.setLeading(20);
                contenido.newLineAtOffset(60, 720);
                for (String linea : lineas) {
                    contenido.showText(linea);
                    contenido.newLine();
                }
                contenido.endText();

                contenido.drawImage(imagenQr, 60, 300, 180, 180);

                contenido.beginText();
                contenido.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                contenido.newLineAtOffset(60, 285);
                contenido.showText("Escanee el codigo QR para verificar la compra.");
                contenido.endText();
            }

            documento.save(ruta);
        } catch (IOException e) {
            throw new ArchivoException("No se pudo generar el PDF del ticket: " + ruta, e);
        }
        return ruta;
    }

    // Texto que se codifica en el QR, con el formato sugerido en el enunciado.
    private String contenidoQr(Compra compra) {
        return "ID_COMPRA=" + compra.getId()
                + " | TITULAR=" + compra.getComprador().getNombre()
                + " | EVENTO=" + compra.getIdEvento()
                + " | ASIENTOS=" + String.join(",", compra.getAsientos())
                + " | TOTAL=" + formato(compra.getTotal());
    }

    private String formato(double valor) {
        return String.format("%.0f", valor);
    }
}
