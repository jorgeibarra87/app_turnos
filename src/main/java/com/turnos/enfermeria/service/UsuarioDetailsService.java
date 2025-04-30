package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.entity.Usuario;
import com.turnos.enfermeria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String documento) throws UsernameNotFoundException {
        System.out.println("Buscando usuario con documento: " + documento);
        Usuario usuario = usuarioRepository.findByPersona_Documento(documento)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con documento: " + documento));

        return new User(
                usuario.getPersona().getDocumento(), // username = documento
                usuario.getPassword(),
                usuario.getEstado(), true, true, true,
                usuario.getRoles().stream()
                        .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getRol()))
                        .collect(Collectors.toList())
        );
    }
}
