package com.proyecto.examen.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gestión de Órdenes y Conductores")
                        .version("1.0")
                        .description("Examen Técnico - Spring Boot 2.7 + PostgreSQL"));
    }
}
