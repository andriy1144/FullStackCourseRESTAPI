package org.studyeasy.SpringRestDemo.payload.album;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PhotoPayloadDTO {

    @NotBlank(message = "Please enter new name of the photo")
    @Schema(requiredMode = RequiredMode.REQUIRED, description = "Name of the photo!", example = "On the beach")
    private String name;

    @NotBlank(message = "Please enter new photo description")
    @Schema(requiredMode = RequiredMode.REQUIRED, description = "Description of the photo!", example = "Description...")
    private String description;
}
