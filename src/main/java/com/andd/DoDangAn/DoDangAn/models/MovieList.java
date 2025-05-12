package com.andd.DoDangAn.DoDangAn.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

    @Entity
    @Table(name = "movie_lists")
    public class MovieList {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @ManyToMany
        @JoinTable(
                name = "movie_list_products",
                joinColumns = @JoinColumn(name = "movie_list_id"),
                inverseJoinColumns = @JoinColumn(name = "product_id")
        )
        private List<Product> products = new ArrayList<>();

        // Getters và Setters

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }
    }
