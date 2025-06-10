package com.andd.DoDangAn.DoDangAn.repository;

import com.andd.DoDangAn.DoDangAn.models.Category;
import com.andd.DoDangAn.DoDangAn.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    boolean existsByCategoryName(String categoryName);
    
    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products")
    List<Category> findAllWithProducts();
}
//package com.andd.DoDangAn.DoDangAn.repository;

//import com.andd.DoDangAn.DoDangAn.models.Product;
//import org.springframework.data.jpa.repository.JpaRepository;

//import java.util.List;

//public interface ProductRepository extends JpaRepository<Product,String> {
//  List<Product> findByProductName(String productName);
//}
