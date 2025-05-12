package com.andd.DoDangAn.DoDangAn.services;


import com.andd.DoDangAn.DoDangAn.models.Product;
import com.andd.DoDangAn.DoDangAn.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = false)
    public void incrementViewCount(String productID) {
        productRepository.incrementViewCount(productID);
    }
}
