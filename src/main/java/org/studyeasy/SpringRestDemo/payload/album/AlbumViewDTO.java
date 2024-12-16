package org.studyeasy.SpringRestDemo.payload.album;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlbumViewDTO {
    private Long id;

    private String name;

    private String description;
}
