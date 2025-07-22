package bingo.unibague.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bingo.unibague.demo.models.Puntaje;
import bingo.unibague.demo.payload.response.EstadisticasResponse;
import bingo.unibague.demo.services.PuntajeService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/puntajes")
public class ScoreController {

    @Autowired
    private PuntajeService puntajeService;

    /**
     * Guardar puntaje de un juego completado
     * @param puntaje datos del puntaje
     * @param authentication usuario autenticado
     * @return puntaje guardado
     */
    @PostMapping("/guardar")
    public ResponseEntity<Puntaje> guardarPuntaje(@RequestBody Puntaje puntaje, Authentication authentication) {
        String userEmail = authentication.getName();
        Puntaje puntajeGuardado = puntajeService.guardarPuntaje(puntaje, userEmail);
        return ResponseEntity.ok(puntajeGuardado);
    }

    /**
     * Obtener estadísticas del usuario actual
     * @param authentication usuario autenticado
     * @return estadísticas del usuario
     */
    @GetMapping("/mis-estadisticas")
    public ResponseEntity<EstadisticasResponse> obtenerMisEstadisticas(Authentication authentication) {
        String userEmail = authentication.getName();
        EstadisticasResponse estadisticas = puntajeService.obtenerEstadisticasUsuario(userEmail);
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Obtener ranking general
     * @return lista de mejores puntajes
     */
    @GetMapping("/ranking")
    public ResponseEntity<List<Puntaje>> obtenerRanking() {
        List<Puntaje> ranking = puntajeService.obtenerRanking();
        return ResponseEntity.ok(ranking);
    }

    /**
     * Obtener historial de juegos del usuario
     * @param authentication usuario autenticado
     * @return historial de juegos
     */
    @GetMapping("/historial")
    public ResponseEntity<List<Puntaje>> obtenerHistorial(Authentication authentication) {
        String userEmail = authentication.getName();
        List<Puntaje> historial = puntajeService.obtenerHistorialUsuario(userEmail);
        return ResponseEntity.ok(historial);
    }
}