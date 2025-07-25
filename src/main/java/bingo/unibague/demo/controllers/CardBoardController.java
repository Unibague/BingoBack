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
import bingo.unibague.demo.services.BingoCardService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cartones")
public class CardBoardController {

    @Autowired
    private BingoCardService cardService;

    @PostMapping("/generar")
    public ResponseEntity<List<BingoCard>> generarCartones(@RequestParam(defaultValue = "10") int cantidad) {
        // Para juego offline, generar cartones independientes
        List<BingoCard> cartones = new java.util.ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            BingoCard card = cardService.generateSingleCard();
            card.setNumeroCarton(i + 1);
            cartones.add(card);
        }
        return ResponseEntity.ok(cartones);
    }

    @GetMapping("/{cartonId}")
    public ResponseEntity<BingoCard> obtenerCarton(@PathVariable Long cartonId) {
        return cardService.getCardById(cartonId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<List<BingoCard>> obtenerCartonesPorJuego(@PathVariable Long juegoId) {
        List<BingoCard> cartones = cardService.getCardsByGame(juegoId);
        return ResponseEntity.ok(cartones);
    }
}
