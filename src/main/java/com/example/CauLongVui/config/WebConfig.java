package com.example.CauLongVui.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ánh xạ /uploads/** vào thư mục vật lý src/main/resources/static/uploads/
        // file:D:/HUTECH/J2EE/CauLongVui/src/main/resources/static/uploads/
        String uploadPath = Paths.get("src/main/resources/static/uploads/").toAbsolutePath().toUri().toString();
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}
