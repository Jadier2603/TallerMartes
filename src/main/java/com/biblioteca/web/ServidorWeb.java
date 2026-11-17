package com.biblioteca.web;

import com.biblioteca.servicio.BibliotecaException;
import com.biblioteca.servicio.BibliotecaService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Capa de presentación web: expone BibliotecaService como API REST sencilla
 * y sirve la página index.html. No contiene SQL ni reglas de negocio.
 */
public class ServidorWeb {
    private final BibliotecaService servicio = new BibliotecaService();
    private final int puerto;

    public ServidorWeb(int puerto) { this.puerto = puerto; }

    @FunctionalInterface
    private interface Accion {
        String ejecutar(String metodo, Map<String, String> p) throws BibliotecaException;
    }

    public void iniciar() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);
        server.createContext("/", this::servirPagina);
        server.createContext("/api/libros", api(this::libros));
        server.createContext("/api/usuarios", api(this::usuarios));
        server.createContext("/api/prestamos", api(this::prestamos));
        server.start();
        System.out.println("Servidor listo. Abre http://localhost:" + puerto + " en el navegador.");
        System.out.println("(Para detenerlo pulsa el botón Stop del IDE o Ctrl+C)");
    }

    // ---------- Recursos de la API ----------
    private String libros(String m, Map<String, String> p) throws BibliotecaException {
        return switch (m) {
            case "GET" -> JsonUtil.lista(servicio.listarLibros(), JsonUtil::libro);
            case "POST" -> {
                servicio.registrarLibro(p.get("titulo"), p.get("autor"), Integer.parseInt(p.getOrDefault("anio", "")));
                yield JsonUtil.ok();
            }
            case "DELETE" -> {
                servicio.eliminarLibro(Integer.parseInt(p.getOrDefault("id", "")));
                yield JsonUtil.ok();
            }
            default -> throw new BibliotecaException("Método no permitido.");
        };
    }

    private String usuarios(String m, Map<String, String> p) throws BibliotecaException {
        return switch (m) {
            case "GET" -> JsonUtil.lista(servicio.listarUsuarios(), JsonUtil::usuario);
            case "POST" -> {
                servicio.registrarUsuario(p.get("nombre"), p.get("correo"));
                yield JsonUtil.ok();
            }
            default -> throw new BibliotecaException("Método no permitido.");
        };
    }

    private String prestamos(String m, Map<String, String> p) throws BibliotecaException {
        return switch (m) {
            case "GET" -> JsonUtil.lista(servicio.listarPrestamosActivos(), JsonUtil::prestamo);
            case "POST" -> {
                servicio.prestarLibro(Integer.parseInt(p.getOrDefault("libroId", "")),
                                      Integer.parseInt(p.getOrDefault("usuarioId", "")));
                yield JsonUtil.ok();
            }
            case "DELETE" -> { // devolver
                servicio.devolverLibro(Integer.parseInt(p.getOrDefault("id", "")));
                yield JsonUtil.ok();
            }
            default -> throw new BibliotecaException("Método no permitido.");
        };
    }

    // ---------- Infraestructura HTTP ----------
    private HttpHandler api(Accion accion) {
        return ex -> {
            try {
                Map<String, String> params = parsear(ex.getRequestURI().getRawQuery());
                String cuerpo = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                params.putAll(parsear(cuerpo));
                responder(ex, 200, accion.ejecutar(ex.getRequestMethod(), params));
            } catch (BibliotecaException e) {
                responder(ex, 400, JsonUtil.error(e.getMessage()));
            } catch (NumberFormatException e) {
                responder(ex, 400, JsonUtil.error("Falta un dato numérico o no es válido."));
            } catch (Exception e) {
                responder(ex, 500, JsonUtil.error("Error interno: " + e.getMessage()));
            }
        };
    }

    private void servirPagina(HttpExchange ex) throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/web/index.html")) {
            if (in == null || !ex.getRequestURI().getPath().equals("/")) {
                byte[] b = "No encontrado".getBytes(StandardCharsets.UTF_8);
                ex.sendResponseHeaders(404, b.length);
                ex.getResponseBody().write(b);
                ex.close();
                return;
            }
            byte[] bytes = in.readAllBytes();
            ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            ex.sendResponseHeaders(200, bytes.length);
            ex.getResponseBody().write(bytes);
            ex.close();
        }
    }

    private void responder(HttpExchange ex, int codigo, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(codigo, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }

    private Map<String, String> parsear(String raw) {
        Map<String, String> mapa = new HashMap<>();
        if (raw == null || raw.isBlank()) return mapa;
        for (String par : raw.split("&")) {
            String[] kv = par.split("=", 2);
            String k = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String v = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            mapa.put(k, v);
        }
        return mapa;
    }
}
