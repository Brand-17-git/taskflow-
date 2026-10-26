package com.taskflow.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIJO_BEARER = "Bearer ";

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String cabecera = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (cabecera != null && cabecera.startsWith(PREFIJO_BEARER)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = cabecera.substring(PREFIJO_BEARER.length());
            String nombreUsuario = jwtService.validarToken(token);

            if (nombreUsuario != null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(nombreUsuario);
                    UsernamePasswordAuthenticationToken autenticacion =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(autenticacion);
                } catch (UsernameNotFoundException e) {
                    // El usuario del token ya no existe: se deja pasar sin autenticar.
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
