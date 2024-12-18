package org.studyeasy.SpringRestDemo.Sevice;

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

}
