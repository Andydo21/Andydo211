package com.andd.DoDangAn.DoDangAn.repository;

import com.andd.DoDangAn.DoDangAn.models.Episode;
import com.andd.DoDangAn.DoDangAn.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EpisodeRepository extends JpaRepository<Episode,Long> {
    List<Episode> findByProductId(String id);
    void deleteByProductId(String id);
}
