package com.andd.DoDangAn.DoDangAn.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.*;
import java.util.Calendar;
import java.util.Objects;

@Entity
@Table(name="Products")
public class Product {
    @Id
    @Column (name="productID")
    private String id;
    private String categoryID;
    @Column(name="productName")
    @NotNull
    @NotBlank(message = "product name cannot be null")
    @Size(min=3,max=300,message = "thieu roi con ga")
    private String productName;
    @Min(0)
    @NotNull
    private int price;
    @NotNull
    @Size(min=5,max=1000)
    private String description;
    @NotNull(message = "Ảnh không được để trống")
    private String imageUrl = "/uploads/default.png";
    @NotNull(message = "Video không được trống")
    private String videoUrl;
    public Product() {
    }

    public Product(String id, String categoryID,String productName, int price, String description,String imageUrl,String videoUrl) {
        this.id = id;
        this.categoryID = categoryID;
        this.price = price;
        this.productName = productName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getPrice(){
        return price;
    }
    public String getFormattedPrice() {
        return String.format("$ %.2f",(price*100.00)/100.00);
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(@NotNull(message = "Video không được trống") String videoUrl) {
        this.videoUrl = videoUrl;
    }
}

