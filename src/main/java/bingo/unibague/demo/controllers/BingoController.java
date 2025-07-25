package bingo.unibague.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bingo.unibague.demo.models.BingoGame;
import bingo.unibague.demo.payload.response.HistorialPartidaResponse;
import bingo.unibague.demo.services.BingoGameService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/juegos")
public class BingoController {

    @Autowired
    private BingoGameService gameService;

    @PostMapping
    public ResponseEntity<BingoGame> crearJuego(@RequestBody CreateGameRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        BingoGame juego = gameService.createGame(request.getNombre(), userEmail, 
            BingoGame.TipoJuego.valueOf(request.getTipoJuego()));
        return ResponseEntity.ok(juego);
    }

    @GetMapping("/{juegoId}")
    public ResponseEntity<BingoGame> obtenerJuego(@PathVariable Long juegoId) {
        return gameService.getGameById(juegoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mis-juegos")
    public ResponseEntity<List<BingoGame>> obtenerMisJuegos(Authentication authentication) {
        String userEmail = authentication.getName();
        List<BingoGame> juegos = gameService.getGamesByUser(userEmail);
        return ResponseEntity.ok(juegos);
    }

    @PostMapping("/{juegoId}/iniciar")
    public ResponseEntity<BingoGame> iniciarJuego(@PathVariable Long juegoId) {
        return gameService.startGame(juegoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{juegoId}/finalizar")
    public ResponseEntity<BingoGame> finalizarJuego(@PathVariable Long juegoId) {
        return gameService.endGame(juegoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{juegoId}/siguiente-numero")
    public ResponseEntity<Integer> generarSiguienteNumero(@PathVariable Long juegoId) {
        return gameService.callNextNumber(juegoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{juegoId}/numeros-cantados")
    public ResponseEntity<List<Integer>> obtenerNumerosCantados(@PathVariable Long juegoId) {
        return gameService.getGameById(juegoId)
                .map(game -> ResponseEntity.ok(game.getNumerosCantados()))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para obtener el historial detallado de partidas del usuario
     * @param authentication datos del usuario autenticado
     * @return historial de partidas con información detallada de cartones
     */
    @GetMapping("/historial")
    public ResponseEntity<List<HistorialPartidaResponse>> obtenerHistorialPartidas(Authentication authentication) {
        String userEmail = authentication.getName();
        List<HistorialPartidaResponse> historial = gameService.getHistorialPartidas(userEmail);
        return ResponseEntity.ok(historial);
    }

    // Clase interna para el request
    public static class CreateGameRequest {
        private String nombre;
        private String tipoJuego;
        
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        
        public String getTipoJuego() { return tipoJuego; }
        public void setTipoJuego(String tipoJuego) { this.tipoJuego = tipoJuego; }
    }
}
