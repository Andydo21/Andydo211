package com.andd.DoDangAn.DoDangAn.repository;

import com.andd.DoDangAn.DoDangAn.models.MovieList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieListRepository extends JpaRepository<MovieList, Long> {
    List<MovieList> findByUserId(String userId);
    //Optional<MovieList> findByProductId(String productId);
    @Query("SELECT m FROM MovieList m WHERE m.user.id = :userId")
    Optional<MovieList> findByUserId1(@Param("userId") String userId);


}

