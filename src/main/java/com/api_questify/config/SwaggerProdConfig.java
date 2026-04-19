package com.api_questify.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@Profile("prod")
public class SwaggerProdConfig {

        @Bean
        public OpenAPI openApiProd() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("API Questify")
                                                .version("v1")
                                                .description("API pública"))
                                .servers(List.of(
                                                new Server().url("https://api-questify.ramoncode.com.br/questify")));
        }
}