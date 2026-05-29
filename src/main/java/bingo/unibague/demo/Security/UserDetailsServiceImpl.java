// src/main/java/bingo/unibague/demo/security/UserDetailsServiceImpl.java
package bingo.unibague.demo.Security;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bingo.unibague.demo.models.autentication.User;
import bingo.unibague.demo.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    
    @Autowired
    UserRepository userRepository;

    /**
     * Carga un usuario por su nombre de usuario o email
     * @param login puede ser username o email
     * @return detalles del usuario para autenticación
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        logger.info("UserDetailsServiceImpl - Buscando usuario: {}", login);
        
        // Intentar cargar por email primero
        Optional<User> userOptional = userRepository.findByEmail(login);
        logger.info("UserDetailsServiceImpl - Búsqueda por email: {}", userOptional.isPresent() ? "Encontrado" : "No encontrado");
        
        // Si no se encuentra por email, intentar por username
        if (!userOptional.isPresent()) {
            userOptional = userRepository.findByUsername(login);
            logger.info("UserDetailsServiceImpl - Búsqueda por username: {}", userOptional.isPresent() ? "Encontrado" : "No encontrado");
        }
        
        User user = userOptional.orElseThrow(() -> {
            logger.error("UserDetailsServiceImpl - Usuario no encontrado: {}", login);
            return new UsernameNotFoundException("Usuario no encontrado con email o username: " + login);
        });

        logger.info("UserDetailsServiceImpl - Usuario encontrado: {} ({})", user.getUsername(), user.getEmail());
        return UserDetailsImpl.build(user);
    }
}
