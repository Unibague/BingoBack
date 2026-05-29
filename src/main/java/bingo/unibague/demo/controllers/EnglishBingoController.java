package bingo.unibague.demo.controllers;

import bingo.unibague.demo.models.EnglishBingoSala;
import bingo.unibague.demo.services.EnglishBingoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/english-bingo")
@CrossOrigin(origins = "*")
public class EnglishBingoController {

    @Autowired
    private EnglishBingoService service;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ── REST endpoints ──────────────────────────────────────────

    @GetMapping("/salas")
    public ResponseEntity<List<EnglishBingoSala>> getSalas() {
        return ResponseEntity.ok(service.getSalasActivas());
    }

    @GetMapping("/salas/{salaId}")
    public ResponseEntity<EnglishBingoSala> getSala(@PathVariable String salaId) {
        EnglishBingoSala sala = service.getSala(salaId);
        if (sala == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(sala);
    }

    @PostMapping("/salas")
    public ResponseEntity<EnglishBingoSala> crearSala(@RequestBody Map<String, Object> body) {
        String nombre = (String) body.get("nombre");
        String adminId = (String) body.get("adminId");
        String adminUsername = (String) body.get("adminUsername");
        int modulo = (int) body.get("moduloSeleccionado");
        int cartones = (int) body.get("cartonesPerJugador");

        EnglishBingoSala sala = service.crearSala(nombre, adminId, adminUsername, modulo, cartones);
        broadcast(sala);
        return ResponseEntity.ok(sala);
    }

    @PostMapping("/salas/{salaId}/unirse")
    public ResponseEntity<EnglishBingoSala> unirse(@PathVariable String salaId,
                                                    @RequestBody Map<String, String> body) {
        EnglishBingoSala sala = service.unirseASala(salaId, body.get("jugadorId"), body.get("jugadorUsername"));
        if (sala == null) return ResponseEntity.badRequest().build();
        broadcast(sala);
        return ResponseEntity.ok(sala);
    }

    @PostMapping("/salas/{salaId}/iniciar")
    public ResponseEntity<EnglishBingoSala> iniciar(@PathVariable String salaId) {
        EnglishBingoSala sala = service.iniciarJuego(salaId);
        if (sala == null) return ResponseEntity.notFound().build();
        broadcast(sala);
        return ResponseEntity.ok(sala);
    }

    @PostMapping("/salas/{salaId}/llamar")
    public ResponseEntity<EnglishBingoSala> llamar(@PathVariable String salaId) {
        EnglishBingoSala sala = service.llamarPalabra(salaId);
        if (sala == null) return ResponseEntity.notFound().build();
        broadcast(sala);
        return ResponseEntity.ok(sala);
    }

    @PostMapping("/salas/{salaId}/finalizar")
    public ResponseEntity<EnglishBingoSala> finalizar(@PathVariable String salaId,
                                                       @RequestBody Map<String, String> body) {
        EnglishBingoSala sala = service.finalizarJuego(salaId, body.getOrDefault("ganador", ""));
        if (sala == null) return ResponseEntity.notFound().build();
        broadcast(sala);
        return ResponseEntity.ok(sala);
    }

    // ── WebSocket STOMP ─────────────────────────────────────────

    @MessageMapping("/english-bingo/sync")
    public void syncSala(@Payload Map<String, String> payload) {
        String salaId = payload.get("salaId");
        EnglishBingoSala sala = service.getSala(salaId);
        if (sala != null) broadcast(sala);
    }

    private void broadcast(EnglishBingoSala sala) {
        // Broadcast a todos los suscriptores de la sala específica
        messagingTemplate.convertAndSend("/topic/english-bingo/" + sala.getId(), sala);
        // Broadcast lista de salas activas
        messagingTemplate.convertAndSend("/topic/english-bingo/salas", service.getSalasActivas());
    }
}
