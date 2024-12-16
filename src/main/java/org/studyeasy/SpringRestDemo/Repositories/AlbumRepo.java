package org.studyeasy.SpringRestDemo.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringRestDemo.Entities.Album;


@Repository
public interface AlbumRepo extends JpaRepository<Album, Long>{
    List<Album> findByAccount_Id(Long account_id);
}
