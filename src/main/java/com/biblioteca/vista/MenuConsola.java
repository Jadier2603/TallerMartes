package com.biblioteca.vista;

import com.biblioteca.servicio.BibliotecaException;
import com.biblioteca.servicio.BibliotecaService;
import java.util.List;
import java.util.Scanner;

/** Capa de presentación: interfaz de consola. No contiene SQL ni reglas de negocio. */
public class MenuConsola {
    private final BibliotecaService servicio = new BibliotecaService();
    private final Scanner sc = new Scanner(System.in);

    public void iniciar() {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Elige una opción: ");
            try {
                ejecutar(opcion);
            } catch (BibliotecaException e) {
                System.out.println("\n⚠ " + e.getMessage());
            }
        } while (opcion != 0);
        System.out.println("¡Hasta pronto!");
    }

    private void mostrarMenu() {
        System.out.println("\n===== BIBLIOTECA =====");
        System.out.println("1. Registrar libro");
        System.out.println("2. Listar libros");
        System.out.println("3. Eliminar libro");
        System.out.println("4. Registrar usuario");
        System.out.println("5. Listar usuarios");
        System.out.println("6. Prestar libro");
        System.out.println("7. Devolver libro");
        System.out.println("8. Ver préstamos activos");
        System.out.println("0. Salir");
    }

    private void ejecutar(int opcion) throws BibliotecaException {
        switch (opcion) {
            case 1 -> {
                String t = leerTexto("Título: ");
                String a = leerTexto("Autor: ");
                int anio = leerEntero("Año: ");
                servicio.registrarLibro(t, a, anio);
                System.out.println("✔ Libro registrado.");
            }
            case 2 -> imprimir(servicio.listarLibros());
            case 3 -> {
                servicio.eliminarLibro(leerEntero("ID del libro: "));
                System.out.println("✔ Libro eliminado.");
            }
            case 4 -> {
                servicio.registrarUsuario(leerTexto("Nombre: "), leerTexto("Correo: "));
                System.out.println("✔ Usuario registrado.");
            }
            case 5 -> imprimir(servicio.listarUsuarios());
            case 6 -> {
                servicio.prestarLibro(leerEntero("ID del libro: "), leerEntero("ID del usuario: "));
                System.out.println("✔ Préstamo registrado.");
            }
            case 7 -> {
                servicio.devolverLibro(leerEntero("ID del préstamo: "));
                System.out.println("✔ Devolución registrada.");
            }
            case 8 -> imprimir(servicio.listarPrestamosActivos());
            case 0 -> { }
            default -> System.out.println("Opción no válida.");
        }
    }

    private <T> void imprimir(List<T> lista) {
        if (lista.isEmpty()) System.out.println("(sin registros)");
        else lista.forEach(System.out::println);
    }

    private String leerTexto(String msg) {
        System.out.print(msg);
        return sc.nextLine();
    }

    private int leerEntero(String msg) {
        while (true) {
            System.out.print(msg);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Escribe un número válido."); }
        }
    }
}
