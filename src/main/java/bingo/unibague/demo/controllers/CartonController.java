package bingo.unibague.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bingo.unibague.demo.models.Carton;
import bingo.unibague.demo.payload.response.MessageResponse;
import bingo.unibague.demo.services.CartonService;

@RestController
@RequestMapping("/api/cartones")
public class CartonController {

    @Autowired
    private CartonService cartonService;

    /**
     * Crea un nuevo cartón para un usuario
     */
    @PostMapping("/usuario/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<?> crearCarton(@PathVariable Long userId) {
        try {
            Carton carton = cartonService.crearCarton(userId);
            return ResponseEntity.ok(carton);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtiene todos los cartones de un usuario
     */
    @GetMapping("/usuario/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<Carton>> getCartonesByUser(@PathVariable Long userId) {
        List<Carton> cartones = cartonService.getCartonesByUser(userId);
        return ResponseEntity.ok(cartones);
    }

    /**
     * Obtiene cartones activos de un usuario
     */
    @GetMapping("/usuario/{userId}/activos")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<Carton>> getCartonesActivos(@PathVariable Long userId) {
        List<Carton> cartones = cartonService.getCartonesActivos(userId);
        return ResponseEntity.ok(cartones);
    }

    /**
     * Obtiene cartones inactivos de un usuario
     */
    @GetMapping("/usuario/{userId}/inactivos")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<Carton>> getCartonesInactivos(@PathVariable Long userId) {
        List<Carton> cartones = cartonService.getCartonesInactivos(userId);
        return ResponseEntity.ok(cartones);
    }

    /**
     * Activa un cartón específico
     */
    @PutMapping("/{cartonId}/activar")
    @PreAuthorize("hasRole('ADMIN') or @cartonService.getCartonByNumero(#cartonId).map(c -> c.user.id).orElse(-1L) == authentication.principal.id")
    public ResponseEntity<?> activarCarton(@PathVariable Long cartonId) {
        try {
            if (cartonService.activarCarton(cartonId)) {
                return ResponseEntity.ok(new MessageResponse("Cartón activado exitosamente"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Desactiva un cartón específico
     */
    @PutMapping("/{cartonId}/desactivar")
    @PreAuthorize("hasRole('ADMIN') or @cartonService.getCartonByNumero(#cartonId).map(c -> c.user.id).orElse(-1L) == authentication.principal.id")
    public ResponseEntity<?> desactivarCarton(@PathVariable Long cartonId) {
        if (cartonService.desactivarCarton(cartonId)) {
            return ResponseEntity.ok(new MessageResponse("Cartón desactivado exitosamente"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cambia el estado de un cartón (activo <-> inactivo)
     */
    @PutMapping("/{cartonId}/toggle")
    @PreAuthorize("hasRole('ADMIN') or @cartonService.getCartonByNumero(#cartonId).map(c -> c.user.id).orElse(-1L) == authentication.principal.id")
    public ResponseEntity<?> toggleEstadoCarton(@PathVariable Long cartonId) {
        try {
            if (cartonService.toggleEstadoCarton(cartonId)) {
                return ResponseEntity.ok(new MessageResponse("Estado del cartón cambiado exitosamente"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Obtiene estadísticas de cartones para un usuario
     */
    @GetMapping("/usuario/{userId}/estadisticas")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<CartonService.CartonStats> getEstadisticasCartones(@PathVariable Long userId) {
        CartonService.CartonStats stats = cartonService.getEstadisticasCartones(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Verifica si un usuario puede activar más cartones
     */
    @GetMapping("/usuario/{userId}/puede-activar-mas")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> puedeActivarMasCartones(@PathVariable Long userId) {
        boolean puedeActivar = cartonService.puedeActivarMasCartones(userId);
        long cartonesActivos = cartonService.contarCartonesActivos(userId);
        
        Map<String, Object> response = Map.of(
            "puedeActivarMas", puedeActivar,
            "cartonesActivos", cartonesActivos
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Busca un cartón por su número
     */
    @GetMapping("/numero/{numeroCarton}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Carton> getCartonByNumero(@PathVariable String numeroCarton) {
        return cartonService.getCartonByNumero(numeroCarton)
                .map(carton -> ResponseEntity.ok(carton))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Elimina un cartón
     */
    @DeleteMapping("/{cartonId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarCarton(@PathVariable Long cartonId) {
        if (cartonService.eliminarCarton(cartonId)) {
            return ResponseEntity.ok(new MessageResponse("Cartón eliminado exitosamente"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Marca un cartón como usado (para cuando termine un juego)
     */
    @PutMapping("/{cartonId}/marcar-usado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> marcarCartonComoUsado(@PathVariable Long cartonId) {
        if (cartonService.marcarCartonComoUsado(cartonId)) {
            return ResponseEntity.ok(new MessageResponse("Cartón marcado como usado"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}