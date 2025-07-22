package bingo.unibague.demo.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import bingo.unibague.demo.Security.UserDetailsImpl;
import bingo.unibague.demo.Security.jwt.JwtUtils;
import bingo.unibague.demo.models.autentication.User;
import bingo.unibague.demo.payload.request.LoginRequest;
import bingo.unibague.demo.payload.request.SignupRequest;
import bingo.unibague.demo.payload.response.JwtResponse;
import bingo.unibague.demo.payload.response.MessageResponse;
import bingo.unibague.demo.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Autentica un usuario y genera un token JWT
     * @param loginRequest datos de inicio de sesión
     * @return respuesta con el token JWT y datos del usuario
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        // Intentar autenticar al usuario
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return new JwtResponse(jwt,
                                   userDetails.getId(),
                                   userDetails.getUsername(),
                                   userDetails.getEmail(),
                                   roles);
        } catch (Exception e) {
            throw new RuntimeException("Error de autenticación: " + e.getMessage(), e);
        }
    }

    /**
     * Registra un nuevo usuario en el sistema
     * @param signUpRequest datos del nuevo usuario
     * @return mensaje de respuesta
     */
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        // Verificar si el nombre de usuario ya existe
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: ¡El nombre de usuario ya está en uso!");
        }

        // Verificar si el email ya existe
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: ¡El email ya está en uso!");
        }

        // Crear nueva cuenta de usuario
        User user = new User(
            signUpRequest.getUsername(),
            encoder.encode(signUpRequest.getPassword()),
            signUpRequest.getEmail(),
            signUpRequest.getRoles() != null ? signUpRequest.getRoles() : List.of("ROLE_USER")
        );

        userRepository.save(user);

        return new MessageResponse("¡Usuario registrado exitosamente!");
    }
}
