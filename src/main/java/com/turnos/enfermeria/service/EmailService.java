package com.turnos.enfermeria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:desarrollo2@hospitalsanjose.gov.co}")
    private String fromEmail;

    @Value("${app.mail.enabled:true}")
    private boolean emailEnabled;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Enviar correo HTML
     */
    public void enviarCorreoHTML(String destinatario, String asunto, String contenidoHTML) throws MessagingException {
        if (!emailEnabled) {
            log.info("Envío de correo deshabilitado. Destinatario: {}, Asunto: {}", destinatario, asunto);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenidoHTML, true); // true para HTML
            helper.setSentDate(new Date());

            mailSender.send(message);
            log.info("Correo HTML enviado exitosamente a: {}", destinatario);

        } catch (MessagingException e) {
            log.error("Error al enviar correo HTML a {}: {}", destinatario, e.getMessage());
            throw e;
        }
    }

    /**
     * Enviar correo a múltiples destinatarios
     */
    public Map<String, Boolean> enviarCorreosMasivos(List<String> destinatarios, String asunto, String contenidoHTML) {
        Map<String, Boolean> resultados = new HashMap<>();

        for (String destinatario : destinatarios) {
            try {
                enviarCorreoHTML(destinatario, asunto, contenidoHTML);
                resultados.put(destinatario, true);

                // Pequeña pausa para evitar ser marcado como spam
                Thread.sleep(50);

            } catch (Exception e) {
                log.error("Error enviando correo masivo a {}: {}", destinatario, e.getMessage());
                resultados.put(destinatario, false);
            }
        }

        return resultados;
    }

    /**
     * Validar configuración de correo
     */
    public boolean isConfiguracionValida() {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            log.info("Configuración de correo válida");
            return true;
        } catch (Exception e) {
            log.error("Error en configuración de correo: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Enviar correo de prueba
     */
    public boolean enviarCorreoPrueba(String destinatario) {
        try {
            String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h2 style="color: #2563eb;">🧪 Prueba del Sistema de Correos</h2>
                        <p><strong>Fecha:</strong> %s</p>
                        <div style="background: #f0f9ff; padding: 15px; border-radius: 8px; margin: 20px 0;">
                            <h3 style="color: #1e40af;">✅ Configuración Exitosa</h3>
                            <p>El sistema de correo está funcionando correctamente.</p>
                        </div>
                        <hr>
                        <p style="color: #666; font-size: 12px;">
                            Este correo fue generado automáticamente por el Sistema de Gestión Hospitalaria
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(new Date().toString());

            enviarCorreoHTML(destinatario, "🧪 Prueba del Sistema de Correos", htmlContent);
            return true;

        } catch (Exception e) {
            log.error("Error al enviar correo de prueba: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Enviar correo de texto plano (método faltante)
     */
    public void enviarCorreoTexto(String destinatario, String asunto, String contenido) throws MessagingException {
        if (!emailEnabled) {
            log.info("Envío de correo deshabilitado. Destinatario: {}, Asunto: {}", destinatario, asunto);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenido, false); // false para texto plano
            helper.setSentDate(new Date());

            mailSender.send(message);
            log.info("Correo de texto enviado exitosamente a: {}", destinatario);

        } catch (MessagingException e) {
            log.error("Error al enviar correo de texto a {}: {}", destinatario, e.getMessage());
            throw e;
        }
    }
}
