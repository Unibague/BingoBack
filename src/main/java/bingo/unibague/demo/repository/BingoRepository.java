package bingo.unibague.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bingo.unibague.demo.models.BingoGame;

@Repository
public interface BingoRepository extends JpaRepository<BingoGame, Long> {
     List<BingoGame> findByUserEmailOrderByFechaCreacionDesc(String userEmail);
    
    // Encontrar juegos activos del usuario
    List<BingoGame> findByUserEmailAndEstado(String userEmail, BingoGame.EstadoJuego estado);
    
    // Encontrar juego por ID y usuario
    Optional<BingoGame> findByIdAndUserEmail(Long id, String userEmail);
    
    // Contar juegos por usuario
    long countByUserEmail(String userEmail);
    
    // Encontrar juegos activos en general
    List<BingoGame> findByEstado(BingoGame.EstadoJuego estado);
    
    // Encontrar juegos por tipo
    List<BingoGame> findByTipoJuego(BingoGame.TipoJuego tipoJuego);
    
    // Query personalizada para estadísticas
    @Query("SELECT COUNT(g) FROM BingoGame g WHERE g.userEmail = :userEmail AND g.estado = 'FINALIZADO'")
    long countJuegosFinalizadosByUserEmail(@Param("userEmail") String userEmail);

}