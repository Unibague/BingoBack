package bingo.unibague.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bingo.unibague.demo.models.BingoCard;

@Repository
public interface BingoCardRepository extends JpaRepository<BingoCard, Long> {
    
    // Encontrar cartones por juego
    List<BingoCard> findByGameIdOrderByNumeroCarton(Long gameId);
    
    // Encontrar cartones ganadores de un juego
    List<BingoCard> findByGameIdAndEsGanadorTrue(Long gameId);
    
    // Contar cartones de un juego
    long countByGameId(Long gameId);
    
    // Encontrar cartón por juego y número
    BingoCard findByGameIdAndNumeroCarton(Long gameId, Integer numeroCarton);
    
    // Encontrar todos los cartones ganadores
    List<BingoCard> findByEsGanadorTrue();
}
