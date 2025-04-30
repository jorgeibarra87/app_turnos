package com.turnos.enfermeria.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "APP PROGRAMACIÓN TURNOS HUSJ",
                description = "Aplicación para Gestionar los Turnos del Personal Del Hospital Universitario San José",
                version = "1.0.0",
                contact = @Contact(
                        name = "Jorge Ibarra",
                        url = "https://hospitalsanjose.gov.co/",
                        email = "jorgeibarra87@gmail.com"
                )
        ),
        servers = {
                @Server(
                        description = "DEV SERVER",
                        url = "http://localhost:***REMOVED***"
                )
        },
        security = {
                @SecurityRequirement(name = "basicAuth"),
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic",
        description = "Autenticación básica con usuario y contraseña"
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Autenticación con JWT token"
)
public class SwaggerConfig {

//    @Bean
//    public GroupedOpenApi allApi() {
//        return GroupedOpenApi.builder()
//                .group("all")
//                .pathsToMatch("/**") // Captura todos los endpoints
//                .build();
//    }
//    @Bean
//    public GroupedOpenApi bloquesServicioApi() {
//        return GroupedOpenApi.builder()
//                .group("bloques-servicio")
//                .displayName("📅 Bloques de Servicio")
//                .pathsToMatch("/bloqueServicio/**")
//                .build();
//    }
//
//    @Bean
//    public GroupedOpenApi turnosApi() {
//        return GroupedOpenApi.builder()
//                .group("turnos")
//                .displayName("⏰ Gestión de Turnos")
//                .pathsToMatch("/turnos/**")
//                .build();
//    }
//
//    @Bean
//    public GroupedOpenApi usuariosApi() {
//        return GroupedOpenApi.builder()
//                .group("usuarios")
//                .displayName("👤 Administración de Usuarios")
//                .pathsToMatch("/usuarios/**", "/auth/**")
//                .build();
//    }
}
