package bingo.unibague.demo.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    /**
     * Autentica un usuario y genera un token JWT
     * @param loginRequest datos de inicio de sesión
     * @return respuesta con el token JWT y datos del usuario
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        // Intentar autenticar al usuario
        try {
            // Usar directamente el email para autenticación
            String loginIdentifier = loginRequest.getEmail();
            logger.debug("AuthService - Intento de login con: {}", loginIdentifier);
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginIdentifier, loginRequest.getPassword()));

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
    @Transactional
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
        String rawPassword = signUpRequest.getPassword();
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new RuntimeException("Error: La contraseña es obligatoria. El administrador debe especificar una contraseña.");
        }
        
        // Crear nueva cuenta de usuario con existEmail
        User user = new User(
            signUpRequest.getUsername(),
            encoder.encode(rawPassword),
            signUpRequest.getEmail(),
            signUpRequest.getExistEmail(), // Guardar el email donde enviar credenciales
            signUpRequest.getRoles() != null ? signUpRequest.getRoles() : List.of("ROLE_USER")
        );
        
        // Guardar usuario en la base de datos PRIMERO
        User savedUser = userRepository.save(user);
        System.out.println("Usuario guardado en BD con ID: " + savedUser.getId());

        // DESPUÉS de guardar, enviar credenciales por correo
        try {
            // Usar existEmail si está disponible, sino usar el email del usuario
            String emailDestino = savedUser.getExistEmail() != null && !savedUser.getExistEmail().isBlank() 
                ? savedUser.getExistEmail() 
                : savedUser.getEmail();
            
            System.out.println("Enviando credenciales a: " + emailDestino);
            emailService.sendCredentials(emailDestino, savedUser.getUsername(), rawPassword);
            System.out.println("Credenciales enviadas exitosamente a: " + emailDestino);
            
        } catch (MailException e) {
            // Log del error pero no detiene el registro ya que el usuario ya está guardado
            System.err.println("Error enviando correo: " + e.getMessage());
            e.printStackTrace();
            // Podrías usar un logger aquí: logger.error("Error enviando correo", e);
        }

        return new MessageResponse("¡Usuario registrado exitosamente!");
    }
}
