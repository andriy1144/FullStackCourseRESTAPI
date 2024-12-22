package org.studyeasy.SpringRestDemo.Sevice;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studyeasy.SpringRestDemo.Entities.Photo;
import org.studyeasy.SpringRestDemo.Repositories.PhotoRepo;

@Service
public class PhotoService {
    
    @Autowired
    private PhotoRepo photoRepo;

    public Photo savePhoto(Photo photo){
        return photoRepo.save(photo);
    }

    public Optional<Photo> findById(Long id){
        return photoRepo.findById(id);
    }

    public List<Photo> findAllByAlbumId(Long id){
        return photoRepo.findByAlbum_Id(id);
    }
}
