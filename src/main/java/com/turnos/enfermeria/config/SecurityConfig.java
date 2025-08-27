package com.turnos.enfermeria.config;

import com.turnos.enfermeria.service.UsuarioDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;

    @Autowired
    public SecurityConfig(UsuarioDetailsService usuarioDetailsService) {
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization"));

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // ✅ Permite todo sin autenticación
                )
                .cors(cors -> cors.configurationSource(request -> corsConfiguration))
                .formLogin(AbstractHttpConfigurer::disable) // ⛔️ Desactiva login
                .httpBasic(AbstractHttpConfigurer::disable) // ⛔️ Desactiva auth básica
                .logout(AbstractHttpConfigurer::disable);   // ⛔️ Desactiva logout
//        httpSecurity
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        // Permitir sin autenticación (rutas públicas)
//                        .requestMatchers("/auth/**", "/public/**").permitAll()
//
//                        // Restringir por rol (opcional)
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/usuario/**").hasRole("ADMIN")
//                        .requestMatchers("/roles/**").hasRole("ADMIN")
//                        .requestMatchers("/macroprocesos/**").hasRole("ADMIN")
//                        .requestMatchers("/procesos/**").hasRole("ADMIN")
//                        .requestMatchers("/servicio/**").hasRole("ADMIN")
//                        .requestMatchers("/procesosAtencion/**").hasRole("ADMIN")
//                        .requestMatchers("/tutulosFormacionAcademica/**").hasRole("ADMIN")
//                        .requestMatchers("/tipoFormacionAcademica/**").hasRole("ADMIN")
//                        .requestMatchers("/servicio/**").hasRole("ADMIN")
//                        .requestMatchers("/bloqueServicio/**").hasRole("ADMIN")
//                        .requestMatchers("/seccionesServicio/**").hasRole("ADMIN")
//                        .requestMatchers("/contrato/**").hasAnyRole("ADMIN", "GESTOR_TURNOS")
//                        .requestMatchers("/procesosContrato/**").hasAnyRole("ADMIN","GESTOR_TURNOS")
//                        .requestMatchers("/tipoatencion/**").hasAnyRole("ADMIN","GESTOR_TURNOS")
//                        .requestMatchers("/tipoturno/**").hasAnyRole("ADMIN","GESTOR_TURNOS")
//
//
//
//                        // Todas las demás rutas requieren autenticación
//                        .anyRequest().authenticated()
//                )
//                        .formLogin(withDefaults())
//                        .httpBasic(withDefaults())
//                        .logout(logout -> logout
//                                .logoutUrl("/api/logout")
//                                .logoutSuccessUrl("/swagger-ui.html")
//                                .invalidateHttpSession(true)
//                                .deleteCookies("JSESSIONID")
//                                .clearAuthentication(true)
//                        ); // También habilita login por curl/postman

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(usuarioDetailsService); // Usa servicio real
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Habilitar BCrypt
    }

    //    @Bean
//    public AuthenticationProvider authenticationProvider(){
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(passwordEncoder());
//        provider.setUserDetailsService(userDetailsService());
//        return provider;
//    }

//    @Bean
//    public UserDetailsService userDetailsService(){
//        UserDetails userDetails = User.withUsername("JorgeIbarra")
//                .password("12345")
//                .roles("ADMIN")
//                .authorities("READ","CREATE")
//                .build();
//        return  new InMemoryUserDetailsManager(userDetails);
//    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        //return new BCryptPasswordEncoder();
//        return NoOpPasswordEncoder.getInstance();
//    }
}
