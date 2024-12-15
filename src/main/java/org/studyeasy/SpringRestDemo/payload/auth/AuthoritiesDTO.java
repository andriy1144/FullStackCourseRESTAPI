package org.studyeasy.SpringRestDemo.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AuthoritiesDTO {

    @NotBlank(message = "Please enter authorities")
    @Schema(requiredMode = RequiredMode.REQUIRED,description = "Authorities", example = "USER",nullable = false)
    private String authorities;
}
