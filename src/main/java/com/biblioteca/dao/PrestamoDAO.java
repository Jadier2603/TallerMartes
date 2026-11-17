package com.biblioteca.dao;

import com.biblioteca.modelo.Prestamo;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    public void insertar(int libroId, int usuarioId) throws SQLException {
        String sql = "INSERT INTO prestamos (libro_id, usuario_id, fecha_prestamo) VALUES (?, ?, ?)";
        try (Connection c = ConexionBD.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            ps.setInt(2, usuarioId);
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
        }
    }

    public Prestamo buscarPorId(int id) throws SQLException {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM prestamos WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Date dev = rs.getDate("fecha_devolucion");
                return new Prestamo(rs.getInt("id"), rs.getInt("libro_id"), rs.getInt("usuario_id"),
                        rs.getDate("fecha_prestamo").toLocalDate(), dev == null ? null : dev.toLocalDate());
            }
        }
    }

    public void registrarDevolucion(int id) throws SQLException {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement("UPDATE prestamos SET fecha_devolucion = ? WHERE id = ?")) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public List<Prestamo> listarActivos() throws SQLException {
        String sql = "SELECT p.*, l.titulo, u.nombre FROM prestamos p "
                   + "JOIN libros l ON p.libro_id = l.id "
                   + "JOIN usuarios u ON p.usuario_id = u.id "
                   + "WHERE p.fecha_devolucion IS NULL ORDER BY p.id";
        List<Prestamo> lista = new ArrayList<>();
        try (Connection c = ConexionBD.obtener();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Prestamo p = new Prestamo(rs.getInt("id"), rs.getInt("libro_id"), rs.getInt("usuario_id"),
                        rs.getDate("fecha_prestamo").toLocalDate(), null);
                p.setTituloLibro(rs.getString("titulo"));
                p.setNombreUsuario(rs.getString("nombre"));
                lista.add(p);
            }
        }
        return lista;
    }
}
