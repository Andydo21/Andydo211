package com.andd.DoDangAn.DoDangAn.repository;

import com.andd.DoDangAn.DoDangAn.models.UserMovieList;
import com.andd.DoDangAn.DoDangAn.models.UserMovieListId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMovieListRepository extends CrudRepository<UserMovieList, UserMovieListId> {
    List<UserMovieList> findByUserId(String userId);
    boolean existsByUserIdAndProductId(String userId, String productId);
    void deleteByUserIdAndProductId(String userId, String productId);
}
