package org.studyeasy.SpringRestDemo.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringRestDemo.Entities.Account;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long>{
    Optional<Account> findByEmail(String email);
}
