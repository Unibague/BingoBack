package bingo.unibague.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bingo.unibague.demo.dto.UserDTO;
import bingo.unibague.demo.payload.response.MessageResponse;
import bingo.unibague.demo.services.UserService;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {
    
    @Autowired
    private UserService userService;

    /**
     * Obtiene todos los usuarios (solo administradores)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Obtiene solo los jugadores (usuarios con rol ROLE_USER)
     */
    @GetMapping("/jugadores")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getPlayers() {
        List<UserDTO> players = userService.getPlayers();
        return ResponseEntity.ok(players);
    }

    /**
     * Obtiene un usuario específico por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.userHasUsername(#id, authentication.name)")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualiza el número máximo de cartones para un usuario (solo administradores)
     */
    @PutMapping("/{id}/max-cartones")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarMaxCartones(@PathVariable Long id, @RequestParam Integer maxCartones) {
        try {
            if (userService.updateMaxCartones(id, maxCartones)) {
                return ResponseEntity.ok(new MessageResponse("Límite de cartones actualizado exitosamente"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Actualiza el límite de cartones usando JSON en el body
     */
    @PutMapping("/{id}/max-cartones-json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarMaxCartonesJson(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Integer maxCartones = request.get("maxCartones");
            if (maxCartones == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: maxCartones es requerido"));
            }
            
            if (userService.updateMaxCartones(id, maxCartones)) {
                return ResponseEntity.ok(new MessageResponse("Límite de cartones actualizado exitosamente"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Valida si un usuario puede tener más cartones activos
     */
    @GetMapping("/{id}/puede-mas-cartones")
    public ResponseEntity<Map<String, Object>> puedeActivarMasCartones(@PathVariable Long id, @RequestParam Integer cartonesActivos) {
        boolean puedeActivar = userService.canHaveMoreCartones(id, cartonesActivos);
        Integer maxCartones = userService.getMaxCartonesForUser(id);
        
        if (maxCartones == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> response = Map.of(
            "puedeActivarMas", puedeActivar,
            "cartonesActivos", cartonesActivos,
            "maxCartones", maxCartones,
            "cartonesDisponibles", maxCartones - cartonesActivos
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene información del límite de cartones para un usuario
     */
    @GetMapping("/{id}/limite-cartones")
    public ResponseEntity<Map<String, Integer>> getLimiteCartones(@PathVariable Long id) {
        Integer maxCartones = userService.getMaxCartonesForUser(id);
        if (maxCartones == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Integer> response = Map.of("maxCartones", maxCartones);
        return ResponseEntity.ok(response);
    }
}
