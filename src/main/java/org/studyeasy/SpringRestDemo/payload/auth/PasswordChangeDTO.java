package org.studyeasy.SpringRestDemo.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordChangeDTO {
    @Size(min = 6, max = 20,message = "Size of a password should be between 6 and 20!")
    @Schema(requiredMode = RequiredMode.REQUIRED,description = "Password", example = "123116236", maxLength = 20, minLength = 6)
    private String password;
}
