package bingo.unibague.demo.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bingo.unibague.demo.models.BingoGame;
import bingo.unibague.demo.payload.response.HistorialCartonResponse;
import bingo.unibague.demo.payload.response.HistorialPartidaResponse;
import bingo.unibague.demo.repository.BingoRepository;

@Service
@Transactional
public class BingoGameService {
    
    @Autowired
    private BingoRepository gameRepository;
    
    @Autowired
    private BingoCardService cardService;
    
    private final Random random = new Random();
    
    public BingoGame createGame(String nombre, String userEmail, BingoGame.TipoJuego tipoJuego) {
        BingoGame game = new BingoGame();
        game.setNombre(nombre);
        game.setUserEmail(userEmail);
        game.setTipoJuego(tipoJuego);
        
        game = gameRepository.save(game);
        
        // Generar 10 cartones automáticamente
        cardService.generateCardsForGame(game, 10);
        
        return game;
    }
    
    public Optional<BingoGame> getGameById(Long gameId) {
        return gameRepository.findById(gameId);
    }
    
    public List<BingoGame> getGamesByUser(String userEmail) {
        return gameRepository.findByUserEmailOrderByFechaCreacionDesc(userEmail);
    }
    
    public List<BingoGame> getActiveGames() {
        return gameRepository.findByEstado(BingoGame.EstadoJuego.ACTIVO);
    }
    
    public Optional<BingoGame> startGame(Long gameId) {
        return gameRepository.findById(gameId)
                .map(game -> {
                    if (game.getEstado() == BingoGame.EstadoJuego.ESPERANDO) {
                        game.setEstado(BingoGame.EstadoJuego.ACTIVO);
                        game.setFechaInicio(LocalDateTime.now());
                        return gameRepository.save(game);
                    }
                    return game;
                });
    }
    
    public Optional<BingoGame> endGame(Long gameId) {
        return gameRepository.findById(gameId)
                .map(game -> {
                    game.setEstado(BingoGame.EstadoJuego.FINALIZADO);
                    game.setFechaFin(LocalDateTime.now());
                    return gameRepository.save(game);
                });
    }
    
    public Optional<Integer> callNextNumber(Long gameId) {
        return gameRepository.findById(gameId)
                .map(game -> {
                    if (!game.isActive()) {
                        return null;
                    }
                    
                    // Encontrar números disponibles (1-75)
                    List<Integer> availableNumbers = new java.util.ArrayList<>();
                    for (int i = 1; i <= 75; i++) {
                        if (!game.getNumerosCantados().contains(i)) {
                            availableNumbers.add(i);
                        }
                    }
                    
                    if (availableNumbers.isEmpty()) {
                        endGame(gameId);
                        return null;
                    }
                    
                    // Seleccionar número aleatorio
                    int nextNumber = availableNumbers.get(random.nextInt(availableNumbers.size()));
                    game.addCalledNumber(nextNumber);
                    
                    gameRepository.save(game);
                    return nextNumber;
                });
    }
    
    public List<BingoGame> getAllGames() {
        return gameRepository.findAll();
    }
    
    public void deleteGame(Long gameId) {
        gameRepository.deleteById(gameId);
    }
    
    public long countGamesByUser(String userEmail) {
        return gameRepository.countByUserEmail(userEmail);
    }
    
    public long countFinishedGamesByUser(String userEmail) {
        return gameRepository.countJuegosFinalizadosByUserEmail(userEmail);
    }
    
    /**
     * Obtener historial detallado de partidas del usuario
     * @param userEmail email del usuario
     * @return lista de partidas con información detallada de cartones
     */
    public List<HistorialPartidaResponse> getHistorialPartidas(String userEmail) {
        List<BingoGame> juegos = gameRepository.findByUserEmailOrderByFechaCreacionDesc(userEmail);
        
        return juegos.stream()
                .map(this::convertToHistorialPartidaResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convertir BingoGame a HistorialPartidaResponse
     */
    private HistorialPartidaResponse convertToHistorialPartidaResponse(BingoGame game) {
        // Convertir cartones
        List<HistorialCartonResponse> cartones = game.getCartones().stream()
                .map(carton -> new HistorialCartonResponse(
                    carton.getId(),
                    carton.getNumeroCarton(),
                    carton.isEsGanador(),
                    carton.getFechaCreacion(),
                    carton.getNumbers(),
                    carton.getMarkedNumbers()
                ))
                .collect(Collectors.toList());
        
        return new HistorialPartidaResponse(
            game.getId(),
            game.getNombre(),
            game.getFechaInicio(),
            game.getFechaFin(),
            game.getEstado().name(),
            cartones,
            game.getNumerosCantados().size(),
            game.getPuntajeTotal()
        );
    }
}
