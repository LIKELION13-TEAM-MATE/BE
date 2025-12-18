package com.example.team_mate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8081");
        devServer.setDescription("로컬 개발 서버");

        Server prodServer = new Server();
        prodServer.setUrl("https://team-mate.shop");
        prodServer.setDescription("운영 서버");

        return new OpenAPI()
                .servers(List.of(devServer, prodServer)); // 리스트에 추가
    }
}
