package com.andd.DoDangAn.DoDangAn.repository;

import com.andd.DoDangAn.DoDangAn.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,String> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    long count();
}
