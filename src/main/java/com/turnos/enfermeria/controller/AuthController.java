package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.RegistroDTO;
import com.turnos.enfermeria.model.entity.Usuario;
import com.turnos.enfermeria.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@Tag(name = "Gestion Sesion", description = "gestion sesiones de usuario")
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroDTO registroDTO) {
        try {
            Usuario usuario = authService.registrarUsuario(registroDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "documento", usuario.getPersona().getDocumento(),
                            "mensaje", "Usuario registrado exitosamente"
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Endpoint para cerrar sesion del usuario logueado")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        // Invalidar la sesión
        request.getSession().invalidate();

        // Limpiar la autenticación
        SecurityContextHolder.clearContext();

        // Eliminar cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                cookie.setValue("");
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }

        return ResponseEntity.ok("Sesión cerrada exitosamente");
    }
}