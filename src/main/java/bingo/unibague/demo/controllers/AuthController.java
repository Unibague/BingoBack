// src/main/java/bingo/unibague/demo/controllers/AuthController.java
package bingo.unibague.demo.controllers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bingo.unibague.demo.payload.request.LoginRequest;
import bingo.unibague.demo.payload.request.SignupRequest;
import bingo.unibague.demo.payload.response.JwtResponse;
import bingo.unibague.demo.payload.response.MessageResponse;
import bingo.unibague.demo.services.AuthService;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;

    /**
     * Endpoint para iniciar sesión
     * @param loginRequest datos de inicio de sesión
     * @return token JWT y datos del usuario
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("AuthController - Recibida petición de login");
        logger.info("AuthController - Email: {}", loginRequest != null ? loginRequest.getEmail() : "null");
        logger.info("AuthController - Password presente: {}", loginRequest != null && loginRequest.getPassword() != null ? "Sí" : "No");
        
        if (loginRequest == null) {
            logger.error("AuthController - LoginRequest es null");
            return ResponseEntity.badRequest().body(new MessageResponse("Datos de login requeridos"));
        }
        
        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
            logger.error("AuthController - Email es null o vacío");
            return ResponseEntity.badRequest().body(new MessageResponse("Email es requerido"));
        }
        
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            logger.error("AuthController - Password es null o vacío");
            return ResponseEntity.badRequest().body(new MessageResponse("Password es requerido"));
        }
        
        try {
            JwtResponse response = authService.authenticateUser(loginRequest);
            logger.info("AuthController - Login exitoso para: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("AuthController - Error en login para {}: {}", loginRequest.getEmail(), e.getMessage(), e);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error de autenticación: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para registrar un nuevo usuario
     * @param signUpRequest datos del nuevo usuario
     * @return mensaje de confirmación
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            MessageResponse response = authService.registerUser(signUpRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Endpoint para verificar si el token JWT es válido
     * @param auth información de autenticación
     * @return estado del token y datos del usuario
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "user", auth.getName(),
                "authorities", auth.getAuthorities()
            ));
        }
        return ResponseEntity.ok(Map.of("valid", false));
    }
}
