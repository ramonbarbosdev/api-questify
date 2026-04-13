package com.api_nivra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Nivra")
                        .version("v1")
                        .description("API pública"))
                .servers(List.of(
                        new Server().url("https://api-nivra.ramoncode.com.br/nivra")
                        // new Server().url("http://localhost:8080/nivra")
                ));
    }
}