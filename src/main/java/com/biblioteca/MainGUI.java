package com.biblioteca;

import com.biblioteca.vista.VentanaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Arranque de la versión gráfica. */
public class MainGUI {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
