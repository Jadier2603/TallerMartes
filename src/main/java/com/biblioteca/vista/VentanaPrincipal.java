package com.biblioteca.vista;

import com.biblioteca.modelo.Libro;
import com.biblioteca.modelo.Prestamo;
import com.biblioteca.modelo.Usuario;
import com.biblioteca.servicio.BibliotecaException;
import com.biblioteca.servicio.BibliotecaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Capa de presentación (interfaz gráfica). Solo usa BibliotecaService, igual que la consola. */
public class VentanaPrincipal extends JFrame {
    private final BibliotecaService servicio = new BibliotecaService();

    private final DefaultTableModel modeloLibros = crearModelo("ID", "Título", "Autor", "Año", "Estado");
    private final DefaultTableModel modeloUsuarios = crearModelo("ID", "Nombre", "Correo");
    private final DefaultTableModel modeloPrestamos = crearModelo("ID", "Libro", "Usuario", "Fecha préstamo");
    private final JTable tablaLibros = new JTable(modeloLibros);
    private final JTable tablaUsuarios = new JTable(modeloUsuarios);
    private final JTable tablaPrestamos = new JTable(modeloPrestamos);

    private final JComboBox<Libro> comboLibros = new JComboBox<>();
    private final JComboBox<Usuario> comboUsuarios = new JComboBox<>();

    public VentanaPrincipal() {
        super("Biblioteca");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(850, 520);
        setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Libros", panelLibros());
        pestanas.addTab("Usuarios", panelUsuarios());
        pestanas.addTab("Préstamos", panelPrestamos());
        add(pestanas);

        recargar();
    }

    // ---------- Pestaña Libros ----------
    private JPanel panelLibros() {
        JTextField titulo = new JTextField(14), autor = new JTextField(12), anio = new JTextField(5);
        JButton agregar = new JButton("Agregar");
        JButton eliminar = new JButton("Eliminar seleccionado");

        agregar.addActionListener(e -> {
            try {
                servicio.registrarLibro(titulo.getText(), autor.getText(), Integer.parseInt(anio.getText().trim()));
                titulo.setText(""); autor.setText(""); anio.setText("");
                recargar();
            } catch (NumberFormatException ex) {
                error("El año debe ser un número.");
            } catch (BibliotecaException ex) {
                error(ex.getMessage());
            }
        });

        eliminar.addActionListener(e -> {
            int fila = tablaLibros.getSelectedRow();
            if (fila < 0) { error("Selecciona un libro de la tabla."); return; }
            try {
                servicio.eliminarLibro((int) modeloLibros.getValueAt(fila, 0));
                recargar();
            } catch (BibliotecaException ex) {
                error(ex.getMessage());
            }
        });

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Título:")); formulario.add(titulo);
        formulario.add(new JLabel("Autor:")); formulario.add(autor);
        formulario.add(new JLabel("Año:")); formulario.add(anio);
        formulario.add(agregar); formulario.add(eliminar);
        return armar(tablaLibros, formulario);
    }

    // ---------- Pestaña Usuarios ----------
    private JPanel panelUsuarios() {
        JTextField nombre = new JTextField(14), correo = new JTextField(16);
        JButton agregar = new JButton("Agregar");

        agregar.addActionListener(e -> {
            try {
                servicio.registrarUsuario(nombre.getText(), correo.getText());
                nombre.setText(""); correo.setText("");
                recargar();
            } catch (BibliotecaException ex) {
                error(ex.getMessage());
            }
        });

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Nombre:")); formulario.add(nombre);
        formulario.add(new JLabel("Correo:")); formulario.add(correo);
        formulario.add(agregar);
        return armar(tablaUsuarios, formulario);
    }

    // ---------- Pestaña Préstamos ----------
    private JPanel panelPrestamos() {
        JButton prestar = new JButton("Prestar");
        JButton devolver = new JButton("Devolver seleccionado");

        prestar.addActionListener(e -> {
            Libro libro = (Libro) comboLibros.getSelectedItem();
            Usuario usuario = (Usuario) comboUsuarios.getSelectedItem();
            if (libro == null || usuario == null) { error("Elige un libro disponible y un usuario."); return; }
            try {
                servicio.prestarLibro(libro.getId(), usuario.getId());
                recargar();
            } catch (BibliotecaException ex) {
                error(ex.getMessage());
            }
        });

        devolver.addActionListener(e -> {
            int fila = tablaPrestamos.getSelectedRow();
            if (fila < 0) { error("Selecciona un préstamo de la tabla."); return; }
            try {
                servicio.devolverLibro((int) modeloPrestamos.getValueAt(fila, 0));
                recargar();
            } catch (BibliotecaException ex) {
                error(ex.getMessage());
            }
        });

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Libro:")); formulario.add(comboLibros);
        formulario.add(new JLabel("Usuario:")); formulario.add(comboUsuarios);
        formulario.add(prestar); formulario.add(devolver);
        return armar(tablaPrestamos, formulario);
    }

    // ---------- Utilidades ----------
    private void recargar() {
        try {
            modeloLibros.setRowCount(0);
            comboLibros.removeAllItems();
            for (Libro l : servicio.listarLibros()) {
                modeloLibros.addRow(new Object[]{l.getId(), l.getTitulo(), l.getAutor(), l.getAnio(),
                        l.isDisponible() ? "Disponible" : "Prestado"});
                if (l.isDisponible()) comboLibros.addItem(l);
            }

            modeloUsuarios.setRowCount(0);
            comboUsuarios.removeAllItems();
            for (Usuario u : servicio.listarUsuarios()) {
                modeloUsuarios.addRow(new Object[]{u.getId(), u.getNombre(), u.getCorreo()});
                comboUsuarios.addItem(u);
            }

            modeloPrestamos.setRowCount(0);
            for (Prestamo p : servicio.listarPrestamosActivos()) {
                modeloPrestamos.addRow(new Object[]{p.getId(), p.getTituloLibro(), p.getNombreUsuario(), p.getFechaPrestamo()});
            }
        } catch (BibliotecaException ex) {
            error(ex.getMessage());
        }
    }

    private JPanel armar(JTable tabla, JPanel formulario) {
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(formulario, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private static DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private void error(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
