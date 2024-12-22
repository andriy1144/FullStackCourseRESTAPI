package org.studyeasy.SpringRestDemo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringRestDemo.Entities.Photo;
import java.util.List;



@Repository
public interface PhotoRepo extends JpaRepository<Photo, Long>{
    List<Photo> findByAlbum_Id(Long id);
}
