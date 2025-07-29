package bingo.unibague.demo.services;

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
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envía credenciales de acceso por correo electrónico con formato HTML
     * @param to email del destinatario
     * @param username nombre de usuario
     * @param password contraseña
     * @throws MailException si hay error en el envío
     */
    public void sendCredentials(String to, String username, String password) throws MailException {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject("🎯 Bienvenido al Bingo - Tus Credenciales de Acceso");
            
            String htmlContent = buildCredentialsEmailTemplate(username, password);
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Fallback a texto plano si falla HTML
            sendSimpleCredentials(to, username, password);
        }
    }

    /**
     * Método de respaldo para envío de texto plano
     */
    private void sendSimpleCredentials(String to, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Bienvenido al Bingo - Tus Credenciales de Acceso");
        message.setText(
            "¡Bienvenido al Bingo!\n\n" +
            "Tus credenciales de acceso son:\n" +
            "Usuario: " + username + "\n" +
            "Contraseña: " + password + "\n\n" +
            "Instrucciones de acceso:\n" +
            "1. Ingresa a la plataforma del bingo\n" +
            "2. Usa las credenciales proporcionadas\n" +
            "3. ¡Disfruta del juego!\n\n" +
            "Nota: Por seguridad, te recomendamos cambiar tu contraseña después del primer acceso.\n\n" +
            "¡Que tengas suerte!"
        );
        mailSender.send(message);
    }

    /**
     * Construye la plantilla HTML para el correo de credenciales
     */
    private String buildCredentialsEmailTemplate(String username, String password) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Credenciales de Acceso - Bingo</title>
                <style>
                    body {
                        font-family: 'Arial', sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f4f4f4;
                    }
                    .container {
                        background-color: white;
                        padding: 30px;
                        border-radius: 10px;
                        box-shadow: 0 0 10px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        padding: 20px;
                        border-radius: 10px 10px 0 0;
                        margin: -30px -30px 30px -30px;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 28px;
                    }
                    .credentials-box {
                        background-color: #f8f9fa;
                        border: 2px solid #e9ecef;
                        border-radius: 8px;
                        padding: 20px;
                        margin: 20px 0;
                        text-align: center;
                    }
                    .credential-item {
                        margin: 15px 0;
                        font-size: 16px;
                    }
                    .credential-label {
                        font-weight: bold;
                        color: #495057;
                        display: inline-block;
                        width: 100px;
                        text-align: left;
                    }
                    .credential-value {
                        background-color: #e3f2fd;
                        padding: 8px 15px;
                        border-radius: 5px;
                        font-family: 'Courier New', monospace;
                        font-weight: bold;
                        color: #1976d2;
                        display: inline-block;
                        margin-left: 10px;
                    }
                    .instructions {
                        background-color: #fff3cd;
                        border: 1px solid #ffeaa7;
                        border-radius: 5px;
                        padding: 15px;
                        margin: 20px 0;
                    }
                    .instructions h3 {
                        color: #856404;
                        margin-top: 0;
                    }
                    .instructions ol {
                        color: #856404;
                        padding-left: 20px;
                    }
                    .security-note {
                        background-color: #d1ecf1;
                        border: 1px solid #bee5eb;
                        border-radius: 5px;
                        padding: 15px;
                        margin: 20px 0;
                        color: #0c5460;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 30px;
                        padding-top: 20px;
                        border-top: 1px solid #eee;
                        color: #666;
                    }
                    .bingo-emoji {
                        font-size: 24px;
                        margin: 0 5px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1><span class="bingo-emoji">🎯</span> ¡Bienvenido al Bingo! <span class="bingo-emoji">🎲</span></h1>
                        <p>Tu cuenta ha sido creada exitosamente</p>
                    </div>
                    
                    <p>Hola <strong>%s</strong>,</p>
                    
                    <p>¡Nos complace darte la bienvenida a nuestra plataforma de Bingo! Tu cuenta ha sido creada y ya puedes comenzar a jugar.</p>
                    
                    <div class="credentials-box">
                        <h3>🔐 Tus Credenciales de Acceso</h3>
                        <div class="credential-item">
                            <span class="credential-label">Usuario:</span>
                            <span class="credential-value">%s</span>
                        </div>
                        <div class="credential-item">
                            <span class="credential-label">Contraseña:</span>
                            <span class="credential-value">%s</span>
                        </div>
                    </div>
                    
                    <div class="instructions">
                        <h3>📋 Instrucciones de Acceso</h3>
                        <ol>
                            <li>Ingresa a la plataforma del bingo</li>
                            <li>Utiliza las credenciales proporcionadas arriba</li>
                            <li>¡Comienza a jugar y diviértete!</li>
                        </ol>
                    </div>
                    
                    <div class="security-note">
                        <strong>🔒 Nota de Seguridad:</strong> Por tu seguridad, te recomendamos cambiar tu contraseña después del primer acceso. Mantén tus credenciales seguras y no las compartas con nadie.
                    </div>
                    
                    <div class="footer">
                        <p><strong>¡Que tengas mucha suerte! 🍀</strong></p>
                        <p><em>Equipo del Bingo</em></p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(username, username, password);
    }
}