package com.citycharge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${avatar.upload.path:uploads/avatars}")
    private String avatarUploadPath;
    
    @Value("${station.upload.path:uploads/stations}")
    private String stationUploadPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String avatarAbsolutePath = Paths.get(avatarUploadPath).toAbsolutePath().normalize().toString();
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + avatarAbsolutePath + "/");
        
        String stationAbsolutePath = Paths.get(stationUploadPath).toAbsolutePath().normalize().toString();
        registry.addResourceHandler("/uploads/stations/**")
                .addResourceLocations("file:" + stationAbsolutePath + "/");
    }
    
    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }
    
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, responseBodyConverter());
    }
}
