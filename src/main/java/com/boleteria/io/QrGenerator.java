package com.boleteria.io;

import com.boleteria.exception.ArchivoException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;

/** Genera imagenes de codigo QR con ZXing. */
public class QrGenerator {

    public BufferedImage generar(String texto, int tamano) throws ArchivoException {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matriz = writer.encode(texto, BarcodeFormat.QR_CODE, tamano, tamano);
            return MatrixToImageWriter.toBufferedImage(matriz);
        } catch (WriterException e) {
            throw new ArchivoException("No se pudo generar el codigo QR", e);
        }
    }
}
