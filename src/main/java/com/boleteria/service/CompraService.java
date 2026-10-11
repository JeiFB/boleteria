package com.boleteria.service;

import com.boleteria.exception.ArchivoException;
import com.boleteria.exception.CompraInvalidaException;
import com.boleteria.io.Auditoria;
import com.boleteria.io.ComprasRepository;
import com.boleteria.model.Asiento;
import com.boleteria.model.Compra;
import com.boleteria.model.Comprador;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Logica de compra: validaciones, calculo de valores y persistencia. */
public class CompraService {

    private static final double PORCENTAJE_SERVICIO = 0.10;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventoService eventoService;
    private final ComprasRepository comprasRepository;
    private final Auditoria auditoria;
    private final List<Compra> compras;

    public CompraService(EventoService eventoService, ComprasRepository comprasRepository,
                         Auditoria auditoria, List<Compra> compras) {
        this.eventoService = eventoService;
        this.comprasRepository = comprasRepository;
        this.auditoria = auditoria;
        this.compras = compras;
    }

    public double calcularSubtotal(List<Asiento> asientos) {
        double subtotal = 0;
        for (Asiento asiento : asientos) {
            subtotal += asiento.getPrecio();
        }
        return subtotal;
    }

    public double calcularServicio(double subtotal) {
        return subtotal * PORCENTAJE_SERVICIO;
    }

    public double calcularTotal(double subtotal, double servicio) {
        return subtotal + servicio;
    }

    public boolean validarCorreo(String correo) {
        return correo != null && correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    public void validarComprador(Comprador comprador) throws CompraInvalidaException {
        if (comprador == null
                || esVacio(comprador.getNombre())
                || esVacio(comprador.getDocumento())
                || esVacio(comprador.getCorreo())) {
            throw new CompraInvalidaException("Los datos del comprador son obligatorios.");
        }
        if (!validarCorreo(comprador.getCorreo())) {
            throw new CompraInvalidaException("El correo '" + comprador.getCorreo() + "' no es valido.");
        }
    }

    /** Valida evento, comprador y asientos, calcula los valores y persiste la compra. */
    public Compra comprar(String idEvento, Comprador comprador, List<String> codigosAsientos)
            throws CompraInvalidaException, ArchivoException {

        eventoService.buscarEvento(idEvento);
        validarComprador(comprador);

        if (codigosAsientos == null || codigosAsientos.isEmpty()) {
            throw new CompraInvalidaException("Debe seleccionar al menos un asiento.");
        }

        List<Asiento> seleccionados = new ArrayList<>();
        List<String> yaAgregados = new ArrayList<>();
        for (String codigo : codigosAsientos) {
            if (yaAgregados.contains(codigo)) {
                throw new CompraInvalidaException("El asiento '" + codigo + "' esta repetido en la seleccion.");
            }
            Asiento asiento = eventoService.buscarAsiento(idEvento, codigo);
            if (asiento == null) {
                throw new CompraInvalidaException("El asiento '" + codigo + "' no existe en el evento.");
            }
            if (asiento.isVendido()) {
                throw new CompraInvalidaException("El asiento '" + codigo + "' ya fue vendido.");
            }
            seleccionados.add(asiento);
            yaAgregados.add(codigo);
        }

        double subtotal = calcularSubtotal(seleccionados);
        double servicio = calcularServicio(subtotal);
        double total = calcularTotal(subtotal, servicio);

        Compra compra = new Compra(
                generarId(),
                idEvento,
                comprador,
                new ArrayList<>(codigosAsientos),
                subtotal,
                servicio,
                total,
                LocalDateTime.now().format(FORMATO_FECHA)
        );

        for (Asiento asiento : seleccionados) {
            asiento.setVendido(true);
        }

        compras.add(compra);
        comprasRepository.guardar(compras);
        auditoria.registrar("Compra " + compra.getId() + " registrada para el evento " + idEvento
                + " (" + codigosAsientos.size() + " asientos, total " + total + ")");

        return compra;
    }

    public List<Compra> getCompras() {
        return compras;
    }

    public String generarId() {
        return String.format("CMP-%03d", compras.size() + 1);
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
