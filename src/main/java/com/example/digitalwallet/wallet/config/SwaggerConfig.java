package com.example.digitalwallet.wallet.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()

                // Production server URL
                .addServersItem(new io.swagger.v3.oas.models.servers.Server()
                        .url("https://digitalwalletbackendsystem-production.up.railway.app")
                        .description("Production server"))

                // JWT Security
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))

                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))

                // API Information
                .info(new Info()
                        .title("Wallet Management API")
                        .version("1.0")
                        .description("Secure Wallet & Transaction APIs")
                        .contact(new Contact()
                                .name("Deepak")
                                .email("deepakmahade777@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")))

                // External Docs
                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation"));
    }
}