package bingo.unibague.demo.controllers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bingo.unibague.demo.dto.UserDTO;
import bingo.unibague.demo.payload.request.CreateUserRequest;
import bingo.unibague.demo.services.UserService;

@RestController
@RequestMapping("/api/usuarios")
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("UserManagementController - Usuario autenticado: {}", auth != null ? auth.getName() : "null");
        logger.debug("UserManagementController - Request: username={}, email={}", request.getUsername(), request.getEmail());
        
        try {
            UserDTO userDTO = userService.createUser(request);
            logger.debug("UserManagementController - Usuario creado: {}", userDTO.getUsername());
            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException e) {
            logger.error("UserManagementController - Error de validación: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("UserManagementController - Error inesperado: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }
}