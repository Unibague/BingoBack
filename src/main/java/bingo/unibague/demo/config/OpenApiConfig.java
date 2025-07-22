package bingo.unibague.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bingoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bingo API")
                        .description("API para el juego de Bingo")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Desarrollador")
                                .email("jonatan25sanchez@gmail.com")));
    }
}
