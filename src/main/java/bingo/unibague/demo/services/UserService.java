package bingo.unibague.demo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bingo.unibague.demo.dto.UserDTO;
import bingo.unibague.demo.models.Carton.EstadoCarton;
import bingo.unibague.demo.models.autentication.User;
import bingo.unibague.demo.repository.CartonRepository;
import bingo.unibague.demo.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartonRepository cartonRepository;

    /**
     * Obtiene todos los usuarios como DTOs
     * @return lista de UserDTO
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un usuario por ID como DTO
     * @param id ID del usuario
     * @return UserDTO opcional
     */
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Obtiene un usuario por username como DTO
     * @param username nombre de usuario
     * @return UserDTO opcional
     */
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDTO);
    }

    /**
     * Actualiza el número máximo de cartones para un usuario
     * @param userId ID del usuario
     * @param maxCartones nuevo límite de cartones
     * @return true si se actualizó correctamente
     */
    public boolean updateMaxCartones(Long userId, Integer maxCartones) {
        if (maxCartones == null || maxCartones < 0) {
            throw new IllegalArgumentException("El número máximo de cartones debe ser mayor o igual a 0");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setMaxCartones(maxCartones);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    /**
     * Valida si un usuario puede tener más cartones activos
     * @param userId ID del usuario
     * @param cartonesActivos número actual de cartones activos
     * @return true si puede tener más cartones
     */
    public boolean canHaveMoreCartones(Long userId, Integer cartonesActivos) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return cartonesActivos < user.getMaxCartones();
        }
        return false;
    }

    /**
     * Obtiene el límite de cartones para un usuario
     * @param userId ID del usuario
     * @return límite de cartones o null si no existe el usuario
     */
    public Integer getMaxCartonesForUser(Long userId) {
        return userRepository.findById(userId)
                .map(User::getMaxCartones)
                .orElse(null);
    }

    /**
     * Convierte una entidad User a UserDTO
     * @param user entidad User
     * @return UserDTO
     */
    private UserDTO convertToDTO(User user) {
        // Calcular cartones activos reales si existe el repositorio
        Integer cartonesActivos = 0;
        try {
            Long count = cartonRepository.countByUserIdAndEstado(user.getId(), EstadoCarton.ACTIVO);
            cartonesActivos = count.intValue();
        } catch (Exception e) {
            // Si hay error, mantener en 0
            cartonesActivos = 0;
        }
        
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRoles(),
            user.getMaxCartones(),
            cartonesActivos
        );
    }

    /**
     * Obtiene usuarios con rol específico
     * @param role rol a filtrar
     * @return lista de UserDTO con ese rol
     */
    public List<UserDTO> getUsersByRole(String role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(role))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene solo los jugadores (usuarios con rol ROLE_USER)
     * @return lista de UserDTO de jugadores
     */
    public List<UserDTO> getPlayers() {
        return getUsersByRole("ROLE_USER");
    }
}