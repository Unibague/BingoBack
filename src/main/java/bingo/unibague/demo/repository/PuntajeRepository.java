package bingo.unibague.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bingo.unibague.demo.models.Puntaje;

@Repository
public interface PuntajeRepository extends JpaRepository<Puntaje, Long> {
    
    // Encontrar puntajes por usuario
    List<Puntaje> findByUserEmailOrderByFechaJuegoDesc(String userEmail);
    
    // Obtener ranking general (top puntajes)
    @Query("SELECT p FROM Puntaje p ORDER BY p.puntaje DESC")
    List<Puntaje> findTopPuntajes();
    
    // Obtener estadísticas de un usuario
    @Query("SELECT " +
           "COUNT(p) as juegosJugados, " +
           "SUM(CASE WHEN p.esGanador = true THEN 1 ELSE 0 END) as juegosGanados, " +
           "COALESCE(SUM(p.puntaje), 0) as puntosTotales " +
           "FROM Puntaje p WHERE p.userEmail = :userEmail")
    Object[] getEstadisticasUsuario(@Param("userEmail") String userEmail);
    
    // Contar juegos jugados por usuario
    long countByUserEmail(String userEmail);
    
    // Contar juegos ganados por usuario
    long countByUserEmailAndEsGanadorTrue(String userEmail);
    
    // Suma total de puntajes por usuario
    @Query("SELECT COALESCE(SUM(p.puntaje), 0) FROM Puntaje p WHERE p.userEmail = :userEmail")
    Integer sumPuntajesByUserEmail(@Param("userEmail") String userEmail);
}
