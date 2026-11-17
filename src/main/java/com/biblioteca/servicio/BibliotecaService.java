package com.biblioteca.servicio;

import com.biblioteca.dao.LibroDAO;
import com.biblioteca.dao.PrestamoDAO;
import com.biblioteca.dao.UsuarioDAO;
import com.biblioteca.modelo.Libro;
import com.biblioteca.modelo.Prestamo;
import com.biblioteca.modelo.Usuario;
import java.sql.SQLException;
import java.time.Year;
import java.util.List;

/** Capa de negocio: valida reglas y coordina los DAO. La vista solo habla con esta clase. */
public class BibliotecaService {
    private final LibroDAO libroDAO = new LibroDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    public void registrarLibro(String titulo, String autor, int anio) throws BibliotecaException {
        if (titulo == null || titulo.isBlank() || autor == null || autor.isBlank())
            throw new BibliotecaException("El título y el autor son obligatorios.");
        if (anio < 1000 || anio > Year.now().getValue())
            throw new BibliotecaException("El año no es válido.");
        try {
            libroDAO.insertar(new Libro(titulo.trim(), autor.trim(), anio));
        } catch (SQLException e) {
            throw new BibliotecaException("No se pudo guardar el libro: " + e.getMessage(), e);
        }
    }

    public List<Libro> listarLibros() throws BibliotecaException {
        try { return libroDAO.listar(); }
        catch (SQLException e) { throw new BibliotecaException("Error al consultar libros: " + e.getMessage(), e); }
    }

    public void eliminarLibro(int id) throws BibliotecaException {
        try {
            if (!libroDAO.eliminar(id)) throw new BibliotecaException("No existe un libro con ID " + id);
        } catch (SQLException e) {
            throw new BibliotecaException("No se puede eliminar (¿tiene préstamos asociados?): " + e.getMessage(), e);
        }
    }

    public void registrarUsuario(String nombre, String correo) throws BibliotecaException {
        if (nombre == null || nombre.isBlank()) throw new BibliotecaException("El nombre es obligatorio.");
        if (correo == null || !correo.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$"))
            throw new BibliotecaException("El correo no tiene un formato válido.");
        try {
            usuarioDAO.insertar(new Usuario(nombre.trim(), correo.trim()));
        } catch (SQLException e) {
            throw new BibliotecaException("No se pudo guardar el usuario (¿correo repetido?): " + e.getMessage(), e);
        }
    }

    public List<Usuario> listarUsuarios() throws BibliotecaException {
        try { return usuarioDAO.listar(); }
        catch (SQLException e) { throw new BibliotecaException("Error al consultar usuarios: " + e.getMessage(), e); }
    }

    public void prestarLibro(int libroId, int usuarioId) throws BibliotecaException {
        try {
            Libro libro = libroDAO.buscarPorId(libroId);
            if (libro == null) throw new BibliotecaException("El libro no existe.");
            if (!libro.isDisponible()) throw new BibliotecaException("El libro ya está prestado.");
            if (usuarioDAO.buscarPorId(usuarioId) == null) throw new BibliotecaException("El usuario no existe.");

            prestamoDAO.insertar(libroId, usuarioId);
            libroDAO.actualizarDisponibilidad(libroId, false);
        } catch (SQLException e) {
            throw new BibliotecaException("Error al registrar el préstamo: " + e.getMessage(), e);
        }
    }

    public void devolverLibro(int prestamoId) throws BibliotecaException {
        try {
            Prestamo p = prestamoDAO.buscarPorId(prestamoId);
            if (p == null) throw new BibliotecaException("El préstamo no existe.");
            if (p.getFechaDevolucion() != null) throw new BibliotecaException("Ese préstamo ya fue devuelto.");

            prestamoDAO.registrarDevolucion(prestamoId);
            libroDAO.actualizarDisponibilidad(p.getLibroId(), true);
        } catch (SQLException e) {
            throw new BibliotecaException("Error al registrar la devolución: " + e.getMessage(), e);
        }
    }

    public List<Prestamo> listarPrestamosActivos() throws BibliotecaException {
        try { return prestamoDAO.listarActivos(); }
        catch (SQLException e) { throw new BibliotecaException("Error al consultar préstamos: " + e.getMessage(), e); }
    }
}
