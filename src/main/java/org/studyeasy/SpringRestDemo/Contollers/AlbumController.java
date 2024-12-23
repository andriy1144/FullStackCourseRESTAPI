package org.studyeasy.SpringRestDemo.Contollers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.studyeasy.SpringRestDemo.Entities.Account;
import org.studyeasy.SpringRestDemo.Entities.Album;
import org.studyeasy.SpringRestDemo.Entities.Photo;
import org.studyeasy.SpringRestDemo.Sevice.AccountService;
import org.studyeasy.SpringRestDemo.Sevice.AlbumService;
import org.studyeasy.SpringRestDemo.Sevice.PhotoService;
import org.studyeasy.SpringRestDemo.Util.AppUtils.AppUtil;
import org.studyeasy.SpringRestDemo.Util.Constants.AlbumError;
import org.studyeasy.SpringRestDemo.payload.album.AlbumPayloadDTO;
import org.studyeasy.SpringRestDemo.payload.album.AlbumViewDTO;
import org.studyeasy.SpringRestDemo.payload.album.PhotoDTO;
import org.studyeasy.SpringRestDemo.payload.album.PhotoPayloadDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/albums")
@Slf4j
@Tag(name = "Album controller!", description = "Controller for album and photo management!")
public class AlbumController {
    static final String PHOTOS_FOLDER_NAME = "photos";
    static final String THUMBNAIL_FOLDER_NAME = "thumbnails";
    static final int THUMBNAIL_WIDTH = 300;
   
    @Autowired
    private AccountService accountService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private PhotoService photoService;

    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
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
            
