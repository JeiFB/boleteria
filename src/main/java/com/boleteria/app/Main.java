package com.boleteria.app;

import com.boleteria.exception.ArchivoException;
import com.boleteria.exception.CompraInvalidaException;
import com.boleteria.io.Auditoria;
import com.boleteria.io.ComprasRepository;
import com.boleteria.io.CsvLoader;
import com.boleteria.model.Asiento;
import com.boleteria.model.Compra;
import com.boleteria.model.Comprador;
import com.boleteria.model.Evento;
import com.boleteria.service.CompraService;
import com.boleteria.service.EventoService;

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

    private final Scanner scanner = new Scanner(System.in);
    private EventoService eventoService;
    private CompraService compraService;

    public static void main(String[] args) {
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
        System.out.print("Ingrese el id del evento: ");
        String idEvento = scanner.nextLine().trim();
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
        System.out.print("Ingrese el id del evento: ");
        String idEvento = scanner.nextLine().trim();

        System.out.print("Nombre del comprador: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Documento: ");
        String documento = scanner.nextLine().trim();
        System.out.print("Correo: ");
        String correo = scanner.nextLine().trim();

        System.out.print("Codigos de asientos separados por coma (ej: VIP-A-01,VIP-A-02): ");
        String entrada = scanner.nextLine().trim();
        List<String> codigos = Arrays.stream(entrada.split(","))
                .map(String::trim)
                .filter(c -> !c.isEmpty())
                .collect(Collectors.toList());

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
        } catch (CompraInvalidaException e) {
            System.out.println("No se pudo completar la compra: " + e.getMessage());
        } catch (ArchivoException e) {
            System.out.println("Error al guardar la compra: " + e.getMessage());
        }
    }

    private String formatMoneda(double valor) {
        return String.format("%,.0f", valor);
    }
}
