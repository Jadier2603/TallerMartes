package com.biblioteca.dao;

import com.biblioteca.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void insertar(Usuario u) throws SQLException {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement("INSERT INTO usuarios (nombre, correo) VALUES (?, ?)")) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.executeUpdate();
        }
    }

    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = ConexionBD.obtener();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM usuarios ORDER BY id")) {
            while (rs.next()) {
                lista.add(new Usuario(rs.getInt("id"), rs.getString("nombre"), rs.getString("correo")));
            }
        }
        return lista;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM usuarios WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new Usuario(rs.getInt("id"), rs.getString("nombre"), rs.getString("correo")) : null;
            }
        }
    }
}
