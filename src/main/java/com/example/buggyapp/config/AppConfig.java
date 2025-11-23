package com.example.buggyapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {

    // DEFECT: Hardcoded credentials in configuration
    private static final String DB_USERNAME = "admin";
    private static final String DB_PASSWORD = "admin123";
    private static final String API_KEY = "sk_live_1234567890abcdef";

    // DEFECT: Exposing credentials via bean
    @Bean
    public Map<String, String> databaseConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("username", DB_USERNAME);
        config.put("password", DB_PASSWORD);
        config.put("url", "jdbc:h2:mem:testdb");
        return config;
    }

    // DEFECT: Overly permissive CORS configuration
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // DEFECT: Allowing all origins
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        // DEFECT: Allowing credentials with wildcard origin
                        .allowCredentials(false); // Had to set to false to make it work with *
            }
        };
    }

    // DEFECT: Bean returning mutable static field
    private static Map<String, Object> globalSettings = new HashMap<>();

    @Bean
    public Map<String, Object> globalSettings() {
        // DEFECT: Returning mutable map
        globalSettings.put("maxUploadSize", 10485760); // 10MB
        globalSettings.put("sessionTimeout", 3600);
        globalSettings.put("debug", true); // DEFECT: Debug enabled
        return globalSettings;
    }

    // DEFECT: Hardcoded encryption key
    @Bean
    public String encryptionKey() {
        return "ThisIsAVerySecretKey123!@#";
    }

    // DEFECT: Exposing internal configuration
    @Bean
    public String systemInfo() {
        return "Server: " + System.getProperty("os.name") +
               ", Java: " + System.getProperty("java.version") +
               ", User: " + System.getProperty("user.name");
    }
}
