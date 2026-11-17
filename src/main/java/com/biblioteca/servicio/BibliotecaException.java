package com.biblioteca.servicio;

/** Error de regla de negocio (dato inválido, libro no disponible, etc.). */
public class BibliotecaException extends Exception {
    public BibliotecaException(String mensaje) { super(mensaje); }
    public BibliotecaException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
