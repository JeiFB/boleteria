package com.boleteria.app;

import com.boleteria.exception.ArchivoException;
import com.boleteria.exception.CompraInvalidaException;
import com.boleteria.io.Auditoria;
import com.boleteria.io.ComprasRepository;
import com.boleteria.io.CsvLoader;
import com.boleteria.io.GraficaVentas;
import com.boleteria.io.TicketPdf;
import com.boleteria.model.Asiento;
import com.boleteria.model.Compra;
import com.boleteria.model.Comprador;
import com.boleteria.model.Evento;
import com.boleteria.model.ReporteEvento;
import com.boleteria.service.CompraService;
import com.boleteria.service.EventoService;
import com.boleteria.service.ReporteService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/** Punto de entrada: muestra el menu de consola y delega en los servicios. */
public class Main {

    private static final String RUTA_EVENTOS = "data/eventos.csv";
    private static final String RUTA_ASIENTOS = "data/asientos.csv";
    private static final String RUTA_COMPRAS = "data/compras.json";
    private static final String RUTA_AUDITORIA = "logs/auditoria.txt";
    private static final String CARPETA_TICKETS = "tickets";
    private static final String RUTA_REPORTE = "reportes/ventas_por_evento.pdf";

    private final Scanner scanner = new Scanner(System.in);
    private final TicketPdf ticketPdf = new TicketPdf(CARPETA_TICKETS);
    private final GraficaVentas graficaVentas = new GraficaVentas();
    private EventoService eventoService;
    private CompraService compraService;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        Main app = new Main();
        if (app.iniciar()) {
            app.ejecutarMenu();
        }
    }

    // Carga los datos de los archivos y prepara los servicios.
    private boolean iniciar() {
        try {
            CsvLoader loader = new CsvLoader();
            List<Evento> eventos = loader.cargarEventos(RUTA_EVENTOS);
            List<Asiento> asientos = loader.cargarAsientos(RUTA_ASIENTOS);

            ComprasRepository comprasRepository = new ComprasRepository(RUTA_COMPRAS);
            List<Compra> compras = comprasRepository.cargar();

            eventoService = new EventoService(eventos, asientos);
            eventoService.marcarVendidos(compras);

            Auditoria auditoria = new Auditoria(RUTA_AUDITORIA);
            compraService = new CompraService(eventoService, comprasRepository, auditoria, compras);
            return true;
        } catch (ArchivoException e) {
            System.out.println("Error al iniciar: " + e.getMessage());
            return false;
        }
    }

    private void ejecutarMenu() {
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1" -> listarEventos();
                case "2" -> mostrarAsientos();
                case "3" -> comprar();
                case "4" -> generarReporte();
                case "0" -> {
                    salir = true;
                    System.out.println("Hasta luego.");
                }
                default -> System.out.println("Opcion no valida.");
            }
        }
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("===== BOLETERIA =====");
        System.out.println("1. Listar eventos");
        System.out.println("2. Ver asientos de un evento");
        System.out.println("3. Comprar boletas");
        System.out.println("4. Reporte de ventas");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opcion: ");
    }

    private void listarEventos() {
        System.out.println();
        System.out.println("Eventos disponibles:");
        for (Evento evento : eventoService.listarEventos()) {
            System.out.println("- " + evento.getIdEvento() + " | " + evento.getNombre()
                    + " | " + evento.getFecha() + " " + evento.getHora()
                    + " | " + evento.getLugar() + " | " + evento.getTipoEvento());
        }
    }

    private void mostrarAsientos() {
        listarEventos();
        System.out.print("\nIngrese el id del evento: ");
        String idEvento = scanner.nextLine().trim().toUpperCase();
        if (!eventoService.existeEvento(idEvento)) {
            System.out.println("El evento '" + idEvento + "' no existe.");
            return;
        }
        System.out.println();
        System.out.println("Asientos del evento " + idEvento + ":");
        for (Asiento asiento : eventoService.asientosDeEvento(idEvento)) {
            String estado = asiento.isVendido() ? "[VENDIDO]   " : "[DISPONIBLE]";
            System.out.println(estado + " " + asiento.getCodigo()
                    + " | " + asiento.getLocalidad()
                    + " | fila " + asiento.getFila() + " num " + asiento.getNumero()
                    + " | $" + formatMoneda(asiento.getPrecio()));
        }
    }

    private void comprar() {
        listarEventos();
        String idEvento = leerEvento();
        if (idEvento == null) {
            return;
        }
        mostrarDisponibles(idEvento);

        String nombre = leerObligatorio("Nombre del comprador");
        String documento = leerObligatorio("Documento");
        String correo = leerCorreo();
        List<String> codigos = leerAsientos(idEvento);

        try {
            Comprador comprador = new Comprador(nombre, documento, correo);
            Compra compra = compraService.comprar(idEvento, comprador, codigos);
            System.out.println();
            System.out.println("Compra registrada con exito.");
            System.out.println("ID de compra: " + compra.getId());
            System.out.println("Asientos: " + String.join(", ", compra.getAsientos()));
            System.out.println("Subtotal: $" + formatMoneda(compra.getSubtotal()));
            System.out.println("Servicio (10%): $" + formatMoneda(compra.getServicio()));
            System.out.println("Total: $" + formatMoneda(compra.getTotal()));

            Evento evento = eventoService.buscarEvento(idEvento);
            String rutaTicket = ticketPdf.generar(compra, evento);
            System.out.println("Ticket generado en: " + rutaTicket);
        } catch (CompraInvalidaException e) {
            System.out.println("No se pudo completar la compra: " + e.getMessage());
        } catch (ArchivoException e) {
            System.out.println("Error al guardar la compra: " + e.getMessage());
        }
    }

    // Pide el id del evento hasta que exista (o 0 para cancelar).
    private String leerEvento() {
        while (true) {
            System.out.print("\nIngrese el id del evento (0 para cancelar): ");
            String id = scanner.nextLine().trim().toUpperCase();
            if (id.equals("0")) {
                return null;
            }
            if (eventoService.existeEvento(id)) {
                return id;
            }
            System.out.println("El evento '" + id + "' no existe. Intente de nuevo.");
        }
    }

    private String leerObligatorio(String etiqueta) {
        while (true) {
            System.out.print(etiqueta + ": ");
            String valor = scanner.nextLine().trim();
            if (!valor.isEmpty()) {
                return valor;
            }
            System.out.println("Este dato es obligatorio.");
        }
    }

    private String leerCorreo() {
        while (true) {
            System.out.print("Correo: ");
            String correo = scanner.nextLine().trim();
            if (compraService.validarCorreo(correo)) {
                return correo;
            }
            System.out.println("El correo no es valido (ejemplo: nombre@dominio.com).");
        }
    }

    // Pide los asientos y valida uno por uno antes de continuar.
    private List<String> leerAsientos(String idEvento) {
        while (true) {
            System.out.print("Codigos de asientos separados por coma (ej: VIP-A-01,VIP-A-02): ");
            String entrada = scanner.nextLine().trim();
            List<String> codigos = Arrays.stream(entrada.split(","))
                    .map(c -> c.trim().toUpperCase())
                    .filter(c -> !c.isEmpty())
                    .collect(Collectors.toList());

            if (codigos.isEmpty()) {
                System.out.println("Debe elegir al menos un asiento.");
                continue;
            }
            String error = revisarAsientos(idEvento, codigos);
            if (error == null) {
                return codigos;
            }
            System.out.println(error + " Intente de nuevo.");
        }
    }

    private String revisarAsientos(String idEvento, List<String> codigos) {
        List<String> vistos = new ArrayList<>();
        for (String codigo : codigos) {
            if (vistos.contains(codigo)) {
                return "El asiento '" + codigo + "' esta repetido.";
            }
            Asiento asiento = eventoService.buscarAsiento(idEvento, codigo);
            if (asiento == null) {
                return "El asiento '" + codigo + "' no existe en el evento.";
            }
            if (asiento.isVendido()) {
                return "El asiento '" + codigo + "' ya fue vendido.";
            }
            vistos.add(codigo);
        }
        return null;
    }

    private void mostrarDisponibles(String idEvento) {
        System.out.println();
        System.out.println("Asientos disponibles en " + idEvento + ":");
        boolean hay = false;
        for (Asiento asiento : eventoService.asientosDeEvento(idEvento)) {
            if (!asiento.isVendido()) {
                hay = true;
                System.out.println("  " + asiento.getCodigo()
                        + " | " + asiento.getLocalidad()
                        + " | fila " + asiento.getFila() + " num " + asiento.getNumero()
                        + " | $" + formatMoneda(asiento.getPrecio()));
            }
        }
        if (!hay) {
            System.out.println("  (no hay asientos disponibles)");
        }
    }

    private void generarReporte() {
        ReporteService reporteService = new ReporteService(eventoService, compraService.getCompras());
        List<ReporteEvento> datos = reporteService.generarReporte();

        System.out.println();
        System.out.println("Reporte de ventas:");
        for (ReporteEvento dato : datos) {
            System.out.println("- " + dato.getIdEvento() + " | " + dato.getNombre()
                    + " | vendidas: " + dato.getBoletasVendidas() + "/" + dato.getTotalAsientos()
                    + " | ocupacion: " + String.format("%.1f", dato.getOcupacion()) + "%"
                    + " | ingresos: $" + formatMoneda(dato.getIngresos()));
        }

        try {
            String ruta = graficaVentas.generar(datos, RUTA_REPORTE);
            System.out.println("Grafica generada en: " + ruta);
        } catch (ArchivoException e) {
            System.out.println("Error al generar la grafica: " + e.getMessage());
        }
    }

    private String formatMoneda(double valor) {
        return String.format("%,.0f", valor);
    }
}
