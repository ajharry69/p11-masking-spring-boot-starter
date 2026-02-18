package com.github.ajharry69.demo.apidoc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;


@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Harrison",
                        email = "oharry0535@gmail.com"
                ),
                description = "OpenAPI documentation for KCB Test",
                title = "OpenAPI specification - KCB Test",
                version = "v1"
        ),
        servers = {@Server(
                description = "Development",
                url = "http://localhost:8080"
        )}
)
public class OpenAPIConfig {
}