            return ResponseEntity.ok(new AlbumViewDTO(album.getId(),album.getName(),album.getDescription(),null));
        }catch(Exception e){
            log.debug(AlbumError.ADD_ALBUM_ERROR.toString() + ": " + e.getMessage());
            return new ResponseEntity<>(new AlbumViewDTO(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ApiResponse(responseCode = "200", description = "List of albums!")
    @Operation(summary = "Lists all albums!")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<List<AlbumViewDTO>> albums(Authentication authentication){
        Account account = accountService.findByEmail(authentication.getName()).get();

        List<AlbumViewDTO> albums = new ArrayList<>();
        for(Album album : albumService.findAllByAccountId(account.getId())){

            List<PhotoDTO> photos = new ArrayList<>();
            for(Photo photo: photoService.findAllByAlbumId(album.getId())){
                String link = "albums/" + album.getId() + "/photos/" + photo.getId() + "/download_photo";
                photos.add(new PhotoDTO(photo.getId(),photo.getName(),photo.getDescription(),photo.getFileName(),link));
            }

            albums.add(new AlbumViewDTO(album.getId(), album.getName(), album.getDescription(),photos));
        }

    
        return ResponseEntity.ok(albums);
    }

    @GetMapping(value = "/{album_id}",produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ApiResponse(responseCode = "200", description = "List album by Id!")
    @Operation(summary = "Lists all albums!")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<AlbumViewDTO> albumById(@PathVariable(name = "album_id") Long album_id,Authentication authentication){
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        Optional<Album> optionalAlbum = albumService.findById(album_id);
        Album album;
        if(optionalAccount.isPresent()){
            album = optionalAlbum.get();

            if(account.getId() != album.getAccount().getId()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<PhotoDTO> photos = new ArrayList<>();
        for(Photo photo: photoService.findAllByAlbumId(album.getId())){
            String link = "albums/" + album.getId() + "/photos/" + photo.getId() + "/download_photo";
            photos.add(new PhotoDTO(photo.getId(),photo.getName(),photo.getDescription(),photo.getFileName(),link));
        }

        AlbumViewDTO albumViewDTO = new AlbumViewDTO(album.getId(),album.getName(),album.getDescription(),photos); 
        return ResponseEntity.ok(albumViewDTO);
    }

    @PutMapping(value="/{album_id}/update")
    @SecurityRequirement(name = "studyeasy-demo-api")
    @Operation(summary = "Update album by id!")
    public ResponseEntity<AlbumViewDTO> uploadAlbum(@Valid @RequestBody AlbumPayloadDTO albumPayloadDTO, @PathVariable(name = "album_id") Long album_id, Authentication authentication){
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        Optional<Album> optionalAlbum = albumService.findById(album_id);
        Album album;
        if(optionalAccount.isPresent()){
            album = optionalAlbum.get();

            if(account.getId() != album.getAccount().getId()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        album.setDescription(albumPayloadDTO.getDescription());
        album.setName(albumPayloadDTO.getName());

        album = albumService.saveAlbum(album);

        
        List<PhotoDTO> photos = new ArrayList<>();
        for(Photo photo: photoService.findAllByAlbumId(album.getId())){
            String link = "albums/" + album.getId() + "/photos/" + photo.getId() + "/download_photo";
            photos.add(new PhotoDTO(photo.getId(),photo.getName(),photo.getDescription(),photo.getFileName(),link));
        }

        return ResponseEntity.ok(new AlbumViewDTO(album.getId(), album.getName(), album.getDescription(), photos));
    }

    @PutMapping(value = "/{album_id}/photos/{photo_id}/update", consumes = "application/json")
    @SecurityRequirement(name = "studyeasy-demo-api")
    @Operation(summary = "Update photo by id!")
    public ResponseEntity<PhotoDTO> updatePhoto(@Valid @RequestBody PhotoPayloadDTO photoPayloadDTO, @PathVariable(name = "photo_id") Long photo_id, @PathVariable(name = "album_id") Long album_id, Authentication authentication){
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        Optional<Album> optionalAlbum = albumService.findById(album_id);
        Album album;
        if(optionalAccount.isPresent()){
            album = optionalAlbum.get();

            if(account.getId() != album.getAccount().getId()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Optional<Photo> optionalPhoto = photoService.findById(photo_id);
        if(!optionalPhoto.isPresent() || optionalPhoto.get().getAlbum().getId()!= album.getId()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Photo photo = optionalPhoto.get();
        photo.setDescription(photoPayloadDTO.getDescription());
        photo.setName(photoPayloadDTO.getName());

        photo = photoService.savePhoto(photo);
        
        String photoDownloadLink = String.format("albums/%d/photos/%d/download", album_id,photo_id);

        PhotoDTO photoDTO = new PhotoDTO(photo.getId(), photo.getName(), photo.getDescription(),photo.getFileName(),photoDownloadLink);

        return ResponseEntity.ok(photoDTO);
    }


    @PostMapping(value = "/{album_id}/upload_photos", consumes = {"multipart/form-data"})
    @ApiResponse(responseCode = "400", description = "Check the payload or token")
    @SecurityRequirement(name = "studyeasy-demo-api")
    @Operation(summary = "Upload photos into album!")
    public ResponseEntity<List<HashMap<String, List<String>>>> photosUpload(@RequestPart(required = true) MultipartFile[] files, @PathVariable(name = "album_id") Long album_id, Authentication authentication){
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        Optional<Album> optionalAlbum = albumService.findById(album_id);
        Album album;
        if(optionalAccount.isPresent()){
            album = optionalAlbum.get();

            if(account.getId() != album.getAccount().getId()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        List<String> fileNamesWithSuccess = new ArrayList<>();
        List<String> fileNamesWithError = new ArrayList<>();

        Arrays.stream(files).forEach(file -> {
            String contentType = file.getContentType();
            if(contentType.equals("image/png") ||
               contentType.equals("image/png") ||
               contentType.equals("image/jpeg")){
                fileNamesWithSuccess.add(file.getOriginalFilename());
            
                //Generating random file name
                int length = 10;
                boolean useLetter = true;
                boolean useNumber = true;

                try{
                    String fileName = file.getOriginalFilename();
                    String generatedString = RandomStringUtils.random(length, useLetter, useNumber);
                    String finalPhotoName = generatedString + fileName;

                    String absolute_fileLocation = AppUtil.getPhotoUploadPath(finalPhotoName, PHOTOS_FOLDER_NAME, album_id);

                    Path path = Paths.get(absolute_fileLocation);
                    Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                    
                    Photo photo = new Photo();
                    photo.setName(fileName);
                    photo.setFileName(finalPhotoName);
                    photo.setOriginalFileName(fileName);
                    photo.setAlbum(album);             
                    
                    photoService.savePhoto(photo);

                    BufferedImage thumbnailImage = AppUtil.getThumbnail(file, THUMBNAIL_WIDTH);
                    File thumbnailLocation = new File(AppUtil.getPhotoUploadPath(finalPhotoName, THUMBNAIL_FOLDER_NAME, album_id));
                    ImageIO.write(thumbnailImage, file.getContentType().split("/")[1], thumbnailLocation);
                }catch(Exception e){
                    log.debug("{} : {}",AlbumError.PHOTO_UPLOAD_ERROR.toString(),e.getMessage());
                    fileNamesWithError.add(file.getOriginalFilename());
                }

            }else{
                fileNamesWithError.add(file.getOriginalFilename());
            }

        });

        HashMap<String, List<String>> result = new HashMap<>();
        result.put("SUCCESS", fileNamesWithSuccess);
        result.put("ERRORS", fileNamesWithError);

        List<HashMap<String, List<String>>> response = new ArrayList<>();
        response.add(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{album_id}/photos/{photo_id}/download")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<?> downloadPhoto(@PathVariable(name = "album_id") Long album_id,
                                           @PathVariable(name = "photo_id") Long photo_id,
                                           Authentication authentication){
            return downloadFile(album_id, photo_id, PHOTOS_FOLDER_NAME, authentication);
    }

    @GetMapping(value = "/{album_id}/photos/{photo_id}/download_thumbnail")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<?> downloadThumbNail(@PathVariable(name = "album_id") Long album_id,
                                               @PathVariable(name = "photo_id") Long photo_id,
                                               Authentication authentication){
            return downloadFile(album_id,photo_id,THUMBNAIL_FOLDER_NAME,authentication);
    }

    public ResponseEntity<?> downloadFile(Long album_id, Long photo_id, String folderName, Authentication authentication){
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        Optional<Album> optionalAlbum = albumService.findById(album_id);
        Album album;
        if(optionalAccount.isPresent()){
            album = optionalAlbum.get();

            if(account.getId() != album.getAccount().getId()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Optional<Photo> optionalPhoto = photoService.findById(photo_id);
        if(optionalPhoto.isPresent()){
            Photo photo = optionalPhoto.get();

            if(photo.getAlbum().getId() != album_id){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            Resource resource = null;

            try{
                resource = AppUtil.getFileAsResource(album_id, THUMBNAIL_FOLDER_NAME, photo.getFileName());
            }catch(IOException e){
                return ResponseEntity.internalServerError().build();
            }

            if(resource == null){
                return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
            }

            String contentType = "application/cotet-stream";
            String headerValue = "attachment; filename=\"" + photo.getOriginalFileName() + "\"";

            return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,headerValue)
                        .body(resource);
                        
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


}
