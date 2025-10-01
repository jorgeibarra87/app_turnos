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
        )
)

public class SwaggerConfig {

}
