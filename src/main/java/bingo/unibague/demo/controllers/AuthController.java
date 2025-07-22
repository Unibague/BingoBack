// src/main/java/bingo/unibague/demo/controllers/AuthController.java
package bingo.unibague.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    
    @Autowired
    private AuthService authService;

    /**
     * Endpoint para iniciar sesión
     * @param loginRequest datos de inicio de sesión
     * @return token JWT y datos del usuario
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
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
}
