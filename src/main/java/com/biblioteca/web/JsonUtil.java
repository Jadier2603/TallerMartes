package com.biblioteca.web;

import com.biblioteca.modelo.Libro;
import com.biblioteca.modelo.Prestamo;
import com.biblioteca.modelo.Usuario;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Convierte objetos del modelo a JSON (sin librerías externas). */
public class JsonUtil {

    public static String texto(String s) {
        if (s == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> { if (c < 0x20) sb.append(String.format("\\u%04x", (int) c)); else sb.append(c); }
            }
        }
        return sb.append('"').toString();
    }

    public static String libro(Libro l) {
        return "{\"id\":" + l.getId() + ",\"titulo\":" + texto(l.getTitulo())
                + ",\"autor\":" + texto(l.getAutor()) + ",\"anio\":" + l.getAnio()
                + ",\"disponible\":" + l.isDisponible() + "}";
    }

    public static String usuario(Usuario u) {
        return "{\"id\":" + u.getId() + ",\"nombre\":" + texto(u.getNombre())
                + ",\"correo\":" + texto(u.getCorreo()) + "}";
    }

    public static String prestamo(Prestamo p) {
        return "{\"id\":" + p.getId() + ",\"libro\":" + texto(p.getTituloLibro())
                + ",\"usuario\":" + texto(p.getNombreUsuario())
                + ",\"fecha\":" + texto(String.valueOf(p.getFechaPrestamo())) + "}";
    }

    public static <T> String lista(List<T> items, Function<T, String> conv) {
        return items.stream().map(conv).collect(Collectors.joining(",", "[", "]"));
    }

    public static String error(String mensaje) { return "{\"error\":" + texto(mensaje) + "}"; }
    public static String ok() { return "{\"ok\":true}"; }
}
