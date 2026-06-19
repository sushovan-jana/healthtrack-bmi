package com.healthtrack.bmi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI healthTrackOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HealthTrack BMI API")
                        .description("REST API documentation for the HealthTrack BMI single-doctor healthcare platform")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("CookieAuth"))
                .components(new Components()
                        .addSecuritySchemes("CookieAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("JWT-TOKEN")
                                .description("Secure HttpOnly cookie containing the signed JWT token")));
    }
}
