package com.andd.DoDangAn.DoDangAn.repository;

import com.andd.DoDangAn.DoDangAn.models.Comment;
import com.andd.DoDangAn.DoDangAn.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProductId(String productID);
    List<Comment> findByProductIdAndUserId(Product productID, String userID);
    void deleteByProductId(String productID);
}