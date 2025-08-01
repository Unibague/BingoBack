package bingo.unibague.demo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import bingo.unibague.demo.dto.UserDTO;
import bingo.unibague.demo.models.Carton.EstadoCarton;
import bingo.unibague.demo.models.autentication.User;
import bingo.unibague.demo.payload.request.CreateUserRequest;
import bingo.unibague.demo.repository.CartonRepository;
import bingo.unibague.demo.repository.UserRepository;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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
            user.getExistEmail(),
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

    @Autowired
private PasswordEncoder passwordEncoder;

    @Autowired
private EmailService emailService;

public UserDTO createUser(CreateUserRequest request) {
    // Limpiar username y email de números aleatorios
    String cleanUsername = cleanIdentifier(request.getUsername());
    String cleanEmail = cleanIdentifier(request.getEmail());
    String cleanExistEmail = cleanIdentifier(request.getExistEmail());
    
    logger.debug("UserService - Creando usuario con datos limpios: username={}, email={}, existEmail={}, role={}", 
                cleanUsername, cleanEmail, cleanExistEmail, request.getRole());
    logger.debug("UserService - Password presente: {}", request.getPassword() != null ? "Sí" : "No");
    logger.debug("UserService - MaxCartones: {}", request.getMaxCartones());
    
    if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
        logger.error("UserService - Password es null o vacío");
        throw new IllegalArgumentException("La contraseña no puede ser null o vacía");
    }
    
    // Verificar si ya existe un usuario con el mismo username o email (usando datos limpios)
    if (userRepository.findByUsername(cleanUsername).isPresent()) {
        logger.error("UserService - Usuario ya existe con username: {}", cleanUsername);
        throw new IllegalArgumentException("Ya existe un usuario con ese nombre de usuario");
    }
    
    if (userRepository.findByEmail(cleanEmail).isPresent()) {
        logger.error("UserService - Usuario ya existe con email: {}", cleanEmail);
        throw new IllegalArgumentException("Ya existe un usuario con ese email");
    }
    
    User user = new User();
    user.setUsername(cleanUsername);
    user.setEmail(cleanEmail);
    user.setExistEmail(cleanExistEmail);
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setMaxCartones(request.getMaxCartones());
    user.setRoles(List.of("ROLE_" + request.getRole()));
    
    logger.debug("UserService - Usuario preparado para guardar");
    User savedUser = userRepository.save(user);
    logger.debug("UserService - Usuario guardado con ID: {}", savedUser.getId());
    
    // Intentar enviar email con credenciales al email especificado
    try {
        logger.info("UserService - Intentando enviar email a: {}", cleanExistEmail);
        emailService.sendCredentials(cleanExistEmail, cleanUsername, request.getPassword(), cleanEmail);
        logger.info("UserService - Email enviado exitosamente");
    } catch (Exception e) {
        logger.error("UserService - Error enviando email: {}", e.getMessage(), e);
    }
    
    return convertToDTO(savedUser);
}

/**
 * Limpia identificadores removiendo números aleatorios agregados por el frontend
 * Ejemplo: jonatan2_1753999119022@example.com -> jonatan2@example.com
 */
private String cleanIdentifier(String identifier) {
    if (identifier == null) return null;
    
    // Remover patrón _números al final del nombre (antes del @)
    if (identifier.contains("@")) {
        // Es un email
        String[] parts = identifier.split("@");
        String localPart = parts[0].replaceAll("_\\d+$", ""); // Remover _números al final
        return localPart + "@" + parts[1];
    } else {
        // Es un username
        return identifier.replaceAll("_\\d+$", ""); // Remover _números al final
    }
}
}