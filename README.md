# Sistema de Venta de Boletas

Aplicacion de consola en Java (Maven) para gestionar eventos, disponibilidad de
asientos y compra de boletas. Cada compra se persiste y genera un ticket PDF con
codigo QR verificable. Tambien produce un reporte de ventas con una grafica de
barras.

Laboratorio 2 - Unidad 4 - Tecnicas de Programacion y Laboratorio.

## Integrantes

- Jeifher Fuentes
- Andres Perez

## Requisitos

- Java 17 o superior
- Maven 3.8 o superior

## Estructura del proyecto

```
boleteria/
├── pom.xml
├── data/                 archivos de entrada
│   ├── eventos.csv
│   ├── asientos.csv
│   └── compras.json      (se actualiza con cada compra)
├── logs/                 auditoria.txt (se genera al ejecutar)
├── tickets/              tickets PDF generados
├── reportes/             grafica de ventas en PDF
├── src/main/java/com/boleteria/
│   ├── app/              Main (menu de consola)
│   ├── model/            Evento, Asiento, Comprador, Compra, ReporteEvento
│   ├── service/          EventoService, CompraService, ReporteService
│   ├── io/               CsvLoader, ComprasRepository, Auditoria,
│   │                     QrGenerator, TicketPdf, GraficaVentas
│   └── exception/        ArchivoException, CompraInvalidaException
└── src/test/java/        pruebas unitarias JUnit
```

## Compilar

```
mvn compile
```

## Ejecutar

```
mvn exec:java
```

Menu de la aplicacion:

```
1. Listar eventos
2. Ver asientos de un evento
3. Comprar boletas
4. Reporte de ventas
0. Salir
```

Al comprar se piden nombre, documento y correo del comprador y los codigos de
los asientos separados por coma (por ejemplo: `VIP-A-01,VIP-A-02`). Una compra
valida:

- reserva los asientos (no se pueden vender dos veces),
- actualiza `data/compras.json`,
- registra la operacion en `logs/auditoria.txt`,
- genera el ticket en `tickets/<idCompra>.pdf` con codigo QR.

## Ejecutar las pruebas

```
mvn test
```

## Librerias utilizadas

- **Jackson** — lectura y escritura de JSON (`compras.json`).
- **Apache PDFBox** — generacion de los tickets y del reporte en PDF.
- **ZXing** — generacion del codigo QR.
- **XChart** — grafica de barras de ingresos por evento.
- **JUnit 5** — pruebas unitarias.

## Formato del codigo QR

```
ID_COMPRA=CMP-001 | TITULAR=Laura Gomez | EVENTO=EV001 | ASIENTOS=VIP-A-01,VIP-A-02 | TOTAL=770000
```
