package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.RegistroDTO;
import com.turnos.enfermeria.model.entity.Persona;
import com.turnos.enfermeria.model.entity.Usuario;
import com.turnos.enfermeria.repository.PersonaRepository;
import com.turnos.enfermeria.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    //private final JwtTokenProvider tokenProvider;

    @Transactional
    public Usuario registrarUsuario(RegistroDTO registroDTO) {
       // Buscar persona por documento
        Persona persona = personaRepository.findByDocumento(registroDTO.getDocumento())
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con documento: " + registroDTO.getDocumento()));

        // Verificar si ya tiene usuario
        if (usuarioRepository.findByPersona_Documento(registroDTO.getDocumento()).isPresent()) {
            throw new IllegalStateException("Ya existe un usuario para este documento");
        }

        // Crear y guardar usuario
        Usuario usuario = new Usuario();
        usuario.setIdPersona(persona.getIdPersona());
        usuario.setPersona(persona);
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setEstado(true);

        return usuarioRepository.save(usuario);
    }

//    public String autenticarUsuario(LoginDTO loginDTO) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginDTO.getIdPersona().toString(),
//                        loginDTO.getPassword()
//                )
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        return tokenProvider.generateToken(authentication);
//    }
}
