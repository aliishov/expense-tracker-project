package com.example.expensetracker.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Raul Alishov",
                        email = "alishov7394@gmail.com",
                        url = "https://github.com/aliishov"
                ),
                description = "OpenApi Documentation",
                title = "Authentication Service",
                version = "1.0"
        )
)
public class SwaggerConfig {
}
