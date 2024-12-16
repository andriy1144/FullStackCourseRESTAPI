package org.studyeasy.SpringRestDemo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringRestDemo.Entities.Album;

@Repository
public interface AlbumRepo extends JpaRepository<Album, Long>{
    
}
