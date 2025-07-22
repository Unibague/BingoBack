package bingo.unibague.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bingo.unibague.demo.models.BingoCard;
import bingo.unibague.demo.services.CartonService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cartones")
public class CardBoardController {

    @Autowired
    private CartonService cartonService;

    /**
     * Generar cartones para un juego
     * @param cantidad número de cartones a generar (por defecto 10)
     * @return lista de cartones generados
     */
    @PostMapping("/generar")
    public ResponseEntity<List<BingoCard>> generarCartones(@RequestParam(defaultValue = "10") int cantidad) {
        List<BingoCard> cartones = cartonService.generarCartones(cantidad);
        return ResponseEntity.ok(cartones);
    }

    /**
     * Obtener un cartón específico
     * @param cartonId ID del cartón
     * @return cartón solicitado
     */
    @GetMapping("/{cartonId}")
    public ResponseEntity<BingoCard> obtenerCarton(@PathVariable Long cartonId) {
        return cartonService.obtenerCarton(cartonId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener todos los cartones de un juego
     * @param juegoId ID del juego
     * @return lista de cartones del juego
     */
    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<List<BingoCard>> obtenerCartonesPorJuego(@PathVariable Long juegoId) {
        List<BingoCard> cartones = cartonService.obtenerCartonesPorJuego(juegoId);
        return ResponseEntity.ok(cartones);
    }
}