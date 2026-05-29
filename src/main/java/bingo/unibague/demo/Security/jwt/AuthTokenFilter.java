package bingo.unibague.demo.Security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import bingo.unibague.demo.Security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.debug("AuthTokenFilter - Procesando request: {} {}", request.getMethod(), request.getRequestURI());
        
        try {
            String jwt = parseJwt(request);
            logger.debug("AuthTokenFilter - JWT extraído: {}", jwt != null ? "Token presente" : "No token");
            
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                logger.debug("AuthTokenFilter - Usuario del token: {}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                logger.debug("AuthTokenFilter - Authorities del usuario: {}", userDetails.getAuthorities());
                
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("AuthTokenFilter - Autenticación establecida para: {}", username);
            } else {
                logger.debug("AuthTokenFilter - Token inválido o no presente");
            }
        } catch (Exception e) {
            logger.error("AuthTokenFilter - No se puede establecer la autenticación del usuario: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        logger.debug("AuthTokenFilter - Authorization header completo: {}", headerAuth);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);
            logger.debug("AuthTokenFilter - Token extraído: {} caracteres, primeros 20: {}", 
                        token.length(), token.length() > 20 ? token.substring(0, 20) + "..." : token);
            logger.debug("AuthTokenFilter - Puntos en token: {}", token.split("\\.").length - 1);
            return token;
        }

        logger.debug("AuthTokenFilter - No se encontró token Bearer válido");
        return null;
    }
}
