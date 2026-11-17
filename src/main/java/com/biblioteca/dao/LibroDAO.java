package com.biblioteca.dao;

import com.biblioteca.modelo.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    public void insertar(Libro l) throws SQLException {
        String sql = "INSERT INTO libros (titulo, autor, anio, disponible) VALUES (?, ?, ?, ?)";
        try (Connection c = ConexionBD.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, l.getTitulo());
            ps.setString(2, l.getAutor());
            ps.setInt(3, l.getAnio());
            ps.setBoolean(4, l.isDisponible());
            ps.executeUpdate();
        }
    }

    public List<Libro> listar() throws SQLException {
        List<Libro> lista = new ArrayList<>();
        try (Connection c = ConexionBD.obtener();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM libros ORDER BY id")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Libro buscarPorId(int id) throws SQLException {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM libros WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public void actualizarDisponibilidad(int id, boolean disponible) throws SQLException {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement("UPDATE libros SET disponible = ? WHERE id = ?")) {
            ps.setBoolean(1, disponible);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public boolean eliminar(int id) throws SQLException {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement("DELETE FROM libros WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Libro mapear(ResultSet rs) throws SQLException {
        return new Libro(rs.getInt("id"), rs.getString("titulo"), rs.getString("autor"),
                rs.getInt("anio"), rs.getBoolean("disponible"));
    }
}
