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
import bingo.unibague.demo.payload.request.CrearJuegoRequest;
import bingo.unibague.demo.payload.response.JuegoResponse;
import bingo.unibague.demo.services.BingoService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/juegos")
public class BingoController {

    @Autowired
    private BingoService bingoService;

    /**
     * Crear un nuevo juego offline
     *
     * @param request datos del juego
     * @param authentication usuario autenticado
     * @return juego creado con cartones
     */
    @PostMapping
    public ResponseEntity<JuegoResponse> crearJuego(@RequestBody CrearJuegoRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        JuegoResponse juego = bingoService.crearJuegoOffline(request, userEmail);
        return ResponseEntity.ok(juego);
    }

    /**
     * Obtener detalles de un juego
     *
     * @param juegoId ID del juego
     * @return detalles del juego
     */
    @GetMapping("/{juegoId}")
    public ResponseEntity<JuegoResponse> obtenerJuego(@PathVariable Long juegoId) {
        return bingoService.obtenerJuego(juegoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener juegos del usuario actual
     *
     * @param authentication usuario autenticado
     * @return lista de juegos del usuario
     */
    @GetMapping("/mis-juegos")
    public ResponseEntity<List<JuegoResponse>> obtenerMisJuegos(Authentication authentication) {
        String userEmail = authentication.getName();
        List<JuegoResponse> juegos = bingoService.obtenerJuegosUsuario(userEmail);
        return ResponseEntity.ok(juegos);
    }

    /**
     * Iniciar un juego (cambiar estado a ACTIVO)
     *
     * @param juegoId ID del juego
     * @return juego actualizado
     */
    @PostMapping("/{juegoId}/iniciar")
    public ResponseEntity<JuegoResponse> iniciarJuego(@PathVariable Long juegoId) {
        return bingoService.iniciarJuego(juegoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Finalizar un juego
     *
     * @param juegoId ID del juego
     * @return juego finalizado
     */
    @PostMapping("/{juegoId}/finalizar")
    public ResponseEntity<JuegoResponse> finalizarJuego(@PathVariable Long juegoId) {
        return bingoService.finalizarJuego(juegoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Generar siguiente número del bingo
     *
     * @param juegoId ID del juego
     * @return número generado
     */
    @PostMapping("/{juegoId}/siguiente-numero")
    public ResponseEntity<Integer> generarSiguienteNumero(@PathVariable Long juegoId) {
        return bingoService.generarSiguienteNumero(juegoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener números ya cantados en el juego
     *
     * @param juegoId ID del juego
     * @return lista de números cantados
     */
    @GetMapping("/{juegoId}/numeros-cantados")
    public ResponseEntity<List<Integer>> obtenerNumerosCantados(@PathVariable Long juegoId) {
        List<Integer> numeros = bingoService.obtenerNumerosCantados(juegoId);
        return ResponseEntity.ok(numeros);
    }
}
