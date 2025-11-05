package com.example.telitobodeguero.beans;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailSenderBean {

    private final String remitente = "liherman533@gmail.com";
    private final String contraseña = "xomwdisbbevsmqjl";

    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session sesion = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, contraseña);
            }
        });

        try {
            Message correo = new MimeMessage(sesion);
            correo.setFrom(new InternetAddress(remitente));
            correo.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            correo.setSubject(asunto);
            correo.setText(mensaje);
            Transport.send(correo);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
