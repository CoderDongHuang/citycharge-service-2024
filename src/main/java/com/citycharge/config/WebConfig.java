package com.citycharge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${avatar.upload.path:uploads/avatars}")
    private String avatarUploadPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(avatarUploadPath).toAbsolutePath().normalize().toString();
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
