// src/main/java/bingo/unibague/demo/services/UserInitializationService.java
package bingo.unibague.demo.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import bingo.unibague.demo.models.autentication.User;
import bingo.unibague.demo.repository.UserRepository;

@Service
public class UserInitializationService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UserInitializationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

   @Override
    public void run(String... args) throws Exception {
    initializeAdminUser();
    
    // Verificar todos los usuarios en la base de datos
    List<User> allUsers = userRepository.findAll();
    System.out.println("Total de usuarios en la base de datos: " + allUsers.size());
    
    for (User user : allUsers) {
        System.out.println("Usuario: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Roles: " + user.getRoles());
        System.out.println("------------------------");
    }
    
    // Verificar si existe el usuario específico
    Optional<User> userByEmail = userRepository.findByEmail("jonatan@example.com");
    if (userByEmail.isPresent()) {
        System.out.println("Usuario encontrado con email jonatan@example.com");
    } else {
        System.out.println("No se encontró usuario con email jonatan@example.com");
        
        // Verificar si hay un usuario con email similar
        List<User> allUsersWithSimilarEmail = userRepository.findAll().stream()
                .filter(u -> u.getEmail().contains("jonatan"))
                .collect(Collectors.toList());
        
        if (!allUsersWithSimilarEmail.isEmpty()) {
            System.out.println("Usuarios con email similar encontrados:");
            for (User u : allUsersWithSimilarEmail) {
                System.out.println("Email: " + u.getEmail());
            }
        }
    }
    }
    
   
    private void initializeAdminUser() {
    // Crear usuario admin si no existe
    if (!userRepository.existsByUsername("admin")) {
        try {
            User admin = new User(
                "admin", // Cambiado a "admin" para ser más claro
                passwordEncoder.encode("js25112005"),
                "jonatanan@example.com",
                Arrays.asList("ROLE_ADMIN")
            );
            userRepository.save(admin);
            logger.info("Usuario administrador creado exitosamente");
        } catch (Exception e) {
            logger.error("Error al crear usuario administrador: {}", e.getMessage());
        }
    } else {
        logger.info("Usuario administrador ya existe, no es necesario crearlo");
    }
    
    // Add the test user creation inside the same method
    if (!userRepository.existsByEmail("jonatan@example.com")) {
        try {
            User testUser = new User(
                    "jonatan",
                    passwordEncoder.encode("123456"),
                    "jonatan@example.com",
                    Arrays.asList("ROLE_USER")
                );
                userRepository.save(testUser);
                logger.info("Usuario de prueba creado exitosamente");
            } catch (Exception e) {
                logger.error("Error al crear usuario de prueba: {}", e.getMessage());
            }
        }
    }
}