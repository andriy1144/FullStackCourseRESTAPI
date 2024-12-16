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
@NoArgsConstructor
@AllArgsConstructor
public class AlbumPayloadDTO {

    @NotBlank
    @Schema(description = "Name", example = "My Family",requiredMode = RequiredMode.REQUIRED)
    private String name;

    @NotBlank
    @Schema(description = "A description of an album", example = "This is my family album",requiredMode = RequiredMode.REQUIRED)
    private String description;
}
