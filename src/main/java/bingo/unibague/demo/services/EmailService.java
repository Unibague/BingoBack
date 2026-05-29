package bingo.unibague.demo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envía credenciales de acceso por correo electrónico con formato HTML
     * @param to email del destinatario
     * @param username nombre de usuario
     * @param password contraseña
     * @param email email para mostrar en el mensaje
     * @throws MailException si hay error en el envío
     */
    public void sendCredentials(String to, String username, String password, String email) throws MailException {
        logger.info("EmailService - Intentando enviar correo a: {}", to);
        logger.info("EmailService - Usuario: {}", username);
        logger.debug("EmailService - Configuración de mail sender: {}", mailSender != null ? "OK" : "NULL");
        
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(to);
            helper.setFrom("jonatan25sanchez@gmail.com"); // Establecer el remitente
            helper.setSubject("🎯 Bienvenido al Bingo - Tus Credenciales de Acceso");
            
            String htmlContent = buildCredentialsEmailTemplate(username, password, email);
            helper.setText(htmlContent, true);
            
            logger.info("EmailService - Enviando correo HTML...");
            mailSender.send(mimeMessage);
            logger.info("EmailService - Correo HTML enviado exitosamente");
            
        } catch (MessagingException e) {
            logger.error("EmailService - Error con correo HTML, intentando texto plano: {}", e.getMessage());
            // Fallback a texto plano si falla HTML
            sendSimpleCredentials(to, username, password, email);
        }
    }

    /**
     * Método de respaldo para envío de texto plano
     */
    private void sendSimpleCredentials(String to, String username, String password, String email) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("jonatan25sanchez@gmail.com");
            message.setSubject("Bienvenido al Bingo - Tus Credenciales de Acceso");
            message.setText(
                "¡Bienvenido al Bingo!\n\n" +
                "Tus credenciales de acceso son:\n" +
                "Usuario: " + username + "\n" +
                "Email: " + email + "\n" +
                "Contraseña: " + password + "\n\n" +
                "Instrucciones de acceso:\n" +
                "1. Ingresa a la plataforma del bingo\n" +
                "2. Usa las credenciales proporcionadas\n" +
                "3. ¡Disfruta del juego!\n\n" +
                "Nota: Por seguridad, te recomendamos cambiar tu contraseña después del primer acceso.\n\n" +
                "¡Que tengas suerte!"
            );
            
            logger.info("EmailService - Enviando correo de texto plano...");
            mailSender.send(message);
            logger.info("EmailService - Correo de texto plano enviado exitosamente");
            
        } catch (Exception e) {
            logger.error("EmailService - Error enviando correo de texto plano: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo enviar el correo: " + e.getMessage());
        }
    }

    /**
     * Construye la plantilla HTML para el correo de credenciales
     */
    private String buildCredentialsEmailTemplate(String username, String password , String email) {
        return "<!DOCTYPE html>" +
            "<html lang='es'>" +
            "<head>" +
            "<meta charset='UTF-8'>" +
            "<title>Credenciales de Acceso - Bingo</title>" +
            "<style>" +
            "body { font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; }" +
            ".container { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }" +
            ".header { text-align: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 10px; margin-bottom: 20px; }" +
            ".credentials-box { background: #f8f9fa; border: 2px solid #e9ecef; border-radius: 8px; padding: 20px; margin: 20px 0; text-align: center; }" +
            ".credential-value { background: #e3f2fd; padding: 8px 15px; border-radius: 5px; font-family: monospace; font-weight: bold; color: #1976d2; }" +
            "</style>" +
            "</head>" +
            "<body>" +
            "<div class='container'>" +
            "<div class='header'>" +
            "<h1>🎯 ¡Bienvenido al Bingo! 🎲</h1>" +
            "<p>Tu cuenta ha sido creada exitosamente</p>" +
            "</div>" +
            "<p>Hola <strong>" + username + "</strong>,</p>" +
            "<p>¡Nos complace darte la bienvenida a nuestra plataforma de Bingo!</p>" +
            "<div class='credentials-box'>" +
            "<h3>🔐 Tus Credenciales de Acceso</h3>" +
            "<p><strong>nombre:</strong> <span class='credential-value'>" + username + "</span></p>" +
            "<p><strong>Contraseña:</strong> <span class='credential-value'>" + password + "</span></p>" +
            "<p><strong>Email:</strong> <span class='credential-value'>" + email + "</span></p>" +

            "</div>" +
            "<p><strong>¡Que tengas mucha suerte! 🍀</strong></p>" +
            "<p><em>Equipo del Bingo</em></p>" +
            "</div>" +
            "</body>" +
            "</html>";
    }
}