package com.example.telitobodeguero.utils;

import com.example.telitobodeguero.beans.EmailSenderBean;

public class TestCorreo {
    public static void main(String[] args) {
        EmailSenderBean email = new EmailSenderBean();
        email.enviarCorreo("destinatario@gmail.com", "Prueba", "Este es un correo de prueba.");
    }
}
