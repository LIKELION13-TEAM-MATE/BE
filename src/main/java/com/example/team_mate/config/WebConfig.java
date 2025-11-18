package com.example.team_mate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "/uploads/**" 로 시작하는 주소를 요청
        registry.addResourceHandler("/uploads/**")
                // 내 컴퓨터의 실제 "uploads/" 폴더에서 파일을 찾아서 보여줌
                .addResourceLocations("file:uploads/");
    }
}