package com.api_questify.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@Profile("dev")
public class SwaggerDevConfig {

        @Bean
        public OpenAPI openApiDev() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("API Questify - DEV")
                                                .version("v1")
                                                .description("Ambiente de desenvolvimento"))
                                .servers(List.of(
                                                new Server().url("http://localhost:8080/questify")));
        }
}