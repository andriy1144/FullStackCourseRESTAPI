package org.studyeasy.SpringRestDemo.Sevice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studyeasy.SpringRestDemo.Entities.Album;
import org.studyeasy.SpringRestDemo.Repositories.AlbumRepo;

@Service
public class AlbumService {
    @Autowired
    private AlbumRepo albumRepo;

    public Album saveAlbum(Album album){
        return albumRepo.save(album);
    }

    public List<Album> findAllByAccountId(Long id){
        return albumRepo.findByAccount_Id(id);
    }
}
