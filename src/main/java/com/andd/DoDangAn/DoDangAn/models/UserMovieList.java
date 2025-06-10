package com.andd.DoDangAn.DoDangAn.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usermovielist")
public class UserMovieList {
    @EmbeddedId
    private UserMovieListId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "userId", columnDefinition = "VARCHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private User user;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "productId", columnDefinition = "VARCHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private Product product;

    @Column(name = "addedDate")
    private LocalDateTime addedDate;

    // Constructors
    public UserMovieList() {
    }

    public UserMovieList(User user, Product product) {
        this.user = user;
        this.product = product;
        this.id = new UserMovieListId(user.getId(), product.getId());
        this.addedDate = LocalDateTime.now();
    }

    // Getters and setters
    public UserMovieListId getId() {
        return id;
    }

    public void setId(UserMovieListId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }
}