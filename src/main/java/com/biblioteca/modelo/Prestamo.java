package com.biblioteca.modelo;

import java.time.LocalDate;

public class Prestamo {
    private int id;
    private int libroId;
    private int usuarioId;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    // Datos de apoyo para mostrar en pantalla (vienen de un JOIN)
    private String tituloLibro;
    private String nombreUsuario;

    public Prestamo(int id, int libroId, int usuarioId, LocalDate fechaPrestamo, LocalDate fechaDevolucion) {
        this.id = id;
        this.libroId = libroId;
        this.usuarioId = usuarioId;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
    }

    public int getId() { return id; }
    public int getLibroId() { return libroId; }
    public int getUsuarioId() { return usuarioId; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public String getTituloLibro() { return tituloLibro; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setTituloLibro(String t) { this.tituloLibro = t; }
    public void setNombreUsuario(String n) { this.nombreUsuario = n; }

    @Override
    public String toString() {
        return String.format("Préstamo #%d | Libro: %s | Usuario: %s | Desde: %s",
                id, tituloLibro, nombreUsuario, fechaPrestamo);
    }
}
