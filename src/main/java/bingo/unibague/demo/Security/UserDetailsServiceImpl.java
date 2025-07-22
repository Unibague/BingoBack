// src/main/java/bingo/unibague/demo/security/UserDetailsServiceImpl.java
package bingo.unibague.demo.Security;

import java.util.Optional;

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
        // Intentar cargar por email primero
        Optional<User> userOptional = userRepository.findByEmail(login);
        
        // Si no se encuentra por email, intentar por username
        if (!userOptional.isPresent()) {
            userOptional = userRepository.findByUsername(login);
        }
        
        User user = userOptional.orElseThrow(() -> 
            new UsernameNotFoundException("Usuario no encontrado con email o username: " + login));

        return UserDetailsImpl.build(user);
    }
}
