package br.unicatolica.ace.frequencia.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Documentação da API no Swagger UI: http://localhost:8080/swagger-ui.html */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI().info(new Info()
                .title("Sistema Web de Controle de Frequência — New Center Informática")
                .version("0.2.0")
                .description("ACE 8 · Análise e Projeto de Sistemas II · UniCatólica 2026.2. "
                        + "Chamada digital, histórico de frequência e painel de faltosos. Dados fictícios."));
    }
}
