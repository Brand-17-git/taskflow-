package com.taskflow.seguridad;

import com.taskflow.entidades.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtService(@Value("${jwt.secret}") String secreto,
                      @Value("${jwt.expiration}") long expiracionMs) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = expiracionMs;
    }

    /** Genera un JWT con el nombre de usuario como subject y el rol como claim. */
    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(usuario.getNombreUsuario())
                .claim("rol", usuario.getRol().name())
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(clave)
                .compact();
    }

    /** Devuelve el nombre de usuario si el token es válido; null si es inválido o expiró. */
    public String validarToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
