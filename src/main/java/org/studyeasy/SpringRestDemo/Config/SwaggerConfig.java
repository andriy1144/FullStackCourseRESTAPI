package org.studyeasy.SpringRestDemo.Config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "User API",
        version = "version 1.0",
        contact = @Contact(
            name = "Andriy", email = "andrijmurgan@gmail.com", url = "https://github.com/andriy1144"
        ),
        license = @License(
            name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
        ),
        termsOfService = "https://www.apache.org/licenses/LICENSE-2.0",
        description = "Spring Boot RestfultFul API Demo by Andriy"
    )
)
public class SwaggerConfig {
    
}