package com.biblioteca;

import com.biblioteca.web.ServidorWeb;

/** Arranque de la versión web. */
public class MainWeb {
    public static void main(String[] args) throws Exception {
        new ServidorWeb(8080).iniciar();
    }
}
