package bingo.unibague.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bingo.unibague.demo.models.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
     // Encontrar jugadores por juego
    List<Player> findByGameId(Long gameId);
    
    // Encontrar jugador por nombre y juego
    Optional<Player> findByNameAndGameId(String name, Long gameId);
    
    // Contar jugadores por juego
    long countByGameId(Long gameId);
    
    // Encontrar jugadores con cartones ganadores
    List<Player> findByBingoCardEsGanadorTrue();
}