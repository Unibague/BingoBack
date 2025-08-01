package bingo.unibague.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import bingo.unibague.demo.dto.UserDTO;
import bingo.unibague.demo.payload.request.CreateUserRequest;
import bingo.unibague.demo.services.UserService;
    
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/create-user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("AdminController - Usuario autenticado: {}", auth != null ? auth.getName() : "null");
        logger.debug("AdminController - Authorities: {}", auth != null ? auth.getAuthorities() : "null");
        logger.debug("AdminController - Request recibido - username: {}, email: {}, role: {}", 
                    request.getUsername(), request.getEmail(), request.getRole());
        logger.debug("AdminController - Password presente: {}", request.getPassword() != null ? "Sí" : "No");
        
        try {
            UserDTO userDTO = userService.createUser(request);
            logger.debug("AdminController - Usuario creado exitosamente: {}", userDTO.getUsername());
            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException e) {
            logger.error("AdminController - Error de validación: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("AdminController - Error inesperado creando usuario: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    @PostMapping("/test-auth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("AdminController - Test Auth - Usuario: {}", auth != null ? auth.getName() : "null");
        logger.debug("AdminController - Test Auth - Authorities: {}", auth != null ? auth.getAuthorities() : "null");
        
        return ResponseEntity.ok("Autenticación exitosa para: " + 
                                (auth != null ? auth.getName() : "usuario desconocido") + 
                                " con roles: " + 
                                (auth != null ? auth.getAuthorities() : "sin roles"));
    }
    
    @PostMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createUserAlternative(@RequestBody CreateUserRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("AdminController - Endpoint alternativo - Usuario: {}", auth != null ? auth.getName() : "null");
        
        try {
            UserDTO userDTO = userService.createUser(request);
            logger.debug("AdminController - Usuario creado exitosamente: {}", userDTO.getUsername());
            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException e) {
            logger.error("AdminController - Error de validación: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("AdminController - Error inesperado: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("AdminController - Error obteniendo usuarios: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAdminStats() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            Map<String, Object> stats = Map.of(
                "totalUsuarios", users.size(),
                "usuariosActivos", users.stream().filter(u -> u.getCartonesActivos() > 0).count(),
                "totalCartones", users.stream().mapToInt(UserDTO::getCartonesActivos).sum()
            );
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("AdminController - Error obteniendo estadísticas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }
}



