package com.example.ecommerce_order_service.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ecommerce Order Service")
                        .version("1.0")
                        .description("API documentation for theEcommerce Order Service")
                        .contact(new Contact()
                                .name("Eric")
                                .email("ericmuganga@outlook.com")
                                .url("https://eric-muganga-portfolio.vercel.app/")
                        )
                )
                .externalDocs(new ExternalDocumentation()
                .description("Project GitHub Repository")
                .url("https://github.com/IvanAndrau/ecommerce-order-service"));
    }
}
