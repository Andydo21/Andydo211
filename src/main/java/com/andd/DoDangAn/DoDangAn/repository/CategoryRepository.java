package com.andd.DoDangAn.DoDangAn.repository;

import com.andd.DoDangAn.DoDangAn.models.Category;
import com.andd.DoDangAn.DoDangAn.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, String>{

}
//package com.andd.DoDangAn.DoDangAn.repository;

//import com.andd.DoDangAn.DoDangAn.models.Product;
//import org.springframework.data.jpa.repository.JpaRepository;

//import java.util.List;

//public interface ProductRepository extends JpaRepository<Product,String> {
//  List<Product> findByProductName(String productName);
//}
