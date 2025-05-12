package com.andd.DoDangAn.DoDangAn.models;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserMovieListId implements Serializable {
    private String userId;
    private String productId;

    // Constructors
    public UserMovieListId() {
    }

    public UserMovieListId(String userId, String productId) {
        this.userId = userId;
        this.productId = productId;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    // Equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserMovieListId that = (UserMovieListId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, productId);
    }
}
