package com.biblioteca.modelo;

public class Libro {
    private int id;
    private String titulo;
    private String autor;
    private int anio;
    private boolean disponible;

    public Libro(int id, String titulo, String autor, int anio, boolean disponible) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.anio = anio;
        this.disponible = disponible;
    }

    public Libro(String titulo, String autor, int anio) {
        this(0, titulo, autor, anio, true);
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getAnio() { return anio; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s (%d) | %s", id, titulo, autor, anio,
                disponible ? "Disponible" : "Prestado");
    }
}
