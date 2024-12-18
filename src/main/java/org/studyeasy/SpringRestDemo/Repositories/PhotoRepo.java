package org.studyeasy.SpringRestDemo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringRestDemo.Entities.Photo;


@Repository
public interface PhotoRepo extends JpaRepository<Photo, Long>{

}
