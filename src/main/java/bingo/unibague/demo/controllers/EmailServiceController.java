package bingo.unibague.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bingo.unibague.demo.payload.request.SendCredentialsRequest;
import bingo.unibague.demo.services.EmailService;
import jakarta.validation.Valid;

// EmailServiceController.java - SOLO para emails
@RestController
@RequestMapping("/api/email")
@Validated
public class EmailServiceController {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceController.class);
    private final EmailService emailService;

    @Autowired
    public EmailServiceController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-credentials")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> sendCredentialsEmail(@Valid @RequestBody SendCredentialsRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("EmailController - Usuario autenticado: {}", auth != null ? auth.getName() : "null");
        logger.debug("EmailController - Enviando credenciales a: {}", request.getTo());
        
        try {
            emailService.sendCredentials(request.getTo(), request.getUsername(), request.getPassword());
            logger.debug("EmailController - Email enviado exitosamente a: {}", request.getTo());
            return ResponseEntity.ok("Correo de credenciales enviado correctamente");
        } catch (Exception e) {
            logger.error("EmailController - Error enviando email: {}", e.getMessage(), e);
            throw e;
        }
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<String> handleMailException(MailException ex) {
        logger.error("EmailController - MailException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al enviar el correo: " + ex.getMessage());
    }
}
