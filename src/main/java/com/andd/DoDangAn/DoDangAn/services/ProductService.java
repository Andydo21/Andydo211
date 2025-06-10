package com.andd.DoDangAn.DoDangAn.services;


import com.andd.DoDangAn.DoDangAn.models.Product;
import com.andd.DoDangAn.DoDangAn.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    private static final String API_KEY = "28111d1a";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Transactional(readOnly = false)
    public void incrementViewCount(String productID) {
        productRepository.incrementViewCount(productID);
    }

    @Transactional
    public void updateOMDBRating(Product product) {
        try {
            String url = String.format("http://www.omdbapi.com/?apikey=%s&t=%s", API_KEY, product.getProductName());
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if (root.has("imdbRating") && !root.get("imdbRating").asText().equals("N/A")) {
                product.setScore(root.get("imdbRating").asDouble());;
                productRepository.save(product);
            }
        } catch (Exception e) {
            product.setScore(0.0);
            productRepository.save(product);
        }
    }
}
