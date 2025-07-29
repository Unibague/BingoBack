package bingo.unibague.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bingo.unibague.demo.models.Carton;
import bingo.unibague.demo.models.Carton.EstadoCarton;
import bingo.unibague.demo.models.autentication.User;

@Repository
public interface CartonRepository extends JpaRepository<Carton, Long> {
    
    // Buscar cartones por usuario
    List<Carton> findByUser(User user);
    
    List<Carton> findByUserId(Long userId);
    
    // Buscar por número de cartón
    Optional<Carton> findByNumeroCarton(String numeroCarton);
    
    // Verificar si existe un número de cartón
    Boolean existsByNumeroCarton(String numeroCarton);
    
    // Buscar cartones por estado
    List<Carton> findByEstado(EstadoCarton estado);
    
    // Buscar cartones por usuario y estado
    List<Carton> findByUserAndEstado(User user, EstadoCarton estado);
    
    List<Carton> findByUserIdAndEstado(Long userId, EstadoCarton estado);
    
    // Contar cartones activos por usuario
    @Query("SELECT COUNT(c) FROM Carton c WHERE c.user.id = :userId AND c.estado = :estado")
    Long countByUserIdAndEstado(@Param("userId") Long userId, @Param("estado") EstadoCarton estado);
    
    // Contar cartones activos por usuario (método directo)
    Long countByUserIdAndEstados(Long userId, EstadoCarton estado);
    
    // Obtener cartones activos de un usuario
    @Query("SELECT c FROM Carton c WHERE c.user.id = :userId AND c.estado = 'ACTIVO'")
    List<Carton> findCartonesActivosByUserId(@Param("userId") Long userId);
    
    // Obtener cartones inactivos de un usuario
    @Query("SELECT c FROM Carton c WHERE c.user.id = :userId AND c.estado = 'INACTIVO'")
    List<Carton> findCartonesInactivosByUserId(@Param("userId") Long userId);
    
    // Verificar si un usuario puede activar más cartones
    @Query("SELECT CASE WHEN COUNT(c) < u.maxCartones THEN true ELSE false END " +
           "FROM Carton c RIGHT JOIN User u ON c.user.id = u.id " +
           "WHERE u.id = :userId AND (c.estado = 'ACTIVO' OR c.estado IS NULL)")
    Boolean canUserActivateMoreCartones(@Param("userId") Long userId);
    
    // Obtener todos los cartones de un usuario ordenados por fecha de creación
    List<Carton> findByUserIdOrderByFechaCreacionDesc(Long userId);
    
    // Eliminar cartones por usuario
    void deleteByUser(User user);
    
    void deleteByUserId(Long userId);
}