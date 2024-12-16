package org.studyeasy.SpringRestDemo.Contollers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.studyeasy.SpringRestDemo.Entities.Account;
import org.studyeasy.SpringRestDemo.Entities.Album;
import org.studyeasy.SpringRestDemo.Sevice.AccountService;
import org.studyeasy.SpringRestDemo.Sevice.AlbumService;
import org.studyeasy.SpringRestDemo.Util.Constants.AlbumError;
import org.studyeasy.SpringRestDemo.payload.album.AlbumPayloadDTO;
import org.studyeasy.SpringRestDemo.payload.album.AlbumViewDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/album")
@Slf4j
@Tag(name = "Album controller!", description = "Controller for album and photo management!")
public class AlbumController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private AlbumService albumService;

    @PostMapping(value = "/albums/add", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "200", description = "Album was succesfully added!")
    @Operation(summary = "Creats new album!")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<AlbumViewDTO> addAlbum(@Valid @RequestBody AlbumPayloadDTO albumPayloadDTO,Authentication authentication) {
        try{
            Album album = new Album();
            album.setDescription(albumPayloadDTO.getDescription());
            album.setName(albumPayloadDTO.getName());
            album.setAccount(accountService.findByEmail(authentication.getName()).get());

            album = albumService.saveAlbum(album);
            
            return ResponseEntity.ok(new AlbumViewDTO(album.getId(),album.getName(),album.getDescription()));
        }catch(Exception e){
            log.debug(AlbumError.ADD_ALBUM_ERROR.toString() + ": " + e.getMessage());
            return new ResponseEntity<>(new AlbumViewDTO(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/albums", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ApiResponse(responseCode = "200", description = "List of albums!")
    @Operation(summary = "Lists all albums!")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<List<AlbumViewDTO>> albums(Authentication authentication){
        Account account = accountService.findByEmail(authentication.getName()).get();

        List<AlbumViewDTO> albums = albumService.findAllByAccountId(account.getId()).stream()
                                    .map((alb) -> new AlbumViewDTO(alb.getId(), alb.getName(), alb.getDescription()))
                                    .toList();

    
        return ResponseEntity.ok(albums);
    }
}
