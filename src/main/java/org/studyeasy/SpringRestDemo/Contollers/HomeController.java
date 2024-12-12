package org.studyeasy.SpringRestDemo.Contollers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
public class HomeController {

    @GetMapping("/")
    public String demo(){
        return "Hello World!";
    }

    @GetMapping("/test")
    @Tag(name = "Test", description = "The Test Api") //Swagger annotation to add tag and description
    @SecurityRequirement(name = "studyeasy-demo-api") //Enabling OAuth2 security
    public String test() {
        return "Test API";
    }
    
}
